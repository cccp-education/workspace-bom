package contracts.catalog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * N0 contract suite — the section-scoped reader must never resolve a version
 * from `[libraries]`/`[plugins]`, never accept a commented line, and never
 * drift when whitespace or trailing comments change.
 */
class VersionCatalogTomlTest {
    private val catalog =
        """
        [metadata]
        format.version = "1.1"

        [versions]
        # cross-borough self versions
        graphify-plugin = "0.0.5"
        bakery-plugin = "0.0.12"
        codex-plugin = "0.0.9"

        [libraries]
        graphify-plugin = {group = "education.cccp", name = "graphify-plugin", version.ref = "graphify-plugin" }

        [plugins]
        graphify = {id = "education.cccp.graphify", version.ref = "graphify-plugin" }
        """.trimIndent()

    @Nested
    inner class VersionLookup {
        @Test
        fun `should resolve a version declared in the versions section`() {
            assertThat(VersionCatalogToml.versionOf(catalog, "graphify-plugin")).isEqualTo("0.0.5")
        }

        @Test
        fun `should resolve the first candidate key found among aliases`() {
            assertThat(VersionCatalogToml.versionOf(catalog, listOf("graphify", "graphify-plugin")))
                .isEqualTo("0.0.5")
        }

        @Test
        fun `should return null when the key is absent from the versions section`() {
            assertThat(VersionCatalogToml.versionOf(catalog, "missing-plugin")).isNull()
        }

        @Test
        fun `should ignore a key declared in another section`() {
            assertThat(VersionCatalogToml.versionOf(catalog, "graphify")).isNull()
        }

        @Test
        fun `should not read a plain assignment declared outside the versions section`() {
            val misplaced =
                """
                [metadata]
                graphify-plugin = "9.9.9"

                [versions]
                bakery-plugin = "0.0.12"
                """.trimIndent()
            assertThat(VersionCatalogToml.versionOf(misplaced, "graphify-plugin")).isNull()
        }
    }

    @Nested
    inner class CommentAndWhitespaceHandling {
        @Test
        fun `should ignore a commented-out version line`() {
            val commented =
                """
                [versions]
                # graphify-plugin = "9.9.9"
                graphify-plugin = "0.0.5"
                """.trimIndent()
            assertThat(VersionCatalogToml.versionOf(commented, "graphify-plugin")).isEqualTo("0.0.5")
        }

        @Test
        fun `should ignore a trailing comment after the value`() {
            val trailing =
                """
                [versions]
                graphify-plugin = "0.0.5" # published graphify
                """.trimIndent()
            assertThat(VersionCatalogToml.versionOf(trailing, "graphify-plugin")).isEqualTo("0.0.5")
        }

        @Test
        fun `should ignore a fully commented-out section`() {
            val commentedSection =
                """
                [versions]
                bakery-plugin = "0.0.12"

                #[versions]
                #graphify-plugin = "9.9.9"
                """.trimIndent()
            assertThat(VersionCatalogToml.versionOf(commentedSection, "graphify-plugin")).isNull()
        }

        @Test
        fun `should tolerate surrounding whitespace around the key and value`() {
            val spaced =
                """
                [versions]
                    graphify-plugin   =   "0.0.5"
                """.trimIndent()
            assertThat(VersionCatalogToml.versionOf(spaced, "graphify-plugin")).isEqualTo("0.0.5")
        }
    }
}
