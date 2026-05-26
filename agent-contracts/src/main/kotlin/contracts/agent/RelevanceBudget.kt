package contracts.agent

import kotlin.math.exp
import kotlin.math.sqrt
import java.util.Locale

data class ChannelScore(
    val name: String,
    val content: String,
    val score: Double
)

data class RelevanceBudget(
    val totalTokens: Int = 8000,
    val channels: List<ChannelScore>
) {
    companion object {
        fun compute(
            intention: String,
            channels: Map<String, String>,
            totalTokens: Int = 8000,
            intentEmbedding: FloatArray? = null,
            channelEmbeddings: Map<String, FloatArray> = emptyMap()
        ): RelevanceBudget {
            if (channels.isEmpty()) {
                return RelevanceBudget(channels = emptyList())
            }

            val scores: List<Pair<String, Double>> = if (intentEmbedding != null) {
                channels.map { (name, content) ->
                    val channelVec = channelEmbeddings[name] ?: embedNaive(content)
                    val sim = cosineSimilarity(intentEmbedding, channelVec)
                    name to sim
                }
            } else {
                channels.map { (name, content) ->
                    name to naiveSimilarity(intention, content)
                }
            }

            val expScores = scores.map { (name, score) ->
                name to exp(score * 5.0)
            }
            val sumExp = expScores.sumOf { it.second }
            val n = scores.size

            val budgets: List<Pair<String, Int>> = expScores.map { (name, expScore) ->
                val proportion = if (sumExp > 0.0) expScore / sumExp else 1.0 / n
                name to (totalTokens * proportion).toInt()
            }

            val minTokens = (totalTokens * 0.05).toInt().coerceAtLeast(100)
            val adjusted = budgets.map { (name, tokens) ->
                name to tokens.coerceAtLeast(minTokens)
            }

            val adjustedTotal = adjusted.sumOf { it.second.toLong() }
            val factor = if (adjustedTotal > 0) totalTokens.toDouble() / adjustedTotal else 1.0
            val final = adjusted.map { (name, tokens) ->
                name to (tokens * factor).toInt()
            }

            val channelScores = final.map { (name, budget) ->
                val originalScore = scores.find { it.first == name }?.second ?: 0.0
                val content = channels[name] ?: ""
                ChannelScore(name, content.take(budget * 4), originalScore)
            }

            return RelevanceBudget(
                totalTokens = totalTokens,
                channels = channelScores
            )
        }

        private fun embedNaive(text: String): FloatArray {
            val words = text.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }
            val vocab = ('a'..'z').map { it.toString() } + ('0'..'9').map { it.toString() }
            val vec = FloatArray(36) { 0f }
            for (word in words) {
                for (ch in word) {
                    val idx = vocab.indexOf(ch.toString())
                    if (idx >= 0) vec[idx] += 1f
                }
            }
            val norm = sqrt(vec.sumOf { (it * it).toDouble() })
            if (norm > 0) {
                for (i in vec.indices) vec[i] = (vec[i] / norm).toFloat()
            }
            return vec
        }

        private fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
            var dot = 0.0
            var normA = 0.0
            var normB = 0.0
            val len = minOf(a.size, b.size)
            for (i in 0 until len) {
                dot += a[i].toDouble() * b[i].toDouble()
                normA += a[i].toDouble() * a[i].toDouble()
                normB += b[i].toDouble() * b[i].toDouble()
            }
            return if (normA > 0.0 && normB > 0.0) dot / (sqrt(normA) * sqrt(normB)) else 0.0
        }

        private fun naiveSimilarity(intention: String, content: String): Double {
            val intentWords = intention.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }.toSet()
            val contentWords = content.lowercase().split(Regex("\\W+")).filter { it.isNotBlank() }.toSet()
            if (intentWords.isEmpty() || contentWords.isEmpty()) return 0.0
            val intersection = intentWords.intersect(contentWords).size.toDouble()
            return intersection / sqrt(intentWords.size.toDouble() * contentWords.size.toDouble())
        }
    }

    fun assemble(): String {
        return channels.joinToString("\n\n") { channel ->
            val truncated = channel.content
            val scoreStr = String.format(Locale.US, "%.2f", channel.score)
            "--- ${channel.name} (score=$scoreStr) ---\n$truncated"
        }
    }

    fun tokenAllocation(): Map<String, Int> {
        return channels.associate { it.name to it.content.length / 4 }
    }
}
