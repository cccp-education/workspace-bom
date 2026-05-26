package contracts.llmpool

/**
 * Instance LLM exposée via un endpoint HTTP (typiquement Ollama en Docker).
 *
 * Contrairement aux clés API classiques, une instance Ollama Pro utilise
 * une clé SSH montée en volume Docker, sans apiKey.
 *
 * @param id identifiant unique (ex: "ollama-a", "ollama-b")
 * @param baseUrl URL de l'instance (ex: "http://localhost:11434")
 * @param model modèle à utiliser (ex: "gpt-oss:120b-cloud")
 * @param quota configuration de quota pour cette instance
 */
data class LlmInstance(
    val id: String,
    val baseUrl: String,
    val model: String,
    val quota: QuotaConfig = QuotaConfig()
)
