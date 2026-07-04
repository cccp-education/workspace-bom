package contracts.pipeline

/**
 * Configuration de generation des release notes.
 *
 * Contrat N0 partage cross-borough : decrit comment un borough N2
 * doit extraire et formatter ses release notes depuis le git log.
 *
 * Chaque champ est optionnel avec un default sane — un borough peut
 * generer des release notes avec une config vide (`ReleaseNotesConfig()`).
 *
 * @property fromTag tag de debut de la plage git log (ex: "v1.0.0").
 *                  Si null, le parser doit detecter le tag precedent
 *                  (ou utiliser le premier commit comme fallback).
 * @property toTag tag de fin de la plage git log (default "HEAD").
 * @property categories mapping type conventionnel -> label affiche.
 *                      Default : 7 categories (feat, fix, chore, perf,
 *                      refactor, docs, test) avec labels en francais.
 * @property outputDir repertoire de sortie du fichier genere
 *                    (default "build/release-notes").
 * @property version version cible des release notes (ex: "1.2.0").
 *                   Si null, le renderer doit utiliser "SNAPSHOT".
 * @property rendererType type de renderer attendu ("asciidoc", "markdown",
 *                        "json"). Default "asciidoc".
 * @property includeDownloads inclure la section "Downloads" (liens JAR,
 *                             badges Maven Central). Default true.
 */
data class ReleaseNotesConfig(
    val fromTag: String? = null,
    val toTag: String = "HEAD",
    val categories: Map<String, String> = mapOf(
        "feat" to "Nouveautés",
        "fix" to "Corrections",
        "chore" to "Maintenance",
        "perf" to "Performance",
        "refactor" to "Refactoring",
        "docs" to "Documentation",
        "test" to "Tests",
    ),
    val outputDir: String = "build/release-notes",
    val version: String? = null,
    val rendererType: String = "asciidoc",
    val includeDownloads: Boolean = true,
)