package contracts.runtime

data class LearnerProfile(
    val learnerId: String,
    val formationId: String,
    val completedModules: List<String> = emptyList(),
    val currentModule: String? = null,
    val progressionPct: Double = 0.0,
    val comprehensionScore: Double = 0.0,
    val weakPoints: List<String> = emptyList(),
    val lastInteractionAt: String? = null,
    val annotations: Map<String, String> = emptyMap(),
) {
    init {
        require(learnerId.isNotBlank()) { "learnerId must be non-blank" }
        require(formationId.isNotBlank()) { "formationId must be non-blank" }
        require(progressionPct in 0.0..100.0) {
            "progressionPct must be between 0 and 100, got $progressionPct"
        }
        require(comprehensionScore in 0.0..100.0) {
            "comprehensionScore must be between 0 and 100, got $comprehensionScore"
        }
    }
}