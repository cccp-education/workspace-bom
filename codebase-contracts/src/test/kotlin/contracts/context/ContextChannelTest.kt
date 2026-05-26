package contracts.context

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContextChannelTest {

    @Test
    fun `Eager variant has correct type`() {
        val eager = ContextChannel.Eager("hello")
        assertEquals(ChannelType.EAGER, eager.type)
        assertEquals("EAGER", eager.source)
        assertEquals("hello", eager.content)
    }

    @Test
    fun `Rag variant has correct type`() {
        val rag = ContextChannel.Rag("world")
        assertEquals(ChannelType.RAG, rag.type)
        assertEquals("RAG", rag.source)
        assertEquals("CONTEXTE_RAG", rag.sectionHeader)
    }

    @Test
    fun `Graphify variant has correct type`() {
        val graphify = ContextChannel.Graphify("graph content")
        assertEquals(ChannelType.GRAPHIFY, graphify.type)
        assertEquals("Graphify", graphify.source)
    }

    @Test
    fun `Docs variant has correct type`() {
        val docs = ContextChannel.Docs("docs content")
        assertEquals(ChannelType.DOCS, docs.type)
        assertEquals("Docs", docs.source)
        assertEquals("CONTEXTE_DOCS", docs.sectionHeader)
    }

    @Test
    fun `Resource variant has correct type`() {
        val resource = ContextChannel.Resource("res content")
        assertEquals(ChannelType.RESOURCE, resource.type)
        assertEquals("Resource", resource.source)
        assertEquals("RESSOURCES_COLD", resource.sectionHeader)
    }

    @Test
    fun `estimateTokens returns reasonable estimate`() {
        val text = "Hello, world!" // 13 chars → 13/3.5 = 3.71 → toInt() truncates to 3
        val tokens = ContextChannel.estimateTokens(text)
        assertEquals(3, tokens)
    }

    @Test
    fun `truncateToTokens reduces content`() {
        val channel = ContextChannel.Eager("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\n")
        // each line "Line X" = 6 chars → 1 token, budget 4 tokens → 4 lines kept + trailing newline
        val truncated = channel.truncateToTokens(4)
        assertTrue(truncated.content.lines().size <= 5) // 4 lignes + empty trailing = 5
    }

    @Test
    fun `all method returns 5 variants`() {
        val all = ContextChannel.all()
        assertEquals(5, all.size)
    }

    @Test
    fun `withContent creates new instance`() {
        val original = ContextChannel.Eager("old")
        val updated = original.withContent("new")
        assertEquals("new", updated.content)
        assertTrue(updated is ContextChannel.Eager)
    }
}
