plugins {
    `java-library`
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
}

group = "education.cccp"
version = "0.1.0"
kotlin.jvmToolchain(24)

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // koog orchestrateur
    implementation("ai.koog:koog-agents:0.8.0")

    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.2")

    // Tests
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("Vibecoding Contracts")
                description.set("Shared contracts for vibecoding agent (data classes, tools, registry) — N0 neutral")
                url.set("https://github.com/cccp-education/workspace-bom")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
