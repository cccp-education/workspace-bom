package contracts.llmpool

/**
 * Stratégie de rotation entre instances LLM.
 */
enum class RotationStrategy {
    /** Tourne séquentiellement à travers toutes les instances */
    ROUND_ROBIN,
    /** Utilise l'instance la moins sollicitée */
    LEAST_USED
}
