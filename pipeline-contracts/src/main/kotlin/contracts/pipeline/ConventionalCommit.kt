package contracts.pipeline

/**
 * Conventional Commit — represente un commit git suivant la spec
 * Conventional Commits (https://www.conventionalcommits.org).
 *
 * Contrat N0 partage cross-borough : chaque borough N2 (document-gradle,
 * magic-stick, ...) implemente son propre GitLogParser pour produire
 * des [ConventionalCommit] depuis le git log de son projet.
 *
 * @property type type conventionnel (feat, fix, chore, perf, refactor,
 *           docs, test, ci, build, style).
 * @property scope scope optionnel du commit (ex: "api", "core"), null
 *           si le commit n'a pas de scope.
 * @property message message du commit (sans prefixe type(scope):).
 * @property hash hash git du commit (SHA-1 ou SHA-256).
 * @property date date ISO-8601 du commit (ex: "2026-06-14T10:30:00Z").
 */
data class ConventionalCommit(
    val type: String,
    val scope: String?,
    val message: String,
    val hash: String,
    val date: String,
)