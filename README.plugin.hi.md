<!-- translated from README.plugin.md rev 0.0.1 -->
# workspace-bom — प्लगइन आंतरिक

> `workspace-bom` बिल ऑफ मैटेरियल्स के लिए डेवलपर और योगदानकर्ता गाइड।

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=License)](./LICENSE)

- **संस्करण**: `0.0.1` · **समूह**: `education.cccp` · **आर्टिफैक्ट**: `workspace-bom`
- **प्रकार**: `java-platform` (Gradle) `api` बाध्यताओं के लिए `allowDependencies()` के साथ
- **बिल्ड**: `./gradlew build` · **परीक्षण**: `./gradlew runAllTests`

🌐 Languages: [English](README.plugin.md) | [中文](README.plugin.zh.md) | **हिन्दी** | [Español](README.plugin.es.md) | [Français](README.plugin.fr.md) | [العربية](README.plugin.ar.md) | [বাংলা](README.plugin.bn.md) | [Português](README.plugin.pt.md) | [Русский](README.plugin.ru.md) | [اردو](README.plugin.ur.md)

---

## मॉड्यूल लेआउट

`workspace-bom` एकल-मॉड्यूल Gradle `java-platform` प्रोजेक्ट है। सभी बाध्यताएँ
`build.gradle.kts` में हैं। N0 अनुबंध सहोदर मॉड्यूल हैं जो स्वतंत्र रूप से प्रकाशित होते हैं
और यहाँ संदर्भित हैं:

```
workspace-bom/
├── build.gradle.kts              # प्लेटफ़ॉर्म बाध्यताएँ + runAllTests + publishing
├── settings.gradle.kts
├── agent-contracts/              # Epic, UserStory, GradleTask, AgentState (N0)
├── codebase-contracts/           # ContextChannel, ChannelBudget, CompositeContext (N0)
├── llm-pool-contracts/           # LlmInstancePool, LlmInstance, QuotaConfig (N0)
├── opencode-session-contracts/   # SessionPrompt, SessionResponse, AgentContext (N0)
├── i18n-contracts/               # SupportedLanguage, LanguageCatalog, I18nConfig (N0)
└── vibecoding-contracts/         # ToolRegistry, ExecShellTool (N0, codebase स्रोत में माइग्रेटेड)
```

## प्लेटफ़ॉर्म बाध्यताएँ

सभी प्रबंधित संस्करण `javaPlatform { allowDependencies() }` के अंदर `api(...)` बाध्यताओं के
माध्यम से घोषित किए जाते हैं। उपभोक्ता BOM को `platform(...)` के माध्यम से जोड़ते हैं
फिर **बिना** संस्करण के निर्भरताएँ घोषित करते हैं।

माइग्रेटेड/हटाए गए अनुबंध (`build.gradle.kts` में टिप्पणियाँ):
- ~~`opencode-session-contracts`~~ — शून्य उपयोग
- ~~`vibecoding-contracts`~~ — `codebase-gradle` स्रोत वृक्ष में माइग्रेटेड
- ~~`i18n-contracts`~~ — `bakery-gradle` में माइग्रेटेड
- ~~`pipeline-contracts`~~ — `bakery-gradle` में माइग्रेटेड

## runAllTests कार्य

`runAllTests` 13 सार्वजनिक boroughs पर पुनरावृत्ति करता है और प्रत्येक में एक स्वतंत्र
Gradle बिल्ड के रूप में `./gradlew check` चलाता है (प्रति borough एक `Exec` कार्य)। रूट `check`
में जोड़ा गया।

परीक्षण योग्य प्रोजेक्ट्स (`build.gradle.kts` में `testableProjects` सूची):

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

## बिल्ड कमांड

```bash
./gradlew build                                       # प्लेटफ़ॉर्म बाध्यताएँ
./gradlew check                                       # runAllTests ट्रिगर करता है
./gradlew runAllTests                                 # सभी 13 boroughs ./gradlew check
./gradlew runTestsForBakeryGradle                     # एकल borough
./gradlew publishToMavenLocal                          # स्थानीय प्रकाशन
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

## प्रकाशन (NMCP)

`com.gradleup.nmcp.settings` (1.5.0) के माध्यम से `settings.gradle.kts` में कॉन्फ़िगर किया गया।
क्रेडेंशियल `~/.gradle/gradle.properties` (`ossrhUsername`, `ossrhPassword`) से पढ़े जाते हैं।
`publishingType = "AUTOMATIC"`। साइनिंग `useGpgCmd()` का उपयोग करती है।
POM Apache 2.0, डेवलपर `cccp-education`, SCM
`github.com/cccp-education/workspace-bom` पर घोषित करता है।

## पूर्वापेक्षाएँ

- **Java** 24+ (Kotlin 2.3.20 टूलचेन)
- **Gradle** 9.5.1+
- **Docker** (borough परीक्षण Testcontainers का उपयोग करते हैं)

## आर्किटेक्चर डॉक्स

- [DEPENDENCY_ARCHITECTURE.adoc](./DEPENDENCY_ARCHITECTURE.adoc) — निर्भरता प्रवाह N0→N4
- [.agents/INDEX.adoc](./.agents/INDEX.adoc) — EPICs व रोडमैप (MEMPHIS)

## लाइसेंस

Apache License 2.0 — देखें [LICENSE](./LICENSE)।

---

_CCCP Education पारिस्थितिकी तंत्र का हिस्सा — `groupId: education.cccp`।_