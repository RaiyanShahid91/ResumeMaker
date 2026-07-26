package co.resume.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Default time every shimmer skeleton stays visible for, regardless of how fast real data arrives. */
const val ShimmerMinDurationMs = 2000L

/**
 * Returns true for [minDurationMs] after first composed, then flips to false — use to gate a
 * card between its shimmer skeleton and its real content so the shimmer is always visible for
 * a full beat instead of flashing on fast/local data.
 *
 * If the card also has a real async loading flag, OR it together with this: the shimmer then
 * shows for at least [minDurationMs] and keeps showing longer if real loading takes longer.
 */
@Composable
fun rememberShimmerGate(minDurationMs: Long = ShimmerMinDurationMs): Boolean {
    var withinMinDuration by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(minDurationMs)
        withinMinDuration = false
    }
    return withinMinDuration
}

/**
 * Animated shimmer brush that sweeps a highlight band across the surface-variant color,
 * used to fake content while real data (resumes, templates) is still loading.
 */
@Composable
private fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translate by transition.animateFloat(
        initialValue = -600f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.surfaceContainerLowest
    return Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(translate - 300f, 0f),
        end = Offset(translate, 0f)
    )
}

/** A single shimmering block — the building unit for skeleton placeholders. */
@Composable
fun ShimmerBlock(modifier: Modifier = Modifier, shape: Shape = MaterialTheme.shapes.small) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .clip(shape)
            .background(rememberShimmerBrush())
    )
}

/** Skeleton placeholder mimicking a resume list row (icon + title + subtitle lines). */
@Composable
fun ResumeCardSkeleton(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(Shapes().medium)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(modifier = Modifier.size(40.dp), shape = CircleShape)
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.3f).height(12.dp))
                Spacer(Modifier.height(6.dp))
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp))
            }
        }
    }
}

/** Generic row skeleton (icon badge + title/subtitle lines) for quick-action, tip and banner cards. */
@Composable
fun ListRowSkeleton(
    modifier: Modifier = Modifier,
    iconSize: Dp = 44.dp,
    titleWidthFraction: Float = 0.5f,
    subtitleWidthFraction: Float = 0.8f
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerBlock(modifier = Modifier.size(iconSize), shape = MaterialTheme.shapes.small)
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            ShimmerBlock(modifier = Modifier.fillMaxWidth(titleWidthFraction).height(16.dp))
            Spacer(Modifier.height(8.dp))
            ShimmerBlock(modifier = Modifier.fillMaxWidth(subtitleWidthFraction).height(12.dp))
        }
    }
}

/** Skeleton placeholder mimicking a template thumbnail card in the featured carousel. */
@Composable
fun TemplateThumbnailSkeleton(width: Dp = 130.dp) {
    Column(modifier = Modifier.width(width)) {
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height((width.value * 1.4f).dp),
            shape = MaterialTheme.shapes.medium
        )
        Spacer(Modifier.height(8.dp))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp))
        Spacer(Modifier.height(4.dp))
        ShimmerBlock(modifier = Modifier.fillMaxWidth(0.4f).height(10.dp))
    }
}
