package contracts.plantuml

/**
 * Request to translate a [PlantUmlBlock] from a source language to a target
 * language.
 *
 * N0 pure value. Introduced by EPIC PLT-DIAGRAM-OWNERSHIP (10th MEMPHIS N0
 * artefact) alongside [PlantUmlTranslationPort].
 *
 * @property block the diagram block to translate
 * @property sourceLanguage ISO 639-1 source language code (e.g. "fr")
 * @property targetLanguage ISO 639-1 target language code (e.g. "en")
 */
data class PlantUmlTranslationRequest(
    val block: PlantUmlBlock,
    val sourceLanguage: String,
    val targetLanguage: String,
)
