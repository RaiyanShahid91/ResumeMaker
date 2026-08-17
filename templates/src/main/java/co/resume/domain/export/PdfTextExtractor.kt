package co.resume.domain.export

import android.content.Context
import android.net.Uri
import co.resume.domain.engine.PdfBoxInitializer
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper

/**
 * Extracts a PDF's real text layer — for resume import (see `ResumeImportSource` in the `app`
 * module), which needs to read an arbitrary PDF the user picked, not just export one this app
 * generated. Lives here rather than in `app` because pdfbox-android is an `implementation` (not
 * `api`) dependency of this module — `app` can't reference PDFBox classes directly, only call a
 * plain public method like this one, same as `PdfExporter`/`PdfBoxInitializer` already do.
 *
 * Returns null for a PDF with no extractable text layer (e.g. a scanned/photographed resume saved
 * as PDF, which is just embedded page images) — callers treat that the same as "couldn't read this
 * file" rather than attempting any image-rendering/OCR fallback.
 */
object PdfTextExtractor {

    fun extractText(context: Context, uri: Uri): String? {
        PdfBoxInitializer.ensureInitialized(context)
        val text = runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                PDDocument.load(input).use { document ->
                    PDFTextStripper().getText(document)
                }
            }
        }.getOrNull()
        return text?.trim()?.takeIf { it.isNotEmpty() }
    }
}
