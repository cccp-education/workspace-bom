package contracts.ci

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

/**
 * MEM-CI-GATE (suite S-338) — regression guard on the workspace-bom CI itself.
 *
 * S-335 proved the class of bug is real: a `run: |` block that ends with a report
 * cannot fail, so the job stays green while the gate is red. [CiShellAudit] fixed
 * the occurrence; this guard prevents the next one — it fails as soon as a
 * workflow or a composite action of this repository reintroduces a swallowed gate.
 *
 * The second test is the anti-vacuous-green guard: a glob that silently matches
 * nothing would report "no findings" forever.
 */
class CiExitCodeGuardTest {
    private val workspace: File =
        sequenceOf(File(".."), File("."))
            .map { it.absoluteFile.normalize() }
            .firstOrNull { it.resolve(".github").isDirectory }
            ?: error(".github introuvable depuis ${File(".").absolutePath}")

    private fun ciFiles(): List<File> =
        listOf(".github/workflows", ".github/actions")
            .map { workspace.resolve(it) }
            .filter { it.isDirectory }
            .flatMap { dir ->
                dir
                    .walkTopDown()
                    .filter { it.isFile && it.extension in setOf("yml", "yaml") }
                    .toList()
            }.sortedBy { it.invariantSeparatorsPath }

    @Test
    fun `the audit really sees the CI files`() {
        val paths = ciFiles().map { it.relativeTo(workspace).invariantSeparatorsPath }

        assertThat(paths).isNotEmpty()
        assertThat(paths).contains(".github/workflows/test.yml")
        assertThat(paths.any { it.startsWith(".github/actions/") })
            .describedAs("au moins une action composite doit être auditée : %s", paths)
            .isTrue()
    }

    @Test
    fun `every workspace-bom CI script can fail`() {
        val findings =
            ciFiles().flatMap { file ->
                CiShellAudit.findings(file.readText(), file.relativeTo(workspace).invariantSeparatorsPath)
            }

        assertThat(findings)
            .describedAs(
                "un bloc `run: |` multi-commandes sans errexit (ou un gate masqué par `|| true`) " +
                    "est un faux vert : sa dernière commande décide du code de sortie (S-335)",
            ).isEmpty()
    }
}
