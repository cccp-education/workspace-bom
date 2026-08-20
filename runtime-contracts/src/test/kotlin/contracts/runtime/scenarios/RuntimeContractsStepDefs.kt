package contracts.runtime.scenarios

import contracts.i18n.SupportedLanguage
import contracts.runtime.ByokLlmConfig
import contracts.runtime.FormationRuntimePort
import contracts.runtime.FormationTurn
import contracts.runtime.LearnerProfile
import contracts.runtime.LlmProviderKind
import contracts.runtime.MaterialUpdateResult
import contracts.runtime.MaterialUpdateResolver
import contracts.runtime.SessionBootstrap
import contracts.runtime.SessionId
import contracts.runtime.SessionMemoryContract
import contracts.runtime.SessionSummary
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RuntimeContractsStepDefs {

    private var bootstrapException: IllegalArgumentException? = null
    private var createdBootstrap: SessionBootstrap? = null
    private var createdProfile: LearnerProfile? = null
    private var profileException: IllegalArgumentException? = null
    private var savedProfile: LearnerProfile? = null
    private var loadedProfile: LearnerProfile? = null
    private var sessionSummary: SessionSummary? = null
    private var materialResult: MaterialUpdateResult? = null

    @Given("a formation session environment")
    fun givenFormationSessionEnvironment() {
        // no-op
    }

    @When("a ByokLlmConfig is created with provider {string} and model {string} without baseUrl")
    fun whenByokConfigNoBaseUrl(provider: String, model: String) {
        try {
            ByokLlmConfig(
                provider = LlmProviderKind.valueOf(provider),
                model = model,
            )
        } catch (e: IllegalArgumentException) {
            bootstrapException = e
        }
    }

    @When("a ByokLlmConfig is created with provider {string} and model {string} without apiKeyEnvVar")
    fun whenByokConfigNoApiKey(provider: String, model: String) {
        try {
            ByokLlmConfig(
                provider = LlmProviderKind.valueOf(provider),
                model = model,
            )
        } catch (e: IllegalArgumentException) {
            bootstrapException = e
        }
    }

    @Then("the config creation fails with {string}")
    fun thenConfigCreationFails(message: String) {
        assertTrue(bootstrapException != null)
        assertTrue(bootstrapException!!.message!!.contains(message))
    }

    @When("a SessionBootstrap is created with blank learnerId")
    fun whenBootstrapBlankLearnerId() {
        try {
            SessionBootstrap(
                learnerId = "",
                formationId = "f",
                byokLlmConfig = ByokLlmConfig(
                    provider = LlmProviderKind.OLLAMA_LOCAL,
                    model = "m",
                    baseUrl = "http://localhost:11437",
                ),
                workspaceRoot = "/ws",
            )
        } catch (e: IllegalArgumentException) {
            bootstrapException = e
        }
    }

    @Then("the bootstrap creation fails with {string}")
    fun thenBootstrapCreationFails(message: String) {
        assertTrue(bootstrapException != null)
        assertTrue(bootstrapException!!.message!!.contains(message))
    }

    @When("a SessionBootstrap is created with valid learner {string} and formation {string}")
    fun whenValidBootstrap(learnerId: String, formationId: String) {
        createdBootstrap = SessionBootstrap(
            learnerId = learnerId,
            formationId = formationId,
            byokLlmConfig = ByokLlmConfig(
                provider = LlmProviderKind.OLLAMA_LOCAL,
                model = "gpt-oss:120b-cloud",
                baseUrl = "http://localhost:11437",
            ),
            workspaceRoot = "/workspace",
        )
    }

    @Then("the bootstrap learnerId is {string}")
    fun thenBootstrapLearnerId(learnerId: String) {
        assertEquals(learnerId, createdBootstrap!!.learnerId)
    }

    @Then("the bootstrap formationId is {string}")
    fun thenBootstrapFormationId(formationId: String) {
        assertEquals(formationId, createdBootstrap!!.formationId)
    }

    @Then("the bootstrap locale is {string}")
    fun thenBootstrapLocale(code: String) {
        assertEquals(code, createdBootstrap!!.locale.code)
    }

    @When("a LearnerProfile is created with learnerId {string} and formationId {string}")
    fun whenProfileCreated(learnerId: String, formationId: String) {
        createdProfile = LearnerProfile(learnerId = learnerId, formationId = formationId)
    }

    @Then("the profile progressionPct is {double}")
    fun thenProgressionPct(value: Double) {
        assertEquals(value, createdProfile!!.progressionPct)
    }

    @Then("the profile comprehensionScore is {double}")
    fun thenComprehensionScore(value: Double) {
        assertEquals(value, createdProfile!!.comprehensionScore)
    }

    @Then("the profile completedModules is empty")
    fun thenCompletedModulesEmpty() {
        assertTrue(createdProfile!!.completedModules.isEmpty())
    }

    @Then("the profile weakPoints is empty")
    fun thenWeakPointsEmpty() {
        assertTrue(createdProfile!!.weakPoints.isEmpty())
    }

    @When("a LearnerProfile is created with progressionPct {double}")
    fun whenProfileInvalidProgression(value: Double) {
        try {
            LearnerProfile(learnerId = "l", formationId = "f", progressionPct = value)
        } catch (e: IllegalArgumentException) {
            profileException = e
        }
    }

    @Then("the profile creation fails with {string}")
    fun thenProfileCreationFails(message: String) {
        assertTrue(profileException != null)
        assertTrue(profileException!!.message!!.contains(message))
    }

    @When("a LearnerProfile is saved via SessionMemoryContract")
    fun whenProfileSaved() {
        savedProfile = LearnerProfile(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            completedModules = listOf("mod-1"),
        )
        val memory = object : SessionMemoryContract {
            private val store = mutableMapOf<Pair<String, String>, LearnerProfile>()
            override fun save(profile: LearnerProfile) {
                store[profile.learnerId to profile.formationId] = profile
            }

            override fun load(learnerId: String, formationId: String): LearnerProfile? =
                store[learnerId to formationId]
        }
        memory.save(savedProfile!!)
        loadedProfile = memory.load("learner-001", "formation-fpa")
    }

    @Then("the loaded profile matches the saved profile")
    fun thenLoadedMatchesSaved() {
        assertEquals(savedProfile, loadedProfile)
    }

    @When("a SessionMemoryContract loads a non-existent profile")
    fun whenLoadNonExistent() {
        val memory = object : SessionMemoryContract {
            override fun save(profile: LearnerProfile) {}
            override fun load(learnerId: String, formationId: String): LearnerProfile? = null
        }
        loadedProfile = memory.load("unknown", "unknown")
    }

    @Then("the loaded profile is null")
    fun thenLoadedProfileNull() {
        assertNull(loadedProfile)
    }

    @When("a FormationRuntimePort starts a session for learner {string}")
    fun whenRuntimeStartsSession(learnerId: String) {
        val runtime = FakeRuntime(learnerId)
        val bootstrap = SessionBootstrap(
            learnerId = learnerId,
            formationId = "formation-fpa",
            byokLlmConfig = ByokLlmConfig(
                provider = LlmProviderKind.OLLAMA_LOCAL,
                model = "gpt-oss:120b-cloud",
                baseUrl = "http://localhost:11437",
            ),
            workspaceRoot = "/workspace",
        )
        val sessionId = runtime.startSession(bootstrap)
        runtime.executeTurn(sessionId, "explain module 1")
        sessionSummary = runtime.endSession(sessionId)
    }

    @When("executes a turn with prompt {string}")
    fun whenExecutesTurn(prompt: String) {
        // Turn executed in the when step above
    }

    @When("ends the session")
    fun whenEndsSession() {
        // Session ended in the when step above
    }

    @Then("the session summary has {int} turn")
    fun thenSummaryTurns(count: Int) {
        assertEquals(count, sessionSummary!!.totalTurns)
    }

    @Then("the session summary learnerId is {string}")
    fun thenSummaryLearnerId(learnerId: String) {
        assertEquals(learnerId, sessionSummary!!.learnerId)
    }

    @When("a MaterialUpdateResolver pulls version {string} and latest is {string}")
    fun whenPullVersion(targetVersion: String, latestVersion: String) {
        val resolver = object : MaterialUpdateResolver {
            override fun fetchLatest(remoteUrl: String): String? = latestVersion
            override fun pull(remoteUrl: String, targetVersion: String): MaterialUpdateResult {
                return if (latestVersion == targetVersion) {
                    MaterialUpdateResult.UpToDate
                } else {
                    MaterialUpdateResult.Updated(targetVersion = targetVersion)
                }
            }
        }
        materialResult = resolver.pull("https://github.com/test/repo", targetVersion)
    }

    @Then("the result is UpToDate")
    fun thenResultUpToDate() {
        assertTrue(materialResult is MaterialUpdateResult.UpToDate)
    }

    @Then("the result is Updated with version {string}")
    fun thenResultUpdated(version: String) {
        assertTrue(materialResult is MaterialUpdateResult.Updated)
        assertEquals(version, (materialResult as MaterialUpdateResult.Updated).targetVersion)
    }
}

private class FakeRuntime(private val learnerId: String) : FormationRuntimePort {
    private var turns = 0

    override fun startSession(bootstrap: SessionBootstrap): SessionId {
        return SessionId("session-fake")
    }

    override fun executeTurn(sessionId: SessionId, userPrompt: String): FormationTurn {
        turns++
        val prompt = contracts.session.SessionPrompt(prompt = userPrompt)
        val response = contracts.session.SessionResponse(
            sessionId = prompt.sessionId,
            output = "Response to: $userPrompt",
            status = contracts.session.SessionStatus.IN_PROGRESS,
        )
        return FormationTurn(prompt = prompt, response = response)
    }

    override fun endSession(sessionId: SessionId): SessionSummary {
        return SessionSummary(
            learnerId = learnerId,
            formationId = "formation-fpa",
            totalTurns = turns,
            finalProfile = LearnerProfile(learnerId = learnerId, formationId = "formation-fpa"),
            endedAt = Instant.now(),
        )
    }
}