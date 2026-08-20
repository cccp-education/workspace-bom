package contracts.runtime

import java.time.Instant

data class SessionSummary(
    val learnerId: String,
    val formationId: String,
    val totalTurns: Int,
    val finalProfile: LearnerProfile,
    val endedAt: Instant,
) {
    init {
        require(learnerId.isNotBlank()) { "learnerId must be non-blank" }
        require(formationId.isNotBlank()) { "formationId must be non-blank" }
        require(totalTurns >= 0) { "totalTurns must be non-negative, got $totalTurns" }
    }
}