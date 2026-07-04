package contracts.pipeline

import java.io.File

/**
 * Generateur de release notes — compose un [GitLogParser] et un
 * [ReleaseNotesRenderer] pour produire un fichier de release notes.
 *
 * Contrat N0 partage cross-borough : chaque borough N2 fournit son
 * implementation (ex: document-gradle avec JGit + AsciiDocRenderer,
 * magic-stick avec git CLI + MarkdownRenderer). Le contrat garantit
 * l'interoperabilite : n'importe quelle paire parser/renderer peut
 * etre composee.
 *
 * Proprietes / Methodes :
 * - [parser] : le [GitLogParser] a utiliser pour extraire les commits.
 * - [renderer] : le [ReleaseNotesRenderer] a utiliser pour formater.
 * - [generate] : orchestration complete — parse la plage git log selon
 *   la config, rend le contenu, l'ecrit dans un fichier et le retourne.
 *   Le fichier de sortie est place dans [ReleaseNotesConfig.outputDir].
 */
interface ReleaseNotesGenerator {
    val parser: GitLogParser
    val renderer: ReleaseNotesRenderer

    fun generate(config: ReleaseNotesConfig): File
}