package contracts.context

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ChannelBudgetTest {

    @Test
    fun `default budget sums to 1`() {
        val budget = ChannelBudget()
        assertEquals(8000, budget.totalTokenBudget)
    }

    @Test
    fun `tokensFor maps channel type correctly`() {
        val budget = ChannelBudget()
        assertEquals(3200, budget.tokensFor(ChannelType.EAGER))   // 8000 * 0.40
        assertEquals(2400, budget.tokensFor(ChannelType.RAG))     // 8000 * 0.30
        assertEquals(1600, budget.tokensFor(ChannelType.GRAPHIFY))// 8000 * 0.20
        assertEquals(800, budget.tokensFor(ChannelType.DOCS))     // 8000 * 0.10
        assertEquals(0, budget.tokensFor(ChannelType.RESOURCE))   // 8000 * 0.0
    }

    @Test
    fun `invalid budget throws`() {
        assertFailsWith<IllegalArgumentException> {
            ChannelBudget(budgetEager = 0.5, budgetRag = 0.5, budgetGraphify = 0.5, budgetDocs = 0.5, budgetResource = 0.5)
        }
    }

    @Test
    fun `fromConfig delegates correctly`() {
        val config = CompositeContextConfig(
            totalTokenBudget = 4000,
            budgetEagerLazy = 0.5,
            budgetRag = 0.25,
            budgetGraphify = 0.15,
            budgetDocs = 0.05,
            budgetOverhead = 0.05
        )
        val budget = ChannelBudget.fromConfig(config)
        assertEquals(4000, budget.totalTokenBudget)
        assertEquals(2000, budget.tokensFor(ChannelType.EAGER))
        assertEquals(1000, budget.tokensFor(ChannelType.RAG))
    }

    @Test
    fun `applyBudget truncates channels`() {
        val budget = ChannelBudget(totalTokenBudget = 10) // très petit budget
        val channels = listOf(
            ContextChannel.Eager("A\nB\nC\nD\nE\nF\nG\n"), // ~14 tokens
            ContextChannel.Rag("1\n2\n3\n4\n5\n")
        )
        val result = budget.applyBudget(channels)
        assertEquals(2, result.size)
        // EAGER with 4 tokens budget (10*0.4=4), RAG with 3 tokens (10*0.3=3)
    }
}
