package contracts.agent.scenarios

import contracts.agent.ChannelScore
import contracts.agent.RelevanceBudget
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

class RelevanceBudgetSteps {

    private var channels: Map<String, String> = emptyMap()
    private var budget: RelevanceBudget? = null

    @Given("an empty channel map")
    fun givenEmptyChannels() {
        channels = emptyMap()
    }

    @Given("channel {string} with content {string}")
    fun givenChannel(name: String, content: String) {
        channels = channels + (name to content)
    }

    @Given("a budget with 2 channels")
    fun givenBudgetWithTwoChannels() {
        val channelScores = listOf(
            ChannelScore("RAG", "relevant content here", 0.85),
            ChannelScore("EAGER", "governance rules", 0.30)
        )
        budget = RelevanceBudget(totalTokens = 1000, channels = channelScores)
    }

    @When("computing budget for {string}")
    fun whenCompute(intention: String) {
        budget = RelevanceBudget.compute(intention = intention, channels = channels)
    }

    @When("assembling output")
    fun whenAssemble() {
        // budget already set, assemble() is called in then
    }

    @Then("the budget has {int} channel")
    @Then("the budget has {int} channels")
    fun thenChannelCount(nb: Int) {
        assertThat(budget?.channels).hasSize(nb)
    }

    @Then("the channel {string} has a positive score")
    fun thenPositiveScore(name: String) {
        val score = budget?.channels?.find { it.name == name }?.score ?: 0.0
        assertThat(score).isGreaterThan(0.0)
    }

    @Then("the assembled output contains {string}")
    fun thenOutputContains(text: String) {
        val output = budget?.assemble() ?: ""
        assertThat(output).contains(text)
    }

    @Then("the token allocation contains {string}")
    fun thenTokenAllocationContains(name: String) {
        val alloc = budget?.tokenAllocation() ?: emptyMap()
        assertThat(alloc).containsKey(name)
    }
}
