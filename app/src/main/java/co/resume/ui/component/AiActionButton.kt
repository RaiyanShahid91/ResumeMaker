package co.resume.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme

/**
 * Sparkle icon <-> spinner toggle used as an [AppTextField] trailingIcon to trigger AI
 * generation/improvement for a field. The caller owns the coroutine and loading state;
 * this only renders the two visual states.
 */
@Composable
fun AiActionButton(
    isGenerating: Boolean,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isGenerating) {
        CircularProgressIndicator(modifier = modifier.size(18.dp).padding(2.dp), strokeWidth = 2.dp)
    } else {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiActionButtonPreview() {
    ResumeBuilderTheme {
        AiActionButton(isGenerating = false, contentDescription = null, onClick = {})
    }
}
