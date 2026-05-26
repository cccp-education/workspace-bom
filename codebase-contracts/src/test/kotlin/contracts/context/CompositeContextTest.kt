package contracts.context

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CompositeContextTest {

    @Test
    fun `toChannels converts all sections`() {
        val config = CompositeContextConfig()
        val ctx = CompositeContext(
            eagerSection = "EAGER content",
            ragSection = "RAG content",
            graphifySection = "GRAPHIFY content",
            docsSection = "DOCS content",
            config = config
        )
        val channels = ctx.toChannels()
        assertEquals(5, channels.size)
        assertEquals("EAGER content", (channels[0] as ContextChannel.Eager).contentOnly)
        assertEquals("RAG content", (channels[1] as ContextChannel.Rag).contentOnly)
        assertEquals("GRAPHIFY content", (channels[2] as ContextChannel.Graphify).contentOnly)
        assertEquals("DOCS content", (channels[3] as ContextChannel.Docs).contentOnly)
        assertEquals("", (channels[4] as ContextChannel.Resource).contentOnly)
    }

    @Test
    fun `channelsWithBudget applies truncation`() {
        val config = CompositeContextConfig(totalTokenBudget = 100)
        val ctx = CompositeContext(
            eagerSection = "A very long eager section that should be truncated because it exceeds the budget",
            ragSection = "RAG section shorter",
            graphifySection = "GRAPHIFY",
            config = config
        )
        val channels = ctx.channelsWithBudget(ChannelBudget(totalTokenBudget = 100))
        assertEquals(5, channels.size)
        // Tous doivent être non-null
        channels.forEach { assertNotNull(it) }
    }
}
