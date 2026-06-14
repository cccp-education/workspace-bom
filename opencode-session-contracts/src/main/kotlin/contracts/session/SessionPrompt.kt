package contracts.session

import java.util.UUID

data class SessionPrompt(
    val sessionId: UUID = UUID.randomUUID(),
    val prompt: String,
    val context: AgentContext? = null,
    val maxActions: Int = 10,
    val model: String? = null,
) {
    init {
        require(prompt.isNotBlank()) { "prompt cannot be blank" }
        require(maxActions > 0) { "maxActions must be > 0" }
    }
}
