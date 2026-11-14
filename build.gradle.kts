

plugins {
    signing
    `java-platform`
    `maven-publish`
}

group = "education.cccp"
version = libs.versions.workspace.bom.get()

javaPlatform {
    allowDependencies()
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
    api(platform(bom.arrow.stack))

    constraints {
        // ── Kotlin ──────────────────────────────────────────────────────────
        api(bom.kotlin.stdlib)
        // MEMPHIS-BOM-FIX — kotlin-stdlib-jdk8 is consumed WITHOUT a version by
        // document-gradle (`implementation(kotlin("stdlib-jdk8"))` + transitive
        // resolution). Central rejects a versionless runtime dependency
        // ("Dependency version information is missing"), so the BOM must pin it
        // exactly like kotlin-stdlib. Aliased and versioned by the toml (same
        // `kotlin` version ref).
        api(bom.kotlin.stdlib.jdk8)
        api(bom.kotlinx.serialization.json)
        api(bom.kotlinx.coroutines.core)
        api(bom.kotlinx.coroutines.reactive)
        api(bom.kotlinx.coroutines.jdk8)
        api(bom.kotlinx.coroutines.test)

        // ── Jackson ─────────────────────────────────────────────────────────
        api(bom.jackson.databind)
        api(bom.jackson.dataformat.yaml)
        api(bom.jackson.module.kotlin)
        api(bom.jackson.datatype.jsr310)

        // ── LangChain4j ─────────────────────────────────────────────────────
        api(bom.langchain4j)
        api(bom.langchain4j.ollama)
        api(bom.langchain4j.open.ai)
        api(bom.langchain4j.google.ai.gemini)
        api(bom.langchain4j.mistral)
        api(bom.langchain4j.anthropic)
        api(bom.langchain4j.pgvector)
        api(bom.langchain4j.minilm)
        api(bom.langchain4j.hugging.face)

        // ── Koog ────────────────────────────────────────────────────────────
        api(bom.koog.agents)

        // ── Logging ─────────────────────────────────────────────────────────
        api(bom.logback.classic)
        api(bom.slf4j.api)

        // ── Database ────────────────────────────────────────────────────────
        api(bom.r2dbc.postgresql)
        api(bom.r2dbc.pool)
        api(bom.r2dbc.spi)
        api(bom.postgresql.jdbc)

        // ── Processing ──────────────────────────────────────────────────────
        api(bom.mapstruct)
        api(bom.arrow.core)
        api(bom.arrow.fx.coroutines)
        api(bom.arrow.jackson)
        api(bom.commons.io)

        // ── Tests ───────────────────────────────────────────────────────────
        api(bom.junit.jupiter)
        api(bom.junit.platform.launcher)
        api(bom.junit.platform.suite)
        api(bom.cucumber.java)
        api(bom.cucumber.junit.platform.engine)
        api(bom.cucumber.picocontainer)
        api(bom.testcontainers.postgresql)
        api(bom.testcontainers.junit5)
        api(bom.mockito.kotlin)
        api(bom.mockito.junit.jupiter)
        api(bom.assertj)

        // ── Docker / Infrastructure ─────────────────────────────────────────
        api(bom.docker.java.core)
        api(bom.docker.java.transport.httpclient5)

        // ── Git ─────────────────────────────────────────────────────────────
        api(bom.jgit.core)
        api(bom.jgit.ssh)
        api(bom.jgit.archive)
        api(bom.xz)

        // ── Asciidoctor ─────────────────────────────────────────────────────
        api(bom.asciidoctorj)
        api(bom.asciidoctorj.diagram)
        api(bom.asciidoctorj.diagram.plantuml)
        api(libs.asciidoctorj.epub3)

        // ── PDF/Extraction ──────────────────────────────────────────────────
        api(bom.pdfbox)
        api(bom.tika.core)
        api(bom.flexmark.all)

        // ── Other ───────────────────────────────────────────────────────────
        api(bom.playwright)

        // ── Internal education.cccp contracts (N0) — source unique de vérité ──
        api(bom.agent.contracts)
        api(bom.codebase.contracts)
        api(bom.llm.pool.contracts)
        api(bom.opencode.session.contracts)
        api(bom.i18n.contracts)
        api(bom.pipeline.contracts)
        api(bom.runtime.contracts)
        api(bom.ocr.contracts)
        api(bom.plantuml.contracts)
        api(bom.catalog.contracts)

        // ── Internal education.cccp plugins (N2) — pilotés par le BOM ───────
        api(bom.bakery.plugin)
        api(bom.capsule.plugin)
        api(bom.codex.plugin)
        api(bom.planner.plugin)
        api(bom.slider.plugin)
        api(bom.plantuml.plugin)
        api(bom.readme.plugin)
        api(bom.hyperframes.plugin)
        api(bom.graphify.plugin)
        api(bom.api.key.pool.plugin)
        api(bom.codebase.plugin)
        api(bom.conventions.plugin)
        // document-plugin : publié Central (S-241), consommé par bakery sans version (MEM-CAT-3)
        api(bom.document.plugin)

        // ── Shared N1 libraries (education.cccp) — noyau vidéo neutre ────────
        api(bom.video.kernel)
        api(bom.youtube.auth.plugin)

        // ── Force resolution (koog 26.0.2-1 vs testcontainers 17.0.0) ─────────
        api(bom.jetbrains.annotations)
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
// Value = build directory relative to the borough root. bakery-gradle and
// dashboard-gradle are pattern-0124 consumer roots (no `check` task); their
// build is the standalone plugin sub-build, so the check runs there.
val testableProjects = mapOf(
    "api-key-pool-gradle" to ".",
    "bakery-gradle" to "bakery-plugin",
    "dashboard-gradle" to "dashboard-plugin",
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


// MEM-CI-CONSISTENCY US-3/US-5 — the cross-borough pin guard moved to
// `agent-contracts` (`verifyCrossBoroughPinsReport`, pattern S-341), where its
// algorithm is unit-tested and the `v`-prefixed pin blind spot is fixed. It
// stays a workspace-local, on-demand check: a CI matrix job clones a single
// borough, so it has no siblings to scan.
tasks.named("check") {
    dependsOn(runAllTestsTask)
    dependsOn(verifyCiMatrixCoverage)
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
// Le toml sélectionné par `-Pchannel` est la source unique de vérité des versions
// cross-borough. Publié comme artefact Maven `education.cccp:workspace-catalog`,
// consommé via settings.gradle.kts : versionCatalogs { create("ws") { from(...) } }.
// Même cycle release que le BOM (même commit, même bundle nmcp). GBL-014 : le
// artefact publié est la projection du canal (JitPack ou Central), jamais le
// catalogue de l'autre canal.
val publicationChannel = providers.gradleProperty("channel").getOrElse("jitpack")
val selectedCatalogue = layout.projectDirectory.file(
    if (publicationChannel == "central") {
        "gradle/central/libs.versions.toml"
    } else {
        "gradle/libs.versions.toml"
    },
)
publishing {
    publications {
        create<MavenPublication>("workspaceCatalog") {
            groupId = "education.cccp"
            artifactId = "workspace-catalog"
            artifact(selectedCatalogue) { extension = "toml" }
            pom {
                packaging = "toml"
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

// ── GBL-014-3 — channel projection of the version catalog ────────────────────
// One source of versions, two channel projections:
//   * JitPack (historical file, `com.github.cccp-education.*`);
//   * Central (generated file, `education.cccp.*` / `education.cccp.build.*`).
// `generateCentralCatalogue` materialises the Central projection; the checked-in
// file is proven by `verifyCatalogueChannels` (drift + no coordinate mixing).
val jitpackCatalogue = layout.projectDirectory.file("gradle/libs.versions.toml")
val centralCatalogue = layout.projectDirectory.file("gradle/central/libs.versions.toml")

val moduleLine = Regex("""^(\s*[A-Za-z0-9_.-]+\s*=\s*\{\s*module\s*=\s*)"([^"]+)"(.*)$""")
val versionLine = Regex("""^(\s*[A-Za-z0-9_.-]+\s*=\s*)"v(\d[^"]*)"(\s*)$""")

fun transformSection(text: String, section: String, transform: (String) -> String): String {
    val lines = text.lines().toMutableList()
    var inside = false
    for (index in lines.indices) {
        val trimmed = lines[index].trim()
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            inside = trimmed == "[$section]"
            continue
        }
        if (inside && trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
            lines[index] = transform(lines[index])
        }
    }
    return lines.joinToString("\n")
}

fun centralCoordinate(coordinate: String): String {
    val parts = coordinate.split(":")
    if (parts.size < 2) return coordinate
    val group = parts[0]
    val artifact = parts[1]
    if (!group.startsWith("com.github.cccp-education")) return coordinate
    return when {
        group == "com.github.cccp-education.workspace-bom" -> "education.cccp:$artifact"
        group == "com.github.cccp-education.conventions-gradle" -> "education.cccp.build:$artifact"
        group == "com.github.cccp-education" && artifact == "video-kernel-gradle" -> "education.cccp:video-kernel"
        group == "com.github.cccp-education" && artifact == "api-key-pool-gradle" -> "education.cccp:api-key-pool"
        else -> "education.cccp:$artifact"
    }
}

fun projectCatalogue(text: String): String {
    val versions = transformSection(text, "versions") { line ->
        line.replace(versionLine) { match ->
            "${match.groupValues[1]}\"${match.groupValues[2]}\"${match.groupValues[3]}"
        }
    }
    return transformSection(versions, "libraries") { line ->
        line.replace(moduleLine) { match ->
            "${match.groupValues[1]}\"${centralCoordinate(match.groupValues[2])}\"${match.groupValues[3]}"
        }
    }
}

fun sectionText(text: String, section: String): String =
    text.lines()
        .dropWhile { it.trim() != "[$section]" }
        .drop(1)
        .takeWhile { !it.trim().startsWith("[") }
        .joinToString("\n")

fun moduleCoordinates(text: String): List<String> =
    moduleLine.findAll(text).map { it.groupValues[2] }.toList()

fun catalogueFindings(jitpackText: String, centralText: String): List<String> {
    val findings = mutableListOf<String>()
    if (projectCatalogue(jitpackText) != centralText) {
        findings += "gradle/central/libs.versions.toml drifts from gradle/libs.versions.toml " +
            "(run ./gradlew generateCentralCatalogue)"
    }
    val jitpackModules = moduleCoordinates(jitpackText)
    val centralModules = moduleCoordinates(centralText)
    jitpackModules.filter { it.startsWith("education.cccp") }.forEach {
        findings += "JitPack catalogue contains a Central coordinate: $it"
    }
    centralModules.filter { it.startsWith("com.github.cccp-education") }.forEach {
        findings += "Central catalogue contains a JitPack coordinate: $it"
    }
    if (sectionText(projectCatalogue(jitpackText), "versions") != sectionText(centralText, "versions")) {
        findings += "the [versions] blocks of the two catalogues differ (single source violated)"
    }
    return findings
}

tasks.register("generateCentralCatalogue") {
    group = "catalogue"
    description = "GBL-014-3 — projects the Central version catalog from the JitPack catalogue"
    inputs.file(jitpackCatalogue)
    outputs.file(centralCatalogue)
    doLast {
        val target = centralCatalogue.asFile
        target.parentFile.mkdirs()
        target.writeText(projectCatalogue(jitpackCatalogue.asFile.readText()))
        println("CATALOGUE GENERATED: ${target.relativeTo(projectDir)}")
    }
}

val verifyCatalogueChannels = tasks.register("verifyCatalogueChannels") {
    group = "verification"
    description = "GBL-014-3 — checks catalogue drift and forbids coordinate mixing between channels"
    inputs.files(jitpackCatalogue, centralCatalogue)
    outputs.upToDateWhen { false }
    doLast {
        if (!centralCatalogue.asFile.isFile) {
            throw GradleException(
                "Missing ${centralCatalogue.asFile.relativeTo(projectDir)} — " +
                    "run ./gradlew generateCentralCatalogue"
            )
        }
        val findings = catalogueFindings(
            jitpackCatalogue.asFile.readText(),
            centralCatalogue.asFile.readText(),
        )
        if (findings.isNotEmpty()) {
            findings.forEach { println("  - $it") }
            throw GradleException("${findings.size} catalogue channel finding(s)")
        }
        println("CATALOGUE OK: JitPack and Central projections are consistent and unmixed")
    }
}

val verifyBomChannel = tasks.register("verifyBomChannel") {
    group = "verification"
    description =
        "GBL-014-3b — the generated BOM POM must only contain the selected channel's coordinates"
    dependsOn("generatePomFileForMavenPublication")
    inputs.file(layout.buildDirectory.file("publications/maven/pom-default.xml"))
    outputs.upToDateWhen { false }
    doLast {
        val pom = layout.buildDirectory.file("publications/maven/pom-default.xml")
            .get().asFile.readText()
        val dependencies = pom
            .substringAfter("<dependencyManagement>")
            .substringBefore("</dependencyManagement>")
        val forbidden =
            if (publicationChannel == "central") {
                "com.github.cccp-education"
            } else {
                "education.cccp"
            }
        if (dependencies.contains(forbidden)) {
            throw GradleException(
                "BOM channel=$publicationChannel contains a forbidden '$forbidden' coordinate " +
                    "in <dependencyManagement>"
            )
        }
        println("BOM OK: channel=$publicationChannel, no '$forbidden' coordinate")
    }
}

tasks.matching { it.name == "check" }.configureEach {
    dependsOn(verifyCatalogueChannels, verifyBomChannel)
}
