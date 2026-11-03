import java.time.Duration

plugins {
    signing
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
    id("education.cccp.build.lint") version "0.0.10"
}

group = "education.cccp"
version = "0.0.5"
kotlin.jvmToolchain(25)

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // MEM-3.5 — the N0 BDD suite (`i18n_contracts.feature`) mirrors the sibling
    // contract modules (runtime-contracts): Cucumber on the JUnit Platform.
    testImplementation(libs.assertj)
    testImplementation(libs.bundles.cucumber)
}

// MEM-3.5 — Cucumber BDD runner for the i18n contracts.
// Local = Build, Tests = CI: the cross-borough CI runs `check` in this
// standalone N0 build (`n0-contracts` matrix in workspace-bom), and `check`
// depends on this task so the feature is never a "test fantôme" (S-269).
val cucumberTest =
    tasks.register<Test>("cucumberTest") {
        description = "Runs Cucumber BDD tests (i18n-contracts)"
        group = "verification"
        testClassesDirs =
            sourceSets.test
                .get()
                .output.classesDirs
        classpath = configurations.testRuntimeClasspath.get() +
            sourceSets.test.get().output +
            sourceSets.main.get().output +
            files(tasks.jar.get().archiveFile)

        dependsOn(tasks.classes)
        useJUnitPlatform { excludeEngines("junit-jupiter") }
        systemProperty("cucumber.junit-platform.naming-strategy", "long")
        maxHeapSize = "1g"
        maxParallelForks = 1
        forkEvery = 1
        jvmArgs("-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m", "-XX:TieredStopAtLevel=1")
        timeout.set(Duration.ofMinutes(5))

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        outputs.upToDateWhen { false }
    }

tasks.named("check") {
    dependsOn(cucumberTest)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("i18n Contracts")
                description.set(
                    "N0 shared library — cross-borough internationalization contracts (SupportedLanguage, LanguageCatalog, I18nConfig, MessageBundle, LocaleResolver, TranslationService)",
                )
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
                    connection.set("https://github.com/cccp-education/workspace-bom.git")
                    developerConnection.set("https://github.com/cccp-education/workspace-bom.git")
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
