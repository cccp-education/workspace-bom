package contracts.plantuml

/**
 * Block-level translation strategy for a PlantUML diagram block.
 *
 * N0 pure enum (no Gradle, no PlantUML, no LLM). Extracted from the divergent
 * `document.translation.plantuml.PlantUmlStrategy` and unified with
 * `plantuml.boundary.TranslationStrategy` by EPIC PLT-DIAGRAM-OWNERSHIP
 * (10th MEMPHIS N0 artefact).
 *
 * There is ONE vocabulary for the translation decision (D4): documents,
 * plantuml and any consumer share these three values — never a second enum.
 *
 * - [TRANSLATE]: translate the presentation labels (Nature 1 / idiomatic Nature 3)
 * - [BORROW]: keep the source term as-is (loanword consecrated by usage)
 * - [PRESERVE]: never translate (semantic identity — identifiers, formulas)
 *
 * @see PlantUmlBlock
 */
enum class PlantUmlStrategy {
    TRANSLATE,
    BORROW,
    PRESERVE,
}
