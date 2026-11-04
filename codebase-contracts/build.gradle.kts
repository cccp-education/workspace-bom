plugins {
    id("education.cccp.build.library") version "0.0.12"
    id("education.cccp.build.publishing") version "0.0.12"
    id("education.cccp.build.lint") version "0.0.12"
}

group = "education.cccp"
version = "0.0.2"

dependencies {
    // Zéro dépendance sauf kotlin-stdlib — contrats purs N0
    implementation(kotlin("stdlib"))
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
    developerEmail = "cccp.edu@gmail.com"
    pomName = "Codebase Contracts"
    pomDescription = "N0 shared library — cross-borough context contracts (ContextChannel, ChannelBudget, CompositeContext)"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
