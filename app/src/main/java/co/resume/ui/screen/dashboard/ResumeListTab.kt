package co.resume.ui.screen.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import co.resumeai.R
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.domain.score.AtsScoreCalculator
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.GradientProgressBar
import co.resume.ui.component.ResumeCardSkeleton
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.component.rememberShimmerGate
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.ResumeListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeListTab(
    onOpenResume: (resumeId: Long) -> Unit,
    viewModel: ResumeListViewModel = hiltViewModel()
) {
    val resumes by viewModel.resumes.collectAsStateWithLifecycle()
    val resumesFetching by viewModel.isLoading.collectAsStateWithLifecycle()
    ResumeListTabContent(
        resumes = resumes,
        resumesLoading = resumesFetching,
        onOpenResume = onOpenResume,
        onDeleteResume = viewModel::deleteResume,
        onReorderResumes = viewModel::reorderResumes
    )
}

@Composable
private fun ResumeListTabContent(
    resumes: List<ResumeWithDetails>,
    resumesLoading: Boolean,
    onOpenResume: (resumeId: Long) -> Unit,
    onDeleteResume: (resumeId: Long) -> Unit,
    onReorderResumes: (orderedIds: List<Long>) -> Unit = {}
) {
    // The shimmer's forced minimum-visible-duration (rememberShimmerGate) exists to avoid a
    // flash-then-content jump on fast local data — but that only makes sense when there's real
    // content coming. Gating it behind `resumes.isNotEmpty()` too means an empty list resolves
    // straight to the "no resumes yet" text the instant loading finishes, instead of forcing a
    // pointless couple of seconds of skeleton rows for content that was never going to appear.
    val isLoading = resumesLoading || (resumes.isNotEmpty() && rememberShimmerGate())
    var pendingDelete by remember { mutableStateOf<ResumeEntity?>(null) }
    val haptics = LocalHapticFeedback.current

    // Live-reorderable local copy of the list, driving what's actually drawn while a drag is in
    // progress. Resynced from the real (DB-backed) list whenever nothing is being dragged, so
    // creations/deletions elsewhere still show up immediately; while dragging, the local order is
    // authoritative so the drag doesn't visually fight incoming recompositions from the same drag's
    // own not-yet-persisted reorderResumes calls (there are none until drag end, but this also
    // protects against any other concurrent list change mid-drag).
    var orderedResumes by remember { mutableStateOf(resumes) }
    var draggingId by remember { mutableStateOf<Long?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val itemHeights = remember { mutableStateMapOf<Long, Int>() }
    LaunchedEffect(resumes) {
        if (draggingId == null) orderedResumes = resumes
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.weight(1f), color = Color.Transparent) {
            if (isLoading) {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    items(4) { ResumeCardSkeleton() }
                }
            } else if (resumes.isEmpty()) {
                co.resume.ui.component.EmptyStateView(
                    icon = Icons.Filled.Description,
                    text = stringResource(R.string.no_resumes_hint)
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    itemsIndexed(orderedResumes, key = { _, details -> details.resume.id }) { index, details ->
                        val id = details.resume.id
                        StaggeredEntrance(
                            index = index,
                            modifier = if (id == draggingId) Modifier else Modifier.animateItem()
                        ) {
                            ResumeCard(
                                details = details,
                                isDragging = id == draggingId,
                                dragOffsetY = if (id == draggingId) dragOffsetY else 0f,
                                onClick = { onOpenResume(id) },
                                onRequestDelete = { pendingDelete = details.resume },
                                onMeasuredHeight = { heightPx -> itemHeights[id] = heightPx },
                                onDragStart = {
                                    draggingId = id
                                    dragOffsetY = 0f
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragDelta = { delta ->
                                    dragOffsetY += delta
                                    val myHeight = itemHeights[id] ?: return@ResumeCard
                                    val currentIndex = orderedResumes.indexOfFirst { it.resume.id == id }
                                    if (dragOffsetY > 0f) {
                                        val nextIndex = currentIndex + 1
                                        val nextHeight = orderedResumes.getOrNull(nextIndex)?.let { itemHeights[it.resume.id] } ?: myHeight
                                        if (nextIndex < orderedResumes.size && dragOffsetY > nextHeight / 2f) {
                                            orderedResumes = orderedResumes.toMutableList().apply {
                                                this[currentIndex] = this[nextIndex].also { this[nextIndex] = this[currentIndex] }
                                            }
                                            dragOffsetY -= nextHeight
                                        }
                                    } else {
                                        val prevIndex = currentIndex - 1
                                        val prevHeight = orderedResumes.getOrNull(prevIndex)?.let { itemHeights[it.resume.id] } ?: myHeight
                                        if (prevIndex >= 0 && -dragOffsetY > prevHeight / 2f) {
                                            orderedResumes = orderedResumes.toMutableList().apply {
                                                this[currentIndex] = this[prevIndex].also { this[prevIndex] = this[currentIndex] }
                                            }
                                            dragOffsetY += prevHeight
                                        }
                                    }
                                },
                                onDragEnd = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    draggingId = null
                                    dragOffsetY = 0f
                                    onReorderResumes(orderedResumes.map { it.resume.id })
                                }
                            )
                        }
                    }
                }
            }
        }
        BannerAdView()
    }

    pendingDelete?.let { resume ->
        DeleteResumeConfirmSheet(
            resume = resume,
            onDismiss = { pendingDelete = null },
            onConfirm = {
                onDeleteResume(resume.id)
                pendingDelete = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ResumeCard(
    details: ResumeWithDetails,
    onClick: () -> Unit,
    onRequestDelete: () -> Unit,
    isDragging: Boolean = false,
    dragOffsetY: Float = 0f,
    onMeasuredHeight: (Int) -> Unit = {},
    onDragStart: () -> Unit = {},
    onDragDelta: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {
    val resume = details.resume
    val score = AtsScoreCalculator.score(details)
    val haptics = LocalHapticFeedback.current
    // Only the left swipe (EndToStart) is enabled and it never actually dismisses the item —
    // confirmValueChange always returns false so the card animates back into place, and the
    // delete confirmation sheet decides whether the resume is actually removed. This is the ONLY
    // way to delete a card now — long-press is reorder (see below), not delete, since a single
    // gesture can't mean both without the user having to guess which one wins.
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .onGloballyPositioned { onMeasuredHeight(it.size.height) }
            .graphicsLayer {
                translationY = dragOffsetY
                shadowElevation = if (isDragging) 16f else 0f
                scaleX = if (isDragging) 1.03f else 1f
                scaleY = if (isDragging) 1.03f else 1f
            }
            .zIndex(if (isDragging) 1f else 0f),
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
                .clickable(onClick = onClick)
                .pointerInput(resume.id) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { onDragStart() },
                        onDragEnd = { onDragEnd() },
                        onDragCancel = { onDragEnd() },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onDragDelta(dragAmount.y)
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(resume.name.ifBlank { "Untitled resume" }, style = MaterialTheme.typography.titleMedium)
                    Text(
                        resume.designation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        formatDate(resume.updatedAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GradientProgressBar(
                            progress = score / 100f,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            stringResource(R.string.editor_ats_score_percent, score),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteResumeConfirmSheet(
    resume: ResumeEntity,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.resume_delete_confirm_title), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.resume_delete_confirm_message, resume.name.ifBlank { "Untitled resume" }),
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
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.btn_delete)) }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String =
    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(timestamp))

private val previewResume = ResumeEntity(id = 1, name = "Alex Morgan", designation = "Product Designer")
private val previewResumeDetails = ResumeWithDetails(resume = previewResume)

@Preview(showBackground = true)
@Composable
private fun ResumeListTabPreview() {
    ResumeBuilderTheme {
        ResumeListTabContent(
            resumes = listOf(
                previewResumeDetails,
                previewResumeDetails.copy(resume = previewResume.copy(id = 2, name = "Jordan Lee", designation = "Software Engineer"))
            ),
            resumesLoading = false,
            onOpenResume = {},
            onDeleteResume = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun ResumeCardPreview() {
    ResumeBuilderTheme {
        ResumeCard(details = previewResumeDetails, onClick = {}, onRequestDelete = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun DeleteResumeConfirmSheetPreview() {
    ResumeBuilderTheme {
        DeleteResumeConfirmSheet(resume = previewResume, onDismiss = {}, onConfirm = {})
    }
}
