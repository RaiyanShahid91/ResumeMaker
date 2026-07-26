package co.resume.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val FabMargin = 16.dp

/**
 * A [FloatingActionButton] that starts in the bottom-right corner and can be dragged
 * anywhere on screen, clamped so it never goes past the edges of its container. On release
 * it always snaps horizontally to the nearest edge (left if exactly centered) instead of
 * resting mid-screen — vertical position is kept wherever it was dropped.
 */
@Composable
fun DraggableFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()
        val marginPx = with(density) { FabMargin.toPx() }
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        var fabSizePx by remember { mutableFloatStateOf(with(density) { 56.dp.toPx() }) }
        val offsetX = remember { Animatable(maxWidthPx - fabSizePx - marginPx) }
        var offsetY by remember { mutableFloatStateOf(maxHeightPx - fabSizePx - marginPx) }

        fun trackWidth() = (maxWidthPx - fabSizePx).coerceAtLeast(0f)

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    if (fabSizePx != coordinates.size.width.toFloat() && coordinates.size.width > 0) {
                        fabSizePx = coordinates.size.width.toFloat()
                    }
                }
                .offset { IntOffset(offsetX.value.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(maxWidthPx, maxHeightPx) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val newX = (offsetX.value + dragAmount.x).coerceIn(0f, trackWidth())
                            scope.launch { offsetX.snapTo(newX) }
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, (maxHeightPx - fabSizePx).coerceAtLeast(0f))
                        },
                        onDragEnd = {
                            // Always settle against the nearest horizontal edge — a drop in
                            // the center (or anywhere left of it) snaps left, never stays put.
                            val target = if (offsetX.value <= trackWidth() / 2f) 0f else trackWidth()
                            scope.launch {
                                offsetX.animateTo(
                                    target,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                                )
                            }
                        }
                    )
                }
        ) {
            icon()
        }
    }
}
