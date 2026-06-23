<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — باطن الإضافة

> دليل المطور والمساهم لقائمة مكونات (Bill of Materials) `workspace-bom`.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=الرخصة)](./LICENSE)

- **الإصدار**: `0.0.1` · **المجموعة**: `education.cccp` · **القطعة**: `workspace-bom`
- **النوع**: `java-platform` (Gradle) مع `allowDependencies()` لقيود `api`
- **البناء**: `./gradlew build` · **الاختبارات**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | [हिन्दी](README.plugin.hi.md) | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | **العربية** | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## تخطيط الوحدات

`workspace-bom` هو مشروع Gradle `java-platform` بوحدة واحدة. جميع القيود موجودة في
`build.gradle.kts`. عقود N0 هي وحدات شقيقة منشورة بشكل مستقل ومُشار إليها هنا:

```
workspace-bom/
├── build.gradle.kts              # قيود المنصة + runAllTests + النشر
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, مُهاجَر إلى مصدر codebase)
```

## قيود المنصة

جميع الإصدارات المُدارة مُعلنة عبر قيود `api(...)` داخل
`javaPlatform { allowDependencies() }`. يضيف المستهلكون BOM عبر `platform(...)`
ثم يُعلنون الاعتمادات **بدون** إصدار.

عقود مُهاجَرة/مُزالة (تعليقات في `build.gradle.kts`):
- ~~`opencode-session-contracts`~~ — صفر استخدام
- ~~`vibecoding-contracts`~~ — هاجر إلى شجرة مصدر `codebase-gradle`
- ~~`i18n-contracts`~~ — هاجر إلى `bakery-gradle`
- ~~`pipeline-contracts`~~ — هاجر إلى `bakery-gradle`

## مهمة runAllTests

يُكرر `runAllTests` على 13 حياً عاماً ويُشغل `./gradlew check` في كل منها
كبناء Gradle مستقل (مهمة `Exec` واحدة لكل حي). موصول بـ `check` الجذري.

المشاريع القابلة للاختبار (قائمة `testableProjects` في `build.gradle.kts`):

| # | الحي (Borough) |
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

## أوامر البناء

```bash
./gradlew build                                       # قيود المنصة
./gradlew check                                       # يُطلق runAllTests
./gradlew runAllTests                                 # جميع الأحياء الـ 13 ./gradlew check
./gradlew runTestsForBakeryGradle                     # حي واحد
./gradlew publishToMavenLocal                          # نشر محلي
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## النشر (NMCP)

مُكوّن عبر `com.gradleup.nmcp.settings` (1.5.0) في `settings.gradle.kts`.
تُقرأ بيانات الاعتماد من `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`).
`publishingType = "AUTOMATIC"`. يستخدم التوقيع `useGpgCmd()`.
يُعلن POM عن Apache 2.0، المطور `cccp-education`، SCM إلى
`github.com/cccp-education/workspace-bom`.

## المتطلبات المسبقة

- **Java** 24+ (سلسلة أدوات Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (اختبارات الأحياء تستخدم Testcontainers)

## وثائق البنية

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — تدفق الاعتمادات N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs وخارطة الطريق (MEMPHIS)

## الرخصة

Apache License 2.0 — انظر [LICENSE](./LICENSE).

---

_جزء من نظام CCCP Education — `groupId: education.cccp`._