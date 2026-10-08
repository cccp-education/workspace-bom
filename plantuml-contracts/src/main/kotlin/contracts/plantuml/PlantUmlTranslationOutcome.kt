package contracts.plantuml

/**
 * Outcome of a [PlantUmlTranslationPort.translate] call.
 *
 * N0 pure sealed class. Introduced by EPIC PLT-DIAGRAM-OWNERSHIP (10th MEMPHIS
 * N0 artefact).
 *
 * A translation either [Translated] the labels (a new block is returned) or
 * [Preserved] the block verbatim (strategy PRESERVE, or no translation available
 * — the caller then keeps the source block). There is never a silent French
 * fallback marked as translated (the DOC-TRANSLATE-RESILIENCE lesson).
 */
sealed class PlantUmlTranslationOutcome {
    /**
     * The labels were translated — the returned [block] carries the new labels.
     */
    data class Translated(val block: PlantUmlBlock) : PlantUmlTranslationOutcome()

    /**
     * The block was kept verbatim — the caller keeps the source block unchanged.
     *
     * @property reason human-readable reason (preserved strategy, unknown
     *   language, empty labels…)
     */
    data class Preserved(val reason: String) : PlantUmlTranslationOutcome()
}
