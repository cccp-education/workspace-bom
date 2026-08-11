package contracts.agent

enum class AgentPhase { BUILD_CONTEXT, CLASSIFY, PLAN, EXECUTE, EVALUATE, REPLAN, FINALIZE, GIVE_UP }

sealed class AgentState {
    abstract val intention: String
    abstract val phase: AgentPhase
    abstract val error: String?

    data class Initial(
        override val intention: String,
        override val phase: AgentPhase = AgentPhase.BUILD_CONTEXT,
        val workspaceRoot: String = System.getProperty("user.dir"),
        override val error: String? = null
    ) : AgentState()

    data class ContextReady(
        override val intention: String,
        val compositeContext: String = "",
        val afnorCorpus: String = "",
        override val phase: AgentPhase = AgentPhase.CLASSIFY,
        override val error: String? = null
    ) : AgentState()

    data class Classified(
        override val intention: String,
        val compositeContext: String = "",
        val afnorCorpus: String = "",
        val classification: String = "",
        val modelChoice: String = "",
        override val phase: AgentPhase = AgentPhase.PLAN,
        override val error: String? = null
    ) : AgentState()

    data class Planned(
        override val intention: String,
        val compositeContext: String = "",
        val afnorCorpus: String = "",
        val classification: String = "",
        val modelChoice: String = "",
        val planJson: String = "",
        val epics: List<Epic> = emptyList(),
        override val phase: AgentPhase = AgentPhase.EXECUTE,
        override val error: String? = null
    ) : AgentState()

    data class Executed(
        override val intention: String,
        val compositeContext: String = "",
        val afnorCorpus: String = "",
        val classification: String = "",
        val modelChoice: String = "",
        val planJson: String = "",
        val epics: List<Epic> = emptyList(),
        val executionResults: Map<String, String> = emptyMap(),
        override val phase: AgentPhase = AgentPhase.EVALUATE,
        override val error: String? = null
    ) : AgentState()

    data class Evaluated(
        override val intention: String,
        val compositeContext: String = "",
        val afnorCorpus: String = "",
        val classification: String = "",
        val modelChoice: String = "",
        val planJson: String = "",
        val epics: List<Epic> = emptyList(),
        val executionResults: Map<String, String> = emptyMap(),
        val evaluationScore: Double = 0.0,
        val evaluationFeedback: String = "",
        val replanCount: Int = 0,
        val maxReplans: Int = 3,
        override val phase: AgentPhase = AgentPhase.FINALIZE,
        override val error: String? = null
    ) : AgentState()

    data class Finalized(
        override val intention: String,
        val finalOutput: String = "",
        override val phase: AgentPhase = AgentPhase.FINALIZE,
        override val error: String? = null
    ) : AgentState() {
        val isSuccessful: Boolean get() = error == null
    }

    val isTerminal: Boolean get() = this is Finalized
}

data class Epic(
    val name: String,
    val description: String,
    val points: Int,
    val userStories: List<UserStory> = emptyList()
)

/**
 * Plan de décomposition — structure de données pure N0.
 * Source unique de vérité pour planner (N2), codebase (N1), runner (N3).
 */
data class Plan(
    val title: String,
    val epics: List<Epic>,
    val totalPoints: Int,
    val estimatedSessions: String
)

data class UserStory(
    val description: String,
    val tasks: List<GradleTask> = emptyList()
)

/**
 * Type of tool a [GradleTask] drives. A plan may emit multi-tool tasks:
 * [GRADLE] invokes a Gradle task via `./gradlew`, while [EDIT_FILE] and
 * [EXEC_SHELL] delegate to the codebase vibecoding hub (ToolRegistry).
 *
 * Default is [GRADLE] to preserve the legacy contract of `PlannerIntegration`
 * (codebase) which only consumes `gradleTask`.
 */
enum class TaskType {
    GRADLE,
    EDIT_FILE,
    EXEC_SHELL
}

data class GradleTask(
    val description: String,
    val gradleTask: String,
    val toolType: TaskType = TaskType.GRADLE,
    val target: String = "",
    val project: String = "",
    val expectedOutput: String = "BUILD SUCCESSFUL",
    val maxRetries: Int = 3,
    val verifyHook: String? = null
) {
    init {
        require(description.isNotBlank()) {
            "GradleTask.description must not be blank"
        }
        require(!(toolType == TaskType.GRADLE && gradleTask.isBlank())) {
            "GradleTask.gradleTask must not be blank when toolType is GRADLE"
        }
        require(!(toolType != TaskType.GRADLE && target.isBlank())) {
            "GradleTask.target must not be blank when toolType is $toolType"
        }
        require(expectedOutput.isNotBlank()) {
            "GradleTask.expectedOutput must not be blank"
        }
        require(maxRetries in 1..10) {
            "GradleTask.maxRetries must be between 1 and 10, got $maxRetries"
        }
    }
}
