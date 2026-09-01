

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

        // ── Internal education.cccp plugins (N2) — pilotés par le BOM ───────
        api(libs.bakery.plugin)
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
val testableProjects = listOf(
    "api-key-pool-gradle",
    "bakery-gradle",
    "dashboard-gradle",
    "planner-gradle",
    "slider-gradle"
)

val runAllTestsTask = tasks.register("runAllTests") {
    group = "verification"
    description = "Runs tests for all public boroughs (each as independent Gradle build)"
}

testableProjects.forEach { projectName ->
    val projectDir = rootProject.projectDir.parentFile.resolve(projectName)
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

tasks.named("check") {
    dependsOn(runAllTestsTask)
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
