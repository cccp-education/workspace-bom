package contracts.session

data class AgentContext(
    val eagerRules: String = "",
    val ragChunks: List<String> = emptyList(),
    val graphRelations: String = "",
    val backlogItems: List<String> = emptyList(),
)
