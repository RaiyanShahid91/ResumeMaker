package co.resume.domain.convert

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import co.resume.domain.engine.PdfBoxInitializer
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.File

/**
 * The PDF-converter toolkit's actual file-format work — image-to-PDF, merge, compress, and
 * PDF-to-JPG — all built on PDFBox-Android (already used for resume PDF export, see
 * [co.resume.domain.export.PdfExporter]) plus Android's built-in [PdfRenderer] for rasterizing
 * pages (same approach as [co.resume.ui.screen.pdf.PdfViewerScreen]).
 */
object PdfConverter {
    private const val PageMarginPt = 24f
    private const val MaxImageDimensionPx = 2000

    /** Embeds each image as its own page (portrait or landscape A4, whichever fits better), centered with a margin. */
    fun imagesToPdf(context: Context, imageUris: List<Uri>, outputFile: File) {
        PdfBoxInitializer.ensureInitialized(context)
        val document = PDDocument()
        try {
            for (uri in imageUris) {
                val bitmap = decodeBitmap(context, uri) ?: continue
                val portrait = bitmap.height >= bitmap.width
                val pageSize = if (portrait) PDRectangle.A4 else PDRectangle(PDRectangle.A4.height, PDRectangle.A4.width)
                val page = PDPage(pageSize)
                document.addPage(page)

                val image = LosslessFactory.createFromImage(document, bitmap)
                val maxWidthPt = pageSize.width - PageMarginPt * 2
                val maxHeightPt = pageSize.height - PageMarginPt * 2
                val scale = minOf(maxWidthPt / bitmap.width, maxHeightPt / bitmap.height)
                val drawWidthPt = bitmap.width * scale
                val drawHeightPt = bitmap.height * scale
                val xPt = (pageSize.width - drawWidthPt) / 2f
                val yPt = (pageSize.height - drawHeightPt) / 2f

                PDPageContentStream(document, page).use { stream ->
                    stream.drawImage(image, xPt, yPt, drawWidthPt, drawHeightPt)
                }
                bitmap.recycle()
            }
            document.save(outputFile)
        } finally {
            document.close()
        }
    }

    /** Concatenates every source PDF's pages, in order, into one output PDF. */
    fun mergePdfs(context: Context, sourceFiles: List<File>, outputFile: File) {
        PdfBoxInitializer.ensureInitialized(context)
        val merger = PDFMergerUtility()
        sourceFiles.forEach { merger.addSource(it) }
        merger.destinationFileName = outputFile.absolutePath
        merger.mergeDocuments(null)
    }

    /** Rasterizes each page and re-embeds it as a lossily-compressed JPEG, shrinking file size at some quality cost. */
    fun compressPdf(context: Context, sourceFile: File, outputFile: File, jpegQuality: Float = 0.5f, dpi: Int = 110) {
        PdfBoxInitializer.ensureInitialized(context)
        val document = PDDocument()
        try {
            renderPdfPages(sourceFile, dpi).forEach { bitmap ->
                val pageWidthPt = bitmap.width * 72f / dpi
                val pageHeightPt = bitmap.height * 72f / dpi
                val page = PDPage(PDRectangle(pageWidthPt, pageHeightPt))
                document.addPage(page)
                val image = JPEGFactory.createFromImage(document, bitmap, jpegQuality, dpi)
                PDPageContentStream(document, page).use { stream ->
                    stream.drawImage(image, 0f, 0f, pageWidthPt, pageHeightPt)
                }
                bitmap.recycle()
            }
            document.save(outputFile)
        } finally {
            document.close()
        }
    }

    /** Rasterizes every page of [sourceFile] to its own JPEG file inside [outputDir]. */
    fun pdfToJpegs(sourceFile: File, outputDir: File, baseName: String, jpegQuality: Int = 90, dpi: Int = 150): List<File> {
        return renderPdfPages(sourceFile, dpi).mapIndexed { index, bitmap ->
            val file = File(outputDir, "${baseName}_page${index + 1}.jpg")
            file.outputStream().use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, out) }
            bitmap.recycle()
            file
        }
    }

    /** Number of pages in a PDF file, via Android's built-in [PdfRenderer]. */
    fun pageCount(file: File): Int {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            PdfRenderer(fd).use { renderer -> return renderer.pageCount }
        }
    }

    /** Rasterizes just the first page, for a lightweight result preview. */
    fun renderFirstPage(file: File, dpi: Int = 130): Bitmap? {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            PdfRenderer(fd).use { renderer ->
                if (renderer.pageCount == 0) return null
                val scale = dpi / 72f
                return renderer.openPage(0).use { page ->
                    val bitmap = Bitmap.createBitmap(
                        (page.width * scale).toInt().coerceAtLeast(1),
                        (page.height * scale).toInt().coerceAtLeast(1),
                        Bitmap.Config.ARGB_8888
                    )
                    bitmap.eraseColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                }
            }
        }
    }

    private fun renderPdfPages(file: File, dpi: Int): List<Bitmap> {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            PdfRenderer(fd).use { renderer ->
                val scale = dpi / 72f
                return (0 until renderer.pageCount).map { index ->
                    renderer.openPage(index).use { page ->
                        val bitmap = Bitmap.createBitmap(
                            (page.width * scale).toInt().coerceAtLeast(1),
                            (page.height * scale).toInt().coerceAtLeast(1),
                            Bitmap.Config.ARGB_8888
                        )
                        bitmap.eraseColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        bitmap
                    }
                }
            }
        }
    }

    /** Decodes [uri] downsampled to at most [MaxImageDimensionPx] on its longest side, to avoid OOM on large camera photos. */
    private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sampleSize = 1
            val longestSide = maxOf(bounds.outWidth, bounds.outHeight)
            while (longestSide / sampleSize > MaxImageDimensionPx) sampleSize *= 2
            val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        } catch (e: Exception) {
            null
        }
    }
}
