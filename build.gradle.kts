

plugins {
    signing
    `java-platform`
    `maven-publish`
    `version-catalog`
}

group = "education.cccp"
version = libs.versions.workspace.bom.get()

javaPlatform {
    allowDependencies()
}

// ── MEM-CAT-2 — Catalog source : le toml est l'unique source de vérité (D1) ──
catalog {
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

dependencies {
    // Arrow publishes its own BOM (`io.arrow-kt:arrow-stack` — official docs
    // https://arrow-kt.io/learn/quickstart/setup/) which versions the whole
    // Arrow stack (core, fx-coroutines, optics, atomic, resilience, …). Imported
    // here (platform dependency, generates `<type>pom</type><scope>import</scope>`
    // in the published POM) so a consumer only needs this BOM to align every
    // Arrow component. The explicit constraints below remain the single source of
    // truth for the components we use; `arrow-integrations-jackson-module` is NOT
    // part of the stack (separate versioning 0.15.1) and stays pinned apart.
    api(platform(libs.arrow.stack))

    constraints {
        // ── Kotlin ──────────────────────────────────────────────────────────
        api(libs.kotlin.stdlib)
        // MEMPHIS-BOM-FIX — kotlin-stdlib-jdk8 is consumed WITHOUT a version by
        // document-gradle (`implementation(kotlin("stdlib-jdk8"))` + transitive
        // resolution). Central rejects a versionless runtime dependency
        // ("Dependency version information is missing"), so the BOM must pin it
        // exactly like kotlin-stdlib. Aliased and versioned by the toml (same
        // `kotlin` version ref).
        api(libs.kotlin.stdlib.jdk8)
        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.coroutines.core)
        api(libs.kotlinx.coroutines.reactive)
        api(libs.kotlinx.coroutines.jdk8)
        api(libs.kotlinx.coroutines.test)

        // ── Jackson ─────────────────────────────────────────────────────────
        api(libs.jackson.databind)
        api(libs.jackson.dataformat.yaml)
        api(libs.jackson.module.kotlin)
        api(libs.jackson.datatype.jsr310)

        // ── LangChain4j ─────────────────────────────────────────────────────
        api(libs.langchain4j)
        api(libs.langchain4j.ollama)
        api(libs.langchain4j.open.ai)
        api(libs.langchain4j.google.ai.gemini)
        api(libs.langchain4j.mistral)
        api(libs.langchain4j.anthropic)
        api(libs.langchain4j.pgvector)
        api(libs.langchain4j.minilm)
        api(libs.langchain4j.hugging.face)

        // ── Koog ────────────────────────────────────────────────────────────
        api(libs.koog.agents)

        // ── Logging ─────────────────────────────────────────────────────────
        api(libs.logback.classic)
        api(libs.slf4j.api)

        // ── Database ────────────────────────────────────────────────────────
        api(libs.r2dbc.postgresql)
        api(libs.r2dbc.pool)
        api(libs.r2dbc.spi)
        api(libs.postgresql.jdbc)

        // ── Processing ──────────────────────────────────────────────────────
        api(libs.mapstruct)
        api(libs.arrow.core)
        api(libs.arrow.fx.coroutines)
        api(libs.arrow.jackson)
        api(libs.commons.io)

        // ── Tests ───────────────────────────────────────────────────────────
        api(libs.junit.jupiter)
        api(libs.junit.platform.launcher)
        api(libs.junit.platform.suite)
        api(libs.cucumber.java)
        api(libs.cucumber.junit.platform.engine)
        api(libs.cucumber.picocontainer)
        api(libs.testcontainers.postgresql)
        api(libs.testcontainers.junit5)
        api(libs.mockito.kotlin)
        api(libs.mockito.junit.jupiter)
        api(libs.assertj)

        // ── Docker / Infrastructure ─────────────────────────────────────────
        api(libs.docker.java.core)
        api(libs.docker.java.transport.httpclient5)

        // ── Git ─────────────────────────────────────────────────────────────
        api(libs.jgit.core)
        api(libs.jgit.ssh)
        api(libs.jgit.archive)
        api(libs.xz)

        // ── Asciidoctor ─────────────────────────────────────────────────────
        api(libs.asciidoctorj)
        api(libs.asciidoctorj.diagram)
        api(libs.asciidoctorj.diagram.plantuml)
        api(libs.asciidoctorj.epub3)

        // ── PDF/Extraction ──────────────────────────────────────────────────
        api(libs.pdfbox)
        api(libs.tika.core)
        api(libs.flexmark.all)

        // ── Other ───────────────────────────────────────────────────────────
        api(libs.playwright)

        // ── Internal education.cccp contracts (N0) — source unique de vérité ──
        api(libs.agent.contracts)
        api(libs.codebase.contracts)
        api(libs.llm.pool.contracts)
        api(libs.opencode.session.contracts)
        api(libs.vibecoding.contracts)
        api(libs.i18n.contracts)
        api(libs.pipeline.contracts)
        api(libs.runtime.contracts)
        api(libs.ocr.contracts)
        api(libs.plantuml.contracts)

        // ── Internal education.cccp plugins (N2) — pilotés par le BOM ───────
        api(libs.bakery.plugin)
        api(libs.capsule.plugin)
        api(libs.codex.plugin)
        api(libs.planner.plugin)
        api(libs.slider.plugin)
        api(libs.plantuml.plugin)
        api(libs.readme.plugin)
        api(libs.hyperframes.plugin)
        api(libs.graphify.plugin)
        api(libs.api.key.pool.plugin)
        api(libs.codebase.plugin)
        api(libs.conventions.plugin)
        // document-plugin : publié Central (S-241), consommé par bakery sans version (MEM-CAT-3)
        api(libs.document.plugin)

        // ── Shared N1 libraries (education.cccp) — noyau vidéo neutre ────────
        api(libs.video.kernel)
        api(libs.youtube.auth.plugin)

        // ── Force resolution (koog 26.0.2-1 vs testcontainers 17.0.0) ─────────
        api(libs.jetbrains.annotations)
    }
}

