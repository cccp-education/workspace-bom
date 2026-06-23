<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — دليل المستهلك

> قائمة مكونات (Bill of Materials) لنظام إضافات CCCP Education — المصدر الموحد للحقيقة للاعتمادات المشتركة.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=الرخصة)](./LICENSE)

- **الإصدار**: `0.0.1` · **المجموعة**: `education.cccp` · **القطعة**: `workspace-bom`
- **النوع**: منصة Java (BOM) · **منشور**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | **العربية** | [বাংলা](README.bn.md) | [Português](README.pt.md) | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## ما الذي يقوم به

يقوم `workspace-bom` بتركيز جميع إصدارات الاعتمادات المشتركة عبر أحياء (boroughs) CCCP
Education. استورده في بنائك للحصول على إصدارات متسقة من Kotlin و Jackson و
LangChain4j و koog-agents و Testcontainers و Arrow و JGit و AsciidoctorJ والمزيد.

كما ينشر **عقود N0** (`agent-contracts` و `codebase-contracts` و
`llm-pool-contracts`) التي يستهلكها جميع أحياء N1–N4.

## البداية السريعة

### 1. تطبيق المنصة

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. استخدام الاعتمادات المُدارة (بدون إصدار)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // الإصدار مُدار بواسطة BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## النطاقات المُدارة

| النطاق | أمثلة |
|-------|----------|
| Kotlin والكوروتينات | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| قاعدة البيانات | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| المعالجة | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| الاختبار | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| أخرى | `playwright` |

## عقود N0 المنشورة عبر هذه BOM

| العقد | القطعة | يوفر |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## البناء والاختبار

```bash
./gradlew build                     # بناء (قيود المنصة)
./gradlew check                     # يشغل runAllTests (جميع الأحياء العامة)
./gradlew runAllTests               # ينفذ `./gradlew check` في كل حي
./gradlew publishToMavenLocal       # نشر محلي
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

يُكرر `runAllTests` على 13 حياً عاماً ويُشغل `./gradlew check` في كل منها
كبناء مستقل.

## المتطلبات المسبقة

- **Java** 24+ (سلسلة أدوات Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (لاختبارات الأحياء التي تستخدم Testcontainers)

## CI / النشر

النشر عبر NMCP (`com.gradleup.nmcp.settings` 1.5.0) مُكوّن في
`settings.gradle.kts`. تُقرأ بيانات الاعتماد من `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). يستخدم التوقيع `useGpgCmd()`. يُعلن POM عن
Apache 2.0، المطور `cccp-education`، SCM إلى `github.com/cccp-education/workspace-bom`.

## الرخصة

Apache License 2.0 — انظر [LICENSE](./LICENSE).

---

_جزء من نظام CCCP Education — `groupId: education.cccp`._