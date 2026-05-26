package contracts.llmpool

/**
 * Configuration de quota pour une instance LLM.
 *
 * Adapté du pattern QuotaConfig de graphify-gradle/api-key-pool.
 * N0 — zéro dépendance.
 *
 * @param limitValue quota maximum (ex: 1000 requêtes ou 1M tokens)
 * @param thresholdPercent seuil de déclenchement rotation (0-100)
 * @param resetPolicy politique de réinitialisation
 */
data class QuotaConfig(
    val limitValue: Long = 1000,
    val thresholdPercent: Int = 80,
    val resetPolicy: ResetPolicy = ResetPolicy.DAILY
) {
    init {
        require(limitValue > 0) { "limitValue must be positive" }
        require(thresholdPercent in 1..100) { "thresholdPercent must be between 1 and 100" }
    }

    fun isExceeded(currentUsage: Long): Boolean {
        val threshold = (limitValue * thresholdPercent) / 100
        return currentUsage >= threshold
    }
}
