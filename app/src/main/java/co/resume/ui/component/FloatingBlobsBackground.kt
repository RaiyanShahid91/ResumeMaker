package co.resume.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Three soft, heavily-blurred color blobs that drift slowly up and down forever, spread across
 * the top/middle/bottom of the screen so the motion reads across the whole page rather than
 * being cramped into one corner. Painted once behind the whole nav host (see MainActivity)
 * rather than per-screen, so every screen shares the same continuous, un-reset animation instead
 * of each screen replaying its own on entry.
 */
@Composable
fun FloatingBlobsBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "floating_blobs")
    // Kept inside a smaller range than before (was ±50-80dp) — that let a blob drift far enough
    // off its corner to leave the visible screen entirely for part of each cycle, which read as
    // "sometimes not visible" rather than continuously animating.
    val float1 by transition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "blob1_float"
    )
    val float2 by transition.animateFloat(
        initialValue = 40f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(tween(11000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "blob2_float"
    )
    val float3 by transition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(tween(7000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "blob3_float"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(220.dp)
                .align(Alignment.TopStart)
                .offset(x = (-30).dp, y = 20.dp)
                .offset(y = float1.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.38f))
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 50.dp, y = 0.dp)
                .offset(y = float2.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.34f))
                .blur(70.dp)
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = (-20).dp)
                .offset(y = float3.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.30f))
                .blur(65.dp)
        )
    }
}
