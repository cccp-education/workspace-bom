package contracts.runtime

import contracts.i18n.SupportedLanguage

data class SessionBootstrap(
    val learnerId: String,
    val formationId: String,
    val byokLlmConfig: ByokLlmConfig,
    val workspaceRoot: String,
    val locale: SupportedLanguage = SupportedLanguage(
        code = "en",
        name = "English",
        nativeName = "English",
    ),
) {
    init {
        require(learnerId.isNotBlank()) { "learnerId must be non-blank" }
        require(formationId.isNotBlank()) { "formationId must be non-blank" }
        require(workspaceRoot.isNotBlank()) { "workspaceRoot must be non-blank" }
    }
}