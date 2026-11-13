package contracts.catalog

import java.io.File

/**
 * MEM-CI-CONSISTENCY (US-3/US-5) — cross-borough pin consistency.
 *
 * A sibling build may not pin a cross-borough artefact **newer** than the version
 * published by this BOM (US-3: a consumer root pinned `slider 0.0.20` while the
 * catalogue carried `0.0.19`), and the catalogue may not be stale relative to
 * what consumers pin (US-5).
 *
 * Pure text parsing: no network, no Gradle model. Siblings that are not checked
 * out are skipped (each CI matrix job clones a single borough).
 *
 * S-226 fix (the documented blind spot): an optional `v`/`V` tag prefix is
 * normalised before the numeric rank, and a `workspace-bom:`/`workspace-catalog:`
 * pin may carry it (`workspace-catalog:v0.0.133`). Without this, `v0.0.4` vs
 * `0.0.4` was ranked `null` and reported as "newer" — a false red.
 */
object CrossBoroughPins {
    private val SKIPPED_DIRECTORIES = setOf("build", ".gradle", "node_modules", "out", ".git")

    /** consumer toml key -> catalogue key (same artifact). */
    private val CROSS_BOROUGH_PIN_FAMILIES =
        mapOf(
            "codebase" to "codebase-plugin",
            "codebase-plugin" to "codebase-plugin",
            "codebase-gradle-plugin" to "codebase-plugin",
            "slider" to "slider-plugin",
            "slider-plugin" to "slider-plugin",
            "capsule" to "capsule-plugin",
            "capsule-plugin" to "capsule-plugin",
            "codex" to "codex-plugin",
            "codex-plugin" to "codex-plugin",
            "planner" to "planner-plugin",
            "planner-plugin" to "planner-plugin",
            "document" to "document-plugin",
            "document-plugin" to "document-plugin",
            "bakery" to "bakery-plugin",
            "bakery-plugin" to "bakery-plugin",
            "plantuml-plugin" to "plantuml-plugin",
            "hyperframes" to "hyperframes-plugin",
            "hyperframes-plugin" to "hyperframes-plugin",
            "graphify" to "graphify-plugin",
            "graphify-plugin" to "graphify-plugin",
            "dashboard" to "dashboard-plugin",
            "dashboard-plugin" to "dashboard-plugin",
            "readme" to "readme-plugin",
            "readme-plugin" to "readme-plugin",
            "conventions" to "conventions-plugin",
            "conventions-plugin" to "conventions-plugin",
            "api-key-pool" to "api-key-pool-plugin",
            "api-key-pool-plugin" to "api-key-pool-plugin",
            "video-kernel" to "video-kernel",
            "youtube-auth" to "youtube-auth-plugin",
            "youtube-auth-plugin" to "youtube-auth-plugin",
            "runtime-contracts" to "runtime-contracts",
            "ocr-contracts" to "ocr-contracts",
            "plantuml-contracts" to "plantuml-contracts",
            "catalog-contracts" to "catalog-contracts",
            "i18n-contracts" to "i18n-contracts",
            "agent-contracts" to "agent-contracts",
            "codebase-contracts" to "codebase-contracts",
            "llm-pool-contracts" to "llm-pool-contracts",
            "opencode-session-contracts" to "opencode-session-contracts",
            "pipeline-contracts" to "pipeline-contracts",
        )

    /** `workspace-bom:<v>` / `workspace-catalog:<v>`, with the optional `v` tag prefix. */
    private val WORKSPACE_PIN = Regex("""workspace-(?:bom|catalog):(v?[0-9][0-9A-Za-z.\-]*)""")

    data class Result(
        val violations: List<String>,
        val scannedCatalogs: Int,
        val scannedBuildScripts: Int,
        val skippedOwn: Int,
        val publishedBom: String,
    ) {
        val ok: Boolean get() = violations.isEmpty()
    }

