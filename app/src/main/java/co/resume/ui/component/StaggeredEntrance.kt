package co.resume.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

private const val StaggerStepMs = 40L
private const val MaxStaggerDelayMs = 320L

/**
 * Wraps [content] in a fade + slide-in entrance that staggers by [index], so lists
 * (quick actions, tips, resume cards) animate in one after another instead of popping onto
 * the screen all at once. Alternates the slide direction by index parity — even items slide
 * in from the left, odd items from the right — then settle into place.
 */
@Composable
fun StaggeredEntrance(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * StaggerStepMs).coerceAtMost(MaxStaggerDelayMs))
        visible = true
    }
    val fromLeft = index % 2 == 0
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(320)) + slideInHorizontally(tween(320)) { fullWidth ->
            if (fromLeft) -fullWidth else fullWidth
        },
        modifier = modifier
    ) {
        content()
    }
}
