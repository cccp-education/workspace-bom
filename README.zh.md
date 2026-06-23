<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — 消费者指南

> CCCP Education 插件生态系统的物料清单 — 共享依赖的单一事实来源。

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **版本**：`0.0.1` · **组**：`education.cccp` · **构件**：`workspace-bom`
- **类型**：Java 平台 (BOM) · **发布**：Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | **中文** | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | [Português](README.pt.md) | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## 功能简介

`workspace-bom` 集中了 CCCP Education 各 borough（区）之间所有共享依赖的版本。在构建中导入它，即可获得 Kotlin、Jackson、LangChain4j、koog-agents、Testcontainers、Arrow、JGit、AsciidoctorJ 等的一致版本。

它还发布了所有 N1–N4 borough 共同消费的 **N0 契约**（`agent-contracts`、`codebase-contracts`、`llm-pool-contracts`）。

## 快速开始

### 1. 应用平台

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. 使用受管依赖（无需版本）

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // 版本由 BOM 管理
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## 受管范围

| 范围 | 示例 |
|-------|----------|
| Kotlin & 协程 | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| 数据库 | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| 处理 | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| 测试 | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| 其他 | `playwright` |

## 通过此 BOM 发布的 N0 契约

| 契约 | 构件 | 提供 |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## 构建与测试

```bash
./gradlew build                     # 构建（平台约束）
./gradlew check                     # 运行 runAllTests（所有公开 borough）
./gradlew runAllTests               # 在每个 borough 中执行 `./gradlew check`
./gradlew publishToMavenLocal       # 本地发布
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` 遍历 13 个公开 borough，并在每个中作为独立构建运行 `./gradlew check`。

## 前置要求

- **Java** 24+（Kotlin 2.3.20 工具链）
- **Gradle** 9.5.1+
- **Docker**（用于使用 Testcontainers 的 borough 测试）

## CI / 发布

通过 NMCP（`com.gradleup.nmcp.settings` 1.5.0）发布，配置位于 `settings.gradle.kts`。凭据从 `~/.gradle/gradle.properties`（`ossrhUsername`、`ossrhPassword`）读取。签名使用 `useGpgCmd()`。POM 声明 Apache 2.0、开发者 `cccp-education`、SCM 指向 `github.com/cccp-education/workspace-bom`。

## 许可证

Apache License 2.0 — 见 [LICENSE](./LICENSE)。

---

_CCCP Education 生态系统的一部分 — `groupId: education.cccp`。_