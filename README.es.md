<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — Guía del Consumidor

> Bill of Materials para el ecosistema de plugins de CCCP Education — única fuente de verdad para las dependencias compartidas.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licencia)](./LICENSE)

- **Versión**: `0.0.1` · **Grupo**: `education.cccp` · **Artefacto**: `workspace-bom`
- **Tipo**: Java Platform (BOM) · **Publicado**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | **Español** | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | [Português](README.pt.md) | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## Qué hace

`workspace-bom` centraliza todas las versiones de dependencias compartidas entre los boroughs
de CCCP Education. Impórtalo en tu build para obtener versiones consistentes de
Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ y más.

También publica los **contratos N0** (`agent-contracts`, `codebase-contracts`,
`llm-pool-contracts`) consumidos por todos los boroughs N1–N4.

## Inicio rápido

### 1. Aplicar la plataforma

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. Usar dependencias gestionadas (sin versión)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // versión gestionada por la BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## Ámbitos gestionados

| Ámbito | Ejemplos |
|-------|----------|
| Kotlin y corrutinas | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| Base de datos | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| Procesamiento | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| Testing | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| Otros | `playwright` |

## Contratos N0 publicados vía esta BOM

| Contrato | Artefacto | Proporciona |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## Build y tests

```bash
./gradlew build                     # build (restricciones de plataforma)
./gradlew check                     # ejecuta runAllTests (todos los boroughs públicos)
./gradlew runAllTests               # ejecuta `./gradlew check` en cada borough
./gradlew publishToMavenLocal       # publicar localmente
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` itera sobre 13 boroughs públicos y ejecuta `./gradlew check` en cada uno
como un build independiente.

## Requisitos previos

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (para tests de boroughs que usan Testcontainers)

## CI / Publicación

Publicación vía NMCP (`com.gradleup.nmcp.settings` 1.5.0) configurada en
`settings.gradle.kts`. Credenciales leídas desde `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). Firmado con `useGpgCmd()`. El POM declara
Apache 2.0, desarrollador `cccp-education`, SCM a `github.com/cccp-education/workspace-bom`.

## Licencia

Apache License 2.0 — ver [LICENSE](./LICENSE).

---

_Parte del ecosistema CCCP Education — `groupId: education.cccp`._