// ── runAllTests — lance les tests de tous les boroughs publics (builds indépendants) ──

// NB : capsule-gradle, codebase-gradle, codex-gradle, graphify-gradle, hyperframes-gradle
// et readme-gradle exclus — racines consommateurs sans tâche check (pattern article 0124,
// racine = client, plugin buildé dans sous-répertoire indépendant).
// NB : jhipster-gradle-plugins exclu — tests persistence référencent un plugin ID périmé
// (com.cheroliv.jhipster.persistence vs education.cccp.jhipster.persistence) — dette préexistante.
// NB : plantuml-gradle exclu — racine applique le plugin sans version ni alias (résolution
// impossible) — dette préexistante, le plugin lui-même a son propre check.
// Value = build directory relative to the borough root. bakery-gradle is a
// pattern-0124 consumer root (no `check` task); its build is the standalone
// `bakery-plugin/` sub-build, so the check runs there.
val testableProjects = mapOf(
    "api-key-pool-gradle" to ".",
    "bakery-gradle" to "bakery-plugin",
    "dashboard-gradle" to ".",
    "planner-gradle" to ".",
    "slider-gradle" to "."
)

val runAllTestsTask = tasks.register("runAllTests") {
    group = "verification"
    description = "Runs tests for all public boroughs (each as independent Gradle build)"
}

testableProjects.forEach { (projectName, buildSubdir) ->
    val projectDir = rootProject.projectDir.parentFile.resolve(projectName).resolve(buildSubdir)
    if (projectDir.resolve("build.gradle.kts").exists()) {
        val testTask = tasks.register<Exec>("runTestsFor${projectName.replace("-", "").replaceFirstChar { it.uppercase() }}") {
            group = "verification"
            description = "Runs tests in $projectName"
            workingDir = projectDir
            commandLine("./gradlew", "check")
        }
        runAllTestsTask.configure {
            dependsOn(testTask)
        }
    }
}

