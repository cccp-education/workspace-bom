package contracts.agent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * XF-FP2-6 (S-331) — `AgentTool.validate` used to return `Result<Unit>`, which
 * erases the error to `Throwable` (and cannot express several problems at once).
 * The contract now carries a **sealed** outcome with typed, non-empty problems,
 * still pure kotlin-stdlib (N0 rule: no Arrow in contracts).
 */
class ValidationOutcomeTest {
    @Test
    fun `valid is a singleton value`() {
        assertTrue(ValidationOutcome.Valid === ValidationOutcome.Valid)
    }

    @Test
    fun `an invalid outcome carries its problems`() {
        val problems = listOf(ValidationProblem("BLANK_PROJECT", "Project must not be blank"))

        val outcome = ValidationOutcome.Invalid(problems)

        assertEquals(problems, (outcome as ValidationOutcome.Invalid).problems)
    }

    @Test
    fun `an invalid outcome cannot be empty`() {
        assertThrows(IllegalArgumentException::class.java) { ValidationOutcome.Invalid(emptyList()) }
    }

    @Test
    fun `invalid outcomes compare by content`() {
        val problem = ValidationProblem("BLANK_TASK", "Task must not be blank")

        assertEquals(ValidationOutcome.Invalid(listOf(problem)), ValidationOutcome.Invalid(listOf(problem)))
    }
}
