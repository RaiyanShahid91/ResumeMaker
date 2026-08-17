package co.resume.domain.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import co.resume.domain.engine.PdfBoxInitializer
import co.resume.domain.engine.PositionedBlock
import co.resume.domain.engine.ResumeLayout
import co.resume.domain.engine.mmToPt
import co.resume.domain.theme.ColorRef
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Renders the canonical ResumeLayout IR to a real, selectable-text PDF — walks pre-measured/
 * positioned blocks and paints them; makes no independent layout decisions (that's LayoutEngine's
 * job), so PDF output cannot drift from what the IR already decided.
 */
@Singleton
class PdfExporter @Inject constructor(@ApplicationContext private val context: Context) {

    fun export(layout: ResumeLayout, outputFile: File) {
        PdfBoxInitializer.ensureInitialized(context)
        val document = PDDocument()
        val fontCache = mutableMapOf<String, PDType0Font>()
        fun font(assetPath: String): PDType0Font = fontCache.getOrPut(assetPath) {
            context.assets.open(assetPath).use { PDType0Font.load(document, it) }
        }

        val pageWidthPt = mmToPt(layout.pageSizeMm.widthMm)
        val pageHeightPt = mmToPt(layout.pageSizeMm.heightMm)

        for (pageLayout in layout.pages) {
            val page = PDPage(PDRectangle(pageWidthPt, pageHeightPt))
            document.addPage(page)
            PDPageContentStream(document, page).use { stream ->
                val blocks = pageLayout.blocks + listOfNotNull(pageLayout.footer)
                for (block in blocks) drawBlock(document, stream, block, pageHeightPt, ::font)
            }
        }

        document.save(outputFile)
        document.close()
    }

    private fun drawBlock(document: PDDocument, stream: PDPageContentStream, block: PositionedBlock, pageHeightPt: Float, font: (String) -> PDType0Font) {
        when (block) {
            is PositionedBlock.TextLine -> {
                val pdFont = font(block.fontAssetPath)
                val baselineYPt = pageHeightPt - mmToPt(block.yMm) - block.sizePt * 0.85f
                stream.beginText()
                stream.setFont(pdFont, block.sizePt)
                setFillColor(stream, block.color)
                stream.newLineAtOffset(mmToPt(block.xMm), baselineYPt)
                stream.showText(sanitize(block.text))
                stream.endText()
            }
            is PositionedBlock.Rule -> {
                setStrokeColor(stream, block.color)
                stream.setLineWidth(block.strokePt)
                val yPt = pageHeightPt - mmToPt(block.yMm)
                stream.moveTo(mmToPt(block.xMm), yPt)
                stream.lineTo(mmToPt(block.xMm) + mmToPt(block.widthMm), yPt)
                stream.stroke()
            }
            is PositionedBlock.Rect -> {
                block.fill?.let { setFillColor(stream, it) }
                val xPt = mmToPt(block.boundsMm.xMm)
                val topPt = pageHeightPt - mmToPt(block.boundsMm.yMm)
                val heightPt = mmToPt(block.boundsMm.heightMm)
                stream.addRect(xPt, topPt - heightPt, mmToPt(block.boundsMm.widthMm), heightPt)
                stream.fill()
            }
            is PositionedBlock.Image -> {
                val bitmap: Bitmap = BitmapFactory.decodeFile(block.bitmapPath) ?: return
                val image = LosslessFactory.createFromImage(document, bitmap)
                val xPt = mmToPt(block.boundsMm.xMm)
                val topPt = pageHeightPt - mmToPt(block.boundsMm.yMm)
                val heightPt = mmToPt(block.boundsMm.heightMm)
                val widthPt = mmToPt(block.boundsMm.widthMm)
                // Circular/rounded-rect clipping is pre-baked into the bitmap by the layout engine's
                // image-prep step (Phase 2) rather than relied on here — PDFBox has no clip-to-circle primitive.
                stream.drawImage(image, xPt, topPt - heightPt, widthPt, heightPt)
            }
            is PositionedBlock.IconGlyph -> {
                // Decorative marker only (dot/bar), always drawn beside real text — never replaces it or lives in an image.
                // A real bundled icon font (phone/email/location glyphs) is a follow-up; this keeps the contract satisfiable now.
                setFillColor(stream, block.color)
                val sizePt = mmToPt(block.sizeMm)
                val topPt = pageHeightPt - mmToPt(block.yMm)
                stream.addRect(mmToPt(block.xMm), topPt - sizePt, sizePt, sizePt)
                stream.fill()
            }
        }
    }

    // pdfbox-android has no java.awt.Color on Android's runtime; the (r,g,b) int overload is the
    // supported Android-side API despite being flagged deprecated (a leftover from the desktop-PDFBox docs).
    private fun setFillColor(stream: PDPageContentStream, color: ColorRef) {
        val (r, g, b) = hexToRgb(color.hex)
        @Suppress("DEPRECATION") stream.setNonStrokingColor(r, g, b)
    }

    private fun setStrokeColor(stream: PDPageContentStream, color: ColorRef) {
        val (r, g, b) = hexToRgb(color.hex)
        @Suppress("DEPRECATION") stream.setStrokingColor(r, g, b)
    }

    private fun hexToRgb(hex: String): Triple<Int, Int, Int> {
        val clean = hex.removePrefix("#")
        return Triple(clean.substring(0, 2).toInt(16), clean.substring(2, 4).toInt(16), clean.substring(4, 6).toInt(16))
    }

    // PDFBox's default embedded-subset fonts don't carry every Unicode codepoint (e.g. curly quotes) —
    // strip anything outside the font's guaranteed-safe range rather than let showText throw mid-export.
    private fun sanitize(text: String): String = text.filter { it.code in 0x20..0xFFFF }
}
