package contracts.plantuml

/**
 * Port for translating a PlantUML diagram block.
 *
 * N0 pure interface (no Gradle, no PlantUML internals). Introduced by EPIC
 * PLT-DIAGRAM-OWNERSHIP (10th MEMPHIS N0 artefact) — the single entry point
 * through which document-gradle (and any consumer) delegates diagram
 * translation to the plantuml borough (D1/D3).
 *
 * The implementation (plantuml-gradle) reuses the rich `plantuml.boundary`
 * domain ([TranslationResolver] + [TextClassifier] + idiomatic glossary) and
 * guarantees that a returned block never breaks PlantUML syntax (escapes such
 * as `\n` are preserved; D5).
 */
interface PlantUmlTranslationPort {
    /**
     * Translates the labels of the request's block.
     *
     * @param request the block + source/target languages
     * @return the translation outcome; on [PlantUmlTranslationOutcome.Preserved]
     *   the block was kept verbatim (strategy PRESERVE, or no translation
     *   available)
     */
    fun translate(request: PlantUmlTranslationRequest): PlantUmlTranslationOutcome
}