// ── Cross-borough CI shards (S-298, S-299) — workspace-bom ↔ bakery coupling ─
// The cross-borough CI no longer runs bakery's monolithic `check`; it runs the
// shard tasks defined in bakery-gradle/bakery-plugin/build.gradle.kts and mirrored
// in bakery-gradle/.github/workflows/test.yml (S-259). This list is the *assumed*
// coupling (workspace-bom invokes bakery task names by string): it lets the guard
// assert the workspace-bom matrix invokes EXACTLY these tasks, exhaustively — a
// shard dropped from the matrix would silently stop testing that slice (anti
// "test fantôme", pattern S-269). Keep in sync with
// bakery-gradle/bakery-plugin/build.gradle.kts.
// S-299 — `nodeTest` is folded into the `test` (unit) matrix entry rather than a
// 16th entry: at 22 entries the 21-slot runner pool queued a job ~35 s and pushed
// a long shard onto the critical path. It produces no kover `.ic`, so it is not
// listed here (the shard entries now equal the pool-bound task set).
val bakeryShardTasks = listOf(
    "test",
    "functionalTestShardA",
    "functionalTestShardB",
    "functionalTestShardC",
    "functionalTestShardD",
    "functionalTestShardE",
    "functionalTestShardF",
    "cucumberTestShardJbakeCoreFoundations",
    "cucumberTestShardJbakeCoreComponents",
    "cucumberTestShardJbakeCorePipelines",
    "cucumberTestShardLensTree",
    "cucumberTestShardSiteTree",
    "cucumberTestShardLangSwitchCore",
    "cucumberTestShardLangSwitchEcosystem",
    "cucumberTestShardI18nMassPipeline"
)

// One entry of the `borough-check` CI matrix (`kind` discriminates a monolithic
// `check` entry from a bakery shard entry).
data class CiMatrixEntry(
    val kind: String,
    val borough: String,
    val dir: String,
    val task: String
)

// ── verifyCiMatrixCoverage — anti "test fantôme" guard (pattern S-269, S-298) ──
// The cross-borough checks are sharded in `.github/workflows/test.yml`, each entry
// on its own CI machine. A borough (or a bakery shard) declared here but missing
// from the workflow matrix would silently stop being tested. This guard asserts,
// from the sources (no Gradle model, pure file parsing), that:
//   • the `check` entries cover EXACTLY the non-sharded testable boroughs;
//   • the `shard` entries cover EXACTLY `bakeryShardTasks` (bakery's surface);
//   • a `bakery-coverage` job merges the shard reports (`needs: borough-check`);
//   • no entry references an unknown borough, and no testable borough is lost.
val ciWorkflowFile = layout.projectDirectory.file(".github/workflows/test.yml")

