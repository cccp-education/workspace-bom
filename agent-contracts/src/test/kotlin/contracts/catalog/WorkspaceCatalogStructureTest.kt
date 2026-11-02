package contracts.catalog

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

/**
 * MEM-CAT-2 — Tests structurels du catalogue publié `workspace-catalog` (D11a).
 *
 * Garantit que le toml source de vérité contient bien les versions des plugins
 * education.cccp consommables via `ws.versions.*` par les boroughs (D3/D4).
 */
class WorkspaceCatalogStructureTest {
    private val tomlVersions: Map<String, String> by lazy {
        val root =
            sequenceOf(File(".."), File("."))
                .map { it.resolve("gradle/libs.versions.toml") }
                .firstOrNull { it.exists() }
                ?.let { it.parentFile.parentFile }
                ?: error("gradle/libs.versions.toml introuvable")
        parseVersions(root.resolve("gradle/libs.versions.toml").readText())
    }

    @Test
    fun `workspace catalog artifact id is workspace-catalog`() {
        val script = repoRoot().resolve("build.gradle.kts").readText()
        assertTrue(
            script.contains("""artifactId = "workspace-catalog""""),
            "la publication versionCatalog doit produire education.cccp:workspace-catalog",
        )
    }

    private fun repoRoot(): File = File("..").takeIf { it.resolve("gradle/libs.versions.toml").exists() } ?: File(".")

    @Test
    fun `all education cccp plugins have resolvable coordinates in catalog`() {
        val plugins =
            mapOf(
                "bakery-plugin" to "education.cccp:bakery-plugin",
                "capsule-plugin" to "education.cccp:capsule-plugin",
                "codex-plugin" to "education.cccp:codex-plugin",
                "planner-plugin" to "education.cccp:planner-plugin",
                "slider-plugin" to "education.cccp:slider-plugin",
                "plantuml-plugin" to "education.cccp:plantuml-plugin",
                "readme-plugin" to "education.cccp:readme-plugin",
                "hyperframes-plugin" to "education.cccp:hyperframes-plugin",
                "youtube-auth-plugin" to "education.cccp:youtube-auth-plugin",
                "graphify-plugin" to "education.cccp:graphify-plugin",
                "codebase-plugin" to "education.cccp:codebase-plugin",
                "conventions-plugin" to "education.cccp.build:conventions-plugin",
            )
        plugins.forEach { (versionKey, coordinates) ->
            val declared = parseLibraries().any { it.contains(coordinates) }
            assertTrue(declared, "alias bibliothèque manquant pour $coordinates")
            val version = tomlVersions[versionKey]
            assertTrue(!version.isNullOrBlank(), "version manquante pour $versionKey")
        }
    }

    @Test
    fun `ghost entries are not in catalog (D8 - only resolvable from Central)`() {
        val libraries = parseLibraries().joinToString("\n")
        // capsule-plugin was published to Central in BKY-LANG-22-10 (S-225), so it
        // is no longer a ghost entry — it is now legitimately in the catalog.
        assertTrue(
            libraries.contains("education.cccp:capsule-plugin"),
            "capsule-plugin publié Central (S-225) — doit être résolvable via le catalogue",
        )
        assertTrue(
            !libraries.contains("jhipster"),
            "jhipster jamais publié Central — entrée fantôme interdite (D8)",
        )
        assertTrue(
            !libraries.contains("training-plugin"),
            "training-plugin jamais publié Central — entrée fantôme interdite (D8)",
        )
    }

    @Test
    fun `document plugin is consumed versionless through the BOM (no consumer pin)`() {
        // MEM-CAT-2 — this test used to compare `document-plugin` against a LITERAL
        // ("0.0.28", then "0.0.29"). It broke `main` twice on release days (S-339,
        // S-341) because the literal has **no source of truth**: bakery no longer
        // pins `document` at all — it consumes it versionless through the
        // workspace BOM (doctrine D5, lazy adoption). The real cross-borough
        // protection is `verifyCrossBoroughPins` (no consumer pin newer than the
        // published catalogue); a stale consumer is an accepted state, not a bug.
        // What remains true and checkable offline: the catalog carries a semver.
        val version = tomlVersions["document-plugin"]
        assertTrue(!version.isNullOrBlank(), "document-plugin must be declared in the catalog")
        assertTrue(
            Regex("""^\d+\.\d+\.\d+$""").matches(version),
            "document-plugin must be a plain release version (was: $version)",
        )
    }

    private fun parseLibraries(): List<String> {
        val root = repoRoot()
        val content = root.resolve("gradle/libs.versions.toml").readText()
        val libs = mutableListOf<String>()
        var inLibraries = false
        content.lines().forEach { raw ->
            val line = raw.trim()
            when {
                line == "[libraries]" -> inLibraries = true
                line.startsWith("[") -> inLibraries = false
                inLibraries && '=' in line -> libs += line
            }
        }
        return libs
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
