package co.resume.domain.scan

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Persists scanned documents into the app's own storage, mirroring the pattern used by
 * [co.resume.domain.ImageStorage] for profile/signature images (`context.filesDir/<folder>/`).
 */
object ScanStorage {
    private const val DocumentsDir = "scanned_documents"
    private const val PagesDir = "scanned_pages"

    /** Copies the scanner's temp PDF content:// Uri into persistent app storage. */
    fun savePdf(context: Context, sourceUri: Uri, fileName: String): String? = copyInto(context, sourceUri, DocumentsDir, fileName)

    /** Copies a single scanned page (JPEG) into persistent app storage. */
    fun savePage(context: Context, sourceUri: Uri, fileName: String): String? = copyInto(context, sourceUri, PagesDir, fileName)

    /** A shareable content:// Uri (via the app's existing FileProvider) for a saved file path. */
    fun getShareUri(context: Context, absolutePath: String): Uri =
        FileProvider.getUriForFile(context, "co.resumeai.fileprovider", File(absolutePath))

    /**
     * Copies [sourceUri] into the device's Downloads folder via MediaStore (Android 10+), or
     * into the app cache + a FileProvider Uri as a fallback on older versions — mirrors the
     * pattern already used for resume PDF/Word export in ResumePreviewScreen.
     */
    fun saveToDownloads(context: Context, sourceUri: Uri, fileName: String, mimeType: String = "application/pdf"): Uri? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
                resolver.openOutputStream(uri)?.use { output ->
                    context.contentResolver.openInputStream(sourceUri)?.use { it.copyTo(output) }
                }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                val cacheFile = File(context.cacheDir, fileName)
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    cacheFile.outputStream().use { input.copyTo(it) }
                } ?: return null
                getShareUri(context, cacheFile.absolutePath)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun copyInto(context: Context, sourceUri: Uri, subFolder: String, fileName: String): String? {
        return try {
            val outputDir = File(context.filesDir, subFolder).apply { mkdirs() }
            val outputFile = File(outputDir, fileName)
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                outputFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
