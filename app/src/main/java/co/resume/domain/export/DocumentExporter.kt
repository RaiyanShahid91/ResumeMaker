package co.resume.domain.export

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.FileProvider
import co.resume.analytics.Analytics
import co.resumeai.R
import org.json.JSONArray
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream

/**
 * WebView-based PDF export + share pipeline for cover letters (CoverLetterHtmlRenderer.kt).
 * Resumes no longer go through this — see ResumeDocumentExporter, which builds a PDF directly
 * from the canonical ResumeLayout IR via PDFBox instead of capturing a WebView.
 */
object DocumentExporter {

    /**
     * Reads the on-screen bounds (in CSS px) of every repeating cover-letter "entry" plus the
     * page's rendered width, so PDF export can snap page breaks to the gaps between entries
     * instead of a fixed pixel height. Matches the `element_*` classes used by every cover-letter
     * template's `<style>`/renderer contract (see CoverLetterHtmlRenderer.kt) — templates with
     * none of these classes simply report an empty list, so this degrades to plain fixed-height
     * slicing instead of failing.
     */
    private const val EntryBoundsScript = """
        (function() {
            var classes = ['element_work_experience','element_educational_details','element_projects_details',
                'element_skills_details','element_achievements_details','element_languages_details',
                'element_interests_details','element_hobbies_details'];
            var entries = [];
            classes.forEach(function(cls) {
                var els = document.getElementsByClassName(cls);
                for (var i = 0; i < els.length; i++) {
                    var r = els[i].getBoundingClientRect();
                    entries.push([r.top + window.scrollY, r.bottom + window.scrollY]);
                }
            });
            return JSON.stringify({
                width: window.innerWidth,
                height: document.documentElement.scrollHeight,
                entries: entries
            });
        })();
    """

    private data class EntryBounds(val cssWidth: Float, val cssHeight: Float, val entries: List<Pair<Float, Float>>)

    private fun readEntryBounds(webView: WebView, onResult: (EntryBounds) -> Unit) {
        webView.evaluateJavascript(EntryBoundsScript) { rawResult ->
            val parsed = runCatching {
                val jsonString = JSONTokener(rawResult).nextValue() as String
                val json = org.json.JSONObject(jsonString)
                val width = json.optDouble("width", 0.0).toFloat()
                val height = json.optDouble("height", 0.0).toFloat()
                val entriesJson: JSONArray = json.optJSONArray("entries") ?: JSONArray()
                val entries = (0 until entriesJson.length()).map { i ->
                    val pair = entriesJson.getJSONArray(i)
                    pair.getDouble(0).toFloat() to pair.getDouble(1).toFloat()
                }
                EntryBounds(width, height, entries)
            }.getOrDefault(EntryBounds(0f, 0f, emptyList()))
            onResult(parsed)
        }
    }

    /** Cumulative page-end offsets (in bitmap px) that avoid cutting through any [entries] range where possible. */
    private fun computePageBreaks(picHeight: Int, pageHeightPx: Int, entries: List<Pair<Float, Float>>): List<Int> {
        val breaks = mutableListOf<Int>()
        var cursor = 0
        while (cursor < picHeight) {
            var pageEnd = (cursor + pageHeightPx).coerceAtMost(picHeight)
            if (pageEnd < picHeight) {
                val collision = entries.firstOrNull { pageEnd > it.first && pageEnd < it.second }
                if (collision != null && collision.first > cursor) {
                    pageEnd = collision.first.toInt()
                }
            }
            breaks.add(pageEnd)
            cursor = pageEnd
        }
        return breaks
    }

