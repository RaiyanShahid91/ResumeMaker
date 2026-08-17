package co.resume.ui.component

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.resumeai.R

/**
 * The opt-in "watch an ad to support the app" prompt shown after a save completes — never a
 * gate. Whatever the user was saving is already saved by the time this shows; declining just
 * closes the dialog with no other effect. Kept as one shared composable so its wording/behavior
 * stays identical everywhere it's offered (resume sections, cover letters, scanned documents)
 * rather than drifting per screen.
 */
@Composable
fun SupportWithAdDialog(onWatchAd: () -> Unit, onSkip: () -> Unit) {
    AppDialog(
        onDismissRequest = onSkip,
        title = { Text(stringResource(R.string.support_ad_title)) },
        text = { Text(stringResource(R.string.support_ad_body)) },
        dismissButton = {
            TextButton(onClick = onSkip) { Text(stringResource(R.string.support_ad_skip)) }
        },
        confirmButton = {
            TextButton(onClick = onWatchAd) { Text(stringResource(R.string.support_ad_watch)) }
        }
    )
}
