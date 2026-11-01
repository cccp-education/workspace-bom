package contracts.ci.scenarios

import contracts.ci.CiShellAudit
import contracts.ci.ShellFinding
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import java.io.File

/**
 * MEM-CI-GATE (suite S-338) — step defs BDD `@ci-shell-gates`.
 *
 * Pure domain: an inline workflow text (docstring) or the repository CI files are
 * audited by [CiShellAudit]. No Gradle task, no network.
 */
class CiShellGatesSteps {
    private var findings: List<ShellFinding> = emptyList()

    @Given("a CI step script")
    fun aCiStepScript(script: String) {
        val block = script.trimIndent().lines().joinToString("\n") { "          $it" }
        findings = CiShellAudit.findings("jobs:\n  gate:\n    steps:\n      - run: |\n$block", "inline.yml")
    }

    @Given("a CI step line {string}")
    fun aCiStepLine(line: String) {
        findings = CiShellAudit.findings("jobs:\n  gate:\n    steps:\n      - run: $line", "inline.yml")
    }

    @When("the workspace-bom CI files are audited")
    fun theWorkspaceCiFilesAreAudited() {
        val workspace =
            sequenceOf(File(".."), File("."))
                .map { it.absoluteFile.normalize() }
                .firstOrNull { it.resolve(".github").isDirectory }
                ?: error(".github introuvable depuis ${File(".").absolutePath}")
        findings =
            listOf(".github/workflows", ".github/actions")
                .map { workspace.resolve(it) }
                .filter { it.isDirectory }
                .flatMap { dir ->
                    dir
                        .walkTopDown()
                        .filter { it.isFile && it.extension in setOf("yml", "yaml") }
                        .toList()
                }.flatMap { file ->
                    CiShellAudit.findings(file.readText(), file.relativeTo(workspace).invariantSeparatorsPath)
                }
    }

    @Then("no exit-code finding is reported")
    fun noExitCodeFindingIsReported() {
        assertThat(findings).isEmpty()
    }

    @Then("the audit reports a missing errexit finding")
    fun theAuditReportsAMissingErrexitFinding() {
        assertThat(findings.filterIsInstance<ShellFinding.NoErrexit>()).hasSize(1)
    }

    @Then("the audit reports a swallowed gate finding")
    fun theAuditReportsASwallowedGateFinding() {
        assertThat(findings.filterIsInstance<ShellFinding.SwallowedGate>()).hasSize(1)
    }

    @Then("the reported command is the trailing report")
    fun theReportedCommandIsTheTrailingReport() {
        assertThat(findings.single().command).startsWith("echo")
    }
}
