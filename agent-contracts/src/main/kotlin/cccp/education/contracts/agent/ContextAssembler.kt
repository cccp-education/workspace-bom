package cccp.education.contracts.agent

interface ContextAssembler {
    suspend fun assemble(intention: String, workspaceRoot: String): AgentState.ContextReady
}

interface PlanExecutor {
    suspend fun execute(state: AgentState.Planned): AgentState.Executed
    suspend fun evaluate(state: AgentState.Executed): AgentState.Evaluated
    suspend fun replan(state: AgentState.Evaluated): AgentState.Evaluated
}
