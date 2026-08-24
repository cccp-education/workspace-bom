package contracts.ocr

/**
 * Port for an OCR engine — transforms an [OcrRequest] into an [OcrResult].
 *
 * N0 pure contract (no Gradle, no coroutines). Extracted from codex.ocr
 * by EPIC CDX-OCR-CONTRACTS (9th MEMPHIS N0 artefact).
 *
 * Boundary rule (EPIC CDX-OCR-BOUNDARY): software OCR (Tesseract) is
 * actioned by codex; AI-assisted OCR is actioned by the codebase socle.
 * This fun interface IS the injection port consumed by the OCR pipeline:
 *
 * - TesseractOcrEngine (codex): local software OCR without AI
 * - AI engines live in the codebase socle (e.g. an adapter wrapping its
 *   VisionProvider). Without injection, the pipeline degrades to
 *   Tesseract-only.
 */
fun interface OcrEngine {
    fun process(request: OcrRequest): OcrResult
}