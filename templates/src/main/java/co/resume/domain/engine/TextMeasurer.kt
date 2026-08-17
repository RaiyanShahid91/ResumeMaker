package co.resume.domain.engine

import android.content.Context
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import co.resume.domain.theme.FontRef
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PT_PER_MM = 72f / 25.4f
private const val WRAP_SAFETY_MARGIN_MM = 1.5f
fun ptToMm(pt: Float): Float = pt / PT_PER_MM
fun mmToPt(mm: Float): Float = mm * PT_PER_MM

/**
 * Canonical text-measurement source of truth. PDFBox's embedded-font glyph metrics are used for
 * every renderer's wrapping decision — PDFBox is the fussiest of the three target formats (real
 * subset-embedded font, no OS font-fallback smoothing), so if wrapping agrees with PDFBox, DOCX
 * and the preview HTML (rendered with the same TTF via @font-face) will match closely too.
 *
 * Owns one internal PDDocument purely to host loaded PDType0Font instances for measurement; this
 * is independent of whatever PDDocument PdfExporter later builds for the actual export.
 */
@Singleton
class TextMeasurer @Inject constructor(@ApplicationContext private val context: Context) {

    init {
        PdfBoxInitializer.ensureInitialized(context)
    }

    private val measurementDoc = PDDocument()
    private val fontCache = mutableMapOf<String, PDType0Font>()

    private fun font(ref: FontRef): PDType0Font = fontCache.getOrPut(ref.assetPath) {
        context.assets.open(ref.assetPath).use { input ->
            PDType0Font.load(measurementDoc, input)
        }
    }

    /** Width in mm of [text] set in [ref] at [sizePt]. */
    fun stringWidthMm(text: String, ref: FontRef, sizePt: Float): Float {
        if (text.isEmpty()) return 0f
        val widthPt = font(ref).getStringWidth(text) / 1000f * sizePt
        return ptToMm(widthPt)
    }

    /** Greedy word-wrap of [text] into lines that each fit within [maxWidthMm]. Single source of line-breaking for every renderer. */
    fun wrapToWidth(text: String, ref: FontRef, sizePt: Float, maxWidthMm: Float): List<String> {
        if (text.isBlank()) return emptyList()
        // Chromium (the preview WebView) renders the same TTF via @font-face rather than through
        // PDFBox's glyph table, and its hinting/kerning can land a line a hair wider than this
        // measurement — invisible most places, but the preview draws text with no CSS wrapping
        // safety net (white-space:pre) and adjacent columns are often only COLUMN_GAP_MM apart, so a
        // line greedily packed right up to the exact boundary can visibly bleed into the neighboring
        // column's text. Reserving a small margin keeps lines off the exact edge without meaningfully
        // changing how much text fits per line.
        val effectiveMaxWidthMm = maxWidthMm - WRAP_SAFETY_MARGIN_MM
        val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "${current} $word"
            if (stringWidthMm(candidate, ref, sizePt) <= effectiveMaxWidthMm || current.isEmpty()) {
                current = StringBuilder(candidate)
            } else {
                lines.add(current.toString())
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) lines.add(current.toString())
        return lines
    }

    fun lineHeightMm(sizePt: Float, lineHeightMultiplier: Float): Float = ptToMm(sizePt * lineHeightMultiplier)
}
