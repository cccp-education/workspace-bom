package contracts.catalog

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * MEM-CI-GATE (P0, S-335) / GBL-013 US-3b — the N0 contract gate must be able to fail.
 *
 * The `n0-contracts` job delegates to `ci/run.sh test`: the adapter carries no
 * logic. The parallel contract builds live in `check_n0_contracts`; each
 * invocation must run with `errexit`, otherwise a trailing command makes the
 * exit status `0` even when `./gradlew check` fails, and `xargs` reports success
 * while `agent-contracts:test` is red — the S-335 false green.
 *
 * Purely textual (the workflow and the script are the contract).
 */
class N0ContractsGateTest {
    private val repoRoot: File =
        sequenceOf(File(".."), File("."))
            .map { it.absoluteFile.normalize() }
            .firstOrNull { it.resolve("ci/run.sh").exists() }
            ?: error("ci/run.sh introuvable depuis ${File(".").absolutePath}")

    private val workflow: File = repoRoot.resolve(".github/workflows/test.yml")
    private val contract: File = repoRoot.resolve("ci/run.sh")

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

    private fun parallelBlock(): String {
        val script = contract.readText()
        val start = script.indexOf("check_n0_contracts()")
        check(start >= 0) { "fonction `check_n0_contracts` introuvable dans ${contract.path}" }
        val end = script.indexOf("\n}", start).let { if (it < 0) script.length else it }
        return script.substring(start, end)
    }

    @Test
    fun `the adapter delegates the N0 gate to the ci contract`() {
        val job = n0Job()

        assertTrue(job.contains("ci/run.sh test"), "le job N0 doit déléguer à `ci/run.sh test`")
        assertFalse(
            job.contains("bash -c"),
            "le job N0 ne doit plus porter le script parallèle inline (contrat `ci/`)",
        )
    }

    @Test
    fun `every parallel contract build runs with errexit`() {
        val block = parallelBlock()

        assertTrue(block.contains("xargs"), "les contrats N0 doivent rester exécutés en parallèle")
        assertTrue(block.contains("./gradlew check"), "le gate doit lancer `./gradlew check`")
        assertTrue(
            Regex("""(^|\n)\s*set\s+-[a-z]*e""").containsMatchIn(block),
            "le sous-shell parallèle doit activer errexit (`set -e`), sinon la commande finale " +
                "rend le code de sortie nul et MASQUE un `./gradlew check` en échec (P0 S-335). " +
                "Bloc:\n$block",
        )
    }

    @Test
    fun `the N0 gate never swallows a failure`() {
        assertFalse(
            n0Job().contains("|| true"),
            "le gate N0 ne doit jamais neutraliser un échec (`|| true`)",
        )
        assertFalse(
            parallelBlock().contains("|| true"),
            "le bloc parallèle ne doit jamais neutraliser un échec (`|| true`)",
        )
    }
}
