package contracts.llmpool

/**
 * Politique de réinitialisation de quota.
 */
enum class ResetPolicy {
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER,
    MANUAL
}
