plugins {
    `java-platform`
    `maven-publish`
}

group = "cccp.education"
version = "0.1.0"

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-stdlib:2.3.20")
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")

        api("com.fasterxml.jackson.core:jackson-databind:2.21.2")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.2")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")

        api("dev.langchain4j:langchain4j:1.14.1")
        api("dev.langchain4j:langchain4j-pgvector:1.14.1-beta24")
        api("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.14.1-beta24")
        api("dev.langchain4j:langchain4j-ollama:1.14.1")

        api("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
        api("io.r2dbc:r2dbc-pool:1.0.2.RELEASE")
        api("io.r2dbc:r2dbc-spi:1.0.0.RELEASE")

        api("ch.qos.logback:logback-classic:1.5.32")

        api("org.mapstruct:mapstruct:1.6.3")
        api("io.arrow-kt:arrow-core:2.2.2")
        api("io.arrow-kt:arrow-fx-coroutines:2.2.2")

        api("org.junit.jupiter:junit-jupiter:5.12.2")
        api("org.junit.platform:junit-platform-suite:1.14.3")
        api("io.cucumber:cucumber-java:7.34.3")
        api("io.cucumber:cucumber-junit-platform-engine:7.34.3")
        api("io.cucumber:cucumber-picocontainer:7.34.3")
        api("org.testcontainers:postgresql:1.21.4")
        api("org.testcontainers:junit-jupiter:1.21.4")

        api("org.asciidoctor:asciidoctorj:3.0.0")
        api("org.apache.pdfbox:pdfbox:3.0.4")
        api("org.apache.tika:tika-core:3.1.0")
        api("com.vladsch.flexmark:flexmark-all:0.64.8")

        api("com.microsoft.playwright:playwright:1.52.0")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
            pom {
                name.set("CCCP Education BOM")
                description.set("Bill of Materials for CCCP Education plugins")
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
    repositories {
        maven {
            name = "local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
