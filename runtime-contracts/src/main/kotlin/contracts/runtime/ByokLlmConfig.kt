package contracts.runtime

data class ByokLlmConfig(
    val provider: LlmProviderKind,
    val model: String,
    val baseUrl: String? = null,
    val apiKeyEnvVar: String? = null,
    val poolPorts: IntRange = 11437..11465,
) {
    init {
        require(model.isNotBlank()) { "model must be non-blank" }
        when (provider) {
            LlmProviderKind.OLLAMA_LOCAL -> {
                require(!baseUrl.isNullOrBlank()) {
                    "baseUrl is required for OLLAMA_LOCAL provider"
                }
            }
            LlmProviderKind.OLLAMA_CLOUD -> {
                // poolPorts defaults to 11437..11465 — no baseUrl, no apiKey required
            }
            LlmProviderKind.GEMINI,
            LlmProviderKind.HUGGINGFACE,
            LlmProviderKind.CUSTOM -> {
                require(!apiKeyEnvVar.isNullOrBlank()) {
                    "apiKeyEnvVar is required for $provider provider"
                }
            }
        }
    }
}