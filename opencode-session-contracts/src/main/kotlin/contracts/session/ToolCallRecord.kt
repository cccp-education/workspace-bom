package contracts.session

import java.time.Instant

data class ToolCallRecord(
    val toolName: String,
    val args: Map<String, String> = emptyMap(),
    val result: String = "",
    val timestamp: Instant = Instant.now(),
)
