<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — Внутренности плагина

> Руководство для разработчиков и контрибьюторов Bill of Materials `workspace-bom`.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Лицензия)](./LICENSE)

- **Версия**: `0.0.1` · **Группа**: `education.cccp` · **Артефакт**: `workspace-bom`
- **Тип**: `java-platform` (Gradle) с `allowDependencies()` для ограничений `api`
- **Билд**: `./gradlew build` · **Тесты**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | **Русский** | [اردو](README.plugin.ur.md)

---

## Расположение модулей

`workspace-bom` — одномодульный проект Gradle `java-platform`. Все ограничения
находятся в `build.gradle.kts`. Контракты N0 — родственные модули, публикуемые
независимо и ссылаемые здесь:

```
workspace-bom/
├── build.gradle.kts              # Ограничения платформы + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, мигрирован в исходники codebase)
```

## Ограничения платформы

Все управляемые версии объявляются через ограничения `api(...)` внутри
`javaPlatform { allowDependencies() }`. Потребители добавляют BOM через `platform(...)`,
затем объявляют зависимости **без** версии.

Мигрированные/удалённые контракты (комментарии в `build.gradle.kts`):
- ~~`opencode-session-contracts`~~ — нулевое использование
- ~~`vibecoding-contracts`~~ — мигрирован в дерево исходников `codebase-gradle`
- ~~`i18n-contracts`~~ — мигрирован в `bakery-gradle`
- ~~`pipeline-contracts`~~ — мигрирован в `bakery-gradle`

## Задача runAllTests

`runAllTests` перебирает 13 публичных boroughs и запускает `./gradlew check` в каждом
как независимый билд Gradle (одна задача `Exec` на borough). Подключён к корневому `check`.

Тестируемые проекты (список `testableProjects` в `build.gradle.kts`):

| # | Borough |
|---|---------|
| 1 | `api-key-pool-gradle` |
| 2 | `bakery-gradle` |
| 3 | `capsule-gradle` |
| 4 | `codebase-gradle` |
| 5 | `codex-gradle` |
| 6 | `dashboard-gradle` |
| 7 | `graphify-gradle` |
| 8 | `hyperframes-gradle` |
| 9 | `jhipster-gradle-plugins` |
| 10 | `planner-gradle` |
| 11 | `plantuml-gradle` |
| 12 | `readme-gradle` |
| 13 | `slider-gradle` |

## Команды билда

```bash
./gradlew build                                       # ограничения платформы
./gradlew check                                       # запускает runAllTests
./gradlew runAllTests                                 # все 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # один borough
./gradlew publishToMavenLocal                          # локальная публикация
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## Публикация (NMCP)

Настроено через `com.gradleup.nmcp.settings` (1.5.0) в `settings.gradle.kts`.
Учётные данные читаются из `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. Подпись использует `useGpgCmd()`.
POM объявляет Apache 2.0, разработчик `cccp-education`, SCM на
`github.com/cccp-education/workspace-bom`.

## Предварительные требования

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (тесты boroughs используют Testcontainers)

## Документация по архитектуре

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — Поток зависимостей N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & roadmap (MEMPHIS)

## Лицензия

Apache License 2.0 — см. [LICENSE](./LICENSE).

---

_Часть экосистемы CCCP Education — `groupId: education.cccp`._