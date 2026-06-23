<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — Guia do Consumidor

> Bill of Materials para o ecossistema de plugins CCCP Education — fonte única de verdade para dependências compartilhadas.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licença)](./LICENSE)

- **Versão**: `0.0.1` · **Grupo**: `education.cccp` · **Artefato**: `workspace-bom`
- **Tipo**: Java Platform (BOM) · **Publicado**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | **Português** | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## O que faz

`workspace-bom` centraliza todas as versões de dependências compartilhadas entre os boroughs
do CCCP Education. Importe-a no seu build para obter versões consistentes de
Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ e mais.

Ela também publica os **contratos N0** (`agent-contracts`, `codebase-contracts`,
`llm-pool-contracts`) consumidos por todos os boroughs N1–N4.

## Início rápido

### 1. Aplicar a plataforma

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. Usar dependências gerenciadas (sem versão)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // versão gerenciada pela BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## Escopos gerenciados

| Escopo | Exemplos |
|-------|----------|
| Kotlin e corrotinas | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| Banco de dados | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| Processamento | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| Testes | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| Outros | `playwright` |

## Contratos N0 publicados via esta BOM

| Contrato | Artefato | Fornece |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## Build e testes

```bash
./gradlew build                     # build (restrições de plataforma)
./gradlew check                     # executa runAllTests (todos os boroughs públicos)
./gradlew runAllTests               # executa `./gradlew check` em cada borough
./gradlew publishToMavenLocal       # publicar localmente
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` itera sobre 13 boroughs públicos e executa `./gradlew check` em cada um
como um build independente.

## Pré-requisitos

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (para testes de boroughs que usam Testcontainers)

## CI / Publicação

Publicação via NMCP (`com.gradleup.nmcp.settings` 1.5.0) configurada em
`settings.gradle.kts`. Credenciais lidas de `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). Assinatura usa `useGpgCmd()`. O POM declara
Apache 2.0, desenvolvedor `cccp-education`, SCM para `github.com/cccp-education/workspace-bom`.

## Licença

Apache License 2.0 — ver [LICENSE](./LICENSE).

---

_Parte do ecossistema CCCP Education — `groupId: education.cccp`._