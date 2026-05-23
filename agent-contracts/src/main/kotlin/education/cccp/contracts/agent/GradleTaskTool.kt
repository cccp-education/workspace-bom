package education.cccp.contracts.agent

import ai.koog.serialization.typeToken
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

private val BLACKLIST_PATTERNS = listOf(
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
        val timeoutSeconds: Long = 300
    )

    override val name: String = "gradle_task"
    override val description: String = "Execute a Gradle task in another borough. " +
        "Available projects: :training-plugin, :codex-plugin, :quizz-benchmark-plugin, :codebase-plugin. " +
        "Available tasks: generateSPD, transformToPdf, generateQuiz, collectCompositeContext. " +
        "Returns EXIT code + stdout (max 8000 chars)."

    private val log = LoggerFactory.getLogger(GradleTaskTool::class.java)

    override suspend fun execute(input: Args): String {
        val validation = validate(input)
        if (validation.isFailure) return "BLOCKED: ${validation.exceptionOrNull()?.message}"

        val workspaceRoot = System.getenv("WORKSPACE_ROOT") ?: System.getProperty("user.dir")

        val command = buildList {
            add("./gradlew")
            add("${input.project}:${input.task}")
            addAll(input.args)
        }

        log.info("[GradleTaskTool] Executing: ${command.joinToString(" ")} (timeout=${input.timeoutSeconds}s)")

        return try {
            val process = ProcessBuilder(command)
                .directory(File(workspaceRoot))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText().take(8000)
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

    override fun validate(input: Args): Result<Unit> {
        for (pattern in BLACKLIST_PATTERNS) {
            if (pattern.containsMatchIn(input.task)) {
                return Result.failure(SecurityException("Task '${input.task}' is blacklisted (destructive operation)"))
            }
        }
        if (input.project.isBlank()) {
            return Result.failure(IllegalArgumentException("Project must not be blank"))
        }
        if (input.task.isBlank()) {
            return Result.failure(IllegalArgumentException("Task must not be blank"))
        }
        if (input.timeoutSeconds < 1 || input.timeoutSeconds > 600) {
            return Result.failure(IllegalArgumentException("Timeout must be between 1 and 600 seconds"))
        }
        return Result.success(Unit)
    }
}
