package contracts.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * MEM-I18N-PRESERVE — the "never translate" catalog.
 *
 * Each case pins a marker measured in the published corpus (a translated
 * `toc::[]` broke 142 files) or the false positive the mask must never produce
 * (ordinary prose mentioning an attribute reference).
 */
class PreserveRuleTest {

    @Test
    fun `the catalog exposes the measured rule ids`() {
        assertEquals(
            listOf(
                "asciidoc-macro",
                "block-attribute",
                "fence",
                "inline-code",
                "anchor",
                "attribute-reference",
                "url",
            ),
            PreserveRule.ids(),
        )
        assertEquals(PreserveRule.FENCE, PreserveRule.of("fence"))
        assertEquals(null, PreserveRule.of("unknown"))
    }

    @Test
    fun `an asciidoc macro is preserved`() {
        assertEquals(listOf("toc::[]", "image::schema.png[]"), PreserveRule.ASIDOC_MACRO.findAll("toc::[] image::schema.png[]"))
    }

    @Test
    fun `a block attribute line is preserved`() {
        assertEquals(listOf("[cols=\"1,1\",options=\"header\"]"), PreserveRule.BLOCK_ATTRIBUTE.findAll("[cols=\"1,1\",options=\"header\"]\n|===\n|A\n"))
    }

    @Test
    fun `a fence and table delimiter are preserved`() {
        val text = "----\ncode\n----\n|===\n"
        assertEquals(listOf("----", "----", "|==="), PreserveRule.FENCE.findAll(text))
    }

    @Test
    fun `an anchor and an attribute reference are preserved`() {
        assertEquals(listOf("[[page-10]]"), PreserveRule.ANCHOR.findAll("see [[page-10]]"))
        assertEquals(listOf("{site_host}"), PreserveRule.ATTRIBUTE_REFERENCE.findAll("{site_host}/fr/"))
    }

    @Test
    fun `an inline code span and a url are preserved`() {
        assertEquals(listOf("`gradle build`"), PreserveRule.INLINE_CODE.findAll("run `gradle build` now"))
        assertEquals(listOf("https://example.com/a?b=1"), PreserveRule.URL.findAll("see https://example.com/a?b=1 for details"))
    }

    @Test
    fun `masking replaces the markers and restore is byte-identical`() {
        val text = "Run `gradle build` then toc::[] and see [[page-1]]."

        val preserved = PreserveCatalog.mask(text)

        assertFalse(preserved.isEmpty)
        assertFalse(preserved.masked.contains("toc::[]"))
        assertTrue(preserved.masked.contains("@@PRESERVE_0@@"))
        assertEquals(text, PreserveCatalog.restore(preserved))
    }

    @Test
    fun `masking is deterministic and numbers by position`() {
        val text = "toc::[] then `a` then {b}"

        val first = PreserveCatalog.mask(text)
        val second = PreserveCatalog.mask(text)

        assertEquals(first.fragments.map { it.original }, listOf("toc::[]", "`a`", "{b}"))
        assertEquals(first.masked, second.masked)
    }

    @Test
    fun `the longest match wins when two rules overlap`() {
        // `toc::[]` is both an inline-code span and an AsciiDoc macro at the same
        // offset: the macro (10 chars) must win over the code span.
        val preserved = PreserveCatalog.mask("`toc::[]`")

        assertEquals(1, preserved.fragments.size)
        assertEquals("`toc::[]`", preserved.fragments.single().original)
    }

    @Test
    fun `prose mentioning an attribute reference is not fully preserved`() {
        assertFalse(PreserveCatalog.isFullyPreserved("Le site {site_host} est en ligne"))
    }

    @Test
    fun `a pure macro fragment is fully preserved`() {
        assertTrue(PreserveCatalog.isFullyPreserved("toc::[]"))
        assertTrue(PreserveCatalog.isFullyPreserved("[[page-10]]"))
        assertFalse(PreserveCatalog.isFullyPreserved("Le processeur embarque 128 Go de mémoire"))
    }
}
