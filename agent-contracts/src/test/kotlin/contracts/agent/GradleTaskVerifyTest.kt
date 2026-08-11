package contracts.agent

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

/**
 * Verification metadata contract — GradleTask must carry the expected
 * output, retry budget, and optional verify hook that the codebase
 * StepVerifier consumes to decide retry/give-up.
 *
 * Defaults preserve the legacy bridge behaviour of
 * `VibecodingGraph.extractCurrentStep` which hardcoded
 * `expectedOutput = "BUILD SUCCESSFUL"` and relied on
 * `VibecodingStep.maxRetries = 3` / `verifyHook = null`.
 *
 * @see GradleTask
 * @since 0.0.3
 */
class GradleTaskVerifyTest {

    @Test
    fun `GradleTask defaults expectedOutput to BUILD SUCCESSFUL`() {
        val task = GradleTask(description = "Run tests", gradleTask = "./gradlew test")

        assertThat(task.expectedOutput).isEqualTo("BUILD SUCCESSFUL")
    }

    @Test
    fun `GradleTask defaults maxRetries to 3`() {
        val task = GradleTask(description = "Run tests", gradleTask = "./gradlew test")

        assertThat(task.maxRetries).isEqualTo(3)
    }

    @Test
    fun `GradleTask defaults verifyHook to null`() {
        val task = GradleTask(description = "Run tests", gradleTask = "./gradlew test")

        assertThat(task.verifyHook).isNull()
    }

    @Test
    fun `GradleTask accepts custom expectedOutput`() {
        val task = GradleTask(
            description = "Generate SPG",
            gradleTask = "./gradlew generateSPG",
            expectedOutput = "SPG generated"
        )

        assertThat(task.expectedOutput).isEqualTo("SPG generated")
    }

    @Test
    fun `GradleTask accepts custom maxRetries within bounds`() {
        val task = GradleTask(
            description = "Compile",
            gradleTask = "./gradlew build",
            maxRetries = 5
        )

        assertThat(task.maxRetries).isEqualTo(5)
    }

    @Test
    fun `GradleTask accepts custom verifyHook`() {
        val task = GradleTask(
            description = "Run tests",
            gradleTask = "./gradlew test",
            verifyHook = "grep FAILED"
        )

        assertThat(task.verifyHook).isEqualTo("grep FAILED")
    }

    @Test
    fun `GradleTask rejects blank expectedOutput`() {
        assertThatThrownBy {
            GradleTask(
                description = "Run tests",
                gradleTask = "./gradlew test",
                expectedOutput = "   "
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("expectedOutput")
    }

    @Test
    fun `GradleTask rejects maxRetries below 1`() {
        assertThatThrownBy {
            GradleTask(
                description = "Run tests",
                gradleTask = "./gradlew test",
                maxRetries = 0
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("maxRetries")
    }

    @Test
    fun `GradleTask rejects maxRetries above 10`() {
        assertThatThrownBy {
            GradleTask(
                description = "Run tests",
                gradleTask = "./gradlew test",
                maxRetries = 11
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("maxRetries")
    }

    @Test
    fun `GradleTask accepts maxRetries at lower bound 1`() {
        assertThatCode {
            GradleTask(
                description = "Run tests",
                gradleTask = "./gradlew test",
                maxRetries = 1
            )
        }.doesNotThrowAnyException()
    }

    @Test
    fun `GradleTask accepts maxRetries at upper bound 10`() {
        assertThatCode {
            GradleTask(
                description = "Run tests",
                gradleTask = "./gradlew test",
                maxRetries = 10
            )
        }.doesNotThrowAnyException()
    }

    @Test
    fun `GradleTask preserves legacy 5-fields construction with verification defaults`() {
        val task = GradleTask(
            description = "Edit config",
            gradleTask = "",
            toolType = TaskType.EDIT_FILE,
            target = "build.gradle.kts"
        )

        assertThat(task.expectedOutput).isEqualTo("BUILD SUCCESSFUL")
        assertThat(task.maxRetries).isEqualTo(3)
        assertThat(task.verifyHook).isNull()
        assertThat(task.toolType).isEqualTo(TaskType.EDIT_FILE)
        assertThat(task.target).isEqualTo("build.gradle.kts")
    }
}