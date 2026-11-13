package contracts.ci

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * GBL-013 US-1 — characterisation of the three portability rules (R3/R4/R5).
 *
 * The contract is textual and pure: each test states one YAML shape and the
 * finding it must (or must not) produce.
 */
class CiPortabilityAuditTest {
    private fun findings(workflow: String) = CiPortabilityAudit.findings(workflow.trimIndent(), "ci.yml")

    @Test
    fun `R3 flags a multi-command block that does not delegate to ci`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - run: |
                      set -euo pipefail
                      ./gradlew check
                      echo "done"
            """

        val result = findings(workflow)

        assertThat(result).hasSize(1)
        assertThat(result.single()).isInstanceOf(PortabilityFinding.LogicOutsideContract::class.java)
    }

    @Test
    fun `R3 accepts a block that delegates to ci`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - run: |
                      set -euo pipefail
                      ci/run.sh test
            """

        assertThat(findings(workflow)).isEmpty()
    }

    @Test
    fun `R3 accepts an inline single-command step`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - run: ./gradlew check
            """

        assertThat(findings(workflow)).isEmpty()
    }

    @Test
    fun `R3 ignores a block that invokes no gate`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - run: |
                      set -euo pipefail
                      echo "report only"
            """

        assertThat(findings(workflow)).isEmpty()
    }

    @Test
    fun `R4 flags an image that is not pinned by digest`() {
        val workflow =
            """
            jobs:
              build:
                container:
                  image: eclipse-temurin:25-jdk
            """

        val result = findings(workflow)

        assertThat(result).hasSize(1)
        assertThat(result.single()).isInstanceOf(PortabilityFinding.UnpinnedImage::class.java)
    }

    @Test
    fun `R4 accepts an image pinned by digest`() {
        val digest = "a".repeat(64)
        val workflow =
            """
            jobs:
              build:
                container:
                  image: eclipse-temurin@sha256:$digest
            """

        assertThat(findings(workflow)).isEmpty()
    }

    @Test
    fun `R5 flags toolchain setup actions`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - uses: actions/setup-java@v4
                  - uses: actions/setup-node@v4
            """

        val result = findings(workflow)

        assertThat(result).hasSize(2)
        assertThat(result).allMatch { it is PortabilityFinding.RedundantToolchainSetup }
    }

    @Test
    fun `R5 ignores actions that are not toolchain setup`() {
        val workflow =
            """
            jobs:
              build:
                steps:
                  - uses: actions/checkout@v4
                  - uses: actions/cache@v4
            """

        assertThat(findings(workflow)).isEmpty()
    }

    @Test
    fun `a migrated adapter produces no finding`() {
        val digest = "b".repeat(64)
        val workflow =
            """
            jobs:
              build:
                container:
                  image: cccpeducation/cccp-ci-base@sha256:$digest
                steps:
                  - uses: actions/checkout@v4
                  - run: |
                      set -euo pipefail
                      ci/run.sh test
            """

        assertThat(findings(workflow)).isEmpty()
    }
}
