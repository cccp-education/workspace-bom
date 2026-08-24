import java.time.Duration

plugins {
    signing
    `java-library`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}

group = "education.cccp"
version = "0.0.1"
kotlin.jvmToolchain(JavaVersion.VERSION_25.ordinal)

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
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
                name.set("OCR Contracts")
                description.set("N0 shared library — cross-borough OCR port contracts (OcrEngine, OcrRequest, OcrResult)")
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