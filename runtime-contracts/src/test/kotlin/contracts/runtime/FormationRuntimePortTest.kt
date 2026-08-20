package contracts.runtime

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class SessionIdTest {

    @Test
    fun `creates session id with value`() {
        val id = SessionId("session-123")
        assertThat(id.value).isEqualTo("session-123")
    }

    @Test
    fun `session id value must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            SessionId("")
        }
        assertThat(ex.message).contains("value")
    }
}

class SessionSummaryTest {

    @Test
    fun `creates session summary with all fields`() {
        val profile = LearnerProfile(learnerId = "l", formationId = "f")
        val endedAt = Instant.parse("2026-08-21T12:00:00Z")
        val summary = SessionSummary(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            totalTurns = 10,
            finalProfile = profile,
            endedAt = endedAt,
        )
        assertThat(summary.learnerId).isEqualTo("learner-001")
        assertThat(summary.totalTurns).isEqualTo(10)
        assertThat(summary.finalProfile).isEqualTo(profile)
        assertThat(summary.endedAt).isEqualTo(endedAt)
    }

    @Test
    fun `totalTurns must be non-negative`() {
        val profile = LearnerProfile(learnerId = "l", formationId = "f")
        val ex = assertThrows<IllegalArgumentException> {
            SessionSummary(
                learnerId = "l",
                formationId = "f",
                totalTurns = -1,
                finalProfile = profile,
                endedAt = Instant.now(),
            )
        }
        assertThat(ex.message).contains("totalTurns")
    }
}

class FormationRuntimePortTest {

    @Test
    fun `start execute end session lifecycle via fake implementation`() {
        val runtime = FakeFormationRuntime()
        val bootstrap = SessionBootstrap(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            byokLlmConfig = ByokLlmConfig(
                provider = LlmProviderKind.OLLAMA_LOCAL,
                model = "gpt-oss:120b-cloud",
                baseUrl = "http://localhost:11437",
            ),
            workspaceRoot = "/workspace",
        )
        val sessionId = runtime.startSession(bootstrap)
        assertThat(sessionId.value).isNotNull()

        val turn = runtime.executeTurn(sessionId, "explain module 1")
        assertThat(turn.response.output).contains("module 1")

        val summary = runtime.endSession(sessionId)
        assertThat(summary.totalTurns).isEqualTo(1)
        assertThat(summary.learnerId).isEqualTo("learner-001")
    }
}

private class FakeFormationRuntime : FormationRuntimePort {
    private var sessionBootstrap: SessionBootstrap? = null
    private var turnCount = 0

    override fun startSession(bootstrap: SessionBootstrap): SessionId {
        sessionBootstrap = bootstrap
        return SessionId("session-fake-001")
    }

    override fun executeTurn(sessionId: SessionId, userPrompt: String): FormationTurn {
        turnCount++
        val prompt = contracts.session.SessionPrompt(prompt = userPrompt)
        val response = contracts.session.SessionResponse(
            sessionId = prompt.sessionId,
            output = "Response to: $userPrompt",
            status = contracts.session.SessionStatus.IN_PROGRESS,
        )
        return FormationTurn(prompt = prompt, response = response)
    }

    override fun endSession(sessionId: SessionId): SessionSummary {
        val bootstrap = sessionBootstrap!!
        return SessionSummary(
            learnerId = bootstrap.learnerId,
            formationId = bootstrap.formationId,
            totalTurns = turnCount,
            finalProfile = LearnerProfile(
                learnerId = bootstrap.learnerId,
                formationId = bootstrap.formationId,
            ),
            endedAt = Instant.now(),
        )
    }
}