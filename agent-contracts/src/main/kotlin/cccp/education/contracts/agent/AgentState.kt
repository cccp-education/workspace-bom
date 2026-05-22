package cccp.education.contracts.agent

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

data class UserStory(
    val description: String,
    val tasks: List<GradleTask> = emptyList()
)

data class GradleTask(
    val description: String,
    val gradleTask: String,
    val project: String = ""
)
