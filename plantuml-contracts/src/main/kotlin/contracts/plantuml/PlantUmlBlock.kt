package contracts.plantuml

/**
 * A PlantUML diagram block extracted from an AsciiDoc article by the pivot parser.
 *
 * N0 pure value (no Gradle, no PlantUML, no LLM). Extracted from the private
 * `document.translation.plantuml.PlantUmlBlock` by EPIC PLT-DIAGRAM-OWNERSHIP
 * (10th MEMPHIS N0 artefact) — so the model of a diagram block has a single
 * owner: the plantuml borough's contract.
 *
 * The block carries the raw PlantUML source between `@startuml`/`@enduml` and the
 * optional borrowed vocabulary (terms that must be kept verbatim for a target
 * language). It exposes the *extractable* labels without translating them.
 *
 * @property raw the raw PlantUML source of the block
 * @property borrowedVocabulary terms to preserve verbatim (anti-split-brain with
 *   the idiomatic glossary; empty when no borrowing applies)
 */
data class PlantUmlBlock(
    val raw: String,
    val borrowedVocabulary: Set<String> = emptySet(),
) {
    private val labelRegex = Regex("\"([^\"]+)\"")

    private val technicalIdentifierRegex =
        Regex("^[a-zA-Z][a-zA-Z0-9_]*\\.[a-zA-Z0-9_.]+$")

    /**
     * PlantUML *directives* whose text is not quoted, so the `"…"` label regex
     * never catches them: `title`, `header`, `footer`, `caption`. A diagram can
     * be entirely labelled by a quoted class but keep its title untranslated.
     * The directive value is captured up to the end of line.
     */
    private val directiveRegex =
        Regex("""(?m)^\s*(?:title|header|footer|caption)\s+(.+?)\s*$""")

    /**
     * The translatable text of the block: quoted labels (excluding technical
     * identifiers like `Foo.Bar`) plus unquoted directive values. Distinct and
     * in first-seen order.
     */
    fun labels(): List<String> {
        val quoted =
            labelRegex.findAll(raw)
                .map { it.groupValues[1] }
                .filter { label ->
                    !technicalIdentifierRegex.matches(label) &&
                        label.any { it.isLetter() }
                }
        val directives =
            directiveRegex.findAll(raw)
                .map { it.groupValues[1].trim() }
                .filter { it.isNotEmpty() && it.any { ch -> ch.isLetter() } }
        return (quoted + directives).distinct().toList()
    }

    /** Whether the block contains any of the borrowed (preserved) terms. */
    fun hasBorrowedVocabulary(): Boolean =
        borrowedVocabulary.any { vocab -> raw.contains("\"$vocab\"") }

    /** Whether the block has at least one translatable label. */
    fun hasTranslatableLabels(): Boolean = labels().isNotEmpty()
}
