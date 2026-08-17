package co.resume.ui.component

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.Animatable

/**
 * Shared "nothing here yet" state for the Documents lists (resumes, cover letters, scans) — a
 * gradient icon badge with a springy pop-in instead of a lone line of gray text, so an empty
 * list still looks like a deliberate, designed screen rather than a placeholder that was
 * forgotten.
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    iconGradient: List<Color> = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
) {
    val pop = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                    .clip(CircleShape)
                    .background(Brush.linearGradient(iconGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )
        }
    }
}
