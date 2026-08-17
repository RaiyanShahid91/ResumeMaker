package co.resume.ui.screen.dashboard

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
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.data.local.entity.CoverLetterEntity
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.ResumeCardSkeleton
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.component.rememberShimmerGate
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.CoverLetterListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CoverLetterListTab(
    onOpenCoverLetter: (id: Long) -> Unit,
    viewModel: CoverLetterListViewModel = hiltViewModel()
) {
    val coverLetters by viewModel.coverLetters.collectAsStateWithLifecycle()
    val isFetching by viewModel.isLoading.collectAsStateWithLifecycle()
    CoverLetterListTabContent(
        coverLetters = coverLetters,
        isLoading = isFetching,
        onOpenCoverLetter = onOpenCoverLetter,
        onDeleteCoverLetter = viewModel::deleteCoverLetter
    )
}

@Composable
private fun CoverLetterListTabContent(
    coverLetters: List<CoverLetterEntity>,
    isLoading: Boolean,
    onOpenCoverLetter: (id: Long) -> Unit,
    onDeleteCoverLetter: (id: Long) -> Unit
) {
    // See ResumeListTab's identical fix: gating the shimmer's forced min-duration behind
    // `coverLetters.isNotEmpty()` too means an empty list shows its "no cover letters" text the
    // instant loading finishes, rather than a pointless couple of seconds of skeleton rows.
    val showSkeleton = isLoading || (coverLetters.isNotEmpty() && rememberShimmerGate())
    var pendingDelete by remember { mutableStateOf<CoverLetterEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (showSkeleton) {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    items(3) { ResumeCardSkeleton() }
                }
            } else if (coverLetters.isEmpty()) {
                co.resume.ui.component.EmptyStateView(
                    icon = Icons.AutoMirrored.Filled.Article,
                    text = stringResource(R.string.no_cover_letters_hint)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    itemsIndexed(coverLetters, key = { _, letter -> letter.id }) { index, letter ->
                        StaggeredEntrance(index = index) {
                            CoverLetterCard(
                                letter = letter,
                                onClick = { onOpenCoverLetter(letter.id) },
                                onRequestDelete = { pendingDelete = letter }
                            )
                        }
                    }
                }
            }
        }
        BannerAdView()
    }

    pendingDelete?.let { letter ->
        DeleteCoverLetterConfirmSheet(
            letter = letter,
            onDismiss = { pendingDelete = null },
            onConfirm = {
                onDeleteCoverLetter(letter.id)
                pendingDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun CoverLetterCard(letter: CoverLetterEntity, onClick: () -> Unit, onRequestDelete: () -> Unit) {
    val haptics = LocalHapticFeedback.current
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
                    Icons.AutoMirrored.Filled.Article,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        listOfNotNull(letter.jobTitle.ifBlank { null }, letter.companyName.ifBlank { null })
                            .joinToString(" · ")
                            .ifBlank { stringResource(R.string.cover_letter_untitled) },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        formatDate(letter.updatedAt),
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
private fun DeleteCoverLetterConfirmSheet(
    letter: CoverLetterEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.cover_letter_delete_confirm_title), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(
                    R.string.cover_letter_delete_confirm_message,
                    letter.companyName.ifBlank { stringResource(R.string.cover_letter_untitled) }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.btn_cancel)) }
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

private val previewLetter = CoverLetterEntity(id = 1, companyName = "Northwind Labs", jobTitle = "Product Designer")

@Preview(showBackground = true)
@Composable
private fun CoverLetterListTabPreview() {
    ResumeBuilderTheme {
        CoverLetterListTabContent(
            coverLetters = listOf(previewLetter, previewLetter.copy(id = 2, companyName = "Brightside Co.")),
            isLoading = false,
            onOpenCoverLetter = {},
            onDeleteCoverLetter = {}
        )
    }
}
