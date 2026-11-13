package contracts.catalog

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * MEM-CAT-1 — Garde anti split-brain toml↔platform (D10/D11a).
 *
 * Le toml `gradle/libs.versions.toml` est la source unique de vérité des versions.
 * La platform (root build.gradle.kts) doit consommer le toml via `libs.*` — jamais
 * hardcoder. Ce test vérifie l'alignement des valeurs clés partagées.
 */
class PlatformCatalogAlignmentTest {
    private val repoRoot: File by lazy {
        sequenceOf(File(".."), File("."))
            .map { it.resolve("gradle/libs.versions.toml") }
            .firstOrNull { it.exists() }
            ?.let { it.parentFile.parentFile }
            ?: error("libs.versions.toml introuvable depuis ${File(".").absolutePath}")
    }

    private val tomlVersions: Map<String, String> by lazy {
        parseVersions(repoRoot.resolve("gradle/libs.versions.toml").readText())
    }

    private val platformScript: String by lazy {
        repoRoot.resolve("build.gradle.kts").readText()
    }

    @Test
    fun `toml version workspace-bom matches platform version`() {
        assertEquals(
            "0.0.136",
            tomlVersions["workspace-bom"],
            "workspace-bom = version du tag JitPack (CNV-17)",
        )
        assertTrue(
            platformScript.contains("version = libs.versions.workspace.bom.get()"),
            "la version BOM doit être dérivée du toml (libs.versions.workspace.bom)",
        )
    }

    @Test
    fun `platform does not hardcode education cccp artifacts`() {
        val hardcoded =
            Regex("""api\("education\.cccp:[^"]+:[^"]+"\)""")
                .findAll(platformScript)
                .map { it.value }
                .toList()
        assertTrue(
            hardcoded.isEmpty(),
            "artefacts education.cccp hardcodés dans la platform (doivent passer par libs.*): $hardcoded",
        )
    }

    @Test
    fun `toml agent contracts versions aligned with published artifacts`() {
        // CNV-17 (D5) — JitPack relabelle tous les modules à la version du tag :
        // les versions par contrat sont abolies, tous les contrats portent la
        // version du tag `workspace-bom`.
        listOf(
            "agent-contracts",
            "codebase-contracts",
            "i18n-contracts",
            "llm-pool-contracts",
            "opencode-session-contracts",
            "pipeline-contracts",
            "runtime-contracts",
            "ocr-contracts",
            "plantuml-contracts",
        ).forEach { key ->
            assertEquals(
                "0.0.136",
                tomlVersions[key],
                "$key doit porter la version du tag workspace-bom (CNV-17 D5)",
            )
        }
        assertEquals(
            "0.0.17",
            tomlVersions["conventions-plugin"],
            "conventions-plugin 0.0.17 (JitPack, CNV-17)",
        )
    }

    @Test
    fun `toml codebase plugin version is the published S-264 release`() {
        assertEquals(
            "0.0.18",
            tomlVersions["codebase-plugin"],
            "codebase-plugin 0.0.18 (KOOG-PIPELINE-DEDUP BS-1 : squelette codebase.koog.pipeline N1)",
        )
    }

    @Test
    fun `toml bakery plugin version is aligned on the latest N2 release`() {
        assertEquals(
            "0.0.38",
            tomlVersions["bakery-plugin"],
            "bakery-plugin 0.0.38 (release consommateur BKY-CI-MIRROR, JitPack)",
        )
    }

    @Test
    fun `toml plugin versions are non blank and semver like`() {
        val plugins =
            listOf(
                "bakery-plugin",
                "capsule-plugin",
                "codex-plugin",
                "planner-plugin",
                "slider-plugin",
                "plantuml-plugin",
                "readme-plugin",
                "hyperframes-plugin",
                "graphify-plugin",
                "api-key-pool-plugin",
                "codebase-plugin",
                "conventions-plugin",
                "document-plugin",
            )
        plugins.forEach { key ->
            val version = tomlVersions[key]
            assertTrue(!version.isNullOrBlank(), "$key doit avoir une version dans le toml")
            assertTrue(Regex("""\d+\.\d+\.\d+""").matches(version), "$key=$version doit être semver")
        }
    }

    @Test
    fun `epub3 version aligned between toml and platform`() {
        assertEquals("2.2.0", tomlVersions["asciidoctorj-epub3"], "asciidoctorj-epub3 (version réelle Central, alignée document-gradle)")
        assertTrue(
            platformScript.contains("api(libs.asciidoctorj.epub3)"),
            "la platform doit consommer asciidoctorj-epub3 via le toml",
        )
    }

    private fun parseVersions(content: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        var inVersions = false
        content.lines().forEach { raw ->
            val line = raw.substringBefore('#').trim()
            when {
                line == "[versions]" -> inVersions = true
                line.startsWith("[") -> inVersions = false
                inVersions && '=' in line -> {
                    val (key, value) = line.split('=', limit = 2)
                    val cleaned = value.trim().trim('"')
                    if (cleaned.isNotEmpty()) result[key.trim()] = cleaned
                }
            }
        }
        return result
    }
}
