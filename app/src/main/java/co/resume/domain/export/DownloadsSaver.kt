package co.resume.domain.export

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import co.resumeai.R
import java.io.File

/**
 * Shared "save a finished export file to Downloads (or share it on old Android)" pipeline, used by
 * both DocumentExporter (WebView-based, cover letters) and ResumeDocumentExporter (PDFBox-based,
 * resumes) — the two document types render differently but land the resulting file the same way.
 */
object DownloadsSaver {

    fun saveToDownloads(context: Context, sourceFile: File, fileName: String, mimeType: String) {
        try {
            val savedUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw Exception("Could not create file in Downloads")
                resolver.openOutputStream(uri)?.use { output ->
                    sourceFile.inputStream().use { it.copyTo(output) }
                }
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                uri
            } else {
                // Android 9 and below: share via FileProvider
                val shareUri = FileProvider.getUriForFile(context, "co.resumeai.fileprovider", sourceFile)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, shareUri)
                    putExtra(Intent.EXTRA_SUBJECT, fileName)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.preview_chooser_save)))
                return
            }
            showSavedDialog(context, fileName, mimeType, savedUri)
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.preview_msg_save_failed, e.message), Toast.LENGTH_LONG).show()
        }
    }

    private fun showSavedDialog(context: Context, fileName: String, mimeType: String, fileUri: Uri?) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.preview_dialog_saved_title))
            .setMessage(context.getString(R.string.preview_dialog_saved_msg, fileName))
            .setPositiveButton(context.getString(R.string.preview_btn_open_file)) { _, _ ->
                if (fileUri != null) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(fileUri, mimeType)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.preview_chooser_open_with)))
                    } catch (_: Exception) {
                        Toast.makeText(context, context.getString(R.string.preview_msg_no_app_found), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNeutralButton(context.getString(R.string.preview_btn_open_folder)) { _, _ ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("content://com.android.externalstorage.documents/root/primary")
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    try {
                        context.packageManager
                            .getLaunchIntentForPackage("com.android.documentsui")
                            ?.let { context.startActivity(it) }
                    } catch (_: Exception) {
                        Toast.makeText(context, context.getString(R.string.preview_msg_saved_to_downloads), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(context.getString(R.string.btn_ok), null)
            .show()
    }
}