val verifyCiMatrixCoverage = tasks.register("verifyCiMatrixCoverage") {
    group = "verification"
    description = "Fails if the CI borough/shard matrix does not exactly cover the testable surface."
    val workflow = ciWorkflowFile.asFile
    val expectedBoroughs = testableProjects.toMap()
    val shardedBorough = "bakery-gradle"
    val shardedDir = expectedBoroughs.getValue(shardedBorough)
    inputs.file(ciWorkflowFile)
    doLast {
        val text = workflow.readText()
        // Each matrix entry begins with `- name:` and carries `kind:`/`borough:`/
        // `dir:`/`task:`. Steps also begin with `- name:` but have no `borough:`
        // field — filtering on that field isolates the matrix entries.
        val entries = text
            .split(Regex("""(?m)^[ \t]*-[ \t]+name:"""))
            .filter { Regex("""(?m)^[ \t]*borough:""").containsMatchIn(it) }
            .map { block ->
                fun field(key: String): String =
                    Regex("""(?m)^[ \t]*$key:[ \t]*"?([^\s"]+)""")
                        .find(block)?.groupValues?.get(1)
                        ?: error("matrix entry without `$key:` in $workflow")
                CiMatrixEntry(
                    kind = field("kind"),
                    borough = field("borough"),
                    dir = field("dir"),
                    task = field("task")
                )
            }
        check(entries.isNotEmpty()) {
            "No `kind:`/`borough:`/`dir:`/`task:` matrix entries found in $workflow"
        }

        val checkEntries = entries.filter { it.kind == "check" }
        val shardEntries = entries.filter { it.kind == "shard" }

        // 1. `check` entries — exactly the non-sharded testable boroughs.
        val expectedCheckBoroughs = expectedBoroughs.keys - shardedBorough
        val checkBoroughs = checkEntries.map { it.borough }
        check(checkBoroughs.size == checkBoroughs.toSet().size) {
            "Duplicate `check` entries in CI matrix: $checkBoroughs"
        }
        check(checkBoroughs.toSet() == expectedCheckBoroughs) {
            "CI matrix `check` entries cover $checkBoroughs, expected $expectedCheckBoroughs"
        }
        checkEntries.forEach { entry ->
            check(entry.task == "check") {
                "CI `check` entry for ${entry.borough} must use task 'check', got '${entry.task}'"
            }
            check(entry.dir == expectedBoroughs.getValue(entry.borough)) {
                "CI matrix dir for ${entry.borough} is '${entry.dir}', " +
                    "expected '${expectedBoroughs.getValue(entry.borough)}'"
            }
        }

        // 2. bakery `shard` entries — exhaustive and disjoint over `bakeryShardTasks`.
        check(shardEntries.isNotEmpty()) { "No CI `shard` entries found for $shardedBorough" }
        shardEntries.forEach { entry ->
            check(entry.borough == shardedBorough) {
                "CI `shard` entry '${entry.task}' targets '${entry.borough}', expected '$shardedBorough'"
            }
            check(entry.dir == shardedDir) {
                "CI `shard` entry '${entry.task}' dir is '${entry.dir}', expected '$shardedDir'"
            }
        }
        val shardTaskNames = shardEntries.map { it.task }
        check(shardTaskNames.size == shardTaskNames.toSet().size) {
            "Duplicate CI `shard` tasks: $shardTaskNames"
        }
        val missingShards = bakeryShardTasks.toSet() - shardTaskNames.toSet()
        val unknownShards = shardTaskNames.toSet() - bakeryShardTasks.toSet()
        check(missingShards.isEmpty()) { "CI matrix is missing bakery shard(s): $missingShards (test fantôme)" }
        check(unknownShards.isEmpty()) { "CI matrix references unknown bakery shard(s): $unknownShards" }

        // 3. the merged-coverage job must exist and depend on the shard matrix.
        check(Regex("""(?m)^\s{2}bakery-coverage:""").containsMatchIn(text)) {
            "CI workflow is missing the `bakery-coverage` job that merges the bakery shard reports"
        }
        check(Regex("""(?m)^\s{4}needs:\s*borough-check""").containsMatchIn(text)) {
            "`bakery-coverage` must depend on the `borough-check` matrix (`needs: borough-check`)"
        }

        // 4. no unknown borough anywhere, and every testable borough is covered.
        val referencedBoroughs = entries.map { it.borough }.toSet()
        val orphan = referencedBoroughs - expectedBoroughs.keys
        check(orphan.isEmpty()) { "CI matrix references unknown borough(s): $orphan" }
        val covered = checkBoroughs.toSet() +
            if (shardEntries.isNotEmpty()) setOf(shardedBorough) else emptySet()
        check(covered == expectedBoroughs.keys) {
            "CI matrix coverage $covered != testable boroughs ${expectedBoroughs.keys}"
        }

        logger.lifecycle(
            "verifyCiMatrixCoverage OK — ${checkEntries.size} borough check(s), " +
                "${shardEntries.size} $shardedBorough shard(s), 1 coverage job"
        )
    }
}

