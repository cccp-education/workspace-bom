pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention").version("0.9.0") }

rootProject.name = "agent-contracts"
