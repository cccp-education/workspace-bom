package contracts.session

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionPromptTest {

    @Test
    fun `construct with minimal fields`() {
        val prompt = SessionPrompt(prompt = "Generate a README")
        assertEquals("Generate a README", prompt.prompt)
        assertNotNull(prompt.sessionId)
        assertEquals(10, prompt.maxActions)
        assertNull(prompt.model)
        assertNull(prompt.context)
    }

    @Test
    fun `construct with all fields`() {
        val sessionId = UUID.randomUUID()
        val context = AgentContext(eagerRules = "RULES")
        val prompt = SessionPrompt(
            sessionId = sessionId,
            prompt = "Fix compilation error",
            context = context,
            maxActions = 5,
            model = "deepseek-v4-pro",
        )
        assertEquals(sessionId, prompt.sessionId)
        assertEquals("Fix compilation error", prompt.prompt)
        assertEquals(context, prompt.context)
        assertEquals(5, prompt.maxActions)
        assertEquals("deepseek-v4-pro", prompt.model)
    }

    @Test
    fun `blank prompt throws`() {
        assertThrows<IllegalArgumentException> {
            SessionPrompt(prompt = "   ")
        }
    }

    @Test
    fun `empty prompt throws`() {
        assertThrows<IllegalArgumentException> {
            SessionPrompt(prompt = "")
        }
    }

    @Test
    fun `zero maxActions throws`() {
        assertThrows<IllegalArgumentException> {
            SessionPrompt(prompt = "test", maxActions = 0)
        }
    }

    @Test
    fun `negative maxActions throws`() {
        assertThrows<IllegalArgumentException> {
            SessionPrompt(prompt = "test", maxActions = -1)
        }
    }

    @Test
    fun `default sessionId is random UUID`() {
        val p1 = SessionPrompt(prompt = "a")
        val p2 = SessionPrompt(prompt = "b")
        assertTrue(p1.sessionId != p2.sessionId)
    }
}
