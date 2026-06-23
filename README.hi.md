<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — उपभोक्ता गाइड

> CCCP Education प्लगइन पारिस्थितिकी तंत्र के लिए बिल ऑफ मैटेरियल्स — साझा निर्भरताओं का एकमात्र सत्य स्रोत।

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **संस्करण**: `0.0.1` · **समूह**: `education.cccp` · **आर्टिफैक्ट**: `workspace-bom`
- **प्रकार**: Java प्लेटफ़ॉर्म (BOM) · **प्रकाशित**: Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Languages: [English](README.md) | [中文](README.zh.md) | **हिन्दी** | [Español](README.es.md) | [Français](README.fr.md) | [العربية](README.ar.md) | [বাংলা](README.bn.md) | [Português](README.pt.md) | [Русский](README.ru.md) | [اردو](README.ur.md)

---

## यह क्या करता है

`workspace-bom` CCCP Education boroughs के बीच सभी साझा निर्भरता संस्करणों को केंद्रीकृत करता है। इसे अपने बिल्ड में आयात करें ताकि Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ और अन्य के सुसंगत संस्करण मिल सकें।

यह सभी N1–N4 boroughs द्वारा उपयोग किए जाने वाले **N0 अनुबंध** (`agent-contracts`, `codebase-contracts`, `llm-pool-contracts`) भी प्रदान करता है।

## त्वरित आरंभ

### 1. प्लेटफ़ॉर्म लागू करें

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. प्रबंधित निर्भरताओं का उपयोग करें (संस्करण की आवश्यकता नहीं)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // संस्करण BOM द्वारा प्रबंधित
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## प्रबंधित दायरे

| दायरा | उदाहरण |
|-------|----------|
| Kotlin व कोरूटीन | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| डेटाबेस | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| प्रोसेसिंग | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| परीक्षण | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| अन्य | `playwright` |

## इस BOM के माध्यम से प्रकाशित N0 अनुबंध

| अनुबंध | आर्टिफैक्ट | प्रदान करता है |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## बिल्ड और परीक्षण

```bash
./gradlew build                     # बिल्ड (प्लेटफ़ॉर्म बाध्यताएँ)
./gradlew check                     # runAllTests चलाता है (सभी सार्वजनिक boroughs)
./gradlew runAllTests               # प्रत्येक borough में `./gradlew check` निष्पादित करता है
./gradlew publishToMavenLocal       # स्थानीय रूप से प्रकाशित करें
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` 13 सार्वजनिक boroughs पर पुनरावृत्ति करता है और प्रत्येक में स्वतंत्र बिल्ड के रूप में `./gradlew check` चलाता है।

## पूर्वापेक्षाएँ

- **Java** 24+ (Kotlin 2.3.20 टूलचेन)
- **Gradle** 9.5.1+
- **Docker** (Testcontainers का उपयोग करने वाले borough परीक्षणों के लिए)

## CI / प्रकाशन

NMCP (`com.gradleup.nmcp.settings` 1.5.0) के माध्यम से प्रकाशन, `settings.gradle.kts` में कॉन्फ़िगर किया गया। क्रेडेंशियल `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) से पढ़े जाते हैं। साइनिंग `useGpgCmd()` का उपयोग करती है। POM Apache 2.0, डेवलपर `cccp-education`, SCM `github.com/cccp-education/workspace-bom` पर घोषित करता है।

## लाइसेंस

Apache License 2.0 — देखें [LICENSE](./LICENSE)।

---

_CCCP Education पारिस्थितिकी तंत्र का हिस्सा — `groupId: education.cccp`।_