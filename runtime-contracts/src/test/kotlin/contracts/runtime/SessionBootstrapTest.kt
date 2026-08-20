package contracts.runtime

import contracts.i18n.SupportedLanguage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ByokLlmConfigTest {

    @Test
    fun `ollama local requires baseUrl`() {
        val ex = assertThrows<IllegalArgumentException> {
            ByokLlmConfig(
                provider = LlmProviderKind.OLLAMA_LOCAL,
                model = "gpt-oss:120b-cloud",
            )
        }
        assertThat(ex.message).contains("baseUrl")
    }

    @Test
    fun `ollama local does not require apiKeyEnvVar`() {
        val config = ByokLlmConfig(
            provider = LlmProviderKind.OLLAMA_LOCAL,
            model = "gpt-oss:120b-cloud",
            baseUrl = "http://localhost:11437",
        )
        assertThat(config.provider).isEqualTo(LlmProviderKind.OLLAMA_LOCAL)
        assertThat(config.apiKeyEnvVar).isNull()
    }

    @Test
    fun `ollama cloud poolPorts defaults to 11437-11465`() {
        val config = ByokLlmConfig(
            provider = LlmProviderKind.OLLAMA_CLOUD,
            model = "gpt-oss:120b-cloud",
        )
        assertThat(config.poolPorts).isEqualTo(11437..11465)
    }

    @Test
    fun `gemini requires apiKeyEnvVar`() {
        val ex = assertThrows<IllegalArgumentException> {
            ByokLlmConfig(
                provider = LlmProviderKind.GEMINI,
                model = "gemini-pro",
            )
        }
        assertThat(ex.message).contains("apiKeyEnvVar")
    }

    @Test
    fun `huggingface requires apiKeyEnvVar`() {
        val config = ByokLlmConfig(
            provider = LlmProviderKind.HUGGINGFACE,
            model = "mistral-7b",
            apiKeyEnvVar = "HF_TOKEN",
        )
        assertThat(config.apiKeyEnvVar).isEqualTo("HF_TOKEN")
    }

    @Test
    fun `custom requires apiKeyEnvVar`() {
        val ex = assertThrows<IllegalArgumentException> {
            ByokLlmConfig(
                provider = LlmProviderKind.CUSTOM,
                model = "custom-model",
            )
        }
        assertThat(ex.message).contains("apiKeyEnvVar")
    }

    @Test
    fun `model must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            ByokLlmConfig(
                provider = LlmProviderKind.OLLAMA_LOCAL,
                model = "",
                baseUrl = "http://localhost:11437",
            )
        }
        assertThat(ex.message).contains("model")
    }

    @Test
    fun `ollama cloud does not require baseUrl`() {
        val config = ByokLlmConfig(
            provider = LlmProviderKind.OLLAMA_CLOUD,
            model = "gpt-oss:120b-cloud",
        )
        assertThat(config.baseUrl).isNull()
    }
}

class SessionBootstrapTest {

    private val byokConfig = ByokLlmConfig(
        provider = LlmProviderKind.OLLAMA_LOCAL,
        model = "gpt-oss:120b-cloud",
        baseUrl = "http://localhost:11437",
    )

    private val french = SupportedLanguage(
        code = "fr",
        name = "French",
        nativeName = "Français",
    )

    @Test
    fun `creates session bootstrap with all fields`() {
        val bootstrap = SessionBootstrap(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            byokLlmConfig = byokConfig,
            workspaceRoot = "/home/learner/workspace",
            locale = french,
        )
        assertThat(bootstrap.learnerId).isEqualTo("learner-001")
        assertThat(bootstrap.formationId).isEqualTo("formation-fpa")
        assertThat(bootstrap.byokLlmConfig).isEqualTo(byokConfig)
        assertThat(bootstrap.workspaceRoot).isEqualTo("/home/learner/workspace")
        assertThat(bootstrap.locale).isEqualTo(french)
    }

    @Test
    fun `learnerId must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            SessionBootstrap(
                learnerId = "",
                formationId = "formation-fpa",
                byokLlmConfig = byokConfig,
                workspaceRoot = "/home/learner/workspace",
                locale = french,
            )
        }
        assertThat(ex.message).contains("learnerId")
    }

    @Test
    fun `formationId must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            SessionBootstrap(
                learnerId = "learner-001",
                formationId = "",
                byokLlmConfig = byokConfig,
                workspaceRoot = "/home/learner/workspace",
                locale = french,
            )
        }
        assertThat(ex.message).contains("formationId")
    }

    @Test
    fun `workspaceRoot must be non-blank`() {
        val ex = assertThrows<IllegalArgumentException> {
            SessionBootstrap(
                learnerId = "learner-001",
                formationId = "formation-fpa",
                byokLlmConfig = byokConfig,
                workspaceRoot = "",
                locale = french,
            )
        }
        assertThat(ex.message).contains("workspaceRoot")
    }

    @Test
    fun `locale defaults to English when not specified`() {
        val bootstrap = SessionBootstrap(
            learnerId = "learner-001",
            formationId = "formation-fpa",
            byokLlmConfig = byokConfig,
            workspaceRoot = "/home/learner/workspace",
        )
        assertThat(bootstrap.locale.code).isEqualTo("en")
    }
}