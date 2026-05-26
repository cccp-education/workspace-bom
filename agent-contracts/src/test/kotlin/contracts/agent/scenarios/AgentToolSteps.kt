package contracts.agent.scenarios

import contracts.agent.AgentTool
import contracts.agent.GradleTaskTool
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

class AgentToolSteps {

    private var validationResult: Result<Unit>? = null
    private var lastArgs: GradleTaskTool.Args? = null

    @Given("the GradleTaskTool")
    fun givenGradleTaskTool() {
        assertThat(GradleTaskTool.name).isEqualTo("gradle_task")
    }

    @Given("a default Args")
    fun givenDefaultArgs() {
        lastArgs = GradleTaskTool.Args(project = ":training-plugin", task = "generateSPD")
    }

    @Then("the tool name is {string}")
    fun thenToolName(nom: String) {
        assertThat(GradleTaskTool.name).isEqualTo(nom)
    }

    @Then("the tool description is not empty")
    fun thenDescriptionNotEmpty() {
        assertThat(GradleTaskTool.description).isNotBlank()
    }

    @When("validating Args with project {string} and task {string}")
    fun whenValidate(project: String, task: String) {
        lastArgs = GradleTaskTool.Args(project = project, task = task)
        validationResult = GradleTaskTool.validate(lastArgs!!)
    }

    @When("validating Args with timeout {int}")
    fun whenValidateTimeout(timeout: Int) {
        lastArgs = GradleTaskTool.Args(project = ":training-plugin", task = "generateSPD", timeoutSeconds = timeout.toLong())
        validationResult = GradleTaskTool.validate(lastArgs!!)
    }

    @Then("the timeout is {int}s")
    fun thenTimeout(seconds: Int) {
        assertThat(lastArgs?.timeoutSeconds).isEqualTo(seconds.toLong())
    }

    @Then("validation succeeds")
    fun thenValidationSuccess() {
        assertThat(validationResult?.isSuccess).isTrue()
    }

    @Then("validation fails")
    fun thenValidationFailure() {
        assertThat(validationResult?.isFailure).isTrue()
    }

    @Then("the error message contains {string}")
    fun thenErrorMessageContains(texte: String) {
        val message = validationResult?.exceptionOrNull()?.message ?: ""
        assertThat(message.lowercase()).contains(texte.lowercase())
    }

    @Then("GradleTaskTool is an instance of AgentTool")
    fun thenImplementsAgentTool() {
        val tool: AgentTool<*, *> = GradleTaskTool
        assertThat(tool).isNotNull
    }
}
