package contracts.session

import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionResponseTest {

    @Test
    fun `construct completed response`() {
        val sessionId = UUID.randomUUID()
        val response = SessionResponse(
            sessionId = sessionId,
            output = "BUILD SUCCESSFUL",
            status = SessionStatus.COMPLETED,
        )
        assertEquals(sessionId, response.sessionId)
        assertEquals("BUILD SUCCESSFUL", response.output)
        assertEquals(SessionStatus.COMPLETED, response.status)
        assertTrue(response.toolCalls.isEmpty())
        assertEquals(0, response.tokenUsage.totalTokens)
    }

    @Test
    fun `construct error response with tool calls`() {
        val sessionId = UUID.randomUUID()
        val toolCalls = listOf(
            ToolCallRecord(
                toolName = "exec_gradle",
                args = mapOf("task" to "compileKotlin"),
                result = "BUILD FAILED",
                timestamp = Instant.now(),
            )
        )
        val tokenUsage = TokenUsage(promptTokens = 500, completionTokens = 200, totalTokens = 700)
        val response = SessionResponse(
            sessionId = sessionId,
            output = "Compilation failed: missing import",
            toolCalls = toolCalls,
            tokenUsage = tokenUsage,
            status = SessionStatus.ERROR,
        )
        assertEquals(SessionStatus.ERROR, response.status)
        assertEquals(1, response.toolCalls.size)
        assertEquals("exec_gradle", response.toolCalls[0].toolName)
        assertEquals(700, response.tokenUsage.totalTokens)
    }

    @Test
    fun `construct in progress response`() {
        val response = SessionResponse(
            sessionId = UUID.randomUUID(),
            output = "Executing step 3/5...",
            status = SessionStatus.IN_PROGRESS,
        )
        assertEquals(SessionStatus.IN_PROGRESS, response.status)
    }

    @Test
    fun `construct cancelled response`() {
        val response = SessionResponse(
            sessionId = UUID.randomUUID(),
            output = "Session cancelled by user",
            status = SessionStatus.CANCELLED,
        )
        assertEquals(SessionStatus.CANCELLED, response.status)
    }
}
