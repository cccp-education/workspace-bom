package contracts.pipeline

import java.io.File

/**
 * Renderer de release notes — transforme une liste de [ConventionalCommit]
 * en un document formate (AsciiDoc, Markdown, JSON, ...).
 *
 * Contrat N0 partage cross-borough : chaque borough N2 fournit son
 * implementation selon son format de sortie (AsciiDoc pour document-gradle,
 * Markdown pour magic-stick, ...). Le contrat garantit l'interoperabilite :
 * un [ReleaseNotesGenerator] peut consommer n'importe quel renderer.
 *
 * Proprietes / Methodes :
 * - [format] : format declare (ex: "asciidoc", "markdown", "json").
 *   Permet au [ReleaseNotesGenerator] de selectionner le renderer selon
 *   [ReleaseNotesConfig.rendererType].
 * - [render] : produit le contenu formate en chaine depuis les commits
 *   et la config. Retourne une chaine vide si aucun commit.
 * - [renderToFile] : ecrit le contenu rendu dans `outputFile` et le retourne.
 *   Le repertoire parent doit exister (le generator le cree).
 */
interface ReleaseNotesRenderer {
    val format: String

    fun render(commits: List<ConventionalCommit>, config: ReleaseNotesConfig): String
    fun renderToFile(commits: List<ConventionalCommit>, config: ReleaseNotesConfig, outputFile: File): File
}