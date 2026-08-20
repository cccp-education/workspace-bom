package contracts.runtime

interface SessionMemoryContract {
    fun save(profile: LearnerProfile)
    fun load(learnerId: String, formationId: String): LearnerProfile?
}