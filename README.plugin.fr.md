<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — Internes du Plugin

> Guide développeur et contributeur pour la Bill of Materials `workspace-bom`.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licence)](./LICENSE)

- **Version** : `0.0.1` · **Groupe** : `education.cccp` · **Artefact** : `workspace-bom`
- **Type** : `java-platform` (Gradle) avec `allowDependencies()` pour les contraintes `api`
- **Build** : `./gradlew build` · **Tests** : `./gradlew runAllTests`

🌐 Langues : [English](README.plugin.md) | **Français**

---

## Organisation des modules

`workspace-bom` est un projet Gradle `java-platform` mono-module. Toutes les contraintes
se trouvent dans `build.gradle.kts`. Les contrats N0 sont des modules frères publiés
indépendamment et référencés ici :

```
workspace-bom/
├── build.gradle.kts              # Contraintes plateforme + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, migré dans codebase)
```

## Contraintes plateforme

Toutes les versions managées sont déclarées via des contraintes `api(...)` à l'intérieur de
`javaPlatform { allowDependencies() }`. Les consommateurs ajoutent la BOM via `platform(...)`
puis déclarent les dépendances **sans** version.

Contrats migrés/supprimés (commentaires dans `build.gradle.kts`) :
- ~~`opencode-session-contracts`~~ — zéro usage
- ~~`vibecoding-contracts`~~ — migré dans l'arbre source `codebase-gradle`
- ~~`i18n-contracts`~~ — migré dans `bakery-gradle`
- ~~`pipeline-contracts`~~ — migré dans `bakery-gradle`

## Tâche runAllTests

`runAllTests` itère sur 13 boroughs publics et exécute `./gradlew check` dans chacun
comme un build Gradle indépendant (une tâche `Exec` par borough). Câblé dans `check` racine.

Projets testables (liste `testableProjects` dans `build.gradle.kts`) :

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

## Commandes de build

```bash
./gradlew build                                       # contraintes plateforme
./gradlew check                                       # déclenche runAllTests
./gradlew runAllTests                                 # les 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # un seul borough
./gradlew publishToMavenLocal                          # publication locale
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## Publication (NMCP)

Configurée via `com.gradleup.nmcp.settings` (1.5.0) dans `settings.gradle.kts`.
Identifiants lus depuis `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. Signature via `useGpgCmd()`.
Le POM déclare Apache 2.0, développeur `cccp-education`, SCM vers
`github.com/cccp-education/workspace-bom`.

## Prérequis

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (les tests de boroughs utilisent Testcontainers)

## Docs d'architecture

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — Flux de dépendances N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & roadmap (MEMPHIS)

## Licence

Apache License 2.0 — voir [LICENSE](./LICENSE).

---

_Partie de l'écosystème CCCP Education — `groupId: education.cccp`._