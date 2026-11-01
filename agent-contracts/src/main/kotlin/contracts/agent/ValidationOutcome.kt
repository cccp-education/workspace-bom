package contracts.agent

/**
 * XF-FP2-6 (S-331) — one reason an [AgentTool] input is rejected.
 *
 * [code] is a stable, machine-readable token (e.g. `BLANK_PROJECT`) so a caller
 * can branch without parsing the human message.
 */
data class ValidationProblem(
    val code: String,
    val message: String,
) {
    init {
        require(code.isNotBlank()) { "code must not be blank" }
        require(message.isNotBlank()) { "message must not be blank" }
    }
}

/**
 * XF-FP2-6 (S-331) — the outcome of [AgentTool.validate].
 *
 * Replaces `Result<Unit>` (which erased the error to `Throwable`, lost the
 * distinction between causes, and could not carry *several* problems). Sealed so
 * a caller `when` is exhaustive, with **no** Arrow type: contracts stay
 * kotlin-stdlib only (the N0 rule).
 *
 * The checks of a tool are independent, so an invalid outcome **accumulates**
 * every problem rather than stopping at the first one.
 */
sealed interface ValidationOutcome {
    /** The input is accepted; the tool may execute. */
    data object Valid : ValidationOutcome

    /** The input is rejected; [problems] is never empty. */
    data class Invalid(
        val problems: List<ValidationProblem>,
    ) : ValidationOutcome {
        init {
            require(problems.isNotEmpty()) { "an invalid outcome must carry at least one problem" }
        }
    }
}