    @Suppress("DEPRECATION")
    private fun renderPdf(context: Context, webView: WebView, fileTitle: String, entryBounds: EntryBounds): File {
        val safeTitle = fileTitle.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")
        val fileName = "$safeTitle.pdf"

        // Chromium only fully rasterizes content that's actually been laid out within (or near)
        // the WebView's on-screen viewport — for a long resume the live WebView is sized to the
        // screen, so content further down the scrollable page was never painted, and
        // capturePicture() silently returns those regions blank even though its reported height is
        // correct (this is what caused "only half the data shows, rest is a blank page"). Forcing
        // the WebView to lay out at its full scrollable content height before capturing makes
        // Chromium paint the whole page at once; we restore the original on-screen size
        // immediately after so the live preview the user sees is never visibly resized.
        //
        // The height must be set to the REAL content height in both directions, not just floored at
        // the current on-screen size (coerceAtLeast(originalHeight) previously did that) — a short
        // cover letter's on-screen WebView is sized to fill the whole preview screen regardless of
        // how little content it holds, so capturing at that on-screen height captured a page's worth
        // of blank space below the actual text, which then split into a second, all-blank PDF page.
        val originalWidth = webView.width
        val originalHeight = webView.height
        val density = context.resources.displayMetrics.density
        // entryBounds.cssHeight is document/layout pixels (what JS's scrollHeight reports — always
        // unzoomed, regardless of the page's current on-screen zoom). Converting that straight to
        // physical pixels via density alone assumes the page is displayed at 1:1 zoom, which is only
        // true when there's no viewport meta tag forcing that; cover letter/resume previews instead
        // rely on loadWithOverviewMode to zoom the (wider, fixed-mm) page down to fit the on-screen
        // width, so the real on-screen scale is well below 1. Skipping that factor here previously
        // resized the WebView to several times the height actually needed on screen —
        // capturePicture() then captured mostly blank space below the real content, which sliced
        // into extra all-blank PDF pages (a one-page cover letter exporting as three or four pages).
        //
        // WebView.getScale() would be the obvious way to read that factor back, but it's unreliable
        // on modern Chromium-backed WebView — many builds simply freeze it at 1.0 regardless of the
        // page's actual on-screen zoom, since the real composited scale lives in a separate render
        // process getScale() was never updated to reach into. Instead, the scale is derived
        // empirically: entryBounds.cssWidth is the same unzoomed document width in CSS px, and
        // originalWidth is what that width actually measures on screen right now in physical px —
        // their ratio IS the current effective scale (density × zoom combined), with no dependency
        // on any API that may or may not reflect it. loadWithOverviewMode applies one uniform scale
        // to both axes, so the same ratio converts cssHeight to physical px just as validly.
        val widthRatio = if (entryBounds.cssWidth > 0f && originalWidth > 0) originalWidth / entryBounds.cssWidth else density
        val fullContentHeightPx = if (entryBounds.cssHeight > 0f) {
            (entryBounds.cssHeight * widthRatio).toInt()
        } else {
            webView.contentHeight
        }.coerceAtLeast(1)
        val needsResize = originalWidth > 0 && fullContentHeightPx != originalHeight

        if (needsResize) {
            webView.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(originalWidth, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(fullContentHeightPx, android.view.View.MeasureSpec.EXACTLY)
            )
            webView.layout(0, 0, webView.measuredWidth, webView.measuredHeight)
        }

        // capturePicture captures the full scrollable content, not just the visible area
        val picture = webView.capturePicture()
        val picWidth = picture.width.coerceAtLeast(1)
        val picHeight = picture.height.coerceAtLeast(1)

        if (needsResize) {
            webView.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(originalWidth, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(originalHeight, android.view.View.MeasureSpec.EXACTLY)
            )
            webView.layout(0, 0, originalWidth, originalHeight)
        }

        // A4 at 72 pt/inch
        val a4WidthPt = 595
        val a4HeightPt = 842
        val scaleX = a4WidthPt.toFloat() / picWidth
        val pageHeightPx = (a4HeightPt / scaleX).toInt().coerceAtLeast(1)

        // entryBounds are in CSS px; capturePicture's coordinate space is proportional to the
        // page's rendered CSS width, so a single width ratio converts one to the other.
        val cssToPicScale = if (entryBounds.cssWidth > 0f) picWidth / entryBounds.cssWidth else 1f
        val entriesInPicSpace = entryBounds.entries.map { (top, bottom) -> top * cssToPicScale to bottom * cssToPicScale }

        val pageBreaks = computePageBreaks(picHeight, pageHeightPx, entriesInPicSpace)

        // Page 1 already reads with some breathing room at the top, from the template's own
        // header/photo spacing. A raw content slice for page 2+ has no such gap — it just starts
        // wherever the previous page happened to cut off — which looked jarring next to page 1.
        // Nudging every page after the first down by a small fixed margin gives them the same
        // "top of a fresh page" feel instead of text butting right up against the paper edge.
        val topMarginPt = 28f

        val pdfDocument = PdfDocument()
        var srcTop = 0
        pageBreaks.forEachIndexed { index, pageEnd ->
            val srcHeight = (pageEnd - srcTop).coerceAtLeast(1)

            val bmp = Bitmap.createBitmap(picWidth, srcHeight, Bitmap.Config.RGB_565)
            bmp.eraseColor(Color.WHITE)
            val bmpCanvas = Canvas(bmp)
            bmpCanvas.translate(0f, -srcTop.toFloat())
            picture.draw(bmpCanvas)

            val pageInfo = PdfDocument.PageInfo.Builder(a4WidthPt, a4HeightPt, index + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val matrix = Matrix().apply {
                setScale(scaleX, scaleX)
                if (index > 0) postTranslate(0f, topMarginPt)
            }
            page.canvas.drawBitmap(bmp, matrix, null)
            pdfDocument.finishPage(page)
            bmp.recycle()

            srcTop = pageEnd
        }

        val tempFile = File(context.cacheDir, fileName)
        FileOutputStream(tempFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()
        return tempFile
    }

    // The preview's page-break dashes/"Page N" labels (see ResumeWebPreview's AutoFitPagesScript)
    // are a preview-only aid — hide them for the capture, then restore, so the exported PDF
    // pixels never include them.
    private const val HideMarkersScript =
        "document.querySelectorAll('.__resume_page_marker').forEach(function(el){el.style.display='none';});"
    private const val ShowMarkersScript =
        "document.querySelectorAll('.__resume_page_marker').forEach(function(el){el.style.display='';});"

    // The cover-letter page card's drop shadow (see CoverLetterHtmlRenderer's `__page_shadow_style`)
    // is an on-screen depth cue only — baked into a captured PDF bitmap it reads as a stray gray
    // smudge around the page rather than anything a printed/exported document should contain, so
    // it's disabled for the capture and restored right after, same as markers.
    private const val HideShadowScript =
        "var s=document.getElementById('__page_shadow_style'); if(s) s.disabled = true;"
    private const val ShowShadowScript =
        "var s=document.getElementById('__page_shadow_style'); if(s) s.disabled = false;"

    private fun generatePdfFile(context: Context, webView: WebView, fileTitle: String, onResult: (File) -> Unit) {
        webView.evaluateJavascript(HideMarkersScript) {
            webView.evaluateJavascript(HideShadowScript) {
                readEntryBounds(webView) { bounds ->
                    val file = renderPdf(context, webView, fileTitle, bounds)
                    webView.evaluateJavascript(ShowShadowScript, null)
                    webView.evaluateJavascript(ShowMarkersScript, null)
                    onResult(file)
                }
            }
        }
    }

    fun exportAsPdf(context: Context, webView: WebView, fileTitle: String, docType: String) {
        generatePdfFile(context, webView, fileTitle) { tempFile ->
            try {
                saveToDownloads(context, tempFile, tempFile.name, "application/pdf")
                Analytics.logEvent(
                    Analytics.Event.DOCUMENT_EXPORTED,
                    mapOf(Analytics.Param.FORMAT to "pdf", "doc_type" to docType)
                )
            } catch (e: Exception) {
                Analytics.logError("DocumentExporter.exportAsPdf", e)
                Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Shares the document as a PDF via the system chooser — always PDF regardless of which
     * export format the user last picked, so WhatsApp/Email/Teams/ShareChat/Zoom/etc. (whichever
     * apps are installed and register for ACTION_SEND + application/pdf) all receive the same
     * file. Selecting Email specifically opens the compose screen with the PDF pre-attached —
     * that's standard ACTION_SEND behavior, no email-specific handling needed.
     */
    fun sharePdf(context: Context, webView: WebView, fileTitle: String, chooserTitle: String, docType: String) {
        generatePdfFile(context, webView, fileTitle) { tempFile ->
            try {
                val shareUri = FileProvider.getUriForFile(context, "co.resumeai.fileprovider", tempFile)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    putExtra(Intent.EXTRA_SUBJECT, tempFile.name)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, chooserTitle))
                Analytics.logEvent(
                    Analytics.Event.DOCUMENT_SHARED,
                    mapOf(Analytics.Param.FORMAT to "pdf", "doc_type" to docType)
                )
            } catch (e: Exception) {
                Analytics.logError("DocumentExporter.sharePdf", e)
                Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveToDownloads(context: Context, sourceFile: File, fileName: String, mimeType: String) =
        DownloadsSaver.saveToDownloads(context, sourceFile, fileName, mimeType)
}
