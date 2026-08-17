package co.resume.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme

/** Fully-rounded pill shape used for primary/secondary buttons, matching the reference design. */
private val PillShape = RoundedCornerShape(percent = 50)

private const val PressedScale = 0.95f

@Composable
private fun rememberPressScale(interactionSource: MutableInteractionSource): Float {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "buttonPressScale"
    )
    return scale
}

/** Primary call-to-action button with a springy press animation. */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = PillShape,
        modifier = modifier
            .height(52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        icon?.invoke()
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    ResumeBuilderTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppButton(text = "Create resume", onClick = {})
            AppButton(text = "Disabled", onClick = {}, enabled = false)
            AppOutlinedButton(text = "Cancel", onClick = {})
        }
    }
}

/** Secondary/outlined button with the same tactile press feedback as [AppButton]. */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = rememberPressScale(interactionSource)
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = PillShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        modifier = modifier
            .height(52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
    ) {
        icon?.invoke()
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
