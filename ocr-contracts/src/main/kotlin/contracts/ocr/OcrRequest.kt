package contracts.ocr

/**
 * OCR request contract — payload sent to an [OcrEngine] for extracting
 * structured AsciiDoc from image data.
 *
 * N0 pure type (no Jackson, no Gradle). Extracted from codex.ocr by
 * EPIC CDX-OCR-CONTRACTS (9th MEMPHIS N0 artefact).
 *
 * @property imageData raw bytes of the image to OCR (PNG, JPEG, TIFF)
 * @property format image MIME type (e.g. "image/png", "image/jpeg", "image/tiff")
 * @property language ISO 639-1 language code hint (e.g. "fr", "en", "de")
 * @property prompt optional LLM prompt override for structuring instructions
 */
data class OcrRequest(
    val imageData: ByteArray,
    val format: String,
    val language: String,
    val prompt: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OcrRequest) return false
        return imageData.contentEquals(other.imageData) &&
            format == other.format &&
            language == other.language &&
            prompt == other.prompt
    }

    override fun hashCode(): Int {
        var result = imageData.contentHashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + (prompt?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "OcrRequest(format='$format', language='$language', imageSize=${imageData.size} bytes, prompt=$prompt)"
}