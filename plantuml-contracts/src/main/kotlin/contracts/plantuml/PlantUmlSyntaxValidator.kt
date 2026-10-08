package contracts.plantuml

/**
 * Port for PlantUML syntax validation.
 *
 * N0 pure interface (no Gradle, no PlantUML internals). Extracted from
 * `document.translation.validation.PlantUmlSyntaxValidator` by EPIC
 * PLT-DIAGRAM-OWNERSHIP (10th MEMPHIS N0 artefact).
 *
 * The implementation lives in plantuml-gradle (wrapping the native
 * `net.sourceforge.plantuml.SourceStringReader` parser); document-gradle and any
 * other consumer depend on THIS contract, never on the plugin (D2 — ends the
 * N2→N2 coupling).
 */
fun interface PlantUmlSyntaxValidator {
    fun validate(plantumlCode: String): SyntaxValidationResult
}
