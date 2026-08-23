package co.resume.ui.screen.scan

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.resumeai.R
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppButton
import co.resume.ui.component.AppCard
import co.resume.ui.component.AppTextField
import co.resume.ui.component.SupportWithAdDialog
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.ScanUiState
import co.resume.ui.viewmodel.ScannedPageUi
import co.resume.ui.viewmodel.ScannerViewModel

@Composable
fun ScanResultScreen(
    viewModel: ScannerViewModel,
    onBack: () -> Unit,
    adViewModel: co.resume.ui.viewmodel.AdViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val adManager = adViewModel.adManager
    var showSavePdfDialog by remember { mutableStateOf(false) }
    var showAdPrompt by remember { mutableStateOf(false) }

    val savePdfSuccessMsg = stringResource(R.string.scan_pdf_saved)
    val savePdfFailedMsg = stringResource(R.string.scan_pdf_save_failed)
    val shareChooserTitle = stringResource(R.string.scan_share_chooser_title)

    // Save/share run immediately — never gated behind an ad. Afterward we may offer an
    // opt-in "watch an ad to support us" prompt (see SupportWithAdDialog), same as the resume
    // and cover letter editors, subject to AdManager's own cooldown.
    fun offerAdAfter() {
        if (adManager.canOfferRewardedPrompt()) showAdPrompt = true
    }

    ScanResultContent(
        uiState = uiState,
        onBack = onBack,
        onPageTextChange = viewModel::updatePageText,
        onSavePdfClick = { showSavePdfDialog = true },
        onShareClick = {
            val pdfUri = uiState.pdfUri
            if (pdfUri != null) shareScanPdf(context, pdfUri, shareChooserTitle)
            offerAdAfter()
        }
    )

    if (showSavePdfDialog) {
        SaveScanNameSheet(
            onDismiss = { showSavePdfDialog = false },
            onConfirm = { title ->
                showSavePdfDialog = false
                viewModel.saveAsPdf(context, title) { success ->
                    Toast.makeText(context, if (success) savePdfSuccessMsg else savePdfFailedMsg, Toast.LENGTH_SHORT).show()
                }
                offerAdAfter()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanResultContent(
    uiState: ScanUiState,
    onBack: () -> Unit,
    onPageTextChange: (Int, String) -> Unit,
    onSavePdfClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.scan_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.pages.isNotEmpty()) {
                ScanResultFabMenu(
                    expanded = fabExpanded,
                    onToggle = { fabExpanded = !fabExpanded },
                    onSavePdfClick = { fabExpanded = false; onSavePdfClick() },
                    onShareClick = { fabExpanded = false; onShareClick() }
                )
            }
        },
        bottomBar = { co.resume.ui.component.BannerAdView() }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .then(if (fabExpanded) Modifier.blur(20.dp) else Modifier)
            ) {
            when {
                uiState.isProcessing -> {
                    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text(stringResource(R.string.scan_extracting), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                uiState.pages.isNotEmpty() -> {
                    val pagerState = rememberPagerState { uiState.pages.size }
                    HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { pageIndex ->
                        val page = uiState.pages[pageIndex]
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                        ) {
                            Text(
                                stringResource(R.string.scan_page_label, pageIndex + 1, uiState.pages.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            AppCard(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                                AsyncImage(
                                    model = page.imageUri,
                                    contentDescription = stringResource(R.string.scan_cd_page_image),
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (page.needsReview) {
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        stringResource(R.string.scan_needs_review),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Spacer(Modifier.height(12.dp))
                            AppTextField(
                                value = page.text,
                                onValueChange = { onPageTextChange(pageIndex, it) },
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            )
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            }

            if (fabExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { fabExpanded = false }
                )
            }
        }
    }
}

@Composable
private fun ScanResultFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    onSavePdfClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniFabOption(
                    icon = Icons.Filled.Share,
                    label = stringResource(R.string.scan_btn_share),
                    onClick = onShareClick
                )
                MiniFabOption(
                    icon = Icons.Filled.PictureAsPdf,
                    label = stringResource(R.string.scan_btn_save_pdf),
                    onClick = onSavePdfClick
                )
                Spacer(Modifier.height(4.dp))
            }
        }
        FloatingActionButton(onClick = onToggle) {
            Icon(
                if (expanded) Icons.Filled.Close else Icons.Filled.Save,
                contentDescription = stringResource(
                    if (expanded) R.string.scan_cd_close_save_options else R.string.scan_cd_save_options
                )
            )
        }
    }
}

@Composable
private fun MiniFabOption(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }
        SmallFloatingActionButton(onClick = onClick) {
            Icon(icon, contentDescription = label)
        }
    }
}

/** Shares the scanner's own generated PDF directly via the system chooser. */
private fun shareScanPdf(context: Context, pdfUri: Uri, chooserTitle: String) {
    val shareUri = if (pdfUri.scheme == "file") {
        FileProvider.getUriForFile(context, "co.resumeai.fileprovider", File(pdfUri.path!!))
    } else {
        pdfUri
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, shareUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveScanNameSheet(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.scan_save_dialog_title), style = MaterialTheme.typography.titleLarge)
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.scan_save_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 20.dp)
            )
            AppButton(
                text = stringResource(R.string.btn_save),
                onClick = { onConfirm(name.trim()) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private val previewPages = listOf(
    ScannedPageUi(imageUri = Uri.EMPTY, text = "Sample extracted text from the scanned page.", needsReview = false),
    ScannedPageUi(imageUri = Uri.EMPTY, text = "Blurry page — please check the text.", needsReview = true)
)

@Preview(showBackground = true)
@Composable
private fun ScanResultContentPreview() {
    ResumeBuilderTheme {
        ScanResultContent(
            uiState = ScanUiState(pages = previewPages, isReady = true),
            onBack = {},
            onPageTextChange = { _, _ -> },
            onSavePdfClick = {},
            onShareClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanResultProcessingPreview() {
    ResumeBuilderTheme {
        ScanResultContent(
            uiState = ScanUiState(isProcessing = true),
            onBack = {},
            onPageTextChange = { _, _ -> },
            onSavePdfClick = {},
            onShareClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveScanNameSheetPreview() {
    ResumeBuilderTheme {
        SaveScanNameSheet(onDismiss = {}, onConfirm = {})
    }
}
