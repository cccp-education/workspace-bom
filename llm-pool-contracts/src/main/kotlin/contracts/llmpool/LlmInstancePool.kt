package contracts.llmpool

/**
 * Contrat N0 pour un pool d'instances LLM avec rotation automatique sur quota exceeded.
 *
 * Implémenté côté N1 (codebase-gradle, graphify-gradle) avec la logique de rotation.
 * Ce contrat définit uniquement l'interface abstraite.
 *
 * Pattern : adapté du ApiKeyPool de graphify-gradle, généralisé pour instances HTTP.
 */
interface LlmInstancePool {
    /** Nombre d'instances dans le pool */
    fun size(): Int

    /** Toutes les instances */
    fun instances(): List<LlmInstance>

    /** Prochaine instance disponible (rotation si l'actuelle est en dépassement quota) */
    fun nextInstance(): LlmInstance

    /** Vrai si le quota de cette instance est dépassé (seuil dépassé) */
    fun isQuotaExceeded(instance: LlmInstance): Boolean

    /** Reset tous les compteurs */
    fun resetUsage()
}
