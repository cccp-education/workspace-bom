package contracts.runtime

import contracts.session.SessionPrompt
import contracts.session.SessionResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FormationTurnTest {

    @Test
    fun `creates formation turn with prompt and response`() {
        val prompt = SessionPrompt(prompt = "explain module 1")
        val response = SessionResponse(
            sessionId = prompt.sessionId,
            output = "Module 1 covers...",
            status = contracts.session.SessionStatus.COMPLETED,
        )
        val turn = FormationTurn(
            prompt = prompt,
            response = response,
        )
        assertThat(turn.prompt).isEqualTo(prompt)
        assertThat(turn.response).isEqualTo(response)
        assertThat(turn.timestamp).isNotNull()
    }

    @Test
    fun `creates formation turn with explicit timestamp`() {
        val prompt = SessionPrompt(prompt = "test")
        val response = SessionResponse(
            sessionId = prompt.sessionId,
            output = "out",
            status = contracts.session.SessionStatus.COMPLETED,
        )
        val ts = java.time.Instant.parse("2026-08-21T10:00:00Z")
        val turn = FormationTurn(prompt = prompt, response = response, timestamp = ts)
        assertThat(turn.timestamp).isEqualTo(ts)
    }

    @Test
    fun `formation turn preserves tool calls from response`() {
        val prompt = SessionPrompt(prompt = "run tool")
        val toolCall = contracts.session.ToolCallRecord(toolName = "exec_shell")
        val response = SessionResponse(
            sessionId = prompt.sessionId,
            output = "done",
            toolCalls = listOf(toolCall),
            status = contracts.session.SessionStatus.COMPLETED,
        )
        val turn = FormationTurn(prompt = prompt, response = response)
        assertThat(turn.response.toolCalls).hasSize(1)
        assertThat(turn.response.toolCalls[0].toolName).isEqualTo("exec_shell")
    }
}

class InteractionProtocolTest {

    @Test
    fun `nextTurn returns next turn when session continues`() {
        val protocol = FakeInteractionProtocol(shouldEnd = false)
        val currentTurn = makeTurn("turn 1")
        val next = protocol.nextTurn(currentTurn)
        assertThat(next).isNotNull
        assertThat(next!!.response.output).isEqualTo("turn 2")
    }

    @Test
    fun `nextTurn returns null when session ends`() {
        val protocol = FakeInteractionProtocol(shouldEnd = true)
        val currentTurn = makeTurn("turn 1")
        val next = protocol.nextTurn(currentTurn)
        assertThat(next).isNull()
    }

    private fun makeTurn(output: String): FormationTurn {
        val prompt = SessionPrompt(prompt = "prompt")
        val response = SessionResponse(
            sessionId = prompt.sessionId,
            output = output,
            status = contracts.session.SessionStatus.COMPLETED,
        )
        return FormationTurn(prompt = prompt, response = response)
    }
}

private class FakeInteractionProtocol(private val shouldEnd: Boolean) : InteractionProtocol {
    private var count = 0

    override fun nextTurn(current: FormationTurn): FormationTurn? {
        if (shouldEnd) return null
        count++
        val prompt = SessionPrompt(prompt = "prompt $count")
        val response = SessionResponse(
            sessionId = prompt.sessionId,
            output = "turn ${count + 1}",
            status = contracts.session.SessionStatus.COMPLETED,
        )
        return FormationTurn(prompt = prompt, response = response)
    }
}