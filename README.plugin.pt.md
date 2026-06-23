<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — Internos do Plugin

> Guia de desenvolvimento e contribuição para o Bill of Materials `workspace-bom`.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licença)](./LICENSE)

- **Versão**: `0.0.1` · **Grupo**: `education.cccp` · **Artefato**: `workspace-bom`
- **Tipo**: `java-platform` (Gradle) com `allowDependencies()` para restrições `api`
- **Build**: `./gradlew build` · **Testes**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | **Português** | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## Disposição de módulos

`workspace-bom` é um projeto Gradle `java-platform` de módulo único. Todas as restrições
estão em `build.gradle.kts`. Os contratos N0 são módulos irmãos publicados
independentemente e referenciados aqui:

```
workspace-bom/
├── build.gradle.kts              # Restrições de plataforma + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, migrado para fonte do codebase)
```

## Restrições de plataforma

Todas as versões gerenciadas são declaradas via restrições `api(...)` dentro de
`javaPlatform { allowDependencies() }`. Os consumidores adicionam a BOM via `platform(...)`
e então declaram dependências **sem** versão.

Contratos migrados/removidos (comentários em `build.gradle.kts`):
- ~~`opencode-session-contracts`~~ — zero uso
- ~~`vibecoding-contracts`~~ — migrado para a árvore fonte do `codebase-gradle`
- ~~`i18n-contracts`~~ — migrado para `bakery-gradle`
- ~~`pipeline-contracts`~~ — migrado para `bakery-gradle`

## Tarefa runAllTests

`runAllTests` itera sobre 13 boroughs públicos e executa `./gradlew check` em cada um
como um build Gradle independente (uma tarefa `Exec` por borough). Conectado ao `check` raiz.

Projetos testáveis (lista `testableProjects` em `build.gradle.kts`):

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
./gradlew build                                       # restrições de plataforma
./gradlew check                                       # dispara runAllTests
./gradlew runAllTests                                 # os 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # um único borough
./gradlew publishToMavenLocal                          # publicação local
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## Publicação (NMCP)

Configurada via `com.gradleup.nmcp.settings` (1.5.0) em `settings.gradle.kts`.
Credenciais lidas de `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. Assinatura usa `useGpgCmd()`.
O POM declara Apache 2.0, desenvolvedor `cccp-education`, SCM para
`github.com/cccp-education/workspace-bom`.

## Pré-requisitos

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (testes de boroughs usam Testcontainers)

## Docs de arquitetura

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — Fluxo de dependências N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & roadmap (MEMPHIS)

## Licença

Apache License 2.0 — ver [LICENSE](./LICENSE).

---

_Parte do ecossistema CCCP Education — `groupId: education.cccp`._