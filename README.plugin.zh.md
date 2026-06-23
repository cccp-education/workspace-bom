<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — 插件内部

> `workspace-bom` 物料清单的开发者与贡献者指南。

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **版本**：`0.0.1` · **组**：`education.cccp` · **构件**：`workspace-bom`
- **类型**：`java-platform` (Gradle)，使用 `allowDependencies()` 提供 `api` 约束
- **构建**：`./gradlew build` · **测试**：`./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | **中文** | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## 模块布局

`workspace-bom` 是一个单模块 Gradle `java-platform` 项目。所有约束都在
`build.gradle.kts` 中。N0 契约是同级模块，独立发布并在此引用：

```
workspace-bom/
├── build.gradle.kts              # 平台约束 + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, migrated to codebase source)
```

## 平台约束

所有受管版本通过 `javaPlatform { allowDependencies() }` 内的 `api(...)` 约束声明。
消费者通过 `platform(...)` 添加 BOM，然后声明依赖**不带**版本。

已迁移/移除的契约（`build.gradle.kts` 中的注释）：
- ~~`opencode-session-contracts`~~ — 零使用
- ~~`vibecoding-contracts`~~ — 迁移至 `codebase-gradle` 源码树
- ~~`i18n-contracts`~~ — 迁移至 `bakery-gradle`
- ~~`pipeline-contracts`~~ — 迁移至 `bakery-gradle`

## runAllTests 任务

`runAllTests` 遍历 13 个公开 borough，并在每个中作为独立 Gradle 构建运行 `./gradlew check`（每个 borough 一个 `Exec` 任务）。接入根 `check`。

可测试项目（`build.gradle.kts` 中的 `testableProjects` 列表）：

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

## 构建命令

```bash
./gradlew build                                       # 平台约束
./gradlew check                                       # 触发 runAllTests
./gradlew runAllTests                                 # 全部 13 个 borough ./gradlew check
./gradlew runTestsForBakeryGradle                     # 单个 borough
./gradlew publishToMavenLocal                          # 本地发布
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## 发布 (NMCP)

通过 `com.gradleup.nmcp.settings` (1.5.0) 在 `settings.gradle.kts` 中配置。
凭据从 `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) 读取。
`publishingType = "AUTOMATIC"`。签名使用 `useGpgCmd()`。
POM 声明 Apache 2.0、开发者 `cccp-education`、SCM 指向
`github.com/cccp-education/workspace-bom`。

## 前置要求

- **Java** 24+（Kotlin 2.3.20 工具链）
- **Gradle** 9.5.1+
- **Docker**（borough 测试使用 Testcontainers）

## 架构文档

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — 依赖流 N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs & 路线图 (MEMPHIS)

## 许可证

Apache License 2.0 — 见 [LICENSE](./LICENSE)。

---

_CCCP Education 生态系统的一部分 — `groupId: education.cccp`。_