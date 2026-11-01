package contracts.catalog

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * MEM-CI-GATE (P0, S-335) — the N0 contract gate must be able to **fail**.
 *
 * The `n0-contracts` job runs the 9 contracts through `xargs … bash -c '<script>'`.
 * The script used to end with `echo "::endgroup::"`, so its exit status was always
 * `0` even when `./gradlew check` failed: `xargs` (and therefore the step, and the
 * job) reported **success** while `agent-contracts:test` was red — a false green
 * that hid a broken catalog guard for two sessions.
 *
 * The guard is purely textual (the workflow is the contract): it asserts every
 * parallel invocation runs with `errexit`, and that the job never swallows a
 * failure with `|| true`.
 */
class N0ContractsGateTest {
    private val workflow: File =
        sequenceOf(File(".."), File("."))
            .map { it.resolve(".github/workflows/test.yml") }
            .firstOrNull { it.exists() }
            ?: error(".github/workflows/test.yml introuvable depuis ${File(".").absolutePath}")

    private fun n0Job(): String {
        val lines = workflow.readText().lines()
        val start = lines.indexOfFirst { it.trim() == "n0-contracts:" }
        check(start >= 0) { "job `n0-contracts` introuvable dans ${workflow.path}" }
        val nextJob = Regex("""^ {2}[A-Za-z0-9_-]+:\s*$""")
        val end =
            (start + 1 until lines.size).firstOrNull { nextJob.matches(lines[it]) }
                ?: lines.size
        return lines.subList(start, end).joinToString("\n")
    }

    private fun parallelScript(): String =
        Regex("""bash -c '([\s\S]*?)'""")
            .find(n0Job())
            ?.groupValues
            ?.get(1)
            ?: error("script `bash -c '…'` introuvable dans le job n0-contracts")

    @Test
    fun `every parallel contract build runs with errexit`() {
        val script = parallelScript()

        assertTrue(script.contains("./gradlew check"), "le gate doit lancer `./gradlew check`")
        assertTrue(
            Regex("""(^|\n)\s*set\s+-[a-z]*e""").containsMatchIn(script),
            "le sous-shell parallèle doit activer errexit (`set -e`), sinon le `echo \"::endgroup::\"` " +
                "final rend le code de sortie nul et MASQUE un `./gradlew check` en échec (P0 S-335). Script:\n$script",
        )
    }

    @Test
    fun `the N0 gate never swallows a failure`() {
        assertFalse(
            n0Job().contains("|| true"),
            "le gate N0 ne doit jamais neutraliser un échec (`|| true`)",
        )
    }
}
