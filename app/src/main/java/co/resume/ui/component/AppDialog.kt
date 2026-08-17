package co.resume.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.theme.SheetBackgroundGradient

private val DialogShape = RoundedCornerShape(28.dp)

/**
 * The app's shared dialog — same top-left gradient fill as [AppBottomSheet] instead of
 * M3's [androidx.compose.material3.AlertDialog], whose `containerColor` only accepts a
 * solid [androidx.compose.ui.graphics.Color] (no gradient/Brush support), and whose
 * icon/title/text/buttons all share one internal surface that can't be wrapped piecemeal.
 * Mirrors AlertDialog's default spacing (24dp padding, titleLarge title, bodyMedium text,
 * end-aligned button row) so it reads the same, just with the app's gradient background.
 */
@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(DialogShape)
                .background(SheetBackgroundGradient)
                .padding(24.dp)
        ) {
            if (title != null) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleLarge) {
                    title()
                }
                Spacer(Modifier.height(16.dp))
            }
            if (text != null) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                    text()
                }
                Spacer(Modifier.height(24.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
            ) {
                dismissButton?.invoke()
                confirmButton()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppDialogPreview() {
    ResumeBuilderTheme {
        AppDialog(
            onDismissRequest = {},
            title = { Text("Delete resume?") },
            text = { Text("This action cannot be undone.") },
            dismissButton = { TextButton(onClick = {}) { Text("Cancel") } },
            confirmButton = { TextButton(onClick = {}) { Text("Delete") } }
        )
    }
}