// ── MEM-CI-CONSISTENCY US-3 / US-5 — cross-borough pin consistency guard ──────
// Both US are the same static relation, and both were root causes of the
// S-282→S-284 red CI:
//   • US-3 — a consumer root must never pin a version that is not published yet
//     (slider root pinned 0.0.20 while the published catalogue carried 0.0.19);
//   • US-5 — the catalogue must never be *stale* relative to what consumers pin
//     (catalogue carried codebase 0.0.11/0.0.14 while consumers used an API only
//     present in 0.0.17).
// This BOM *is* the single source of truth of the published versions, so the
// invariant is: every cross-borough pin found in a sibling build is <= the
// version published here. Pure text parsing (no network, no Gradle model), and
// siblings that are not checked out are skipped (each CI matrix job clones a
// single borough). The `-SNAPSHOT`/dev pins are rejected outright (US-3).
val crossBoroughPinFamilies = mapOf(
    // consumer toml key -> catalogue key (same artifact)
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
    "i18n-contracts" to "i18n-contracts",
    "agent-contracts" to "agent-contracts",
    "codebase-contracts" to "codebase-contracts",
    "llm-pool-contracts" to "llm-pool-contracts",
    "opencode-session-contracts" to "opencode-session-contracts",
    "pipeline-contracts" to "pipeline-contracts",
    "vibecoding-contracts" to "vibecoding-contracts",
)

fun parseVersionTable(file: java.io.File): Map<String, String> {
    if (!file.isFile) return emptyMap()
    var inVersions = false
    val table = mutableMapOf<String, String>()
    file.readLines().forEach { raw ->
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

/** Numeric rank of a dotted version, `null` when it is not a plain release. */
fun versionRank(version: String): List<Int>? {
    if (version.contains("SNAPSHOT", ignoreCase = true)) return null
    val core = version.substringBefore('-').substringBefore('+')
    return core.split('.').map { part -> part.toIntOrNull() ?: return null }
}

fun isNewer(candidate: String, reference: String): Boolean {
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

// The artefact a sibling build *produces* may legitimately run ahead of the
// published version (that is what a release session publishes). Only the
// *consumed* pins are constrained, so the own family of a build is skipped.
// The build identity is the directory owning `gradle/`, not the first path
// segment: consumer roots carry a `*-plugin/` sub-build (pattern 0124).
fun ownArtefactFamily(buildDirName: String): String? =
    when {
        buildDirName == "video-kernel-gradle" -> "video-kernel"
        buildDirName.endsWith("-gradle") -> buildDirName.removeSuffix("-gradle") + "-plugin"
        buildDirName in crossBoroughPinFamilies.values -> buildDirName
        else -> null
    }

val verifyCrossBoroughPins = tasks.register("verifyCrossBoroughPins") {
    group = "verification"
    description = "Fails if a sibling build pins a cross-borough version newer than the published catalogue (US-3/US-5)."
    val workspaceRoot = rootProject.projectDir.parentFile
    val catalogFile = layout.projectDirectory.file("gradle/libs.versions.toml").asFile
    val workspacePin = Regex("""workspace-(?:bom|catalog):([0-9][0-9A-Za-z.\-]*)""")
    doLast {
        val catalog = parseVersionTable(catalogFile)
        val publishedBom = catalog["workspace-bom"]
            ?: error("gradle/libs.versions.toml has no `workspace-bom` version")
        if (!workspaceRoot.isDirectory) {
            logger.lifecycle("verifyCrossBoroughPins — no sibling workspace, skipped")
            return@doLast
        }

        val violations = mutableListOf<String>()
        val skippedOwn = mutableSetOf<String>()
        var scannedTomls = 0

        val tomls = workspaceRoot.walkTopDown()
            .onEnter { dir ->
                dir.name != "build" && dir.name != ".gradle" && dir.name != "node_modules" &&
                    dir.name != "out" && dir.name != ".git"
            }
            .filter { it.name == "libs.versions.toml" && it.parentFile?.name == "gradle" }
            .toList()

        tomls.forEach { toml ->
            val relative = toml.relativeTo(workspaceRoot).invariantSeparatorsPath
            val buildDir = relative.substringBeforeLast("/gradle/").substringAfterLast('/')
            val ownFamily = ownArtefactFamily(buildDir)
            val versions = parseVersionTable(toml)

            versions.forEach { (key, value) ->
                val family = crossBoroughPinFamilies[key] ?: return@forEach
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
                    violations += "$relative: `$key = \"$value\"` is newer than the published " +
                        "catalogue `$family = \"$published\"` (catalogue stale or unpublished pin)"
                }
            }
            scannedTomls += 1
        }

        // Platform pins: a build may not consume a BOM/catalog snapshot newer than
        // the one published here.
        val gradleFiles = workspaceRoot.walkTopDown()
            .onEnter { dir ->
                dir.name != "build" && dir.name != ".gradle" && dir.name != "node_modules" &&
                    dir.name != "out" && dir.name != ".git"
            }
            .filter { it.name == "settings.gradle.kts" || it.name == "build.gradle.kts" }
            .toList()
        gradleFiles.forEach { file ->
            val relative = file.relativeTo(workspaceRoot).invariantSeparatorsPath
            if (relative.startsWith("workspace-bom/")) return@forEach
            workspacePin.findAll(file.readText()).forEach { match ->
                val pinned = match.groupValues[1]
                if (isNewer(pinned, publishedBom)) {
                    violations += "$relative: pins `${match.value}` newer than the published " +
                        "`workspace-bom = \"$publishedBom\"`"
                }
            }
        }

        check(violations.isEmpty()) {
            "Cross-borough pin consistency failed (MEM-CI-CONSISTENCY US-3/US-5):\n" +
                violations.joinToString("\n") { "  • $it" }
        }
        logger.lifecycle(
            "verifyCrossBoroughPins OK — $scannedTomls sibling version catalog(s) and " +
                "${gradleFiles.size} build script(s) within catalogue $publishedBom " +
                "(${skippedOwn.size} self-produced pin(s) skipped)"
        )
    }
}

tasks.named("check") {
    dependsOn(runAllTestsTask)
    dependsOn(verifyCiMatrixCoverage)
    dependsOn(verifyCrossBoroughPins)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
            pom {
                name.set("CCCP Education BOM")
                description.set("Bill of Materials for CCCP Education plugins — single source of truth for all shared dependencies")
                url.set("https://github.com/cccp-education/workspace-bom")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("cccp-education")
                        name.set("CCCP Education")
                        email.set("cccp.education@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:cccp-education/workspace-bom.git")
                    developerConnection.set("scm:git:git@github.com:cccp-education/workspace-bom.git")
                    url.set("https://github.com/cccp-education/workspace-bom")
                }
                project.findProperty("relocationGroup")?.let { targetGroup ->
                    withXml {
                        val pom = asElement()
                        val doc = pom.ownerDocument
                        val distMgmt = doc.createElement("distributionManagement")
                        val relocation = doc.createElement("relocation")
                        relocation.appendChild(doc.createElement("groupId")).also { it.textContent = targetGroup.toString() }
                        relocation.appendChild(doc.createElement("artifactId")).also { it.textContent = project.name }
                        distMgmt.appendChild(relocation)
                        pom.appendChild(distMgmt)
                    }
                }
            }
        }
    }
    repositories {
        mavenCentral()
    }
}

