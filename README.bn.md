<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — ভোক্তা নির্দেশিকা

> CCCP Education প্লাগইন ইকোসিস্টেমের জন্য বিল অফ ম্যাটেরিয়ালস — শেয়ার্ড নির্ভরতার একমাত্র সত্যের উৎস।

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=লাইসেন্স)](./LICENSE)

- **সংস্করণ**: `0.0.1` · **গোষ্ঠী**: `education.cccp` · **আর্টিফ্যাক্ট**: `workspace-bom`
- **ধরন**: Java প্ল্যাটফর্ম (BOM) · **প্রকাশিত**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | **বাংলা** | [Português](README.pt.md) | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## এটি কী করে

`workspace-bom` CCCP Education boroughs-এর মধ্যে সকল শেয়ার্ড নির্ভরতা সংস্করণ কেন্দ্রীভূত করে। সামঞ্জস্যপূর্ণ সংস্করণের Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ এবং আরও অনেক কিছু পেতে এটি আপনার বিল্ডে আমদানি করুন।

এটি সমস্ত N1–N4 boroughs-এর জন্য ব্যবহৃত **N0 চুক্তি** (`agent-contracts`, `codebase-contracts`, `llm-pool-contracts`) সরবরাহ করে।

## দ্রুত শুরু

### 1. প্ল্যাটফর্ম প্রয়োগ করুন

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. পরিচালিত নির্ভরতা ব্যবহার করুন (সংস্করণের প্রয়োজন নেই)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // সংস্করণ BOM দ্বারা পরিচালিত
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## পরিচালিত সুযোগ

| সুযোগ | উদাহরণ |
|-------|----------|
| Kotlin ও কোরুটিন | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| ডেটাবেস | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| প্রসেসিং | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| পরীক্ষা | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| অন্যান্য | `playwright` |

## এই BOM এর মাধ্যমে প্রকাশিত N0 চুক্তি

| চুক্তি | আর্টিফ্যাক্ট | সরবরাহ করে |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## বিল্ড ও পরীক্ষা

```bash
./gradlew build                     # বিল্ড (প্ল্যাটফর্ম সীমাবদ্ধতা)
./gradlew check                     # runAllTests চালায় (সকল পাবলিক boroughs)
./gradlew runAllTests               # প্রতিটি borough-এ `./gradlew check` সম্পাদন করে
./gradlew publishToMavenLocal       # স্থানীয়ভাবে প্রকাশ করুন
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` 13টি পাবলিক boroughs-এ পুনরাবৃত্তি করে এবং প্রতিটিতে স্বাধীন বিল্ড হিসেবে `./gradlew check` চালায়।

## পূর্বশর্ত

- **Java** 24+ (Kotlin 2.3.20 টুলচেইন)
- **Gradle** 9.5.1+
- **Docker** (Testcontainers ব্যবহারকারী borough পরীক্ষার জন্য)

## CI / প্রকাশনা

NMCP (`com.gradleup.nmcp.settings` 1.5.0) এর মাধ্যমে প্রকাশনা `settings.gradle.kts`-এ কনফিগার করা। শংসাপত্র `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) থেকে পড়া হয়। স্বাক্ষর `useGpgCmd()` ব্যবহার করে। POM Apache 2.0, ডেভেলপার `cccp-education`, SCM `github.com/cccp-education/workspace-bom` ঘোষণা করে।

## লাইসেন্স

Apache License 2.0 — দেখুন [LICENSE](./LICENSE)।

---

_CCCP Education ইকোসিস্টেমের অংশ — `groupId: education.cccp`।_