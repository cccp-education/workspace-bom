package contracts.ci

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * MEM-CI-GATE (suite S-338) — the textual rules of the CI exit-code audit.
 *
 * GitHub Actions runs a multi-command `run: |` block with the **last** command's
 * exit status unless errexit is enabled. On 2026-10-31 (S-335) the `n0-contracts`
 * job ended its parallel script with `echo "::endgroup::"`, so a red
 * `./gradlew check` was reported as success for two sessions.
 *
 * The audit is deliberately **textual and pure** (no YAML library, no I/O): it
 * is a review guard over the workflow files, whose content is the contract.
 */
class CiShellAuditTest {
    private fun step(script: String): String =
        "jobs:\n  gate:\n    steps:\n      - run: |\n" +
            script.trimIndent().lines().joinToString("\n") { "          $it" }

    private fun findings(script: String): List<ShellFinding> = CiShellAudit.findings(step(script), "workflow.yml")

    // ── Block extraction ───────────────────────────────────────────────────

    @Test
    fun `a single command continued over several lines stays one command`() {
        val script = """
            ./gradlew -p plantuml-plugin functionalTest \
              -Ptest.tags="real-llm" \
              --no-daemon
        """
        val block = CiShellAudit.blocks(step(script)).single()

        assertThat(block.commands).hasSize(1)
        assertThat(block.commands.single()).endsWith("--no-daemon")
        assertThat(findings(script)).isEmpty()
    }

    @Test
    fun `comments, blank lines and shell keywords are not commands`() {
        val script = """
            # install the toolchain
            if [ -z "x" ]; then
              echo "missing"
            fi
        """
        val block = CiShellAudit.blocks(step(script)).single()

        assertThat(block.commands).containsExactly("echo \"missing\"")
    }

    @Test
    fun `a folded scalar is a single command and is not audited as a block`() {
        val text =
            """
            jobs:
              gate:
                steps:
                  - run: >
                      ./gradlew koverXmlReport
                      -x test
            """.trimIndent()

        assertThat(CiShellAudit.blocks(text)).isEmpty()
        assertThat(CiShellAudit.findings(text, "workflow.yml")).isEmpty()
    }

    @Test
    fun `errexit is recognised in every supported spelling`() {
        listOf("set -e", "set -eu", "set -eo pipefail", "set -euo pipefail", "set -o errexit").forEach { guard ->
            val block = CiShellAudit.blocks(step("$guard\n./gradlew check")).single()

            assertThat(block.errexit).describedAs("errexit for `%s`", guard).isTrue()
        }
    }

    @Test
    fun `a block without errexit is reported as such`() {
        val block = CiShellAudit.blocks(step("chmod +x gradlew\n./gradlew check")).single()

        assertThat(block.errexit).isFalse()
    }

    // ── Rule 1 — missing errexit ───────────────────────────────────────────

    @Test
    fun `a multi-command gate without errexit is flagged`() {
        val script = """
            chmod +x gradlew
            npm run ci:backend:test
        """

        val finding = findings(script).filterIsInstance<ShellFinding.NoErrexit>().single()

        assertThat(finding.command).isEqualTo("npm run ci:backend:test")
        assertThat(finding.source).isEqualTo("workflow.yml")
    }

    @Test
    fun `a multi-command block without a gate is not flagged`() {
        val script = """
            mkdir -p out
            echo done
        """

        assertThat(findings(script)).isEmpty()
    }

    @Test
    fun `the historical S-335 script is a false green and is flagged`() {
        val buggy = """
            echo "::group::contracts check"
            ( cd "agent-contracts" && ./gradlew check --no-daemon )
            echo "::endgroup::"
        """

        val finding = findings(buggy).filterIsInstance<ShellFinding.NoErrexit>().single()

        assertThat(finding.command).isEqualTo("echo \"::endgroup::\"")
    }

    @Test
    fun `the historical S-335 script is safe once errexit is enabled`() {
        val fixed = """
            set -eo pipefail
            echo "::group::contracts check"
            ( cd "agent-contracts" && ./gradlew check --no-daemon )
            echo "::endgroup::"
        """

        assertThat(findings(fixed)).isEmpty()
    }

    @Test
    fun `a report tail after a gate is flagged`() {
        val script = """
            echo "config" > site.yml
            python3 -c "print('ok')" > /tmp/keys.json
            cat /tmp/keys.json | python3 -c "print('done')"
        """

        val finding = findings(script).filterIsInstance<ShellFinding.NoErrexit>().single()

        assertThat(finding.command).startsWith("cat /tmp/keys.json")
    }

    // ── Rule 2 — a gate swallowed by `|| true` ─────────────────────────────

    @Test
    fun `a gate neutralised by a fallback is flagged`() {
        val text =
            """
            jobs:
              gate:
                steps:
                  - run: ./gradlew check || true
            """.trimIndent()

        val finding = CiShellAudit.findings(text, "workflow.yml").filterIsInstance<ShellFinding.SwallowedGate>().single()

        assertThat(finding.command).isEqualTo("./gradlew check || true")
    }

    @Test
    fun `a cleanup command neutralised by a fallback is not a gate and is not flagged`() {
        val text =
            """
            jobs:
              cleanup:
                steps:
                  - run: docker rm -f ollama || true
                  - run: sudo chown -R 1000:1000 /tmp/ollama
            """.trimIndent()

        assertThat(CiShellAudit.findings(text, "workflow.yml")).isEmpty()
    }

    @Test
    fun `a gate neutralised across a line continuation is flagged`() {
        val text =
            """
            jobs:
              gate:
                steps:
                  - name: Validate Firebase config
                    run: |
                      ./gradlew -s --no-daemon \
                        -PsiteName=cheroliv.com \
                        validateFirebaseConfig || echo "non-fatal"
            """.trimIndent()

        val finding = CiShellAudit.findings(text, "workflow.yml").filterIsInstance<ShellFinding.SwallowedGate>().single()

        assertThat(finding.command).contains("validateFirebaseConfig || echo")
    }

    @Test
    fun `a step declaring continue-on-error is exempt from the swallow rule`() {
        val text =
            """
            jobs:
              gate:
                steps:
                  - name: Validate Firebase config
                    continue-on-error: true
                    run: |
                      ./gradlew -s --no-daemon \
                        -PsiteName=cheroliv.com \
                        validateFirebaseConfig || echo "non-fatal"
            """.trimIndent()

        assertThat(CiShellAudit.findings(text, "workflow.yml")).isEmpty()
    }

    @Test
    fun `a commented-out step is never audited`() {
        val text =
            """
            jobs:
              gate:
                steps:
                  # - run: ./gradlew check || true
                  - run: ./gradlew check
            """.trimIndent()

        assertThat(CiShellAudit.findings(text, "workflow.yml")).isEmpty()
    }
}
