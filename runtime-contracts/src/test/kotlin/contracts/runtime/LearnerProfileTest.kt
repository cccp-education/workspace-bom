package contracts.runtime

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LearnerProfileTest {

    @Test
    fun `creates learner profile with defaults`() {
        val profile = LearnerProfile(
            learnerId = "learner-001",
            formationId = "formation-fpa",
        )
        assertThat(profile.learnerId).isEqualTo("learner-001")
        assertThat(profile.formationId).isEqualTo("formation-fpa")
        assertThat(profile.completedModules).isEmpty()
        assertThat(profile.currentModule).isNull()
        assertThat(profile.progressionPct).isEqualTo(0.0)
        assertThat(profile.comprehensionScore).isEqualTo(0.0)
        assertThat(profile.weakPoints).isEmpty()
        assertThat(profile.lastInteractionAt).isNull()
        assertThat(profile.annotations).isEmpty()
    }

    @Test
    fun `creates learner profile with all fields`() {
        val profile = LearnerProfile(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            completedModules = listOf("mod-1", "mod-2"),
            currentModule = "mod-3",
            progressionPct = 65.0,
            comprehensionScore = 80.0,
            weakPoints = listOf("concept-X", "concept-Y"),
            lastInteractionAt = "2026-08-21T10:30:00Z",
            annotations = mapOf("mod-1" to "= Note sur mod-1"),
        )
        assertThat(profile.completedModules).hasSize(2)
        assertThat(profile.progressionPct).isEqualTo(65.0)
        assertThat(profile.comprehensionScore).isEqualTo(80.0)
        assertThat(profile.annotations).hasSize(1)
    }

    @Test
    fun `learnerId must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            LearnerProfile(learnerId = "", formationId = "f")
        }
        assertThat(ex.message).contains("learnerId")
    }

    @Test
    fun `formationId must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            LearnerProfile(learnerId = "l", formationId = "")
        }
        assertThat(ex.message).contains("formationId")
    }

    @Test
    fun `progressionPct must be 0 to 100`() {
        val ex = assertThrows<IllegalArgumentException> {
            LearnerProfile(
                learnerId = "l",
                formationId = "f",
                progressionPct = 150.0,
            )
        }
        assertThat(ex.message).contains("progressionPct")
    }

    @Test
    fun `comprehensionScore must be 0 to 100`() {
        val ex = assertThrows<IllegalArgumentException> {
            LearnerProfile(
                learnerId = "l",
                formationId = "f",
                comprehensionScore = -5.0,
            )
        }
        assertThat(ex.message).contains("comprehensionScore")
    }
}

class SessionMemoryContractTest {

    @Test
    fun `save and load profile via fake implementation`() {
        val memory = FakeSessionMemory()
        val profile = LearnerProfile(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            completedModules = listOf("mod-1"),
        )
        memory.save(profile)
        val loaded = memory.load("learner-001", "formation-fpa")
        assertThat(loaded).isEqualTo(profile)
    }

    @Test
    fun `load returns null when profile does not exist`() {
        val memory = FakeSessionMemory()
        val loaded = memory.load("unknown", "unknown")
        assertThat(loaded).isNull()
    }
}

private class FakeSessionMemory : SessionMemoryContract {
    private val store = mutableMapOf<Pair<String, String>, LearnerProfile>()

    override fun save(profile: LearnerProfile) {
        store[profile.learnerId to profile.formationId] = profile
    }

    override fun load(learnerId: String, formationId: String): LearnerProfile? =
        store[learnerId to formationId]
}