<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — صارفین کے لیے رہنمائی

> CCCP Education پلگ ان ایکو سسٹم کے لیے بل آف میٹیریلز — مشترکہ انحصار کا واحد سچائی کا ذریعہ۔

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=لائسنس)](./LICENSE)

- **ورژن**: `0.0.1` · **گروپ**: `education.cccp` · **آرٹیفیکٹ**: `workspace-bom`
- **قسم**: Java پلیٹ فارم (BOM) · **شائع**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | [हिन्दी](README.hi.md) | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | [Português](README.pt.md) | [Русский](README.ru.md) | **اردو**

---

## یہ کیا کرتا ہے

`workspace-bom` CCCP Education کے boroughs کے درمیان تمام مشترکہ انحصار کے ورژن کو مرکوز کرتا ہے۔ Kotlin، Jackson، LangChain4j، koog-agents، Testcontainers، Arrow، JGit، AsciidoctorJ اور دیگر کے مستقل ورژن حاصل کرنے کے لیے اسے اپنی بِلڈ میں درآمد کریں۔

یہ تمام N1–N4 boroughs کے زیرِ استعمال **N0 معاہدے** (`agent-contracts`، `codebase-contracts`، `llm-pool-contracts`) بھی فراہم کرتا ہے۔

## فوری آغاز

### 1. پلیٹ فارم لاگو کریں

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. منظم انحصار استعمال کریں (ورژن کی ضرورت نہیں)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // ورژن BOM کے زیرِ انتظام
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## منتظم دائرہ کار

| دائرہ کار | مثالیں |
|-------|----------|
| Kotlin اور کوروٹینز | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| ڈیٹا بیس | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| پروسیسنگ | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| ٹیسٹنگ | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| دیگر | `playwright` |

## اس BOM کے ذریعے شائع کردہ N0 معاہدے

| معاہدہ | آرٹیفیکٹ | فراہم کرتا ہے |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## بِلڈ اور ٹیسٹ

```bash
./gradlew build                     # بِلڈ (پلیٹ فارم پابندیاں)
./gradlew check                     # runAllTests چلاتا ہے (تمام عوامی boroughs)
./gradlew runAllTests               # ہر borough میں `./gradlew check` انجام دیتا ہے
./gradlew publishToMavenLocal       # مقامی طور پر شائع کریں
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` 13 عوامی boroughs پر دہراتا ہے اور ہر ایک میں ایک آزاد بِلڈ کے طور پر `./gradlew check` چلاتا ہے۔

## پیشگی ضروریات

- **Java** 24+ (Kotlin 2.3.20 ٹول چین)
- **Gradle** 9.5.1+
- **Docker** (Testcontainers استعمال کرنے والے borough ٹیسٹس کے لیے)

## CI / اشاعت

NMCP (`com.gradleup.nmcp.settings` 1.5.0) کے ذریعے اشاعت `settings.gradle.kts` میں ترتیب دی گئی۔ اسناد `~/.gradle/gradle.properties` (`ossrhUsername`، `ossrhPassword`) سے پڑھی جاتی ہیں۔ دستخط `useGpgCmd()` استعمال کرتا ہے۔ POM Apache 2.0، ڈویلپر `cccp-education`، SCM `github.com/cccp-education/workspace-bom` کا اعلان کرتا ہے۔

## لائسنس

Apache License 2.0 — دیکھیں [LICENSE](./LICENSE)۔

---

_CCCP Education ایکو سسٹم کا حصہ — `groupId: education.cccp`۔_