package contracts.session

import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToolCallRecordTest {

    @Test
    fun `construct with minimal fields`() {
        val record = ToolCallRecord(toolName = "read_file")
        assertEquals("read_file", record.toolName)
        assertTrue(record.args.isEmpty())
        assertEquals("", record.result)
        assertTrue(record.timestamp <= Instant.now())
    }

    @Test
    fun `construct with all fields`() {
        val ts = Instant.parse("2026-06-14T10:00:00Z")
        val record = ToolCallRecord(
            toolName = "exec_gradle",
            args = mapOf("task" to "compileKotlin", "projectDir" to "/tmp/test"),
            result = "BUILD SUCCESSFUL in 2s",
            timestamp = ts,
        )
        assertEquals("exec_gradle", record.toolName)
        assertEquals(2, record.args.size)
        assertEquals("compileKotlin", record.args["task"])
        assertEquals("BUILD SUCCESSFUL in 2s", record.result)
        assertEquals(ts, record.timestamp)
    }

    @Test
    fun `default timestamp is recent`() {
        val before = Instant.now().minusSeconds(1)
        val record = ToolCallRecord(toolName = "list_directory")
        val after = Instant.now().plusSeconds(1)
        assertTrue(record.timestamp >= before)
        assertTrue(record.timestamp <= after)
    }
}
