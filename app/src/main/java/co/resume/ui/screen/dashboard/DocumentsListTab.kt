package co.resume.ui.screen.dashboard

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.analytics.Analytics
import co.resume.data.local.entity.DocumentKind
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.domain.convert.ConverterStorage
import co.resume.domain.scan.ScanStorage
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.ResumeCardSkeleton
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.component.rememberShimmerGate
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.DocumentsListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DocumentsListTab(
    viewModel: DocumentsListViewModel = hiltViewModel(),
    onOpenPdfViewer: (path: String, title: String) -> Unit = { _, _ -> }
) {
    val documents by viewModel.documents.collectAsStateWithLifecycle()
    val documentsFetching by viewModel.isLoading.collectAsStateWithLifecycle()
    // See ResumeListTab's identical fix: gating the shimmer's forced min-duration behind
    // `documents.isNotEmpty()` too means an empty list shows its "no documents" text the instant
    // loading finishes, rather than a pointless couple of seconds of skeleton rows.
    val isLoading = documentsFetching || (documents.isNotEmpty() && rememberShimmerGate())
    var pendingDelete by remember { mutableStateOf<ScannedDocumentEntity?>(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.weight(1f), color = Color.Transparent) {
            if (isLoading) {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    items(4) { ResumeCardSkeleton() }
                }
            } else if (documents.isEmpty()) {
                co.resume.ui.component.EmptyStateView(
                    icon = Icons.Filled.DocumentScanner,
                    text = stringResource(R.string.no_documents_hint)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    itemsIndexed(documents, key = { _, document -> document.id }) { index, document ->
                        StaggeredEntrance(index = index) {
                            DocumentCard(
                                document = document,
                                onClick = {
                                    if (document.kind == DocumentKind.CONVERTED_IMAGE) {
                                        val path = document.imagePath
                                        val uri = path?.let { ConverterStorage.getShareUri(context, it) }
                                        val intent = uri?.let {
                                            Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(it, "image/jpeg")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                        }
                                        val hasImageApp = intent != null && intent.resolveActivity(context.packageManager) != null
                                        if (hasImageApp) {
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, context.getString(R.string.document_open_failed), Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.document_open_failed), Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        val uri = ScanStorage.getShareUri(context, document.pdfPath)
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "application/pdf")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        val hasPdfApp = intent.resolveActivity(context.packageManager) != null
                                        if (hasPdfApp) {
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                onOpenPdfViewer(document.pdfPath, document.title)
                                            }
                                        } else {
                                            onOpenPdfViewer(document.pdfPath, document.title)
                                        }
                                    }
                                },
                                onRequestDelete = { pendingDelete = document }
                            )
                        }
                    }
                }
            }
        }
        BannerAdView()
    }

    pendingDelete?.let { document ->
        DeleteDocumentConfirmSheet(
            document = document,
            onDismiss = { pendingDelete = null },
            onConfirm = {
                viewModel.deleteDocument(document.id)
                Analytics.logEvent(Analytics.Event.DOCUMENT_DELETED)
                pendingDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun DocumentCard(document: ScannedDocumentEntity, onClick: () -> Unit, onRequestDelete: () -> Unit) {
    val haptics = LocalHapticFeedback.current
    // Only the left swipe (EndToStart) is enabled and it never actually dismisses the item —
    // confirmValueChange always returns false so the card animates back into place, and the
    // delete confirmation sheet decides whether the document is actually removed.
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onRequestDelete()
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        AppCard(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onRequestDelete()
                    }
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (document.kind == DocumentKind.CONVERTED_IMAGE) Icons.Filled.Image else Icons.Filled.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(document.title.ifBlank { "Untitled document" }, style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.document_pages_label, document.pageCount, formatDate(document.createdAt)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteDocumentConfirmSheet(
    document: ScannedDocumentEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.document_delete_confirm_title), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.document_delete_confirm_message, document.title.ifBlank { "Untitled document" }),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.btn_cancel)) }
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.btn_delete)) }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(timestamp))

private val previewDocument = ScannedDocumentEntity(
    id = 1,
    title = "Old Resume Draft",
    pdfPath = "",
    pageCount = 2,
    ocrText = "",
    needsReview = false,
    createdAt = System.currentTimeMillis()
)

@Preview(showBackground = true)
@Composable
private fun DocumentCardPreview() {
    ResumeBuilderTheme {
        DocumentCard(document = previewDocument, onClick = {}, onRequestDelete = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun DocumentsListContentPreview() {
    ResumeBuilderTheme {
        LazyColumn(contentPadding = PaddingValues(20.dp)) {
            itemsIndexed(
                listOf(
                    previewDocument,
                    previewDocument.copy(id = 2, title = "Certificate Scan", pageCount = 1)
                ),
                key = { _, document -> document.id }
            ) { index, document ->
                StaggeredEntrance(index = index) {
                    DocumentCard(document = document, onClick = {}, onRequestDelete = {})
                }
            }
        }
    }
}
