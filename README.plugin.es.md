<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — Internos del Plugin

> Guía de desarrollo y contribución para el Bill of Materials `workspace-bom`.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licencia)](./LICENSE)

- **Versión**: `0.0.1` · **Grupo**: `education.cccp` · **Artefacto**: `workspace-bom`
- **Tipo**: `java-platform` (Gradle) con `allowDependencies()` para restricciones `api`
- **Build**: `./gradlew build` · **Tests**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | **Español** | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## Disposición de módulos

`workspace-bom` es un proyecto Gradle `java-platform` de módulo único. Todas las restricciones
están en `build.gradle.kts`. Los contratos N0 son módulos hermanos publicados
independientemente y referenciados aquí:

```
workspace-bom/
├── build.gradle.kts              # Restricciones de plataforma + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, migrado al fuente de codebase)
```

## Restricciones de plataforma

Todas las versiones gestionadas se declaran vía restricciones `api(...)` dentro de
`javaPlatform { allowDependencies() }`. Los consumidores añaden la BOM vía `platform(...)`
y luego declaran dependencias **sin** versión.

Contratos migrados/eliminados (comentarios en `build.gradle.kts`):
- ~~`opencode-session-contracts`~~ — cero uso
- ~~`vibecoding-contracts`~~ — migrado al árbol fuente de `codebase-gradle`
- ~~`i18n-contracts`~~ — migrado a `bakery-gradle`
- ~~`pipeline-contracts`~~ — migrado a `bakery-gradle`

## Tarea runAllTests

`runAllTests` itera sobre 13 boroughs públicos y ejecuta `./gradlew check` en cada uno
como un build Gradle independiente (una tarea `Exec` por borough). Conectado al `check` raíz.

Proyectos testeables (lista `testableProjects` en `build.gradle.kts`):

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

## Comandos de build

```bash
./gradlew build                                       # restricciones de plataforma
./gradlew check                                       # dispara runAllTests
./gradlew runAllTests                                 # los 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # un solo borough
./gradlew publishToMavenLocal                          # publicación local
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## Publicación (NMCP)

Configurada vía `com.gradleup.nmcp.settings` (1.5.0) en `settings.gradle.kts`.
Credenciales leídas de `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. Firmado con `useGpgCmd()`.
El POM declara Apache 2.0, desarrollador `cccp-education`, SCM a
`github.com/cccp-education/workspace-bom`.

## Requisitos previos

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (los tests de boroughs usan Testcontainers)

## Docs de arquitectura

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — Flujo de dependencias N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & roadmap (MEMPHIS)

## Licencia

Apache License 2.0 — ver [LICENSE](./LICENSE).

---

_Parte del ecosistema CCCP Education — `groupId: education.cccp`._