package contracts.pipeline

import java.io.File

/**
 * Parser de git log — extrait les [ConventionalCommit] entre deux tags.
 *
 * Contrat N0 partage cross-borough : chaque borough N2 fournit son
 * implementation (JGit, `git log` CLI, parsing de sortie, etc.) selon
 * son contexte. Le contrat garantit l'interoperabilite : un
 * [ReleaseNotesGenerator] peut consommer n'importe quel [GitLogParser].
 *
 * Methodes :
 * - [parse] : extrait les commits entre `fromTag` (exclus) et `toTag`
 *   (inclus). Retourne une liste vide si aucun commit dans la plage.
 * - [detectVersion] : detecte la version cible depuis le projet
 *   (ex: VERSION file, build.gradle.kts, gradle.properties).
 *   Retourne null si non trouve.
 * - [detectFromTag] : detecte le tag precedent `toTag` (ex: "v1.0.0").
 *   Retourne null si aucun tag precedent (premiere release).
 */
interface GitLogParser {
    fun parse(fromTag: String, toTag: String): List<ConventionalCommit>
    fun detectVersion(projectDir: File): String?
    fun detectFromTag(projectDir: File, toTag: String): String?
}