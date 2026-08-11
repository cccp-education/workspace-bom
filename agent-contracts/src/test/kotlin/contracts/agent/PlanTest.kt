package contracts.agent

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PlanTest {

    @Test
    fun `Plan holds title epics totalPoints and estimatedSessions`() {
        val epic = Epic(
            name = "EPIC-1",
            description = "First epic",
            points = 3,
            userStories = listOf(
                UserStory(
                    description = "US-1",
                    tasks = listOf(
                        GradleTask(description = "Run tests", gradleTask = "./gradlew test")
                    )
                )
            )
        )
        val plan = Plan(
            title = "Sample plan",
            epics = listOf(epic),
            totalPoints = 3,
            estimatedSessions = "1-2"
        )

        assertThat(plan.title).isEqualTo("Sample plan")
        assertThat(plan.epics).hasSize(1)
        assertThat(plan.epics[0].name).isEqualTo("EPIC-1")
        assertThat(plan.epics[0].userStories[0].tasks[0].gradleTask).isEqualTo("./gradlew test")
        assertThat(plan.totalPoints).isEqualTo(3)
        assertThat(plan.estimatedSessions).isEqualTo("1-2")
    }

    @Test
    fun `Plan allows empty epics list`() {
        val plan = Plan(
            title = "Empty plan",
            epics = emptyList(),
            totalPoints = 0,
            estimatedSessions = "0"
        )

        assertThat(plan.epics).isEmpty()
        assertThat(plan.totalPoints).isZero()
    }

    @Test
    fun `Plan carries multi-tool tasks via GradleTask toolType`() {
        val editTask = GradleTask(
            description = "Patch file",
            gradleTask = "",
            toolType = TaskType.EDIT_FILE,
            target = "build.gradle.kts"
        )
        val shellTask = GradleTask(
            description = "Run npm install",
            gradleTask = "",
            toolType = TaskType.EXEC_SHELL,
            target = "npm install"
        )
        val epic = Epic(
            name = "MULTI",
            description = "Multi-tool epic",
            points = 2,
            userStories = listOf(
                UserStory(description = "Multi US", tasks = listOf(editTask, shellTask))
            )
        )
        val plan = Plan(
            title = "Multi-tool plan",
            epics = listOf(epic),
            totalPoints = 2,
            estimatedSessions = "1"
        )

        val tasks = plan.epics[0].userStories[0].tasks
        assertThat(tasks).hasSize(2)
        assertThat(tasks[0].toolType).isEqualTo(TaskType.EDIT_FILE)
        assertThat(tasks[1].toolType).isEqualTo(TaskType.EXEC_SHELL)
    }
}