package co.resume.ui.screen.convert

import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.domain.convert.PdfConverter
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.SupportWithAdDialog
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.ConverterTool
import co.resume.ui.viewmodel.ConverterViewModel
import coil.compose.AsyncImage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterToolScreen(
    tool: ConverterTool,
    viewModel: ConverterViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onGoToDocuments: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showNameSheet by remember { mutableStateOf(false) }
    var showAdPrompt by remember { mutableStateOf(false) }
    val adManager = adViewModel.adManager

    val isMultiSelect = tool == ConverterTool.IMAGE_TO_PDF || tool == ConverterTool.MERGE_PDF

    val imagesPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) selectedUris = selectedUris + uris
    }
    val multiplePdfPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        if (uris.isNotEmpty()) selectedUris = selectedUris + uris
    }
    val singlePdfPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) selectedUris = listOf(uri)
    }

    val (titleRes, descRes) = when (tool) {
        ConverterTool.IMAGE_TO_PDF -> R.string.converter_tool_image_to_pdf_title to R.string.converter_tool_image_to_pdf_desc
        ConverterTool.MERGE_PDF -> R.string.converter_tool_merge_pdf_title to R.string.converter_tool_merge_pdf_desc
        ConverterTool.COMPRESS_PDF -> R.string.converter_tool_compress_pdf_title to R.string.converter_tool_compress_pdf_desc
        ConverterTool.PDF_TO_JPG -> R.string.converter_tool_pdf_to_jpg_title to R.string.converter_tool_pdf_to_jpg_desc
    }
    val pickButtonRes = when (tool) {
        ConverterTool.IMAGE_TO_PDF -> if (selectedUris.isEmpty()) R.string.converter_add_image_btn else R.string.converter_add_another_image_btn
        ConverterTool.MERGE_PDF -> if (selectedUris.isEmpty()) R.string.converter_add_pdf_btn else R.string.converter_add_another_pdf_btn
        ConverterTool.COMPRESS_PDF, ConverterTool.PDF_TO_JPG -> R.string.converter_select_pdf_btn
    }
    val canConvert = if (tool == ConverterTool.MERGE_PDF) selectedUris.size >= 2 else selectedUris.isNotEmpty()

    fun launchPicker() {
        when (tool) {
            ConverterTool.IMAGE_TO_PDF -> imagesPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            ConverterTool.MERGE_PDF -> multiplePdfPickerLauncher.launch(arrayOf("application/pdf"))
            ConverterTool.COMPRESS_PDF, ConverterTool.PDF_TO_JPG -> singlePdfPickerLauncher.launch(arrayOf("application/pdf"))
        }
    }

    fun startConvert(name: String) {
        when (tool) {
            ConverterTool.IMAGE_TO_PDF -> viewModel.convertImagesToPdf(context, selectedUris, name)
            ConverterTool.MERGE_PDF -> viewModel.mergePdfs(context, selectedUris, name)
            ConverterTool.COMPRESS_PDF -> selectedUris.firstOrNull()?.let { viewModel.compressPdf(context, it, name) }
            ConverterTool.PDF_TO_JPG -> selectedUris.firstOrNull()?.let { viewModel.pdfToJpegs(context, it, name) }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(titleRes), maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        bottomBar = { co.resume.ui.component.BannerAdView() }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isProcessing -> ConverterStatusContent(
                    icon = null,
                    message = stringResource(R.string.converter_processing),
                    showProgress = true
                )
                uiState.isDone -> ConverterSuccessContent(
                    resultTitle = uiState.resultTitle,
                    resultPdfPath = uiState.resultPdfPath,
                    resultImagePaths = uiState.resultImagePaths,
                    isDownloading = uiState.isDownloading,
                    downloadedMessage = uiState.downloadedMessage,
                    onDownload = {
                        viewModel.downloadResult(context)
                        if (adManager.canOfferRewardedPrompt()) showAdPrompt = true
                    },
                    onConvertAnother = {
                        viewModel.reset()
                        selectedUris = emptyList()
                    },
                    onGoToDocuments = onGoToDocuments
                )
                uiState.errorMessage != null -> ConverterStatusContent(
                    icon = Icons.Filled.Error,
                    message = uiState.errorMessage ?: stringResource(R.string.converter_error_generic),
                    actions = {
                        Button(onClick = { viewModel.reset() }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.btn_cancel))
                        }
                    }
                )
                else -> Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ) {
                    Text(
                        stringResource(descRes),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.padding(top = 16.dp))
                    OutlinedButton(onClick = { launchPicker() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(stringResource(pickButtonRes))
                    }

                    if (selectedUris.isNotEmpty()) {
                        Text(
                            stringResource(R.string.converter_selected_count, selectedUris.size),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        if (tool == ConverterTool.MERGE_PDF && selectedUris.size < 2) {
                            Text(
                                stringResource(R.string.converter_merge_min_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        SelectedFilesPreview(
                            uris = selectedUris,
                            isImage = tool == ConverterTool.IMAGE_TO_PDF,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Button(
                            onClick = { showNameSheet = true },
                            enabled = canConvert,
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
                        ) { Text(stringResource(R.string.converter_convert_btn)) }
                    }
                }
            }
        }
    }

    if (showNameSheet) {
        ConverterNameSheet(
            onDismiss = { showNameSheet = false },
            onConfirm = { name ->
                showNameSheet = false
                startConvert(name)
            }
        )
    }

    if (showAdPrompt) {
        SupportWithAdDialog(
            onWatchAd = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
                activity?.let { adManager.showRewarded(it) {} }
            },
            onSkip = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
            }
        )
    }
}

