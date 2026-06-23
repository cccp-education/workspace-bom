<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — پلگ ان کے باطن

> `workspace-bom` بل آف میٹیریلز کے لیے ڈویلپر اور معاون کی رہنمائی۔

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=لائسنس)](./LICENSE)

- **ورژن**: `0.0.1` · **گروپ**: `education.cccp` · **آرٹیفیکٹ**: `workspace-bom`
- **قسم**: `java-platform` (Gradle) `api` پابندیوں کے لیے `allowDependencies()` کے ساتھ
- **بِلڈ**: `./gradlew build` · **ٹیسٹس**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | **اردو**

---

## ماڈیول کا ترتیب

`workspace-bom` ایک واحد-ماڈیول Gradle `java-platform` پروجیکٹ ہے۔ تمام پابندیاں
`build.gradle.kts` میں ہیں۔ N0 معاہدے شریک ماڈیولز ہیں جو آزادانہ طور پر شائع
اور یہاں حوالہ دیے گئے ہیں:

```
workspace-bom/
├── build.gradle.kts              # پلیٹ فارم پابندیاں + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, codebase سورس میں مائیگریٹڈ)
```

## پلیٹ فارم پابندیاں

تمام منظم ورژن `javaPlatform { allowDependencies() }` کے اندر `api(...)` پابندیوں کے
ذریعے اعلان کیے جاتے ہیں۔ صارفین BOM کو `platform(...)` کے ذریعے شامل کرتے ہیں
پھر **بغیر** ورژن کے انحصار اعلان کرتے ہیں۔

مائیگریٹڈ/ہٹائے گئے معاہدے (`build.gradle.kts` میں تبصرے):
- ~~`opencode-session-contracts`~~ — صفر استعمال
- ~~`vibecoding-contracts`~~ — `codebase-gradle` سورس درخت میں مائیگریٹڈ
- ~~`i18n-contracts`~~ — `bakery-gradle` میں مائیگریٹڈ
- ~~`pipeline-contracts`~~ — `bakery-gradle` میں مائیگریٹڈ

## runAllTests کام

`runAllTests` 13 عوامی boroughs پر دہراتا ہے اور ہر ایک میں ایک آزاد
Gradle بِلڈ کے طور پر `./gradlew check` چلاتا ہے (ہر borough کے لیے ایک `Exec` کام)۔ روٹ `check`
سے جڑا ہوا۔

ٹیسٹ قابل پروجیکٹس (`build.gradle.kts` میں `testableProjects` فہرست):

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

## بِلڈ کمانڈز

```bash
./gradlew build                                       # پلیٹ فارم پابندیاں
./gradlew check                                       # runAllTests کو متحرک کرتا ہے
./gradlew runAllTests                                 # تمام 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # ایک borough
./gradlew publishToMavenLocal                          # مقامی اشاعت
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## اشاعت (NMCP)

`com.gradleup.nmcp.settings` (1.5.0) کے ذریعے `settings.gradle.kts` میں ترتیب دی گئی۔
اسناد `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) سے پڑھی جاتی ہیں۔
`publishingType = "AUTOMATIC"`۔ دستخط `useGpgCmd()` استعمال کرتا ہے۔
POM Apache 2.0، ڈویلپر `cccp-education`، SCM
`github.com/cccp-education/workspace-bom` کا اعلان کرتا ہے۔

## پیشگی ضروریات

- **Java** 24+ (Kotlin 2.3.20 ٹول چین)
- **Gradle** 9.5.1+
- **Docker** (borough ٹیسٹس Testcontainers استعمال کرتے ہیں)

## فن تعمیر کی دستاویزات

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — انحصار کا بہاؤ N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs اور روڈ میپ (MEMPHIS)

## لائسنس

Apache License 2.0 — دیکھیں [LICENSE](./LICENSE)۔

---

_CCCP Education ایکو سسٹم کا حصہ — `groupId: education.cccp`۔_