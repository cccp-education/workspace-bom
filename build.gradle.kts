

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

// ── Cross-borough CI shards (S-298) — workspace-bom ↔ bakery coupling ────────
// The cross-borough CI no longer runs bakery's monolithic `check`; it runs the
// 16 shard tasks defined in bakery-gradle/bakery-plugin/build.gradle.kts and
// mirrored in bakery-gradle/.github/workflows/test.yml (S-259). This list is the
// *assumed* coupling (workspace-bom invokes bakery task names by string): it lets
// the guard assert the workspace-bom matrix invokes EXACTLY these tasks,
// exhaustively — a shard dropped from the matrix would silently stop testing that
// slice (anti "test fantôme", pattern S-269). Keep in sync with
// bakery-gradle/bakery-plugin/build.gradle.kts.
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
    "cucumberTestShardI18nMassPipeline",
    "nodeTest"
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
