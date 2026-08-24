package contracts.ocr

/**
 * OCR result contract — structured AsciiDoc output returned by an [OcrEngine].
 *
 * N0 pure type (no Jackson, no Gradle). Extracted from codex.ocr by
 * EPIC CDX-OCR-CONTRACTS (9th MEMPHIS N0 artefact).
 *
 * @property structuredText extracted text in structured AsciiDoc format
 * @property confidence OCR confidence score in [0.0, 1.0]
 * @property language detected or confirmed language code (ISO 639-1)
 * @property sourceFormat original image MIME type
 * @property generatedAt ISO-8601 timestamp of generation
 * @property model OCR/LLM model identifier used for extraction
 * @property metadata arbitrary key-value metadata (e.g. page number, document title)
 */
data class OcrResult(
    val structuredText: String,
    val confidence: Double,
    val language: String,
    val sourceFormat: String,
    val generatedAt: String,
    val model: String,
    val metadata: Map<String, String> = emptyMap()
) {
    companion object {
        /**
         * Creates a minimal [OcrResult] for testing or scaffolding.
         *
         * @param text extracted structured AsciiDoc text
         * @param confidence OCR confidence (0.0-1.0)
         * @param language ISO 639-1 language code
         * @param model LLM/OCR model identifier
         * @param metadata extra key-value pairs
         * @return a new [OcrResult] with sensible defaults
         */
        fun of(
            text: String,
            confidence: Double,
            language: String,
            model: String,
            metadata: Map<String, String> = emptyMap()
        ): OcrResult = OcrResult(
            structuredText = text,
            confidence = confidence,
            language = language,
            sourceFormat = "image/png",
            generatedAt = java.time.Instant.now().toString(),
            model = model,
            metadata = metadata
        )
    }
}