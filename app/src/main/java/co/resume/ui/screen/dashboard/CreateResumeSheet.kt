package co.resume.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.resumeai.R
import co.resume.ui.viewmodel.ResumeListViewModel
import co.resume.utils.ImeLocaleHint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResumeSheet(
    onDismiss: () -> Unit,
    onCreated: (resumeId: Long) -> Unit,
    viewModel: ResumeListViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }

    ImeLocaleHint()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.create_resume_heading), style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = designation,
                onValueChange = { designation = it },
                label = { Text(stringResource(R.string.label_designation)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = {
                    viewModel.createResume(name.trim(), designation.trim()) { id ->
                        onCreated(id)
                    }
                },
                enabled = name.isNotBlank() && designation.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp)
            ) {
                Text(stringResource(R.string.btn_create))
            }
        }
    }
}
