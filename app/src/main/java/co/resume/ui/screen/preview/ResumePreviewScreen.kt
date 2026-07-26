package co.resume.ui.screen.preview

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ui.component.ResumeWebPreview
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.ResumePreviewViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumePreviewScreen(
    onBack: () -> Unit,
    viewModel: ResumePreviewViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var webView by remember { mutableStateOf<WebView?>(null) }
    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val adManager = adViewModel.adManager
    var showFormatDialog by remember { mutableStateOf(false) }

    if (showFormatDialog) {
        ExportFormatDialog(
            onDismiss = { showFormatDialog = false },
            onPdfSelected = {
                showFormatDialog = false
                val wv = webView ?: return@ExportFormatDialog
                if (activity != null) {
                    adManager.showInterstitial(activity) {
                        exportAsPdf(context, wv, uiState.fileTitle)
                    }
                } else {
                    exportAsPdf(context, wv, uiState.fileTitle)
                }
            },
            onWordSelected = {
                showFormatDialog = false
                val html = uiState.html ?: return@ExportFormatDialog
                exportAsWord(context, html, uiState.fileTitle)
            }
        )
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { Text(stringResource(R.string.preview_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = {
                        val wv = webView ?: return@FloatingActionButton
                        if (activity != null) {
                            adManager.showInterstitial(activity) {
                                sharePdfResume(context, wv, uiState.fileTitle)
                            }
                        } else {
                            sharePdfResume(context, wv, uiState.fileTitle)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.preview_cd_share))
                }
                FloatingActionButton(onClick = { showFormatDialog = true }) {
                    Icon(Icons.Filled.Download, contentDescription = stringResource(R.string.preview_cd_download))
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            ResumeWebPreview(
                html = uiState.html,
                modifier = Modifier.fillMaxSize(),
                onWebViewReady = { webView = it }
            )
        }
    }
}

@Composable
private fun ExportFormatDialog(
    onDismiss: () -> Unit,
    onPdfSelected: () -> Unit,
    onWordSelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.preview_export_title), fontWeight = FontWeight.SemiBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    stringResource(R.string.preview_export_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedCard(
                    onClick = onPdfSelected,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.PictureAsPdf,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(stringResource(R.string.preview_pdf_title), fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge)
                            Text(stringResource(R.string.preview_pdf_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                OutlinedCard(
                    onClick = onWordSelected,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(stringResource(R.string.preview_word_title), fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge)
                            Text(stringResource(R.string.preview_word_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } }
    )
}

@Suppress("DEPRECATION")
private fun generatePdfFile(context: Context, webView: WebView, fileTitle: String): File {
    val safeTitle = fileTitle.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")
    val fileName = "$safeTitle.pdf"

    // capturePicture captures the full scrollable content, not just the visible area
    val picture = webView.capturePicture()
    val picWidth = picture.width.coerceAtLeast(1)
    val picHeight = picture.height.coerceAtLeast(1)

    // A4 at 72 pt/inch
    val a4WidthPt = 595
    val a4HeightPt = 842
    val scaleX = a4WidthPt.toFloat() / picWidth
    val pageHeightPx = (a4HeightPt / scaleX).toInt().coerceAtLeast(1)
    val totalPages = ceil(picHeight.toDouble() / pageHeightPx).toInt().coerceAtLeast(1)

    val pdfDocument = PdfDocument()
    for (pageNum in 0 until totalPages) {
        val srcTop = pageNum * pageHeightPx
        val srcHeight = minOf(pageHeightPx, picHeight - srcTop).coerceAtLeast(1)

        val bmp = Bitmap.createBitmap(picWidth, srcHeight, Bitmap.Config.RGB_565)
        bmp.eraseColor(Color.WHITE)
        val bmpCanvas = Canvas(bmp)
        bmpCanvas.translate(0f, -srcTop.toFloat())
        picture.draw(bmpCanvas)

        val pageInfo = PdfDocument.PageInfo.Builder(a4WidthPt, a4HeightPt, pageNum + 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val matrix = Matrix().apply { setScale(scaleX, scaleX) }
        page.canvas.drawBitmap(bmp, matrix, null)
        pdfDocument.finishPage(page)
        bmp.recycle()
    }

    val tempFile = File(context.cacheDir, fileName)
    FileOutputStream(tempFile).use { pdfDocument.writeTo(it) }
    pdfDocument.close()
    return tempFile
}

private fun exportAsPdf(context: Context, webView: WebView, fileTitle: String) {
    try {
        val tempFile = generatePdfFile(context, webView, fileTitle)
        saveToDownloads(context, tempFile, tempFile.name, "application/pdf")
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show()
    }
}

/**
 * Shares the resume as a PDF via the system chooser — always PDF regardless of which
 * export format the user last picked, so WhatsApp/Email/Teams/ShareChat/Zoom/etc. (whichever
 * apps are installed and register for ACTION_SEND + application/pdf) all receive the same
 * file. Selecting Email specifically opens the compose screen with the PDF pre-attached —
 * that's standard ACTION_SEND behavior, no email-specific handling needed.
 */
private fun sharePdfResume(context: Context, webView: WebView, fileTitle: String) {
    try {
        val tempFile = generatePdfFile(context, webView, fileTitle)
        val shareUri = FileProvider.getUriForFile(context, "co.resumeai.fileprovider", tempFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, shareUri)
            putExtra(Intent.EXTRA_SUBJECT, tempFile.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.preview_share_chooser_title)))
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.preview_msg_pdf_failed, e.message), Toast.LENGTH_LONG).show()
    }
}

private fun exportAsWord(context: Context, html: String, fileTitle: String) {
    val safeTitle = fileTitle.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")
    val fileName = "$safeTitle.doc"
    try {
        val tempFile = File(context.cacheDir, fileName)
        tempFile.writeText(html)
        saveToDownloads(context, tempFile, fileName, "application/msword")
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.preview_msg_export_failed, e.message), Toast.LENGTH_LONG).show()
    }
}

private fun saveToDownloads(context: Context, sourceFile: File, fileName: String, mimeType: String) {
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
