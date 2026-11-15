@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
// GBL-014 — the publication channel is a build property, never a branch.
val publicationChannel = providers.gradleProperty("channel").getOrElse("jitpack")
require(publicationChannel == "jitpack" || publicationChannel == "central") {
    "Unknown publication channel '$publicationChannel'. Expected 'central' or 'jitpack' (-Pchannel)."
}

if (publicationChannel == "jitpack") {
    // CNV-16 — JitPack mirror for the education.cccp.build.* conventions
    // (bypasses Sonatype / Gradle Plugin Portal). The JitPack tag is pinned while
    // the canonical plugin declarations may stay on the released version.
    pluginManagement.repositories.maven("https://jitpack.io")
    pluginManagement.resolutionStrategy.eachPlugin {
        if (requested.id.id.startsWith("education.cccp.build.")) {
            useModule("com.github.cccp-education.conventions-gradle:conventions-plugin:0.0.19")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
    id("com.gradleup.nmcp.settings").version("1.5.0")
}

// GBL-014 — channel projection of the version catalog. The root build never
// uses the auto-loaded `libs` catalog for the BOM: it consumes an explicit
// `bom` catalog, selected by `-Pchannel` (default `jitpack`). This is what lets
// the same commit publish a JitPack BOM and a Central BOM without mixing.
// The channel property itself is declared at the top of this file (GBL-014).

val globalProps = java.util.Properties().also {
    val globalFile = file(System.getProperty("user.home") + "/.gradle/gradle.properties")
    if (globalFile.exists()) it.load(globalFile.inputStream())
}

nmcpSettings {
    centralPortal {
        username = globalProps.getProperty("ossrhUsername") ?: ""
        password = globalProps.getProperty("ossrhPassword") ?: ""
        publishingType = "AUTOMATIC"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("bom") {
            from(
                files(
                    if (publicationChannel == "central") {
                        "gradle/central/libs.versions.toml"
                    } else {
                        "gradle/libs.versions.toml"
                    },
                ),
            )
        }
    }
}

rootProject.name = "workspace-bom"

if (publicationChannel == "jitpack") {
    dependencyResolutionManagement.repositories.maven("https://jitpack.io")
}
