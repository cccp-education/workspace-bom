<!-- master source — other languages are translations of this file -->
# workspace-bom — Plugin Internals

> Developer & contributor guide for the `workspace-bom` Bill of Materials.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **Version**: `0.0.1` · **Group**: `education.cccp` · **Artifact**: `workspace-bom`
- **Type**: `java-platform` (Gradle) with `allowDependencies()` for `api` constraints
- **Build**: `./gradlew build` · **Tests**: `./gradlew runAllTests`

🌐 Languages: **EN** | [Français](README.fr.md)

---

## Module layout

`workspace-bom` is a single-module Gradle `java-platform` project. All constraints live in
`build.gradle.kts`. N0 contracts are sibling modules published independently and
referenced here:

```
workspace-bom/
├── build.gradle.kts              # Platform constraints + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, migrated to codebase source)
```

## Platform constraints

All managed versions are declared via `api(...)` constraints inside
`javaPlatform { allowDependencies() }`. Consumers add the BOM via `platform(...)`
and then declare dependencies **without** versions.

Migrated/removed contracts (comments in `build.gradle.kts`):
- ~~`opencode-session-contracts`~~ — zero usage
- ~~`vibecoding-contracts`~~ — migrated into `codebase-gradle` source tree
- ~~`i18n-contracts`~~ — migrated into `bakery-gradle`
- ~~`pipeline-contracts`~~ — migrated into `bakery-gradle`

## runAllTests task

`runAllTests` iterates over 13 public boroughs and runs `./gradlew check` in each
as an independent Gradle build (one `Exec` task per borough). Wired into root `check`.

Testable projects (`testableProjects` list in `build.gradle.kts`):

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

## Build commands

```bash
./gradlew build                                       # platform constraints
./gradlew check                                       # triggers runAllTests
./gradlew runAllTests                                 # all 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # single borough
./gradlew publishToMavenLocal                          # local publish
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## Publication (NMCP)

Configured via `com.gradleup.nmcp.settings` (1.5.0) in `settings.gradle.kts`.
Credentials read from `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. Signing uses `useGpgCmd()`.
POM declares Apache 2.0, developer `cccp-education`, SCM to
`github.com/cccp-education/workspace-bom`.

## Prerequisites

- **Java** 24+ (Kotlin 2.3.20 toolchain)
- **Gradle** 9.5.1+
- **Docker** (borough tests use Testcontainers)

## Architecture docs

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — Dependency flow N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & roadmap (MEMPHIS)

## License

Apache License 2.0 — see [LICENSE](./LICENSE).

---

_Part of the CCCP Education ecosystem — `groupId: education.cccp`._