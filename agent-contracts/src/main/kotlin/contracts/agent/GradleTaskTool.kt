package contracts.agent

import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

private val BLACKLIST_PATTERNS =
    listOf(
        Regex("""^(clean|cleanBuild|cleanAll)$""", RegexOption.IGNORE_CASE),
        Regex("""^(delete|deleteAll)$""", RegexOption.IGNORE_CASE),
        Regex("""^rm$""", RegexOption.IGNORE_CASE),
        Regex("""^(reset|nuke|purge|wipeout)$""", RegexOption.IGNORE_CASE),
        Regex("""^\-\-refresh-dependencies$"""),
    )

object GradleTaskTool : AgentTool<GradleTaskTool.Args, String> {
    @Serializable
    data class Args(
        val project: String,
        val task: String,
        val args: List<String> = emptyList(),
        val workingDir: String = ".",
        val timeoutSeconds: Long = 300,
    )

    override val name: String = "gradle_task"
    override val description: String =
        "Execute a Gradle task in another borough. " +
            "Available projects: :training-plugin, :codex-plugin, :quizz-benchmark-plugin, :codebase-plugin. " +
            "Available tasks: generateSPD, transformToPdf, generateQuiz, collectCompositeContext. " +
            "Returns EXIT code + stdout (max 8000 chars)."

    private val log = LoggerFactory.getLogger(GradleTaskTool::class.java)

    override suspend fun execute(input: Args): String {
        val validation = validate(input)
        if (validation is ValidationOutcome.Invalid) {
            // XF-FP2-6 — the problems are values; the boundary formats them.
            return "BLOCKED: " + validation.problems.joinToString("; ") { it.message }
        }

        val workspaceRoot = System.getenv("WORKSPACE_ROOT") ?: System.getProperty("user.dir")

        val command =
            buildList {
                add("./gradlew")
                add("${input.project}:${input.task}")
                addAll(input.args)
            }

        log.info("[GradleTaskTool] Executing: ${command.joinToString(" ")} (timeout=${input.timeoutSeconds}s)")

        return try {
            val process =
                ProcessBuilder(command)
                    .directory(File(workspaceRoot))
                    .redirectErrorStream(true)
                    .start()

            val output =
                process.inputStream
                    .bufferedReader()
                    .readText()
                    .take(8000)
            val exited = process.waitFor(input.timeoutSeconds, TimeUnit.SECONDS)

            if (!exited) {
                process.destroyForcibly()
                "TIMEOUT: task '${input.task}' exceeded ${input.timeoutSeconds}s"
            } else {
                "EXIT: ${process.exitValue()}\n$output"
            }
        } catch (e: Exception) {
            log.error("[GradleTaskTool] Failed: {}", e.message)
            "ERROR: ${e.message}"
        }
    }

    /**
     * XF-FP2-6 — the four checks are **independent**, so they accumulate: an
     * invalid input reports every problem at once (no `Result`, no `Throwable`,
     * no first-failure short-circuit).
     */
    override fun validate(input: Args): ValidationOutcome {
        val problems =
            buildList {
                if (BLACKLIST_PATTERNS.any { it.containsMatchIn(input.task) }) {
                    add(
                        ValidationProblem(
                            code = "BLACKLISTED_TASK",
                            message = "Task '${input.task}' is blacklisted (destructive operation)",
                        ),
                    )
                }
                if (input.project.isBlank()) {
                    add(ValidationProblem(code = "BLANK_PROJECT", message = "Project must not be blank"))
                }
                if (input.task.isBlank()) {
                    add(ValidationProblem(code = "BLANK_TASK", message = "Task must not be blank"))
                }
                if (input.timeoutSeconds < 1 || input.timeoutSeconds > 600) {
                    add(
                        ValidationProblem(
                            code = "TIMEOUT_OUT_OF_RANGE",
                            message = "Timeout must be between 1 and 600 seconds",
                        ),
                    )
                }
            }
        return if (problems.isEmpty()) ValidationOutcome.Valid else ValidationOutcome.Invalid(problems)
    }
}
