import java.time.Duration

plugins {
    id("education.cccp.build.library") version "0.0.13"
    id("education.cccp.build.publishing") version "0.0.13"
    id("education.cccp.build.lint") version "0.0.13"
}

group = "education.cccp"
version = "0.0.5"

dependencies {
    implementation(kotlin("stdlib"))

    // MEM-3.5 — the N0 BDD suite (`i18n_contracts.feature`) mirrors the sibling
    // contract modules (runtime-contracts): Cucumber on the JUnit Platform.
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

publishingConventions {
    publicationType = "LIBRARY"
    createMavenPublication = true
    website = "https://github.com/cccp-education/workspace-bom"
    vcsUrl = "https://github.com/cccp-education/workspace-bom"
    developerEmail = "cccp.education@gmail.com"
    pomName = "i18n Contracts"
    pomDescription =
        "N0 shared library — cross-borough internationalization contracts (SupportedLanguage, LanguageCatalog, I18nConfig, MessageBundle, LocaleResolver, TranslationService)"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
