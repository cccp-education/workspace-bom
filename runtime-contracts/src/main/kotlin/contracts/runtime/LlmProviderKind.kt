package contracts.runtime

enum class LlmProviderKind {
    OLLAMA_LOCAL,
    OLLAMA_CLOUD,
    GEMINI,
    HUGGINGFACE,
    CUSTOM,
}