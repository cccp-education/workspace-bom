plugins {
    signing
    `java-platform`
    `maven-publish`
}

group = "education.cccp"
version = "0.0.1"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        // ── Kotlin ──────────────────────────────────────────────────────────
        api("org.jetbrains.kotlin:kotlin-stdlib:2.3.20")
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

        // ── Jackson ─────────────────────────────────────────────────────────
        api("com.fasterxml.jackson.core:jackson-databind:2.21.2")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.2")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.2")

        // ── LangChain4j ─────────────────────────────────────────────────────
        api("dev.langchain4j:langchain4j:1.16.3")
        api("dev.langchain4j:langchain4j-ollama:1.16.3")
        api("dev.langchain4j:langchain4j-open-ai:1.16.3")
        api("dev.langchain4j:langchain4j-google-ai-gemini:1.16.3")
        api("dev.langchain4j:langchain4j-mistral-ai:1.16.3")
        api("dev.langchain4j:langchain4j-anthropic:1.16.3")
        api("dev.langchain4j:langchain4j-pgvector:1.16.3-beta26")
        api("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.16.3-beta26")
        api("dev.langchain4j:langchain4j-hugging-face:1.16.3-beta26")

        // ── Koog ────────────────────────────────────────────────────────────
        api("ai.koog:koog-agents:1.0.0")

        // ── Logging ─────────────────────────────────────────────────────────
        api("ch.qos.logback:logback-classic:1.5.32")
        api("org.slf4j:slf4j-api:2.0.17")

        // ── Database ────────────────────────────────────────────────────────
        api("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
        api("io.r2dbc:r2dbc-pool:1.0.2.RELEASE")
        api("io.r2dbc:r2dbc-spi:1.0.0.RELEASE")
        api("org.postgresql:postgresql:42.7.4")

        // ── Processing ──────────────────────────────────────────────────────
        api("org.mapstruct:mapstruct:1.6.3")
        api("io.arrow-kt:arrow-core:2.2.2")
        api("io.arrow-kt:arrow-fx-coroutines:2.2.2")
        api("io.arrow-kt:arrow-integrations-jackson-module:0.15.1")
        api("commons-io:commons-io:2.13.0")

        // ── Tests ───────────────────────────────────────────────────────────
        api("org.junit.jupiter:junit-jupiter:5.12.2")
        api("org.junit.platform:junit-platform-launcher:1.14.3")
        api("org.junit.platform:junit-platform-suite:1.14.3")
        api("io.cucumber:cucumber-java:7.34.3")
        api("io.cucumber:cucumber-junit-platform-engine:7.34.3")
        api("io.cucumber:cucumber-picocontainer:7.34.3")
        api("org.testcontainers:postgresql:1.21.4")
        api("org.testcontainers:junit-jupiter:1.21.4")
        api("org.mockito.kotlin:mockito-kotlin:6.2.3")
        api("org.mockito:mockito-junit-jupiter:5.23.0")
        api("org.assertj:assertj-core:3.25.3")

        // ── Docker / Infrastructure ─────────────────────────────────────────
        api("com.github.docker-java:docker-java-core:3.7.0")
        api("com.github.docker-java:docker-java-transport-httpclient5:3.7.0")

        // ── Git ─────────────────────────────────────────────────────────────
        api("org.eclipse.jgit:org.eclipse.jgit:7.5.0.202512021534-r")
        api("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:7.5.0.202512021534-r")
        api("org.eclipse.jgit:org.eclipse.jgit.archive:7.5.0.202512021534-r")
        api("org.tukaani:xz:1.12")

        // ── Asciidoctor ─────────────────────────────────────────────────────
        api("org.asciidoctor:asciidoctorj:3.0.0")
        api("org.asciidoctor:asciidoctorj-diagram:3.2.0")
        api("org.asciidoctor:asciidoctorj-diagram-plantuml:1.2025.3")

        // ── PDF/Extraction ──────────────────────────────────────────────────
        api("org.apache.pdfbox:pdfbox:3.0.4")
        api("org.apache.tika:tika-core:3.1.0")
        api("com.vladsch.flexmark:flexmark-all:0.64.8")

        // ── Other ───────────────────────────────────────────────────────────
        api("com.microsoft.playwright:playwright:1.52.0")

        // ── Internal education.cccp contracts ───────────────────────────────
        // DELETE opencode-session-contracts (zéro usage)
        // DELETE vibecoding-contracts (migré dans codebase-gradle)
        // DELETE i18n-contracts (migré dans bakery-gradle)
        // DELETE pipeline-contracts (migré dans bakery-gradle)
        // GARDER SEULEMENT agent-contracts, codebase-contracts, llm-pool-contracts
    }
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

signing {
    if (System.getenv("CI") != "true" && !version.toString().endsWith("-SNAPSHOT")) {
        sign(publishing.publications)
    }
    useGpgCmd()
}