    fun scan(
        workspaceRoot: File,
        catalogFile: File,
    ): Result {
        val catalog = parseVersionTable(catalogFile)
        val publishedBom = catalog["workspace-bom"]
            ?: error("${catalogFile.name} has no `workspace-bom` version")
        if (!workspaceRoot.isDirectory) {
            return Result(emptyList(), 0, 0, 0, publishedBom)
        }

        val violations = mutableListOf<String>()
        val skippedOwn = mutableSetOf<String>()
        var scannedCatalogs = 0

        val tomls =
            workspaceRoot
                .walkTopDown()
                .onEnter { it.name !in SKIPPED_DIRECTORIES }
                .filter { it.name == "libs.versions.toml" && it.parentFile?.name == "gradle" }
                .toList()

        tomls.forEach { toml ->
            val relative = toml.relativeTo(workspaceRoot).invariantSeparatorsPath
            val buildDir = relative.substringBeforeLast("/gradle/").substringAfterLast('/')
            val ownFamily = ownArtefactFamily(buildDir)
            val versions = parseVersionTable(toml)

            versions.forEach { (key, value) ->
                val family = CROSS_BOROUGH_PIN_FAMILIES[key] ?: return@forEach
                if (family == ownFamily) {
                    skippedOwn += "$buildDir:$key"
                    return@forEach
                }
                val published = catalog[family]
                if (published == null) {
                    violations += "$relative: `$key = \"$value\"` has no published catalogue entry ('$family')"
                    return@forEach
                }
                if (isNewer(value, published)) {
                    violations +=
                        "$relative: `$key = \"$value\"` is newer than the published " +
                        "catalogue `$family = \"$published\"` (catalogue stale or unpublished pin)"
                }
            }
            scannedCatalogs += 1
        }

        val gradleFiles =
            workspaceRoot
                .walkTopDown()
                .onEnter { it.name !in SKIPPED_DIRECTORIES }
                .filter { it.name == "settings.gradle.kts" || it.name == "build.gradle.kts" }
                .toList()

        gradleFiles.forEach { file ->
            val relative = file.relativeTo(workspaceRoot).invariantSeparatorsPath
            if (relative.startsWith("workspace-bom/")) return@forEach
            WORKSPACE_PIN.findAll(file.readText()).forEach { match ->
                val pinned = match.groupValues[1]
                if (isNewer(pinned, publishedBom)) {
                    violations +=
                        "$relative: pins `${match.value}` newer than the published " +
                        "`workspace-bom = \"$publishedBom\"`"
                }
            }
        }

        return Result(violations, scannedCatalogs, gradleFiles.size, skippedOwn.size, publishedBom)
    }

    fun parseVersionTable(file: File): Map<String, String> = if (file.isFile) parseVersionTable(file.readText()) else emptyMap()

    fun parseVersionTable(text: String): Map<String, String> {
        var inVersions = false
        val table = mutableMapOf<String, String>()
        text.lines().forEach { raw ->
            val line = raw.substringBefore('#').trim()
            when {
                line.startsWith("[") -> inVersions = line == "[versions]"
                inVersions && line.contains('=') -> {
                    val key = line.substringBefore('=').trim()
                    val value = line.substringAfter('=').trim().trim('"')
                    if (key.isNotEmpty()) table[key] = value
                }
            }
        }
        return table
    }

    /** Numeric rank of a dotted release, `null` when it is not a plain release. */
    fun versionRank(version: String): List<Int>? {
        val normalized = version.trim().removePrefix("v").removePrefix("V")
        if (normalized.contains("SNAPSHOT", ignoreCase = true)) return null
        val core = normalized.substringBefore('-').substringBefore('+')
        return core.split('.').map { part -> part.toIntOrNull() ?: return null }
    }

    fun isNewer(
        candidate: String,
        reference: String,
    ): Boolean {
        val left = versionRank(candidate) ?: return true
        val right = versionRank(reference) ?: return false
        val size = maxOf(left.size, right.size)
        for (index in 0 until size) {
            val a = left.getOrElse(index) { 0 }
            val b = right.getOrElse(index) { 0 }
            if (a != b) return a > b
        }
        return false
    }

    private fun ownArtefactFamily(buildDirName: String): String? =
        when {
            buildDirName == "video-kernel-gradle" -> "video-kernel"
            buildDirName.endsWith("-gradle") -> buildDirName.removeSuffix("-gradle") + "-plugin"
            buildDirName in CROSS_BOROUGH_PIN_FAMILIES.values -> buildDirName
            else -> null
        }
}
