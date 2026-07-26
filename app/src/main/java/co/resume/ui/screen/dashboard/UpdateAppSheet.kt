package co.resume.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AppBottomSheet
import co.resume.utils.VersionCheckUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAppSheet(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    AppBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.update_title), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.update_body),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = {
                    VersionCheckUtils.openPlayStore(context)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp)
            ) {
                Text(stringResource(R.string.update_btn))
            }
        }
    }
}
