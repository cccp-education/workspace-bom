package contracts.session

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TokenUsageTest {

    @Test
    fun `construct empty usage`() {
        val usage = TokenUsage()
        assertEquals(0, usage.promptTokens)
        assertEquals(0, usage.completionTokens)
        assertEquals(0, usage.totalTokens)
        assertNull(usage.cost)
    }

    @Test
    fun `construct with token counts`() {
        val usage = TokenUsage(
            promptTokens = 1500,
            completionTokens = 800,
            totalTokens = 2300,
        )
        assertEquals(1500, usage.promptTokens)
        assertEquals(800, usage.completionTokens)
        assertEquals(2300, usage.totalTokens)
        assertNull(usage.cost)
    }

    @Test
    fun `construct with cost`() {
        val usage = TokenUsage(
            promptTokens = 1000,
            completionTokens = 500,
            totalTokens = 1500,
            cost = 0.015,
        )
        assertEquals(1000, usage.promptTokens)
        assertEquals(500, usage.completionTokens)
        assertEquals(1500, usage.totalTokens)
        assertEquals(0.015, usage.cost)
    }
}
