import java.time.Duration

plugins {
    id("education.cccp.build.library") version "0.0.12"
    id("education.cccp.build.publishing") version "0.0.12"
    id("education.cccp.build.lint") version "0.0.12"
}

group = "education.cccp"
version = "0.0.2"

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(libs.bundles.cucumber)
}

val cucumberTest =
    tasks.register<Test>("cucumberTest") {
        description = "Runs Cucumber BDD tests (pipeline-contracts)"
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

publishingConventions {
    publicationType = "LIBRARY"
    website = "https://github.com/cccp-education/workspace-bom"
    vcsUrl = "https://github.com/cccp-education/workspace-bom"
    developerEmail = "cccp.education@gmail.com"
    pomName = "Pipeline Contracts"
    pomDescription =
        "N0 shared library — cross-borough release notes pipeline contracts (ConventionalCommit, ReleaseNotesConfig, GitLogParser, ReleaseNotesRenderer, ReleaseNotesGenerator)"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
