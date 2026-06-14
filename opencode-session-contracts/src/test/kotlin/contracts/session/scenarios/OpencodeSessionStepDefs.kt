package contracts.session.scenarios

import contracts.session.AgentContext
import contracts.session.SessionPrompt
import contracts.session.SessionResponse
import contracts.session.SessionStatus
import contracts.session.TokenUsage
import contracts.session.ToolCallRecord
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OpencodeSessionStepDefs {

    private var prompt: SessionPrompt? = null
    private var response: SessionResponse? = null
    private var tokenUsage: TokenUsage? = null
    private var thrownException: Exception? = null

    @When("a SessionPrompt is created with prompt {string}")
    fun whenPrompt(promptText: String) {
        try {
            prompt = SessionPrompt(prompt = promptText)
        } catch (e: IllegalArgumentException) {
            thrownException = e
        }
    }

    @When("a SessionPrompt is created with prompt {string} and context containing eager rules {string} and RAG chunks {string} and graph relations {string} and backlog items {string}")
    fun whenPromptWithContext(promptText: String, eagerRules: String, ragChunks: String, graphRelations: String, backlogItems: String) {
        val chunks = if (ragChunks.isBlank()) emptyList() else ragChunks.split(",").map { it.trim() }
        val items = if (backlogItems.isBlank()) emptyList() else backlogItems.split(",").map { it.trim() }
        val context = AgentContext(
            eagerRules = eagerRules,
            ragChunks = chunks,
            graphRelations = graphRelations,
            backlogItems = items,
        )
        prompt = SessionPrompt(prompt = promptText, context = context)
    }

    @When("a SessionResponse is created with output {string} and status {word}")
    fun whenResponse(output: String, status: String) {
        response = SessionResponse(
            sessionId = UUID.randomUUID(),
            output = output,
            status = SessionStatus.valueOf(status),
        )
    }

    @When("a SessionResponse is created with output {string} and status {word} and {int} tool call {string} with result {string}")
    fun whenResponseWithToolCalls(output: String, status: String, count: Int, toolName: String, result: String) {
        val toolCalls = (1..count).map {
            ToolCallRecord(
                toolName = toolName,
                args = mapOf("task" to "compileKotlin"),
                result = result,
                timestamp = Instant.now(),
            )
        }
        response = SessionResponse(
            sessionId = UUID.randomUUID(),
            output = output,
            toolCalls = toolCalls,
            status = SessionStatus.valueOf(status),
        )
    }

    @When("a TokenUsage is created with promptTokens {int} and completionTokens {int}")
    fun whenTokenUsage(promptTokens: Int, completionTokens: Int) {
        tokenUsage = TokenUsage(
            promptTokens = promptTokens,
            completionTokens = completionTokens,
            totalTokens = promptTokens + completionTokens,
        )
    }

    @When("a TokenUsage is created with promptTokens {int} and completionTokens {int} and cost {double}")
    fun whenTokenUsageWithCost(promptTokens: Int, completionTokens: Int, cost: Double) {
        tokenUsage = TokenUsage(
            promptTokens = promptTokens,
            completionTokens = completionTokens,
            totalTokens = promptTokens + completionTokens,
            cost = cost,
        )
    }

    @Then("the prompt is {string}")
    fun thenPrompt(expected: String) {
        assertEquals(expected, prompt!!.prompt)
    }

    @Then("the sessionId is a valid UUID")
    fun thenSessionIdValid() {
        assertNotNull(prompt!!.sessionId)
        assertTrue(prompt!!.sessionId.toString().matches(Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
    }

    @Then("maxActions defaults to {int}")
    fun thenMaxActions(expected: Int) {
        assertEquals(expected, prompt!!.maxActions)
    }

    @Then("an IllegalArgumentException is thrown with message containing {string}")
    fun thenExceptionThrown(expectedMessage: String) {
        assertNotNull(thrownException)
        assertTrue(thrownException is IllegalArgumentException)
        assertTrue(thrownException!!.message!!.contains(expectedMessage))
    }

    @Then("the context eagerRules is {string}")
    fun thenContextEagerRules(expected: String) {
        assertEquals(expected, prompt!!.context!!.eagerRules)
    }

    @Then("the context ragChunks contains {int} items")
    fun thenContextRagChunksCount(expected: Int) {
        assertEquals(expected, prompt!!.context!!.ragChunks.size)
    }

    @Then("the context graphRelations is {string}")
    fun thenContextGraphRelations(expected: String) {
        assertEquals(expected, prompt!!.context!!.graphRelations)
    }

    @Then("the context backlogItems contains {int} items")
    fun thenContextBacklogItemsCount(expected: Int) {
        assertEquals(expected, prompt!!.context!!.backlogItems.size)
    }

    @Then("the response output is {string}")
    fun thenResponseOutput(expected: String) {
        assertEquals(expected, response!!.output)
    }

    @Then("the response status is {word}")
    fun thenResponseStatus(expected: String) {
        assertEquals(SessionStatus.valueOf(expected), response!!.status)
    }

    @Then("the response toolCalls is empty")
    fun thenResponseToolCallsEmpty() {
        assertTrue(response!!.toolCalls.isEmpty())
    }

    @Then("the response toolCalls contains {int} record")
    fun thenResponseToolCallsCount(expected: Int) {
        assertEquals(expected, response!!.toolCalls.size)
    }

    @Then("the first tool call name is {string}")
    fun thenFirstToolCallName(expected: String) {
        assertEquals(expected, response!!.toolCalls.first().toolName)
    }

    @Then("the first tool call result is {string}")
    fun thenFirstToolCallResult(expected: String) {
        assertEquals(expected, response!!.toolCalls.first().result)
    }

    @Then("the totalTokens is {int}")
    fun thenTotalTokens(expected: Int) {
        assertEquals(expected, tokenUsage!!.totalTokens)
    }

    @Then("the cost is absent")
    fun thenCostAbsent() {
        assertNull(tokenUsage!!.cost)
    }

    @Then("the cost is {double}")
    fun thenCost(expected: Double) {
        assertEquals(expected, tokenUsage!!.cost)
    }
}
