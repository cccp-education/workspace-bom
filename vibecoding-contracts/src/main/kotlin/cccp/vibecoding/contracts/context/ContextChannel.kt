package cccp.vibecoding.contracts.context

/**
 * Canal de contexte — sealed class 4 variants.
 * Extraite de codebase.rag dans N0 pour partage N1→N2 sans violation DAG.
 */
sealed class ContextChannel(val source: String, val content: String) {
    /** Contexte chargé immédiatement (fichiers .adoc EAGER) */
    class Eager(content: String) : ContextChannel("EAGER", content)

    /** Contexte extrait du RAG pgvector (similarité sémantique) */
    class Rag(content: String) : ContextChannel("RAG", content)

    /** Contexte extrait du graphe de dépendances Graphify */
    class Graphify(content: String) : ContextChannel("Graphify", content)

    /** Contexte chargé à la demande (fichiers LAZY) */
    class Resource(content: String) : ContextChannel("Resource", content)

    companion object {
        fun estimateTokens(text: String): Int = (text.length / 3.5).toInt().coerceAtLeast(1)

        /** Token budget par défaut utilisé pour les calculs tronqués. */
        const val DEFAULT_TOKEN_BUDGET: Int = 8000
    }

    /**
     * Tronque ce canal au nombre de tokens maximum spécifié.
     * Ne modifie pas le contenu si déjà en-dessous du budget.
     */
    fun truncateToTokens(maxTokens: Int): ContextChannel {
        val lines = content.lines()
        val truncated = lines.fold("" to 0) { (acc, count), line ->
            val lineTokens = estimateTokens(line)
            if (count + lineTokens <= maxTokens) (acc + line + "\n") to (count + lineTokens)
            else acc to count
        }.first
        return when (this) {
            is Eager -> Eager(truncated)
            is Rag -> Rag(truncated)
            is Graphify -> Graphify(truncated)
            is Resource -> Resource(truncated)
        }
    }
}

/**
 * Budget token proportionnel 40/30/20/10.
 * Les valeurs (eager, rag, graphify, resource) sont des fractions (0.0–1.0) qui doivent sommer à 1.0.
 * Les propriétés `*Tokens` calculent le nombre de tokens à partir du budget total (DEFAULT_TOKEN_BUDGET).
 */
data class ChannelBudget(
    val eager: Double = 0.40,
    val rag: Double = 0.30,
    val graphify: Double = 0.20,
    val resource: Double = 0.10
) {
    init {
        val sum = eager + rag + graphify + resource
        require(kotlin.math.abs(sum - 1.0) < 0.001) { "ChannelBudget must sum to 1.0, got $sum" }
    }

    val eagerTokens: Int get() = (ContextChannel.DEFAULT_TOKEN_BUDGET * eager).toInt()
    val ragTokens: Int get() = (ContextChannel.DEFAULT_TOKEN_BUDGET * rag).toInt()
    val graphifyTokens: Int get() = (ContextChannel.DEFAULT_TOKEN_BUDGET * graphify).toInt()
    val resourceTokens: Int get() = (ContextChannel.DEFAULT_TOKEN_BUDGET * resource).toInt()

    fun applyBudget(channels: List<ContextChannel>): List<ContextChannel> {
        val fractions = listOf(eager, rag, graphify, resource)
        val tokenCounts = listOf(eagerTokens, ragTokens, graphifyTokens, resourceTokens)
        return channels.zip(tokenCounts) { channel, maxTokens ->
            channel.truncateToTokens(maxTokens)
        }
    }
}
