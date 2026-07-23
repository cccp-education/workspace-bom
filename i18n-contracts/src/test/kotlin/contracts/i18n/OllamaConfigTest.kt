package contracts.i18n

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OllamaConfigTest {

    @Test
    fun `default config has no device keys`() {
        val config = OllamaConfig()
        assertFalse(config.isConfigured)
        assertEquals("gemma4:31b-cloud", config.model)
        assertEquals(11437, config.portStart)
        assertEquals(11465, config.portEnd)
        assertEquals(11437..11465, config.portRange)
    }

    @Test
    fun `config with device keys is configured`() {
        val config = OllamaConfig(
            deviceKeys = listOf(OllamaDeviceKey("k1", "pk1"))
        )
        assertTrue(config.isConfigured)
    }

    @Test
    fun `baseUrls generates correct range`() {
        val config = OllamaConfig(portStart = 11437, portEnd = 11439)
        val urls = config.baseUrls()
        assertEquals(3, urls.size)
        assertEquals("http://localhost:11437", urls[0])
        assertEquals("http://localhost:11438", urls[1])
        assertEquals("http://localhost:11439", urls[2])
    }

    @Test
    fun `reject port below 11437`() {
        assertThrows<IllegalArgumentException> {
            OllamaConfig(portStart = 11436)
        }
    }

    @Test
    fun `reject port above 11465`() {
        assertThrows<IllegalArgumentException> {
            OllamaConfig(portEnd = 11466)
        }
    }

    @Test
    fun `reject inverted port range`() {
        assertThrows<IllegalArgumentException> {
            OllamaConfig(portStart = 11465, portEnd = 11437)
        }
    }

    @Test
    fun `reject blank model`() {
        assertThrows<IllegalArgumentException> {
            OllamaConfig(model = "  ")
        }
    }

    @Test
    fun `reject zero timeout`() {
        assertThrows<IllegalArgumentException> {
            OllamaConfig(timeoutSeconds = 0)
        }
    }

    @Test
    fun `full config with 29 device keys`() {
        val keys = (1..29).map { i ->
            OllamaDeviceKey(keyName = "ollama-${11436 + i}", privateKey = "pk-$i")
        }
        val config = OllamaConfig(deviceKeys = keys)
        assertTrue(config.isConfigured)
        assertEquals(29, config.deviceKeys.size)
        assertEquals(29, config.baseUrls().size)
    }

    @Test
    fun `portRange derived property covers start to end`() {
        val config = OllamaConfig(portStart = 11437, portEnd = 11439)
        val range = config.portRange
        assertEquals(11437, range.first)
        assertEquals(11439, range.last)
    }
}
