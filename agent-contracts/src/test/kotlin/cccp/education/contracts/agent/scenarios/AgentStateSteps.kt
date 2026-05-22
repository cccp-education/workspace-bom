package cccp.education.contracts.agent.scenarios

import cccp.education.contracts.agent.AgentPhase
import cccp.education.contracts.agent.AgentState
import cccp.education.contracts.agent.Epic
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

class AgentStateSteps {

    private var intention: String = ""
    private var currentState: AgentState? = null
    private var classified: AgentState.Classified? = null
    private var planned: AgentState.Planned? = null
    private var executed: AgentState.Executed? = null
    private var evaluated: AgentState.Evaluated? = null
    private var finalized: AgentState.Finalized? = null

    @Given("an intention {string}")
    fun givenIntention(texte: String) {
        intention = texte
    }

    @Given("a Classified state with classification {string} and model {string}")
    fun givenClassified(classification: String, modele: String) {
        classified = AgentState.Classified(
            intention = "Formation Java 17",
            compositeContext = "ctx",
            classification = classification,
            modelChoice = modele
        )
    }

    @Given("a Planned state with {int} EPICs")
    fun givenPlanned(nbEpics: Int) {
        val epics = (1..nbEpics).map { i ->
            Epic(name = "EPIC-$i", description = "Desc", points = i * 3)
        }
        planned = AgentState.Planned(
            intention = "Formation Java 17",
            planJson = "{}",
            epics = epics
        )
    }

    @Given("an Executed state with result {string}")
    fun givenExecuted(resultat: String) {
        val separator = ": "
        val key = resultat.substringBefore(separator)
        val value = resultat.substringAfter(separator)
        executed = AgentState.Executed(
            intention = "Formation Java 17",
            executionResults = mapOf(key to value)
        )
    }

    @Given("an Evaluated state with score {double}")
    fun givenEvaluated(score: Double) {
        evaluated = AgentState.Evaluated(
            intention = "Formation Java 17",
            evaluationScore = score,
            evaluationFeedback = if (score >= 7.0) "OK" else "Revision needed"
        )
    }

    @Given("a Finalized state with output {string}")
    fun givenFinalizedOk(sortie: String) {
        finalized = AgentState.Finalized(
            intention = "Formation Java 17",
            finalOutput = sortie,
            error = null
        )
    }

    @Given("a Finalized state with error {string}")
    fun givenFinalizedError(erreur: String) {
        finalized = AgentState.Finalized(
            intention = "Formation Java 17",
            finalOutput = "",
            error = erreur
        )
    }

    @When("an AgentState.Initial is created")
    fun whenInitial() {
        currentState = AgentState.Initial(intention = intention)
    }

    @When("an AgentState.ContextReady is created")
    fun whenContextReady() {
        currentState = AgentState.ContextReady(
            intention = intention,
            compositeContext = "composite context assembled from EAGER, RAG, Graphify",
            afnorCorpus = "AFNOR/REAC corpus"
        )
    }

    @Then("the state phase is {word}")
    fun thenPhase(phase: String) {
        val expected = AgentPhase.valueOf(phase)
        val actual = stateUnderTest().phase
        assertThat(actual).isEqualTo(expected)
    }

    @Then("the intention is {string}")
    fun thenIntention(texte: String) {
        assertThat(stateUnderTest().intention).isEqualTo(texte)
    }

    @Then("the state has no error")
    fun thenNoError() {
        assertThat(stateUnderTest().error).isNull()
    }

    @Then("the state is not terminal")
    fun thenNotTerminal() {
        assertThat(stateUnderTest().isTerminal).isFalse()
    }

    @Then("the state is terminal")
    fun thenTerminal() {
        assertThat(finalized!!.isTerminal).isTrue()
    }

    @Then("the state is successful")
    fun thenSuccessful() {
        assertThat(finalized!!.isSuccessful).isTrue()
    }

    @Then("the state is not successful")
    fun thenNotSuccessful() {
        assertThat(finalized!!.isSuccessful).isFalse()
    }

    @Then("the classification is {string}")
    fun thenClassification(texte: String) {
        assertThat(classified!!.classification).isEqualTo(texte)
    }

    @Then("the model choice is {string}")
    fun thenModelChoice(texte: String) {
        assertThat(classified!!.modelChoice).isEqualTo(texte)
    }

    @Then("the plan contains {int} EPICs")
    fun thenEpicCount(nb: Int) {
        assertThat(planned!!.epics).hasSize(nb)
    }

    @Then("the result for {string} is {string}")
    fun thenResultat(borough: String, valeur: String) {
        assertThat(executed!!.executionResults).containsEntry(borough, valeur)
    }

    @Then("the evaluation score is {double}")
    fun thenScore(score: Double) {
        assertThat(evaluated!!.evaluationScore).isEqualTo(score)
    }

    private fun stateUnderTest(): AgentState {
        return currentState ?: classified ?: planned ?: executed ?: evaluated ?: finalized
            ?: throw IllegalStateException("No state has been created")
    }
}
