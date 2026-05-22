package cccp.education.contracts.agent.scenarios

import cccp.education.contracts.agent.AgentState
import cccp.education.contracts.agent.ContextAssembler
import cccp.education.contracts.agent.PlanExecutor
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat

class ContextAssemblerPlanExecutorSteps {

    private var assembler: ContextAssembler? = null
    private var executor: PlanExecutor? = null
    private var result: AgentState? = null
    private var workspaceRoot: String = "/tmp"

    @Given("a ContextAssembler with workspace {string}")
    fun givenAssembler(workspace: String) {
        workspaceRoot = workspace
        assembler = object : ContextAssembler {
            override suspend fun assemble(intention: String, workspaceRoot: String): AgentState.ContextReady {
                return AgentState.ContextReady(
                    intention = intention,
                    compositeContext = "assembled from $workspaceRoot for $intention",
                    afnorCorpus = "corpus content"
                )
            }
        }
    }

    @Given("a PlanExecutor")
    fun givenPlanExecutor() {
        executor = object : PlanExecutor {
            override suspend fun execute(state: AgentState.Planned): AgentState.Executed {
                return AgentState.Executed(
                    intention = state.intention,
                    executionResults = mapOf("task1" to "completed")
                )
            }

            override suspend fun evaluate(state: AgentState.Executed): AgentState.Evaluated {
                return AgentState.Evaluated(
                    intention = state.intention,
                    executionResults = state.executionResults,
                    evaluationScore = 0.85,
                    evaluationFeedback = "all tasks completed successfully"
                )
            }

            override suspend fun replan(state: AgentState.Evaluated): AgentState.Evaluated {
                if (state.replanCount >= state.maxReplans) return state
                return state.copy(
                    replanCount = state.replanCount + 1,
                    evaluationFeedback = "replanned: ${state.evaluationFeedback}"
                )
            }
        }
    }

    @When("assembling context for intention {string}")
    fun whenAssemble(intention: String) {
        runBlocking {
            result = assembler!!.assemble(intention, workspaceRoot)
        }
    }

    @Then("the result is an AgentState.ContextReady")
    fun thenResultIsContextReady() {
        assertThat(result).isInstanceOf(AgentState.ContextReady::class.java)
    }

    @Then("the intention matches {string}")
    fun thenIntentionMatches(expected: String) {
        assertThat(result!!.intention).isEqualTo(expected)
    }

    @And("the composite context contains {string}")
    fun thenContextContains(marker: String) {
        val ctx = (result as AgentState.ContextReady).compositeContext
        assertThat(ctx.lowercase()).contains(marker.lowercase())
    }

    @And("a Planned state with intention {string}")
    fun givenPlannedState(intention: String) {
        result = AgentState.Planned(
            intention = intention,
            compositeContext = "context for $intention",
            planJson = """{"plan":"build $intention"}"""
        )
    }

    @When("executing the plan")
    fun whenExecute() {
        runBlocking {
            val planned = result as AgentState.Planned
            result = executor!!.execute(planned)
        }
    }

    @Then("the result is an AgentState.Executed")
    fun thenResultIsExecuted() {
        assertThat(result).isInstanceOf(AgentState.Executed::class.java)
    }

    @And("the execution results contain at least 1 entry")
    fun thenExecutionResultsNotEmpty() {
        val executed = result as AgentState.Executed
        assertThat(executed.executionResults).isNotEmpty
    }

    @And("an Executed state with execution results")
    fun givenExecutedState() {
        result = AgentState.Executed(
            intention = "test intention",
            executionResults = mapOf("task1" to "ok", "task2" to "ok")
        )
    }

    @When("evaluating the execution")
    fun whenEvaluate() {
        runBlocking {
            val executed = result as AgentState.Executed
            result = executor!!.evaluate(executed)
        }
    }

    @Then("the result is an AgentState.Evaluated")
    fun thenResultIsEvaluated() {
        assertThat(result).isInstanceOf(AgentState.Evaluated::class.java)
    }

    @And("the evaluation score is between 0 and 1")
    fun thenScoreBetween0And1() {
        val evaluated = result as AgentState.Evaluated
        assertThat(evaluated.evaluationScore).isBetween(0.0, 1.0)
    }

    @And("the evaluation feedback is not empty")
    fun thenFeedbackNotEmpty() {
        val evaluated = result as AgentState.Evaluated
        assertThat(evaluated.evaluationFeedback).isNotBlank()
    }

    @And("an Evaluated state with evaluation feedback")
    fun givenEvaluatedStateWithFeedback() {
        result = AgentState.Evaluated(
            intention = "test intention",
            executionResults = mapOf("task1" to "ok"),
            evaluationScore = 0.5,
            evaluationFeedback = "needs improvement",
            replanCount = 0,
            maxReplans = 3
        )
    }

    @When("replanning")
    fun whenReplan() {
        runBlocking {
            val evaluated = result as AgentState.Evaluated
            result = executor!!.replan(evaluated)
        }
    }

    @And("the replan count is at least 1")
    fun thenReplanCountAtLeast1() {
        val evaluated = result as AgentState.Evaluated
        assertThat(evaluated.replanCount).isGreaterThanOrEqualTo(1)
    }

    @And("an Evaluated state with replan count {int} and max replans {int}")
    fun givenEvaluatedStateAtMax(count: Int, max: Int) {
        result = AgentState.Evaluated(
            intention = "test intention",
            executionResults = mapOf("task1" to "ok"),
            evaluationScore = 0.3,
            evaluationFeedback = "poor results",
            replanCount = count,
            maxReplans = max
        )
    }

    @And("the replan count does not increase beyond max replans")
    fun thenReplanCountNotBeyondMax() {
        val evaluated = result as AgentState.Evaluated
        assertThat(evaluated.replanCount).isEqualTo(evaluated.maxReplans)
    }
}
