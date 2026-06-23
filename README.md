<!-- master source — other languages are translations of this file -->
# workspace-bom — Consumer Guide

> Bill of Materials for the CCCP Education plugin ecosystem — single source of truth for shared dependencies.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **Version**: `0.0.1` · **Group**: `education.cccp` · **Artifact**: `workspace-bom`
- **Type**: Java Platform (BOM) · **Published**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: **EN** | [Français](README.fr.md)

---

## What it does

`workspace-bom` centralizes all shared dependency versions across the CCCP Education
boroughs. Import it in your build to get consistent versions of Kotlin, Jackson,
LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ and more.

It also ships **N0 contracts** (`agent-contracts`, `codebase-contracts`,
`llm-pool-contracts`) consumed by all N1–N4 boroughs.

## Quick Start

### 1. Apply the platform

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. Use managed dependencies (no version needed)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // version managed by BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## Managed scopes

| Scope | Examples |
|-------|----------|
| Kotlin & coroutines | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| Database | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| Processing | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| Testing | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| Other | `playwright` |

## N0 contracts published via this BOM

| Contract | Artifact | Provides |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## Build & test

```bash
./gradlew build                     # build (platform constraints)
./gradlew check                     # runs runAllTests (all public boroughs)
./gradlew runAllTests               # executes `./gradlew check` in each borough
./gradlew publishToMavenLocal       # publish locally
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` iterates over 13 public boroughs and runs `./gradlew check` in each
as an independent build.

## Prerequisites

- **Java** 24+ (Kotlin 2.3.20 toolchain)
- **Gradle** 9.5.1+
- **Docker** (for borough tests using Testcontainers)

## CI / Publication

Publication via NMCP (`com.gradleup.nmcp.settings` 1.5.0) configured in
`settings.gradle.kts`. Credentials read from `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). Signing uses `useGpgCmd()`. POM declares
Apache 2.0, developer `cccp-education`, SCM to `github.com/cccp-education/workspace-bom`.

## License

Apache License 2.0 — see [LICENSE](./LICENSE).

---

_Part of the CCCP Education ecosystem — `groupId: education.cccp`._