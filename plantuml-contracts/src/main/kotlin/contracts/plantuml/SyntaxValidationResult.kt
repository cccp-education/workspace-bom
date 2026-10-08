package contracts.plantuml

/**
 * Result of PlantUML syntax validation.
 *
 * N0 pure sealed class (no Gradle, no PlantUML internals). Extracted from
 * `plantuml.validation.SyntaxValidationResult` by EPIC PLT-DIAGRAM-OWNERSHIP
 * (10th MEMPHIS N0 artefact) — the plantuml BACKLOG anticipated this exact
 * extraction ("un contrat N0 plantuml-validation-contracts pourra être extrait").
 *
 * Two possible outcomes:
 * - [Valid]: syntax is correct, the diagram can be rendered
 * - [Invalid]: syntax errors detected with detailed error information
 *
 * @see PlantUmlSyntaxValidator
 */
sealed class SyntaxValidationResult {
    /**
     * Indicates valid PlantUML syntax with no errors.
     * The diagram can be safely rendered to PNG/SVG.
     */
    data object Valid : SyntaxValidationResult()

    /**
     * Indicates invalid PlantUML syntax with error details.
     *
     * @property errorMessage human-readable description of the syntax error
     * @property stackTrace full stack trace for debugging
     */
    data class Invalid(
        val errorMessage: String,
        val stackTrace: String,
    ) : SyntaxValidationResult()
}
