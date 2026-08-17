package co.resume.ui.component

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.resumeai.R

/**
 * A plain (non-lazy) reorderable column: long-press the drag handle exposed to each item via
 * `dragHandleModifier`, drag up/down, and items swap live once the drag crosses roughly half of a
 * neighboring item's height — no external reorder library, just incremental index swaps driven by
 * accumulated drag distance. Intended for the short entity lists in the resume editor (work
 * experience, education, etc.), where a plain scrollable [Column] is a fine trade-off against a
 * lazily-composed list.
 */
@Composable
fun <T> DragReorderColumn(
    items: List<T>,
    onMove: (from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (index: Int, item: T, dragHandleModifier: Modifier) -> Unit
) {
    val itemHeights = remember { mutableStateOf(IntArray(items.size)) }
    if (itemHeights.value.size != items.size) itemHeights.value = IntArray(items.size)

    var draggingIndex by remember { mutableStateOf(-1) }
    var dragOffset by remember { mutableStateOf(0f) }
    val haptics = LocalHapticFeedback.current

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val isDragging = draggingIndex == index
            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer { translationY = if (isDragging) dragOffset else 0f }
                    .onGloballyPositioned { coords ->
                        val heights = itemHeights.value
                        if (index < heights.size) heights[index] = coords.size.height
                    }
            ) {
                val dragHandleModifier = Modifier.pointerInput(items.size) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            draggingIndex = index
                            dragOffset = 0f
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragEnd = {
                            draggingIndex = -1
                            dragOffset = 0f
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragCancel = {
                            draggingIndex = -1
                            dragOffset = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset += dragAmount.y
                            val current = draggingIndex
                            if (current < 0) return@detectDragGesturesAfterLongPress
                            val heights = itemHeights.value
                            val currentHeight = heights.getOrNull(current)?.takeIf { it > 0 } ?: return@detectDragGesturesAfterLongPress
                            if (dragOffset > currentHeight / 2f && current < items.size - 1) {
                                onMove(current, current + 1)
                                draggingIndex = current + 1
                                dragOffset -= currentHeight.toFloat()
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            } else if (dragOffset < -currentHeight / 2f && current > 0) {
                                onMove(current, current - 1)
                                draggingIndex = current - 1
                                dragOffset += currentHeight.toFloat()
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    )
                }
                itemContent(index, item, dragHandleModifier)
            }
        }
    }
}

/** Small drag-handle icon meant to receive [DragReorderColumn]'s per-item `dragHandleModifier`. */
@Composable
fun DragHandle(modifier: Modifier = Modifier) {
    Icon(
        Icons.Filled.DragIndicator,
        contentDescription = stringResource(R.string.cd_drag_reorder),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(24.dp)
    )
}
