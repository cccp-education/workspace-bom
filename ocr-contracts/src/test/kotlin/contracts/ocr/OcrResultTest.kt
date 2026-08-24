package contracts.ocr

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OcrResultTest {

    @Test
    fun `should create result with all fields`() {
        val result = OcrResult(
            structuredText = "= Title\n\nContent",
            confidence = 0.95,
            language = "fr",
            sourceFormat = "image/png",
            generatedAt = "2026-08-24T10:00:00Z",
            model = "qwen3-vl:235b-cloud",
            metadata = mapOf("page" to "1")
        )
        assertThat(result.structuredText).isEqualTo("= Title\n\nContent")
        assertThat(result.confidence).isEqualTo(0.95)
        assertThat(result.language).isEqualTo("fr")
        assertThat(result.sourceFormat).isEqualTo("image/png")
        assertThat(result.generatedAt).isEqualTo("2026-08-24T10:00:00Z")
        assertThat(result.model).isEqualTo("qwen3-vl:235b-cloud")
        assertThat(result.metadata).containsEntry("page", "1")
    }

    @Test
    fun `should create result with empty metadata default`() {
        val result = OcrResult(
            structuredText = "text",
            confidence = 0.5,
            language = "en",
            sourceFormat = "image/jpeg",
            generatedAt = "2026-01-01T00:00:00Z",
            model = "gemini-2.5-flash"
        )
        assertThat(result.metadata).isEmpty()
    }

    @Test
    fun `of factory should create result with sensible defaults`() {
        val result = OcrResult.of(
            text = "Extracted text",
            confidence = 0.88,
            language = "fr",
            model = "qwen3-vl:235b-cloud"
        )
        assertThat(result.structuredText).isEqualTo("Extracted text")
        assertThat(result.confidence).isEqualTo(0.88)
        assertThat(result.language).isEqualTo("fr")
        assertThat(result.sourceFormat).isEqualTo("image/png")
        assertThat(result.generatedAt).isNotBlank()
        assertThat(result.model).isEqualTo("qwen3-vl:235b-cloud")
        assertThat(result.metadata).isEmpty()
    }

    @Test
    fun `of factory should accept metadata`() {
        val result = OcrResult.of(
            text = "text",
            confidence = 0.9,
            language = "de",
            model = "gemini-2.5-pro",
            metadata = mapOf("title" to "Invoice", "page" to "2")
        )
        assertThat(result.metadata)
            .containsEntry("title", "Invoice")
            .containsEntry("page", "2")
    }

    @Test
    fun `data class equals should compare all fields`() {
        val r1 = OcrResult("text", 0.9, "fr", "image/png", "2026-01-01T00:00:00Z", "model")
        val r2 = OcrResult("text", 0.9, "fr", "image/png", "2026-01-01T00:00:00Z", "model")
        assertThat(r1).isEqualTo(r2)
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode())
    }

    @Test
    fun `data class equals should differ on metadata`() {
        val r1 = OcrResult("text", 0.9, "fr", "image/png", "ts", "model", mapOf("k" to "v"))
        val r2 = OcrResult("text", 0.9, "fr", "image/png", "ts", "model", emptyMap())
        assertThat(r1).isNotEqualTo(r2)
    }
}