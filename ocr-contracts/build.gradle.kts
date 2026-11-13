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
    pomName = "OCR Contracts"
    pomDescription = "N0 shared library — cross-borough OCR port contracts (OcrEngine, OcrRequest, OcrResult)"
    val relocationGroup = project.findProperty("relocationGroup")?.toString()
    if (relocationGroup != null) {
        relocationGroupId = relocationGroup
        relocationArtifactId = project.name
    }
}
