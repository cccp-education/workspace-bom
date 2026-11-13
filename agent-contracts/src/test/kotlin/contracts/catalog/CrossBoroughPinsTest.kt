package contracts.catalog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * MEM-CI-CONSISTENCY (S-226) — the `v`-prefixed pin blind spot.
 *
 * The guard used to rank `v0.0.4` as `null`, then report it as "newer" than
 * `0.0.4` even when the two were equal (false red), and the `workspace-*` pin
 * regex did not match `workspace-catalog:v0.0.133` at all (blind spot). Both are
 * characterised here.
 */
class CrossBoroughPinsTest {
    @Test
    fun `a v prefix is normalised before ranking`() {
        assertThat(CrossBoroughPins.versionRank("v0.0.4")).containsExactly(0, 0, 4)
        assertThat(CrossBoroughPins.versionRank("V0.0.4")).containsExactly(0, 0, 4)
    }

    @Test
    fun `a v prefixed pin equal to the published version is not newer`() {
        assertThat(CrossBoroughPins.isNewer("v0.9.1", "0.9.1")).isFalse
        assertThat(CrossBoroughPins.isNewer("v0.0.4", "v0.0.4")).isFalse
    }

    @Test
    fun `a strictly newer pin is still reported`() {
        assertThat(CrossBoroughPins.isNewer("v0.9.2", "0.9.1")).isTrue
        assertThat(CrossBoroughPins.isNewer("0.0.5", "0.0.4")).isTrue
    }

    @Test
    fun `a non plain release stays fail closed`() {
        assertThat(CrossBoroughPins.isNewer("1.0.0-SNAPSHOT", "0.0.4")).isTrue
    }

    @Test
    fun `the parser is scoped to the versions section`() {
        val table =
            CrossBoroughPins.parseVersionTable(
                """
                [versions]
                slider-plugin = "0.0.20" # comment
                [libraries]
                slider-plugin = "9.9.9"
                """.trimIndent(),
            )

        assertThat(table).containsEntry("slider-plugin", "0.0.20")
    }

    @Test
    fun `a v prefixed pin equal to the catalogue is not a false positive`(
        @TempDir root: File,
    ) {
        workspace(root, consumerSlider = "v0.0.20", workspacePin = "v0.0.128")

        val result = CrossBoroughPins.scan(root, root.resolve("workspace-bom/gradle/libs.versions.toml"))

        assertThat(result.violations).isEmpty()
        assertThat(result.ok).isTrue()
    }

    @Test
    fun `a newer pin is still reported`(
        @TempDir root: File,
    ) {
        workspace(root, consumerSlider = "v0.0.21", workspacePin = "v0.0.129")

        val result = CrossBoroughPins.scan(root, root.resolve("workspace-bom/gradle/libs.versions.toml"))

        assertThat(result.ok).isFalse()
        assertThat(result.violations).hasSize(2)
        assertThat(result.violations).anyMatch { it.contains("slider = \"v0.0.21\"") }
        assertThat(result.violations).anyMatch { it.contains("workspace-catalog:v0.0.129") }
    }

    private fun workspace(
        root: File,
        consumerSlider: String,
        workspacePin: String,
    ) {
        write(
            root,
            "workspace-bom/gradle/libs.versions.toml",
            """
            [versions]
            workspace-bom = "0.0.128"
            slider-plugin = "0.0.20"
            """,
        )
        write(
            root,
            "consumer/gradle/libs.versions.toml",
            """
            [versions]
            slider = "$consumerSlider"
            """,
        )
        write(
            root,
            "consumer/settings.gradle.kts",
            """
            dependencyResolutionManagement {
                repositories {
                    maven("https://example.invalid/workspace-catalog:$workspacePin")
                }
            }
            """,
        )
    }

    private fun write(
        root: File,
        path: String,
        content: String,
    ) {
        val file = root.resolve(path)
        file.parentFile.mkdirs()
        file.writeText(content.trimIndent())
    }
}
