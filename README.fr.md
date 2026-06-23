<!-- translated from README.md rev 0.0.1 -->
# workspace-bom — Guide Consommateur

> Bill of Materials pour l'écosystème de plugins CCCP Education — source unique de vérité pour les dépendances partagées.

[![Maven Central](https://img.shields.io/maven-central/v/education.cccp/workspace-bom.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/education.cccp/workspace-bom)
[![License](https://img.shields.io/github/license/cccp-education/workspace-bom?label=Licence)](./LICENSE)

- **Version** : `0.0.1` · **Groupe** : `education.cccp` · **Artefact** : `workspace-bom`
- **Type** : Java Platform (BOM) · **Publié** : Maven Central (NMCP, `com.gradleup.nmcp.settings` 1.5.0)

🌐 Langues : [English](README.md) | **Français**

---

## Ce que ça fait

`workspace-bom` centralise toutes les versions de dépendances partagées entre les boroughs
CCCP Education. Importez-la dans votre build pour obtenir des versions cohérentes de
Kotlin, Jackson, LangChain4j, koog-agents, Testcontainers, Arrow, JGit, AsciidoctorJ et plus.

Elle publie également les **contrats N0** (`agent-contracts`, `codebase-contracts`,
`llm-pool-contracts`) consommés par tous les boroughs N1–N4.

## Démarrage rapide

### 1. Importer la plateforme

```gradle
dependencies {
    implementation(platform("education.cccp:workspace-bom:0.0.1"))
}
```

### 2. Utiliser les dépendances managées (sans version)

```gradle
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")       // version gérée par la BOM
    implementation("dev.langchain4j:langchain4j")
    implementation("ai.koog:koog-agents")
    implementation("io.arrow-kt:arrow-core")
    implementation("org.testcontainers:postgresql")
}
```

## Périmètre managé

| Périmètre | Exemples |
|-------|----------|
| Kotlin & coroutines | `kotlin-stdlib`, `kotlinx-serialization-json`, `kotlinx-coroutines-*` |
| Jackson | `jackson-databind`, `jackson-module-kotlin`, `jackson-dataformat-yaml` |
| LangChain4j | `langchain4j`, `langchain4j-ollama`, `langchain4j-open-ai`, `langchain4j-pgvector` |
| Koog | `koog-agents` 1.0.0 |
| Base de données | `r2dbc-postgresql`, `r2dbc-pool`, `postgresql` |
| Traitement | `mapstruct`, `arrow-core`, `arrow-fx-coroutines`, `commons-io` |
| Tests | `junit-jupiter`, `cucumber-java`, `testcontainers-*`, `mockito-kotlin`, `assertj-core` |
| Docker | `docker-java-core`, `docker-java-transport-httpclient5` |
| Git | `org.eclipse.jgit`, `org.eclipse.jgit.ssh.jsch` |
| Asciidoctor | `asciidoctorj`, `asciidoctorj-diagram`, `asciidoctorj-diagram-plantuml` |
| PDF | `pdfbox`, `tika-core`, `flexmark-all` |
| Autre | `playwright` |

## Contrats N0 publiés via cette BOM

| Contrat | Artefact | Fournit |
|----------|----------|----------|
| `agent-contracts` | `education.cccp:agent-contracts:0.0.1` | Epic, UserStory, GradleTask, AgentState |
| `codebase-contracts` | `education.cccp:codebase-contracts:0.0.1` | ContextChannel, ChannelBudget, CompositeContext |
| `llm-pool-contracts` | `education.cccp:llm-pool-contracts:0.0.1` | LlmInstancePool, LlmInstance, QuotaConfig |

## Build et tests

```bash
./gradlew build                     # build (contraintes plateforme)
./gradlew check                     # lance runAllTests (tous les boroughs publics)
./gradlew runAllTests               # exécute `./gradlew check` dans chaque borough
./gradlew publishToMavenLocal       # publier localement
./gradlew publishAggregationToCentralPortal --no-daemon   # Maven Central
```

`runAllTests` itère sur 13 boroughs publics et exécute `./gradlew check` dans chacun
comme un build indépendant.

## Prérequis

- **Java** 24+ (toolchain Kotlin 2.3.20)
- **Gradle** 9.5.1+
- **Docker** (pour les tests de boroughs utilisant Testcontainers)

## CI / Publication

Publication via NMCP (`com.gradleup.nmcp.settings` 1.5.0) configurée dans
`settings.gradle.kts`. Identifiants lus depuis `~/.gradle/gradle.properties`
(`ossrhUsername`, `ossrhPassword`). Signature via `useGpgCmd()`. Le POM déclare
Apache 2.0, développeur `cccp-education`, SCM vers `github.com/cccp-education/workspace-bom`.

## Licence

Apache License 2.0 — voir [LICENSE](./LICENSE).

---

_Partie de l'écosystème CCCP Education — `groupId: education.cccp`._