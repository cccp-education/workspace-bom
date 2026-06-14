package contracts.session

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AgentContextTest {

    @Test
    fun `construct empty context`() {
        val ctx = AgentContext()
        assertEquals("", ctx.eagerRules)
        assertTrue(ctx.ragChunks.isEmpty())
        assertEquals("", ctx.graphRelations)
        assertTrue(ctx.backlogItems.isEmpty())
    }

    @Test
    fun `construct full context`() {
        val ctx = AgentContext(
            eagerRules = "NE JAMAIS commit sans permission",
            ragChunks = listOf("chunk1", "chunk2"),
            graphRelations = "codebase → runner",
            backlogItems = listOf("EPIC X-4", "EPIC Y-3"),
        )
        assertEquals("NE JAMAIS commit sans permission", ctx.eagerRules)
        assertEquals(2, ctx.ragChunks.size)
        assertEquals("chunk1", ctx.ragChunks[0])
        assertEquals("codebase → runner", ctx.graphRelations)
        assertEquals(2, ctx.backlogItems.size)
    }

    @Test
    fun `partial context with only eager rules`() {
        val ctx = AgentContext(eagerRules = "RÈGLES_EAGER")
        assertEquals("RÈGLES_EAGER", ctx.eagerRules)
        assertTrue(ctx.ragChunks.isEmpty())
        assertEquals("", ctx.graphRelations)
        assertTrue(ctx.backlogItems.isEmpty())
    }
}
