package contracts.ocr

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OcrRequestTest {

    @Test
    fun `should create request with all fields`() {
        val data = byteArrayOf(0x50, 0x47, 0x0A)
        val request = OcrRequest(
            imageData = data,
            format = "image/png",
            language = "fr",
            prompt = null
        )
        assertThat(request.imageData).isEqualTo(data)
        assertThat(request.format).isEqualTo("image/png")
        assertThat(request.language).isEqualTo("fr")
        assertThat(request.prompt).isNull()
    }

    @Test
    fun `should create request with custom prompt`() {
        val request = OcrRequest(
            imageData = ByteArray(10),
            format = "image/jpeg",
            language = "en",
            prompt = "Extract tables as AsciiDoc"
        )
        assertThat(request.prompt).isEqualTo("Extract tables as AsciiDoc")
    }

    @Test
    fun `equals should compare imageData by content`() {
        val data = byteArrayOf(1, 2, 3)
        val r1 = OcrRequest(data, "image/png", "fr")
        val r2 = OcrRequest(byteArrayOf(1, 2, 3), "image/png", "fr")
        assertThat(r1).isEqualTo(r2)
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode())
    }

    @Test
    fun `equals should differ on different imageData`() {
        val r1 = OcrRequest(byteArrayOf(1, 2, 3), "image/png", "fr")
        val r2 = OcrRequest(byteArrayOf(1, 2, 4), "image/png", "fr")
        assertThat(r1).isNotEqualTo(r2)
    }

    @Test
    fun `toString should not print raw bytes`() {
        val request = OcrRequest(ByteArray(100), "image/png", "fr")
        val str = request.toString()
        assertThat(str).contains("image/png")
        assertThat(str).contains("fr")
        assertThat(str).contains("100 bytes")
        assertThat(str).doesNotContain("[B@")
    }
}