package contracts.ocr

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OcrEngineTest {

    @Test
    fun `should implement fun interface process`() {
        val engine: OcrEngine = OcrEngine { request ->
            OcrResult.of(
                text = "stub",
                confidence = 1.0,
                language = request.language,
                model = "test-engine"
            )
        }
        val request = OcrRequest(ByteArray(5), "image/png", "fr")
        val result = engine.process(request)
        assertThat(result.structuredText).isEqualTo("stub")
        assertThat(result.language).isEqualTo("fr")
        assertThat(result.model).isEqualTo("test-engine")
    }

    @Test
    fun `should allow lambda implementation via fun interface`() {
        val engine: OcrEngine = { req: OcrRequest ->
            OcrResult.of(req.language, 0.5, req.language, "lambda")
        }
        val result = engine.process(OcrRequest(ByteArray(0), "image/tiff", "de"))
        assertThat(result.language).isEqualTo("de")
    }

    @Test
    fun `should support throwing engine`() {
        val engine: OcrEngine = { _ -> error("OCR failed") }
        val thrown = runCatching { engine.process(OcrRequest(ByteArray(0), "image/png", "fr")) }
        assertThat(thrown.isFailure).isTrue()
        assertThat(thrown.exceptionOrNull()).hasMessage("OCR failed")
    }
}