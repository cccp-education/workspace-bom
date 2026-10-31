package contracts.i18n

/**
 * MEM-I18N-PRESERVE (BKY-OUT-4) — **what a translation must never touch**.
 *
 * The review S-263 measured the same policy re-implemented on every content
 * producer: `document` classifies a whole block before translating it
 * (`TextTranslatableClassifier`), `plantuml` shields its terms
 * (`PlantumlTermShield`). Two independent implementations of the same rule is the
 * factorisation threshold of the doctrine (S-195), so the rule becomes a pure N0
 * contract that both consume.
 *
 * A rule is a **measured** syntax marker of AsciiDoc, never a heuristic: a
 * translated macro breaks the document (`toc::[]` → `tik::[]` in 142 published
 * files), a translated fence breaks the block, a translated anchor breaks every
 * cross-reference.
 *
 * Pure value object: no I/O, no Gradle, no LLM.
 */
enum class PreserveRule(
    val id: String,
    private val pattern: Regex,
) {
    /** `toc::[]`, `image::x.png[]`, `include::file.adoc[]`, `xref:...[]`. */
    ASIDOC_MACRO("asciidoc-macro", Regex("""\b[a-zA-Z]+:{1,2}[^\s\[\]\n]*\[[^\]\n]*]""")),

    /** A block attribute line: `[cols="1,1",options="header"]`. */
    BLOCK_ATTRIBUTE("block-attribute", Regex("""(?m)^\[[^\]\n]+][ \t]*$""")),

    /** A delimited block fence: `----`, `....`, `++++`, `|===`. */
    FENCE("fence", Regex("""(?m)^(?:-{4,}|\.{4,}|\+{4,}|\|={3,})[ \t]*$""")),

    /** An inline code span. */
    INLINE_CODE("inline-code", Regex("""`[^`\n]+`""")),

    /** An anchor: `[[id]]`, `[#id]`. */
    ANCHOR("anchor", Regex("""\[\[[^\]\n]+]]""")),

    /** An attribute reference: `{site_host}`, `{docdir}`. */
    ATTRIBUTE_REFERENCE("attribute-reference", Regex("""\{[a-zA-Z0-9_-]+}""")),

    /** A URL (its path and query are never prose). */
    URL("url", Regex("""https?://[^\s\[\]<>"]+""")),
    ;

    /** Every occurrence of this rule's marker in [text], in reading order. */
    fun findAll(text: String): List<String> = pattern.findAll(text).map { it.value }.toList()

    fun matches(text: String): Boolean = pattern.containsMatchIn(text)

    /** The ranges of every occurrence, parallel to [findAll]. */
    fun findRanges(text: String): List<IntRange> = pattern.findAll(text).map { it.range }.toList()

    companion object {
        fun of(id: String): PreserveRule? = entries.firstOrNull { it.id == id }

        fun ids(): List<String> = entries.map { it.id }
    }
}

/** A fragment protected by [PreserveCatalog.mask], with the rule that matched it. */
data class PreservedFragment(
    val placeholder: String,
    val original: String,
    val ruleId: String,
)

/** The masked text and the fragments to restore after translation. */
data class PreservedText(
    val masked: String,
    val fragments: List<PreservedFragment>,
) {
    val isEmpty: Boolean get() = fragments.isEmpty()
}

/**
 * Deterministic masking: every preserved marker becomes a numbered placeholder
 * (`@@PRESERVE_0@@`), the **longest** match wins when two rules overlap, and
 * [restore] puts the originals back. The placeholder is opaque to a model by
 * design — a translated `toc::[]` cannot come back.
 */
object PreserveCatalog {

    const val DEFAULT_PREFIX: String = "@@PRESERVE_"
    const val SUFFIX: String = "@@"

    fun mask(
        text: String,
        prefix: String = DEFAULT_PREFIX,
    ): PreservedText {
        if (text.isEmpty()) return PreservedText(text, emptyList())

        data class Candidate(val range: IntRange, val rule: PreserveRule, val value: String)

        val candidates =
            PreserveRule.entries
                .flatMap { rule ->
                    rule.findAll(text).let { values ->
                        rule.findRanges(text).zip(values).map { (range, value) -> Candidate(range, rule, value) }
                    }
                }.sortedWith(compareBy({ it.range.first }, { -(it.range.last - it.range.first) }))

        val selected = mutableListOf<Candidate>()
        var cursor = 0
        candidates.forEach { candidate ->
            if (candidate.range.first >= cursor) {
                selected += candidate
                cursor = candidate.range.last + 1
            }
        }
        if (selected.isEmpty()) return PreservedText(text, emptyList())

        val masked = StringBuilder(text.length)
        val fragments = mutableListOf<PreservedFragment>()
        var position = 0
        selected.forEach { candidate ->
            masked.append(text, position, candidate.range.first)
            val placeholder = "$prefix${fragments.size}$SUFFIX"
            masked.append(placeholder)
            fragments += PreservedFragment(placeholder, candidate.value, candidate.rule.id)
            position = candidate.range.last + 1
        }
        masked.append(text, position, text.length)
        return PreservedText(masked.toString(), fragments)
    }

    fun restore(preserved: PreservedText): String {
        var text = preserved.masked
        preserved.fragments.forEach { fragment ->
            text = text.replace(fragment.placeholder, fragment.original)
        }
        return text
    }

    /** True when every letter and digit of [text] belongs to a preserved marker. */
    fun isFullyPreserved(text: String): Boolean {
        val preserved = mask(text)
        if (preserved.isEmpty) return false
        var residue = preserved.masked
        preserved.fragments.forEach { residue = residue.replace(it.placeholder, "") }
        return residue.none { it.isLetterOrDigit() }
    }
}
