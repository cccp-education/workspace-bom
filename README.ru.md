<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — Руководство потребителя

> Bill of Materials для экосистемы плагинов CCCP Education — единый источник истины для общих зависимостей.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Лицензия)](./LICENSE)

- **Версия**: `0.0.1` · **Группа**: `education.cccp` · **Артефакт**: `workspace-bom`
- **Тип**: Java Platform (BOM) · **Опубликовано**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | [Português](README.pt.md) | **Русский** | [اردو](README.ur.md)

---

## Что делает

`workspace-bom` централизует все версии общих зависимостей между boroughs CCCP
Education. Импортируйте его в свой билд для получения согласованных версий
Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ и др.

Он также публикует **контракты N0** (`agent-contracts`, `codebase-contracts`,
`llm-pool-contracts`), используемые всеми boroughs N1–N4.

## Быстрый старт

### 1. Применить платформу

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. Использовать управляемые зависимости (без версии)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // версия управляется BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## Управляемые области

| Область | Примеры |
|-------|----------|
| Kotlin и корутины | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| База данных | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| Обработка | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| Тестирование | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| Прочее | `playwright` |

## Контракты N0, публикуемые через эту BOM

| Контракт | Артефакт | Предоставляет |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## Билд и тесты

```bash
./gradlew build                     # билд (ограничения платформы)
./gradlew check                     # запускает runAllTests (все публичные boroughs)
./gradlew runAllTests               # выполняет `./gradlew check` в каждом borough
./gradlew publishToMavenLocal       # локальная публикация
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` перебирает 13 публичных boroughs и запускает `./gradlew check` в каждом
как независимый билд.

## Предварительные требования

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (для тестов boroughs, использующих Testcontainers)

## CI / Публикация

Публикация через NMCP (`com.gradleup.nmcp.settings` 1.5.0), настроенный в
`settings.gradle.kts`. Учётные данные читаются из `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). Подпись использует `useGpgCmd()`. POM объявляет
Apache 2.0, разработчик `cccp-education`, SCM на `github.com/cccp-education/workspace-bom`.

## Лицензия

Apache License 2.0 — см. [LICENSE](./LICENSE).

---

_Часть экосистемы CCCP Education — `groupId: education.cccp`._