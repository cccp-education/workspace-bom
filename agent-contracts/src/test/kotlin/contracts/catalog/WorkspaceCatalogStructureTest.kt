package contracts.catalog

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * MEM-CAT-2 — Tests structurels du catalogue publié `workspace-catalog` (D11a).
 *
 * Garantit que le toml source de vérité contient bien les versions des plugins
 * education.cccp consommables via `ws.versions.*` par les boroughs (D3/D4).
 */
class WorkspaceCatalogStructureTest {

    private val tomlVersions: Map<String, String> by lazy {
        val root = sequenceOf(File(".."), File("."))
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
            "la publication versionCatalog doit produire education.cccp:workspace-catalog"
        )
    }

    private fun repoRoot(): File = File("..").takeIf { it.resolve("gradle/libs.versions.toml").exists() } ?: File(".")

    @Test
    fun `all education cccp plugins have resolvable coordinates in catalog`() {
        val plugins = mapOf(
            "bakery-plugin" to "education.cccp:bakery-plugin",
            "codex-plugin" to "education.cccp:codex-plugin",
            "planner-plugin" to "education.cccp:planner-plugin",
            "slider-plugin" to "education.cccp:slider-plugin",
            "plantuml-plugin" to "education.cccp:plantuml-plugin",
            "readme-plugin" to "education.cccp:readme-plugin",
            "hyperframes-plugin" to "education.cccp:hyperframes-plugin",
            "graphify-plugin" to "education.cccp:graphify-plugin",
            "codebase-plugin" to "education.cccp:codebase-plugin",
            "conventions-plugin" to "education.cccp.build:conventions-plugin"
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
        assertTrue(
            !libraries.contains("education.cccp:capsule-plugin"),
            "capsule-plugin jamais publié Central — entrée fantôme interdite (D8)"
        )
        assertTrue(
            !libraries.contains("education.cccp:planner-plugin:0.0.2"),
            "n/a — planner plugin version doit être la version publiée"
        )
    }

    @Test
    fun `document plugin version matches bakery consumer pin`() {
        assertEquals(
            "0.0.15", tomlVersions["document-plugin"],
            "document-plugin = version publiée Central (S-241) consommée par bakery"
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