package co.resume.domain.convert

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Persists PDF-converter toolkit output (image-to-PDF, merge, compress, PDF-to-JPG) into the
 * app's own storage, mirroring [co.resume.domain.scan.ScanStorage]'s pattern for scanned documents.
 */
object ConverterStorage {
    private const val DocumentsDir = "converted_documents"
    private const val ImagesDir = "converted_images"

    /** Directory converted PDFs should be written into, created if missing. */
    fun documentsDir(context: Context): File = File(context.filesDir, DocumentsDir).apply { mkdirs() }

    /** Directory converted JPG pages should be written into, created if missing. */
    fun imagesDir(context: Context): File = File(context.filesDir, ImagesDir).apply { mkdirs() }

    /** A shareable content:// Uri (via the app's existing FileProvider) for a saved file path. */
    fun getShareUri(context: Context, absolutePath: String): Uri =
        FileProvider.getUriForFile(context, "co.resumeai.fileprovider", File(absolutePath))

    /**
     * Copies an already-persisted output file into the device's Downloads folder via MediaStore
     * (Android 10+), or the app cache + a FileProvider Uri as a fallback on older versions —
     * mirrors [co.resume.domain.scan.ScanStorage.saveToDownloads]'s pattern.
     */
    fun saveFileToDownloads(context: Context, sourceFile: File, mimeType: String): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, sourceFile.name)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
                resolver.openOutputStream(uri)?.use { output -> sourceFile.inputStream().use { it.copyTo(output) } }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val cacheFile = File(context.cacheDir, sourceFile.name)
                sourceFile.copyTo(cacheFile, overwrite = true)
                getShareUri(context, cacheFile.absolutePath)
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Copies a content:// Uri into the app cache so PDFBox/PdfRenderer (which need a real File) can read it. */
    fun copyToCache(context: Context, sourceUri: Uri, fileName: String): File? {
        return try {
            val cacheFile = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                cacheFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            cacheFile
        } catch (e: Exception) {
            null
        }
    }
}
