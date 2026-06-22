package contracts.session

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionStatusTest {

    @Test
    fun `all four status values exist`() {
        val values = SessionStatus.entries
        assertEquals(4, values.size)
        assertTrue(values.contains(SessionStatus.IN_PROGRESS))
        assertTrue(values.contains(SessionStatus.COMPLETED))
        assertTrue(values.contains(SessionStatus.ERROR))
        assertTrue(values.contains(SessionStatus.CANCELLED))
    }

    @Test
    fun `valueOf parses correctly`() {
        assertEquals(SessionStatus.IN_PROGRESS, SessionStatus.valueOf("IN_PROGRESS"))
        assertEquals(SessionStatus.COMPLETED, SessionStatus.valueOf("COMPLETED"))
        assertEquals(SessionStatus.ERROR, SessionStatus.valueOf("ERROR"))
        assertEquals(SessionStatus.CANCELLED, SessionStatus.valueOf("CANCELLED"))
    }
}
