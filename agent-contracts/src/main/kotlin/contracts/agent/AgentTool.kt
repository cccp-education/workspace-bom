package contracts.agent

interface AgentTool<TInput, TOutput> {
    val name: String
    val description: String
    suspend fun execute(input: TInput): TOutput
    fun validate(input: TInput): Result<Unit>
}
