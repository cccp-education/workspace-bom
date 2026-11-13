plugins {
    id("education.cccp.build.library") version "0.0.14"
    id("education.cccp.build.publishing") version "0.0.14"
    id("education.cccp.build.lint") version "0.0.14"
}

group = "education.cccp"
version = providers.gradleProperty("version").getOrElse("0.0.1")

dependencies {
    implementation(kotlin("stdlib"))
}

publishingConventions {
    publicationType = "LIBRARY"
    createMavenPublication = true
    website = "https://github.com/cccp-education/workspace-bom"
    vcsUrl = "https://github.com/cccp-education/workspace-bom"
    developerEmail = "cccp.education@gmail.com"
    pomName = "Catalog Contracts"
    pomDescription = "N0 shared library — section-scoped Gradle version catalog reader (VersionCatalogToml) used by cross-borough publication hygiene guards"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
