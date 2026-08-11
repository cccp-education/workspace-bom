package contracts.agent

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class GradleTaskTest {

    @Test
    fun `GradleTask defaults to GRADLE toolType with blank target`() {
        val task = GradleTask(description = "Run tests", gradleTask = "./gradlew test")

        assertThat(task.toolType).isEqualTo(TaskType.GRADLE)
        assertThat(task.target).isEqualTo("")
    }

    @Test
    fun `GradleTask accepts EDIT_FILE toolType with target`() {
        val task = GradleTask(
            description = "Patch build.gradle.kts",
            gradleTask = "",
            toolType = TaskType.EDIT_FILE,
            target = "build.gradle.kts"
        )

        assertThat(task.toolType).isEqualTo(TaskType.EDIT_FILE)
        assertThat(task.target).isEqualTo("build.gradle.kts")
    }

    @Test
    fun `GradleTask accepts EXEC_SHELL toolType with target`() {
        val task = GradleTask(
            description = "Run npm install",
            gradleTask = "",
            toolType = TaskType.EXEC_SHELL,
            target = "npm install"
        )

        assertThat(task.toolType).isEqualTo(TaskType.EXEC_SHELL)
        assertThat(task.target).isEqualTo("npm install")
    }

    @Test
    fun `GradleTask rejects blank description`() {
        assertThatThrownBy {
            GradleTask(description = "   ", gradleTask = "./gradlew test")
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("description")
    }

    @Test
    fun `GradleTask rejects blank gradleTask when toolType is GRADLE`() {
        assertThatThrownBy {
            GradleTask(description = "Run tests", gradleTask = "  ")
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("gradleTask")
    }

    @Test
    fun `GradleTask rejects blank target when toolType is EDIT_FILE`() {
        assertThatThrownBy {
            GradleTask(
                description = "Patch file",
                gradleTask = "",
                toolType = TaskType.EDIT_FILE,
                target = "  "
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("target")
    }

    @Test
    fun `GradleTask rejects blank target when toolType is EXEC_SHELL`() {
        assertThatThrownBy {
            GradleTask(
                description = "Run shell",
                gradleTask = "",
                toolType = TaskType.EXEC_SHELL,
                target = ""
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("target")
    }

    @Test
    fun `GradleTask allows blank gradleTask when toolType is not GRADLE`() {
        assertThatCode {
            GradleTask(
                description = "Edit file",
                gradleTask = "",
                toolType = TaskType.EDIT_FILE,
                target = "path/to/file"
            )
        }.doesNotThrowAnyException()
    }

    @Test
    fun `TaskType enum has exactly three variants in stable order`() {
        assertThat(TaskType.values().toList()).containsExactly(
            TaskType.GRADLE,
            TaskType.EDIT_FILE,
            TaskType.EXEC_SHELL
        )
    }
}