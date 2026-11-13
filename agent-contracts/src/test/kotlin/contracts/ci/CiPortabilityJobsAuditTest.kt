package contracts.ci

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * GBL-013 US-9 — job-scoped portability audit.
 *
 * A host job (shards, e2e, coverage, unit-ollama) must not be flagged for
 * `setup-java`; only a job that delegates to `ci/run.sh` is a contract job.
 */
class CiPortabilityJobsAuditTest {
    private fun audit(workflow: String) = CiPortabilityJobsAudit.audit(workflow.trimIndent(), "ci.yml")

    @Test
    fun `a pinned container and ci delegation is portable`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    runs-on: ubuntu-latest
                    container:
                      image: cccpeducation/cccp-ci-base@sha256:4722c1bcc71c0343c05257842b774715fc2574ff79182599a29fe8c1361cbeb3
                    steps:
                      - run: ci/run.sh fast
                """,
            )

        assertThat(audit.portable).isTrue()
        assertThat(audit.findings).isEmpty()
        assertThat(audit.jobs.single().contract).isTrue()
    }

    @Test
    fun `a host job keeps setup-java without a finding`() {
        val audit =
            audit(
                """
                jobs:
                  bdd:
                    runs-on: ubuntu-latest
                    steps:
                      - uses: actions/setup-java@v4
                        with:
                          java-version: '25'
                      - run: ./gradlew cucumberTestShardA
                """,
            )

        assertThat(audit.migrated).isFalse()
        assertThat(audit.findings).isEmpty()
    }

    @Test
    fun `a ci job without a container is a missing runtime`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    runs-on: ubuntu-latest
                    steps:
                      - run: ci/run.sh fast
                """,
            )

        assertThat(audit.portable).isFalse()
        assertThat(audit.findings.single()).isInstanceOf(PortabilityFinding.MissingRuntime::class.java)
    }

    @Test
    fun `an unpinned image is a violation`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    container:
                      image: cccpeducation/cccp-ci-base:latest
                    steps:
                      - run: ci/run.sh fast
                """,
            )

        assertThat(audit.findings.single()).isInstanceOf(PortabilityFinding.UnpinnedImage::class.java)
    }

    @Test
    fun `a toolchain setup in a contract job is redundant`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    container:
                      image: cccpeducation/cccp-ci-base@sha256:4722c1bcc71c0343c05257842b774715fc2574ff79182599a29fe8c1361cbeb3
                    steps:
                      - uses: actions/setup-java@v4
                        with:
                          java-version: '25'
                      - run: ci/run.sh fast
                """,
            )

        assertThat(audit.findings.single())
            .isInstanceOf(PortabilityFinding.RedundantToolchainSetup::class.java)
    }

    @Test
    fun `a multi-command gate in a contract job stays outside the contract`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    container:
                      image: cccpeducation/cccp-ci-base@sha256:4722c1bcc71c0343c05257842b774715fc2574ff79182599a29fe8c1361cbeb3
                    steps:
                      - run: |
                          set -euo pipefail
                          ./gradlew check
                          echo "done"
                      - run: ci/run.sh fast
                """,
            )

        assertThat(audit.findings.map { it::class.simpleName })
            .contains(PortabilityFinding.LogicOutsideContract::class.simpleName)
    }

    @Test
    fun `host and contract jobs coexist without contaminating the unit`() {
        val audit =
            audit(
                """
                jobs:
                  fast:
                    container:
                      image: cccpeducation/cccp-ci-base@sha256:4722c1bcc71c0343c05257842b774715fc2574ff79182599a29fe8c1361cbeb3
                    steps:
                      - run: ci/run.sh fast
                  bdd:
                    steps:
                      - uses: actions/setup-java@v4
                      - run: ./gradlew cucumberTestShardA
                """,
            )

        assertThat(audit.portable).isTrue()
        assertThat(audit.jobs).hasSize(2)
        assertThat(audit.contractJobs).hasSize(1)
        assertThat(audit.findings).isEmpty()
    }
}
