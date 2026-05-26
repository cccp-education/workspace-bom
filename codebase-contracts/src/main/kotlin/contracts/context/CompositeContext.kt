package contracts.context

/**
 * Contexte composite assemblé par le pipeline RAG multi-canal.
 * Contient les sections EAGER, RAG pgvector, Graphify, Docs.
 * Source unique de vérité N0 pour tous les boroughs N1/N2.
 */
data class CompositeContext(
    val eagerSection: String,
    val ragSection: String,
    val graphifySection: String,
    val docsSection: String = "",
    val config: CompositeContextConfig
) {
    fun toChannels(): List<ContextChannel> = listOf(
        ContextChannel.Eager(eagerSection),
        ContextChannel.Rag(ragSection),
        ContextChannel.Graphify(graphifySection),
        ContextChannel.Docs(docsSection),
        ContextChannel.Resource("")
    )

    fun channelsWithBudget(budget: ChannelBudget): List<ContextChannel> =
        budget.applyBudget(toChannels())
}
