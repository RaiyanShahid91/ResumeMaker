package co.resume.domain.importer

import android.content.Context
import android.net.Uri
import co.resume.domain.export.PdfTextExtractor
import co.resume.domain.scan.ScanOcr

/**
 * The one place that decides "PDF text extraction" vs "OCR" for an imported file, so
 * `ResumeListViewModel` doesn't need to know either mechanism exists. `mimeType` normally comes
 * straight from the picker (`ContentResolver`/`OpenDocument` both supply it reliably), but a
 * gallery/file-manager app occasionally hands back a generic `application/octet-stream` — the
 * `.pdf` extension check is a fallback for exactly that case, not the primary signal.
 */
object ResumeImportSource {

    suspend fun readText(context: Context, uri: Uri, mimeType: String?): String? {
        val isPdf = mimeType == "application/pdf" ||
            (mimeType == null && uri.lastPathSegment?.endsWith(".pdf", ignoreCase = true) == true)
        return if (isPdf) {
            PdfTextExtractor.extractText(context, uri)
        } else {
            ScanOcr.recognizeText(context, uri).text.trim().takeIf { it.isNotEmpty() }
        }
    }
}
