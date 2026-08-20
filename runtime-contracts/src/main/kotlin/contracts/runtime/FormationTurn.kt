package contracts.runtime

import contracts.session.SessionPrompt
import contracts.session.SessionResponse
import java.time.Instant

data class FormationTurn(
    val prompt: SessionPrompt,
    val response: SessionResponse,
    val timestamp: Instant = Instant.now(),
)