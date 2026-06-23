<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — প্লাগইন অভ্যন্তরীণ

> `workspace-bom` বিল অফ ম্যাটেরিয়ালস-এর জন্য ডেভেলপার ও অবদানকারী নির্দেশিকা।

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=লাইসেন্স)](./LICENSE)

- **সংস্করণ**: `0.0.1` · **গোষ্ঠী**: `education.cccp` · **আর্টিফ্যাক্ট**: `workspace-bom`
- **ধরন**: `java-platform` (Gradle) `api` সীমাবদ্ধতার জন্য `allowDependencies()` সহ
- **বিল্ড**: `./gradlew build` · **পরীক্ষা**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | **বাংলা** | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## মডিউল বিন্যাস

`workspace-bom` একটি একক-মডিউল Gradle `java-platform` প্রকল্প। সকল সীমাবদ্ধতা
`build.gradle.kts`-এ রয়েছে। N0 চুক্তিগুলি সহোদর মডিউল যা স্বাধীনভাবে প্রকাশিত এবং
এখানে উল্লেখিত:

```
workspace-bom/
├── build.gradle.kts              # প্ল্যাটফর্ম সীমাবদ্ধতা + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, codebase সোর্সে মাইগ্রেটেড)
```

## প্ল্যাটফর্ম সীমাবদ্ধতা

সকল পরিচালিত সংস্করণ `javaPlatform { allowDependencies() }`-এর ভিতরে `api(...)` সীমাবদ্ধতার
মাধ্যমে ঘোষিত হয়। ভোক্তা BOM `platform(...)` এর মাধ্যমে যোগ করে তারপর
**বিনা** সংস্করণে নির্ভরতা ঘোষণা করে।

মাইগ্রেটেড/অপসারিত চুক্তি (`build.gradle.kts`-এ মন্তব্য):
- ~~`opencode-session-contracts`~~ — শূন্য ব্যবহার
- ~~`vibecoding-contracts`~~ — `codebase-gradle` সোর্স ট্রিতে মাইগ্রেটেড
- ~~`i18n-contracts`~~ — `bakery-gradle`-এ মাইগ্রেটেড
- ~~`pipeline-contracts`~~ — `bakery-gradle`-এ মাইগ্রেটেড

## runAllTests কাজ

`runAllTests` 13টি পাবলিক boroughs-এ পুনরাবৃত্তি করে এবং প্রতিটিতে স্বাধীন
Gradle বিল্ড হিসেবে `./gradlew check` চালায় (প্রতি borough একটি `Exec` কাজ)। রুট `check`-এ
যুক্ত।

পরীক্ষণযোগ্য প্রকল্প (`build.gradle.kts`-এ `testableProjects` তালিকা):

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

## বিল্ড কমান্ড

```bash
./gradlew build                                       # প্ল্যাটফর্ম সীমাবদ্ধতা
./gradlew check                                       # runAllTests ট্রিগার করে
./gradlew runAllTests                                 # সকল 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # একটি borough
./gradlew publishToMavenLocal                          # স্থানীয় প্রকাশনা
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## প্রকাশনা (NMCP)

`com.gradleup.nmcp.settings` (1.5.0) এর মাধ্যমে `settings.gradle.kts`-এ কনফিগার করা।
শংসাপত্র `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) থেকে পড়া হয়।
`publishingType = "AUTOMATIC"`। স্বাক্ষর `useGpgCmd()` ব্যবহার করে।
POM Apache 2.0, ডেভেলপার `cccp-education`, SCM
`github.com/cccp-education/workspace-bom` ঘোষণা করে।

## পূর্বশর্ত

- **Java** 24+ (Kotlin 2.3.20 টুলচেইন)
- **Gradle** 9.5.1+
- **Docker** (borough পরীক্ষা Testcontainers ব্যবহার করে)

## আর্কিটেকচার ডক্স

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — নির্ভরতা প্রবাহ N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs ও রোডম্যাপ (MEMPHIS)

## লাইসেন্স

Apache License 2.0 — দেখুন [LICENSE](./LICENSE)।

---

_CCCP Education ইকোসিস্টেমের অংশ — `groupId: education.cccp`।_