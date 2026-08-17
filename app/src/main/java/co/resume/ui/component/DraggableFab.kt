package co.resume.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private val FabMargin = 16.dp
private val FabStackSpacing = 16.dp

/** One button inside a [DraggableFabGroup]. [key] must be stable across recompositions. */
data class DraggableFabItem(
    val key: Any,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit
)

/**
 * A cluster of [FloatingActionButton]s, each stacked bottom-right above the previous one and
 * each independently draggable anywhere on screen. On release, a button always snaps to the
 * nearest horizontal edge — UNLESS that would land it on top of (or too close to) another
 * button in the group, in which case it snaps back to its own original stacked position
 * instead, so the two can never end up overlapping.
 */
@Composable
fun DraggableFabGroup(
    items: List<DraggableFabItem>,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()
        val marginPx = with(density) { FabMargin.toPx() }
        val stackSpacingPx = with(density) { FabStackSpacing.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        var fabSizePx by remember { mutableFloatStateOf(with(density) { 56.dp.toPx() }) }

        fun homeX() = maxWidthPx - fabSizePx - marginPx
        fun homeY(index: Int) = maxHeightPx - fabSizePx - marginPx - index * (fabSizePx + stackSpacingPx)
        fun trackWidth() = (maxWidthPx - fabSizePx).coerceAtLeast(0f)
        fun trackHeight() = (maxHeightPx - fabSizePx).coerceAtLeast(0f)
        val minSeparation = fabSizePx + stackSpacingPx / 2f

        val offsetXs = items.mapIndexed { index, item -> remember(item.key) { Animatable(homeX()) } }
        val offsetYs = items.mapIndexed { index, item -> remember(item.key) { Animatable(homeY(index)) } }

        items.forEachIndexed { index, item ->
            val offsetX = offsetXs[index]
            val offsetY = offsetYs[index]

            FloatingActionButton(
                onClick = item.onClick,
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        if (fabSizePx != coordinates.size.width.toFloat() && coordinates.size.width > 0) {
                            fabSizePx = coordinates.size.width.toFloat()
                        }
                    }
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                    .pointerInput(item.key, maxWidthPx, maxHeightPx) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetX.snapTo((offsetX.value + dragAmount.x).coerceIn(0f, trackWidth()))
                                    offsetY.snapTo((offsetY.value + dragAmount.y).coerceIn(0f, trackHeight()))
                                }
                            },
                            onDragEnd = {
                                val snappedX = if (offsetX.value <= trackWidth() / 2f) 0f else trackWidth()
                                val wouldOverlap = items.indices.any { other ->
                                    other != index &&
                                        abs(snappedX - offsetXs[other].value) < minSeparation &&
                                        abs(offsetY.value - offsetYs[other].value) < minSeparation
                                }
                                scope.launch {
                                    if (wouldOverlap) {
                                        offsetX.animateTo(homeX(), spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                                        offsetY.animateTo(homeY(index), spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                                    } else {
                                        offsetX.animateTo(snappedX, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow))
                                    }
                                }
                            }
                        )
                    }
            ) {
                item.icon()
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
private fun DraggableFabGroupPreview() {
    ResumeBuilderTheme {
        DraggableFabGroup(
            items = listOf(
                DraggableFabItem(key = "scan", onClick = {}) { Icon(Icons.Filled.DocumentScanner, contentDescription = null) },
                DraggableFabItem(key = "create", onClick = {}) { Icon(Icons.Filled.Add, contentDescription = null) }
            ),
            modifier = Modifier.height(400.dp)
        )
    }
}