@Composable
private fun ConverterSuccessContent(
    resultTitle: String,
    resultPdfPath: String?,
    resultImagePaths: List<String>,
    isDownloading: Boolean,
    downloadedMessage: String?,
    onDownload: () -> Unit,
    onConvertAnother: () -> Unit,
    onGoToDocuments: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Text(stringResource(R.string.converter_success_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            stringResource(R.string.converter_success_message, resultTitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        ResultPreview(
            resultPdfPath = resultPdfPath,
            resultImagePaths = resultImagePaths,
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth()
        )

        if (downloadedMessage != null) {
            Text(
                downloadedMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onDownload, enabled = !isDownloading, modifier = Modifier.fillMaxWidth()) {
                if (isDownloading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp).padding(end = 8.dp))
                } else {
                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                }
                Text(stringResource(R.string.converter_download_btn))
            }
            OutlinedButton(onClick = onConvertAnother, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.converter_convert_another_btn))
            }
            TextButton(onClick = onGoToDocuments, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.converter_go_to_documents_btn))
            }
        }
    }
}

@Composable
private fun ResultPreview(resultPdfPath: String?, resultImagePaths: List<String>, modifier: Modifier = Modifier) {
    if (resultImagePaths.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(resultImagePaths) { path ->
                AsyncImage(
                    model = path,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
        return
    }
    if (resultPdfPath == null) return

    var previewBitmap by remember(resultPdfPath) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(resultPdfPath) {
        previewBitmap = withContext(Dispatchers.IO) {
            runCatching { PdfConverter.renderFirstPage(File(resultPdfPath)) }.getOrNull()
        }
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val bitmap = previewBitmap
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
        } else {
            CircularProgressIndicator(modifier = Modifier.padding(32.dp))
        }
    }
}

@Composable
private fun SelectedFilesPreview(uris: List<Uri>, isImage: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(uris) { uri ->
            if (isImage) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                Column(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.InsertDriveFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        queryDisplayName(context, uri),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConverterNameSheet(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.converter_name_sheet_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.converter_title_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            Button(
                onClick = { onConfirm(name) },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) { Text(stringResource(R.string.converter_convert_btn)) }
        }
    }
}

@Composable
private fun ConverterStatusContent(
    icon: ImageVector?,
    message: String,
    title: String? = null,
    showProgress: Boolean = false,
    actions: (@Composable ColumnScope.() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            if (showProgress) {
                CircularProgressIndicator()
            } else if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (icon == Icons.Filled.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            if (title != null) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )
            actions?.let {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) { it() }
            }
        }
    }
}

private fun queryDisplayName(context: android.content.Context, uri: Uri): String {
    return try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
        } ?: uri.lastPathSegment ?: "PDF"
    } catch (e: Exception) {
        uri.lastPathSegment ?: "PDF"
    }
}
