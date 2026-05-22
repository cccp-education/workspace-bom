package cccp.education.contracts.agent

object PlanSerializer {
    fun serialize(epics: List<Epic>): String {
        val lines = mutableListOf("Plan:")
        if (epics.isEmpty()) return lines.joinToString("\n")
        epics.forEachIndexed { i, epic ->
            lines.add("  EPIC ${i + 1}: ${epic.name} (${epic.points}pts)")
            if (epic.description.isNotBlank()) {
                lines.add("    ${epic.description}")
            }
            epic.userStories.forEachIndexed { j, us ->
                lines.add("    US ${i + 1}.${j + 1}: ${us.description}")
                us.tasks.forEach { task ->
                    val bracket = if (task.gradleTask.isNotBlank()) " [${task.gradleTask}]" else ""
                    lines.add("      - ${task.description}$bracket")
                }
            }
        }
        return lines.joinToString("\n")
    }

    fun deserialize(input: String): List<Epic> {
        val lines = input.lines().filter { it.isNotBlank() }
        val epics = mutableListOf<Epic>()
        var currentEpic: Epic? = null
        var currentUs: UserStory? = null
        val usList = mutableListOf<UserStory>()
        val taskList = mutableListOf<GradleTask>()

        fun flushUserStory() {
            currentUs?.let { us ->
                usList.add(us.copy(tasks = taskList.toList()))
            }
            taskList.clear()
            currentUs = null
        }

        fun flushEpic() {
            flushUserStory()
            currentEpic?.let { epics.add(it.copy(userStories = usList.toList())) }
            usList.clear()
            currentEpic = null
        }

        val epicRegex = Regex("""EPIC \d+:\s*(.*?)\s*\((\d+)pts\)""")
        val usRegex = Regex("""US \d+\.\d+:\s*(.*)""")

        for (line in lines) {
            val trimmed = line.trim()

            when {
                trimmed == "Plan:" -> continue

                epicRegex.matches(trimmed) -> {
                    flushEpic()
                    val match = epicRegex.find(trimmed)!!
                    val name = match.groupValues[1]
                    val points = match.groupValues[2].toIntOrNull() ?: 0
                    currentEpic = Epic(name = name, description = "", points = points, userStories = emptyList())
                }

                usRegex.matches(trimmed) -> {
                    flushUserStory()
                    val match = usRegex.find(trimmed)!!
                    val desc = match.groupValues[1]
                    currentUs = UserStory(description = desc, tasks = emptyList())
                }

                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    val content = trimmed.drop(2).trim()
                    val bracketStart = content.indexOf("[")
                    val bracketEnd = content.indexOf("]")
                    if (bracketStart >= 0 && bracketEnd > bracketStart) {
                        val desc = content.substring(0, bracketStart).trim()
                        val gradleTask = content.substring(bracketStart + 1, bracketEnd)
                        taskList.add(GradleTask(description = desc, gradleTask = gradleTask))
                    } else {
                        taskList.add(GradleTask(description = content, gradleTask = ""))
                    }
                }

                else -> {
                    if (currentEpic != null && currentEpic!!.description.isBlank()) {
                        currentEpic = currentEpic!!.copy(description = trimmed)
                    }
                }
            }
        }
        flushEpic()

        return epics
    }
}
