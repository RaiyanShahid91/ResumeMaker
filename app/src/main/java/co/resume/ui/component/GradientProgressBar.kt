package co.resume.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme

private val LowColor = Color(0xFFEF4444)
private val MidColor = Color(0xFFF59E0B)
private val HighColor = Color(0xFF22C55E)

/**
 * A red-to-amber-to-green "health bar" style progress indicator (vs. the single-tint M3
 * [androidx.compose.material3.LinearProgressIndicator]) used for the resume completeness/ATS
 * score. The gradient spans the full track width, and progress reveals a slice of it — so a
 * low score reads red, a mid score amber, and a high score green, the same way a battery or
 * health meter communicates quality at a glance, not just a percentage number.
 */
@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp
) {
    val clamped = progress.coerceIn(0f, 1f)
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.height(height)) {
        val corner = CornerRadius(size.height / 2f, size.height / 2f)
        drawRoundRect(color = trackColor, cornerRadius = corner)
        if (clamped > 0f) {
            val fullGradient = Brush.horizontalGradient(
                colors = listOf(LowColor, MidColor, HighColor),
                startX = 0f,
                endX = size.width
            )
            clipRect(right = size.width * clamped) {
                drawRoundRect(brush = fullGradient, cornerRadius = corner)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GradientProgressBarPreview() {
    ResumeBuilderTheme {
        GradientProgressBar(progress = 0.65f, modifier = Modifier.fillMaxWidth())
    }
}
