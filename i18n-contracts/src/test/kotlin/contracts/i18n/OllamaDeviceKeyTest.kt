package contracts.i18n

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OllamaDeviceKeyTest {

    @Test
    fun `construct valid device key`() {
        val key = OllamaDeviceKey(keyName = "ollama-11437", privateKey = "ed25519-base64-key")
        assertEquals("ollama-11437", key.keyName)
        assertEquals("ed25519-base64-key", key.privateKey)
    }

    @Test
    fun `reject blank keyName`() {
        assertThrows<IllegalArgumentException> {
            OllamaDeviceKey(keyName = "  ", privateKey = "ed25519-base64-key")
        }
    }

    @Test
    fun `reject blank privateKey`() {
        assertThrows<IllegalArgumentException> {
            OllamaDeviceKey(keyName = "ollama-11437", privateKey = "")
        }
    }

    @Test
    fun `toString masks private key`() {
        val key = OllamaDeviceKey(
            keyName = "ollama-11437",
            privateKey = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI-very-secret-private-key-material"
        )
        val str = key.toString()
        assertTrue(str.contains("keyName=ollama-11437"))
        assertTrue(str.contains("ssh-ed25519***["))
        assertTrue(str.endsWith(" chars])"))
        assertFalse(str.contains("AAAAC3Nza"))
    }
}
