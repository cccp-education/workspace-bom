package contracts.ci

/**
 * One `run: |` shell block of a GitHub Actions file.
 *
 * @property line 1-based line of the `run:` key in the source file.
 * @property commands effective commands (comments, blank lines, shell keywords and
 *   line continuations already folded).
 * @property errexit whether the block enables `errexit` (`set -e…` / `set -o errexit`).
 */
data class ShellBlock(
    val line: Int,
    val commands: List<String>,
    val errexit: Boolean,
)

/** A defect found by [CiShellAudit]. */
sealed interface ShellFinding {
    val source: String
    val line: Int
    val command: String

    /** A multi-command block invokes a gate but cannot fail on it (no errexit). */
    data class NoErrexit(
        override val source: String,
        override val line: Int,
        override val command: String,
    ) : ShellFinding

    /** A gate is neutralised by a `|| true` / `|| echo` fallback. */
    data class SwallowedGate(
        override val source: String,
        override val line: Int,
        override val command: String,
    ) : ShellFinding
}

/**
 * MEM-CI-GATE (suite S-338) — textual audit of the CI exit codes.
 *
 * GitHub Actions runs a multi-command `run: |` block with the **last** command's
 * exit status unless errexit is enabled. A gate followed by a report (`echo`) is
 * therefore a **false green**: the P0 of S-335 (`n0-contracts`) hid a red
 * `./gradlew check` behind a trailing `echo "::endgroup::"` for two sessions.
 *
 * Pure and I/O-free: the workflow text is the contract. Line continuations are
 * folded before the rules apply, and a step declaring `continue-on-error: true` is
 * exempt from the swallow rule (the neutralisation is then explicit and intended).
 */
object CiShellAudit {
    private val RUN_LITERAL = Regex("""^([ \t]*)run:[ \t]*\|.*$""")
    private val STEP_ITEM = Regex("""^([ \t]*)- \S.*$""")
    private val CONTINUE_ON_ERROR = Regex("""continue-on-error:[ \t]*true""")

    /** A shell gate: something whose failure must fail the step. */
    private val GATE =
        Regex(
            """(^|[\s;&|(])(\./gradlew|gradlew|npm|npx|bash|sh|shellcheck|ktlint|python3?|ffmpeg)(\s|$)""",
        )

    private val SWALLOW = Regex("""\|\|[ \t]*(true|echo)\b""")
    private val ERREXIT_LONG = Regex("""(^|[\s;&|])set[ \t]+-o[ \t]+errexit\b""")
    private val ERREXIT_SHORT = Regex("""(^|[\s;&|])set[ \t]+-([a-zA-Z]+)""")

    private val KEYWORDS = setOf("fi", "then", "else", "do", "done", "esac", "}", "{", ";;")
    private val KEYWORD_START = Regex("""^(if|elif|while|until|for|case)\b""")

    /**
     * Extracts every `run: |` literal block of [text].
     *
     * A folded scalar (`run: >`) carries a single command spread over several lines:
     * it is deliberately **not** a block, otherwise the long `./gradlew … -x …`
     * invocations would be reported as gates without errexit (false positives).
     */
    fun blocks(text: String): List<ShellBlock> {
        val lines = text.lines()
        val blocks = mutableListOf<ShellBlock>()
        var index = 0
        while (index < lines.size) {
            val header = RUN_LITERAL.find(lines[index])
            if (header == null) {
                index++
                continue
            }
            val indentation = header.groupValues[1].length
            val collected = mutableListOf<Pair<Int, String>>()
            var cursor = index + 1
            while (cursor < lines.size) {
                val raw = lines[cursor]
                if (raw.isBlank()) {
                    cursor++
                    continue
                }
                val indent = raw.takeWhile { it == ' ' || it == '\t' }.length
                if (indent <= indentation) break
                collected += (cursor + 1) to raw.trim()
                cursor++
            }
            if (collected.isNotEmpty()) {
                val logical = fold(collected)
                blocks +=
                    ShellBlock(
                        line = index + 1,
                        commands = logical.map { it.second }.filterNot(::isKeyword),
                        errexit = logical.any { enablesErrexit(it.second) },
                    )
            }
            index = cursor
        }
        return blocks
    }

    /** Every defect of [text] (a workflow or a composite action), attributed to [source]. */
    fun findings(
        text: String,
        source: String,
    ): List<ShellFinding> {
        val missingErrexit =
            blocks(text).mapNotNull { block ->
                if (block.commands.size >= 2 && !block.errexit && block.commands.any { GATE.containsMatchIn(it) }) {
                    ShellFinding.NoErrexit(source, block.line, block.commands.last())
                } else {
                    null
                }
            }
        val intentional = intentionalLines(text)
        val swallowed =
            fold(text.lines().mapIndexed { offset, raw -> (offset + 1) to raw.trim() })
                .filter { (number, line) -> number !in intentional && GATE.containsMatchIn(line) }
                .filter { (_, line) -> SWALLOW.containsMatchIn(line) }
                .map { (number, line) -> ShellFinding.SwallowedGate(source, number, line) }
        return missingErrexit + swallowed
    }

    /** Folds `\` line continuations, keeping the line number of the first physical line. */
    private fun fold(lines: List<Pair<Int, String>>): List<Pair<Int, String>> {
        val logical = mutableListOf<Pair<Int, String>>()
        var pending: Pair<Int, String>? = null
        lines.forEach { (number, line) ->
            if (line.startsWith("#")) return@forEach
            val joined = pending?.let { (start, text) -> start to "$text $line" } ?: (number to line)
            if (joined.second.endsWith("\\")) {
                pending = joined.first to joined.second.dropLast(1).trimEnd()
                return@forEach
            }
            pending = null
            logical += joined
        }
        pending?.let { logical += it }
        return logical
    }

    /**
     * 1-based line numbers of the steps declaring `continue-on-error: true`.
     *
     * Such a step states that the failure is tolerated: neutralising it with
     * `|| echo "non-fatal …"` is then intentional, not a swallowed gate.
     */
    private fun intentionalLines(text: String): Set<Int> {
        val lines = text.lines()
        val intentional = mutableSetOf<Int>()
        var index = 0
        while (index < lines.size) {
            val item = STEP_ITEM.find(lines[index])
            if (item == null) {
                index++
                continue
            }
            val indentation = item.groupValues[1].length
            var end = index
            var cursor = index + 1
            while (cursor < lines.size) {
                val raw = lines[cursor]
                if (raw.isBlank()) {
                    cursor++
                    continue
                }
                val indent = raw.takeWhile { it == ' ' }.length
                if (indent < indentation || (indent == indentation && raw.trimStart().startsWith("- "))) break
                end = cursor
                cursor++
            }
            if (lines.subList(index, end + 1).any { CONTINUE_ON_ERROR.containsMatchIn(it) }) {
                intentional += (index + 1)..(end + 1)
            }
            index = end + 1
        }
        return intentional
    }

    private fun isKeyword(command: String): Boolean = command in KEYWORDS || KEYWORD_START.containsMatchIn(command)

    private fun enablesErrexit(line: String): Boolean {
        if (ERREXIT_LONG.containsMatchIn(line)) return true
        val short = ERREXIT_SHORT.find(line) ?: return false
        return 'e' in short.groupValues[2]
    }
}