// ── MEM-CAT-2 — Publication du version catalog `workspace-catalog` (D1/D8/D9) ──
// Le toml `gradle/libs.versions.toml` est la source unique de vérité des versions
// cross-borough. Publié comme artefact Maven `education.cccp:workspace-catalog`,
// consommé via settings.gradle.kts : versionCatalogs { create("ws") { from(...) } }.
// Même cycle release que le BOM (même commit, même bundle nmcp).
publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            groupId = "education.cccp"
            artifactId = "workspace-catalog"
            from(components["versionCatalog"])
            pom {
                name.set("CCCP Education Workspace Catalog")
                description.set("Version catalog for CCCP Education plugins — single source of truth for cross-borough versions, consumed via settings.gradle.kts versionCatalogs { from(...) }")
                url.set("https://github.com/cccp-education/workspace-bom")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("cccp-education")
                        name.set("CCCP Education")
                        email.set("cccp.education@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:cccp-education/workspace-bom.git")
                    developerConnection.set("scm:git:git@github.com:cccp-education/workspace-bom.git")
                    url.set("https://github.com/cccp-education/workspace-bom")
                }
            }
        }
    }
}

signing {
    if (System.getenv("CI") != "true" && !version.toString().endsWith("-SNAPSHOT")) {
        sign(publishing.publications)
    }
    useGpgCmd()
}
