package co.resume.ui.component

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R

private const val PrefsName = "coach_marks"

/**
 * A small dismissible tip shown once per [id] (persisted in its own SharedPreferences file,
 * independent of any ViewModel/DI so it can drop into any screen) — used to explain a
 * non-obvious interaction (drag-to-reorder, browse-then-pick-a-template) the first time a user
 * reaches that screen. Renders nothing once dismissed or after the first dismissal in any
 * previous session.
 */
@Composable
fun OneTimeCoachMark(id: String, message: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var dismissed by remember(id) { mutableStateOf(isDismissed(context, id)) }

    if (!dismissed) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 14.dp, end = 4.dp, top = 10.dp, bottom = 10.dp)
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f).padding(start = 10.dp)
                )
                IconButton(onClick = {
                    setDismissed(context, id)
                    dismissed = true
                }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.btn_cancel),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun isDismissed(context: Context, id: String): Boolean =
    context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE).getBoolean(id, false)

private fun setDismissed(context: Context, id: String) {
    context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE).edit().putBoolean(id, true).apply()
}
