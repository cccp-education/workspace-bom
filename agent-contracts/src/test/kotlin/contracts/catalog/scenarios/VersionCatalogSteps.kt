package contracts.catalog.scenarios

import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat

/**
 * MEM-CAT-5 — Step defs BDD `@version-catalog`.
 *
 * Domaine pur : parse du toml published (source unique de vérité) + vérification
 * de résolvabilité (mavenLocal / Central). Aucune tâche Gradle — le catalogue est
 * un artefact Maven, pas une tâche.
 */
class VersionCatalogSteps {

    private lateinit var tomlContent: String
    private var accessedVersion: String? = null
    private var hardcodedCoordinates: List<String> = emptyList()

    // ── Given ──────────────────────────────────────────────────────────────

    @Given("the published workspace catalog toml")
    fun givenPublishedCatalogToml() {
        tomlContent = sequenceOf(File(".."), File("."))
            .map { it.resolve("gradle/libs.versions.toml") }
            .firstOrNull { it.exists() }
            ?.readText()
            ?: error("gradle/libs.versions.toml introuvable — le catalog source doit exister")
    }

    // ── When ───────────────────────────────────────────────────────────────

    @When("the consumer reads version {string} through the typed accessor")
    fun whenConsumerReadsVersion(key: String) {
        accessedVersion = versionFromToml(key)
    }

    @When("the platform script is inspected for hardcoded coordinates")
    fun whenPlatformScriptInspected() {
        val script = sequenceOf(File(".."), File("."))
            .map { it.resolve("build.gradle.kts") }
            .firstOrNull { it.exists() }
            ?.readText()
            ?: error("build.gradle.kts introuvable")
        hardcodedCoordinates = Regex("""api\("education\.cccp:[^"]+:[^"]+"\)""")
            .findAll(script).map { it.value }.toList()
    }

    // ── Then ───────────────────────────────────────────────────────────────

    @Then("the catalog contains version entries for the {int} resolvable plugins")
    fun thenCatalogContainsResolvablePlugins(expected: Int) {
        val plugins = listOf(
            "bakery-plugin", "codex-plugin", "planner-plugin", "slider-plugin",
            "plantuml-plugin", "readme-plugin", "hyperframes-plugin", "graphify-plugin",
            "api-key-pool-plugin", "codebase-plugin", "conventions-plugin",
            "document-plugin"
        )
        val present = plugins.count { versionFromTomlOrNull(it) != null }
        assertThat(present).describedAs("plugins education.cccp présents dans le catalog").isEqualTo(expected)
    }

    @Then("every plugin version is non-blank and semver-like")
    fun thenEveryVersionSemver() {
        listOf(
            "bakery-plugin", "codex-plugin", "planner-plugin", "slider-plugin",
            "plantuml-plugin", "readme-plugin", "hyperframes-plugin", "graphify-plugin",
            "api-key-pool-plugin", "codebase-plugin", "conventions-plugin",
            "document-plugin"
        ).forEach { key ->
            val v = versionFromToml(key)
            assertThat(v).describedAs(key).isNotBlank
            assertThat(Regex("""\d+\.\d+\.\d+""").matches(v)).describedAs("$key=$v doit être semver").isTrue
        }
    }

    @Then("no ghost plugin entry exists in the catalog")
    fun thenNoGhostEntry() {
        assertThat(tomlContent).doesNotContain("capsule-plugin")
        assertThat(tomlContent).doesNotContain("jhipster")
        assertThat(tomlContent).doesNotContain("training-plugin")
    }

    @Then("the accessor returns a published semver {string}")
    fun thenAccessorReturnsSemver(expected: String) {
        assertThat(accessedVersion).isEqualTo(expected)
        assertThat(Regex("""\d+\.\d+\.\d+""").matches(accessedVersion!!)).isTrue
    }

    @Then("the version is resolvable from mavenLocal or Central")
    fun thenVersionResolvable() {
        val v = accessedVersion ?: error("aucune version lue au préalable")
        val local = File(System.getProperty("user.home"))
            .resolve(".m2/repository/education/cccp/planner-plugin/$v/planner-plugin-$v.pom")
        val central = "https://repo1.maven.org/maven2/education/cccp/planner-plugin/$v/planner-plugin-$v.pom"
        val resolved = local.exists() || httpOk(central)
        assertThat(resolved).describedAs("planner-plugin:$v résolvable (mavenLocal ou Central)").isTrue
    }

    @Then("no api dependency hardcodes an education cccp group coordinate")
    fun thenNoHardcodedCoordinate() {
        assertThat(hardcodedCoordinates)
            .describedAs("coordonnées education.cccp hardcodées dans la platform")
            .isEmpty()
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private fun versionFromToml(key: String): String =
        versionFromTomlOrNull(key) ?: error("version '$key' absente du toml")

    private fun versionFromTomlOrNull(key: String): String? =
        tomlContent.lineSequence()
            .map { it.substringBefore('#').trim() }
            .firstOrNull { it.startsWith("$key =") }
            ?.substringAfter('"')?.substringBefore('"')

    private fun httpOk(url: String): Boolean = try {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 5_000
        conn.readTimeout = 10_000
        (conn.responseCode == 200).also { conn.disconnect() }
    } catch (_: Exception) {
        false
    }
}