package co.resume.domain.export

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.FileProvider
import co.resume.analytics.Analytics
import co.resume.domain.engine.ResumeLayout
import co.resumeai.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PDF export for resumes, built directly from the canonical ResumeLayout IR via PDFBox — unlike
 * DocumentExporter (kept for cover letters), this never touches a WebView, so the exported file's
 * pagination matches exactly what LayoutEngine already decided rather than re-deriving it from a
 * live DOM capture.
 *
 * Callers are expected to invoke these off the main thread (PDFBox file writing is CPU/IO work);
 * the tail of each method that touches Toast/AlertDialog/startActivity hops back to the main
 * thread itself, so callers don't need to manage that split.
 */
@Singleton
class ResumeDocumentExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfExporter: PdfExporter,
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    private fun safeFileName(fileTitle: String, extension: String): String =
        fileTitle.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_") + ".$extension"

    private fun runOnMain(block: () -> Unit) = mainHandler.post(block)

    // saveToDownloads' "Saved!" AlertDialog (and sharePdf's startActivity below) need a real
    // Activity window token — this class is a Hilt singleton holding only @ApplicationContext,
    // and Application Context has no window at all, so using it for either call reliably threw
    // "Unable to add window -- token null is not valid" (dialog) or would throw for startActivity
    // without FLAG_ACTIVITY_NEW_TASK. uiContext is the screen's own Context (e.g. LocalContext.current
    // from the composable), passed in by the caller for exactly this reason; the injected
    // ApplicationContext remains fine for pure file I/O (cacheDir, MediaStore) below.
    fun exportAsPdf(layout: ResumeLayout, fileTitle: String, uiContext: Context) {
        try {
            val file = File(context.cacheDir, safeFileName(fileTitle, "pdf"))
            pdfExporter.export(layout, file)
            Analytics.logEvent(Analytics.Event.DOCUMENT_EXPORTED, mapOf(Analytics.Param.FORMAT to "pdf", "doc_type" to "resume"))
            runOnMain { DownloadsSaver.saveToDownloads(uiContext, file, file.name, "application/pdf") }
        } catch (e: Exception) {
            Analytics.logError("ResumeDocumentExporter.exportAsPdf", e)
            runOnMain { Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show() }
        }
    }

    fun sharePdf(layout: ResumeLayout, fileTitle: String, chooserTitle: String, uiContext: Context) {
        try {
            val file = File(context.cacheDir, safeFileName(fileTitle, "pdf"))
            pdfExporter.export(layout, file)
            Analytics.logEvent(Analytics.Event.DOCUMENT_SHARED, mapOf(Analytics.Param.FORMAT to "pdf", "doc_type" to "resume"))
            runOnMain {
                val shareUri = FileProvider.getUriForFile(uiContext, "co.resumeai.fileprovider", file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    putExtra(Intent.EXTRA_SUBJECT, file.name)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                uiContext.startActivity(Intent.createChooser(intent, chooserTitle))
            }
        } catch (e: Exception) {
            Analytics.logError("ResumeDocumentExporter.sharePdf", e)
            runOnMain { Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show() }
        }
    }
}
