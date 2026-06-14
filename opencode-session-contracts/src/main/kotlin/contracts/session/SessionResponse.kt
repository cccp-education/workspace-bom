package contracts.session

import java.util.UUID

data class SessionResponse(
    val sessionId: UUID,
    val output: String,
    val toolCalls: List<ToolCallRecord> = emptyList(),
    val tokenUsage: TokenUsage = TokenUsage(),
    val status: SessionStatus,
)
