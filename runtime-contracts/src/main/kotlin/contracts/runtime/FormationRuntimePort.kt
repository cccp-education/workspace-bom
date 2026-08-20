package contracts.runtime

interface FormationRuntimePort {
    fun startSession(bootstrap: SessionBootstrap): SessionId
    fun executeTurn(sessionId: SessionId, userPrompt: String): FormationTurn
    fun endSession(sessionId: SessionId): SessionSummary
}