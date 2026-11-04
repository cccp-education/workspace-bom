import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import java.time.Duration

plugins {
    id("education.cccp.build.library") version "0.0.13"
    id("education.cccp.build.publishing") version "0.0.13"
    id("education.cccp.build.lint") version "0.0.13"
}

group = "education.cccp"
version = "0.0.4"

dependencies {
    api(libs.koog.agents)
    api(libs.kotlinx.serialization.json)

    testImplementation(libs.logback.classic)
    testImplementation(libs.bundles.cucumber)
}

val cucumberTest =
    tasks.register<Test>("cucumberTest") {
        description = "Runs Cucumber BDD tests (agent-contracts)"
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
        systemProperty("cucumber.filter.tags", "not @version-catalog and not @ci-shell-gates")
        maxHeapSize = "1g"
        maxParallelForks = 1
        forkEvery = 1
        jvmArgs("-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m", "-XX:TieredStopAtLevel=1")
        timeout.set(Duration.ofMinutes(5))

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = FULL
        }
        outputs.upToDateWhen { false }
    }

// ── MEM-CAT-5 — Runner Cucumber dédié @version-catalog (pattern S-082) ──
val versionCatalogCucumberTest =
    tasks.register<Test>("versionCatalogCucumberTest") {
        description = "Runs @version-catalog BDD scenarios (MEM-CAT-5)"
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
        systemProperty("cucumber.filter.tags", "@version-catalog")
        maxHeapSize = "1g"
        maxParallelForks = 1
        forkEvery = 1
        jvmArgs("-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m", "-XX:TieredStopAtLevel=1")
        timeout.set(Duration.ofMinutes(5))

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = FULL
        }
        outputs.upToDateWhen { false }
    }

// ── MEM-CI-GATE (suite S-338) — Runner Cucumber dédié @ci-shell-gates (pattern S-082) ──
val ciShellGatesCucumberTest =
    tasks.register<Test>("ciShellGatesCucumberTest") {
        description = "Runs @ci-shell-gates BDD scenarios (MEM-CI-GATE, S-338)"
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
        systemProperty("cucumber.filter.tags", "@ci-shell-gates")
        maxHeapSize = "1g"
        maxParallelForks = 1
        forkEvery = 1
        jvmArgs("-XX:+UseSerialGC", "-XX:MaxMetaspaceSize=256m", "-XX:TieredStopAtLevel=1")
        timeout.set(Duration.ofMinutes(5))

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
            exceptionFormat = FULL
        }
        outputs.upToDateWhen { false }
    }

// ── MEM-CI-GATE-2.5 (S-341) — audit à la demande de TOUT le workspace ──
// Pas de copie de l'audit dans les dépôts consommateurs (zéro split-brain) :
// une seule implémentation, exécutée à la clôture de session sur les dépôts
// voisins. `-PciRoot=<abs>` ; défaut = le dépôt workspace-bom.
val ciShellGatesReport =
    tasks.register<JavaExec>("ciShellGatesReport") {
        description = "Audits the CI shell exit codes of a workspace (MEM-CI-GATE-2.5)"
        group = "verification"
        classpath = sourceSets.test.get().runtimeClasspath
        mainClass.set("contracts.ci.CiShellAuditMain")
        args(project.findProperty("ciRoot")?.toString() ?: "..")
        outputs.upToDateWhen { false }
    }

tasks.named("check") {
    dependsOn(cucumberTest, versionCatalogCucumberTest, ciShellGatesCucumberTest)
}

publishingConventions {
    publicationType = "LIBRARY"
    createMavenPublication = true
    website = "https://github.com/cccp-education/workspace-bom"
    vcsUrl = "https://github.com/cccp-education/workspace-bom"
    developerEmail = "cccp.edu@gmail.com"
    pomName = "Agent Contracts"
    pomDescription = "N0 shared library — cross-borough agent contracts for koog orchestration"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
