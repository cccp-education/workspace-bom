package contracts.plantuml

/**
 * Pure classifier deciding the [PlantUmlStrategy] of a [PlantUmlBlock].
 *
 * N0 pure logic (no Gradle, no PlantUML, no LLM). Extracted from the private
 * `document.translation.plantuml.PlantUmlClassifier` by EPIC
 * PLT-DIAGRAM-OWNERSHIP — the classification of a diagram block is a diagram
 * concern, not a document concern (D1).
 *
 * Order matters: a block carrying borrowed vocabulary is [PlantUmlStrategy.BORROW];
 * otherwise a block with translatable labels is [PlantUmlStrategy.TRANSLATE];
 * otherwise it is [PlantUmlStrategy.PRESERVE] (semantic identity only).
 */
class PlantUmlClassifier {
    fun classify(block: PlantUmlBlock): PlantUmlStrategy =
        when {
            block.hasBorrowedVocabulary() -> PlantUmlStrategy.BORROW
            block.hasTranslatableLabels() -> PlantUmlStrategy.TRANSLATE
            else -> PlantUmlStrategy.PRESERVE
        }
}
