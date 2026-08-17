package co.resume.ui.screen.dashboard

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.resumeai.R
import co.resume.analytics.Analytics
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.component.AppTextField
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.ResumeListViewModel
import co.resume.utils.ImeLocaleHint
import androidx.compose.material3.Button

/** Which step of the "+"-flow bottom sheet is currently showing. */
private sealed interface CreateResumeStep {
    data object ChooseMode : CreateResumeStep
    data object ManualForm : CreateResumeStep
    data object ChooseSource : CreateResumeStep
    data object Importing : CreateResumeStep
    data class ImportError(val message: String) : CreateResumeStep
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateResumeSheet(
    onDismiss: () -> Unit,
    onCreated: (resumeId: Long) -> Unit,
    viewModel: ResumeListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var step by remember { mutableStateOf<CreateResumeStep>(CreateResumeStep.ChooseMode) }
    val unreadableMsg = stringResource(R.string.import_resume_error_unreadable)
    val genericMsg = stringResource(R.string.import_resume_error_generic)

    fun startImport(uri: Uri, mimeType: String?) {
        step = CreateResumeStep.Importing
        viewModel.importResume(context, uri, mimeType) { result ->
            result.onSuccess { id ->
                Analytics.logEvent(Analytics.Event.RESUME_CREATED, mapOf(Analytics.Param.FEATURE to "import"))
                onCreated(id)
            }.onFailure { error ->
                val readFailure = error.message?.contains("read", ignoreCase = true) == true ||
                    error.message?.contains("understand", ignoreCase = true) == true
                step = CreateResumeStep.ImportError(if (readFailure) unreadableMsg else genericMsg)
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) startImport(uri, "image/*") else step = CreateResumeStep.ChooseSource
    }
    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) startImport(uri, context.contentResolver.getType(uri)) else step = CreateResumeStep.ChooseSource
    }

    // A single persistent AppBottomSheet/sheetState for the whole flow — switching `step` only
    // swaps the CONTENT inside it. Mounting a separate AppBottomSheet (each with its own
    // rememberModalBottomSheetState()) per step looked simpler but tears the sheet down and back
    // up on every step change, which M3's ModalBottomSheet doesn't handle as a no-op transition:
    // in practice it closed the sheet outright the moment the step changed, since the outgoing
    // instance's own dismiss cleanup fired before the incoming one could show.
    AppBottomSheet(onDismissRequest = onDismiss) {
        when (val current = step) {
            is CreateResumeStep.ChooseMode -> ChooseModeContent(
                onSelectNew = { step = CreateResumeStep.ManualForm },
                onSelectExisting = { step = CreateResumeStep.ChooseSource }
            )
            is CreateResumeStep.ManualForm -> CreateResumeFormContent(
                onCreate = { name, designation ->
                    viewModel.createResume(name, designation) { id ->
                        Analytics.logEvent(Analytics.Event.RESUME_CREATED)
                        onCreated(id)
                    }
                }
            )
            is CreateResumeStep.ChooseSource -> ChooseSourceContent(
                onBack = { step = CreateResumeStep.ChooseMode },
                onPickGallery = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                onPickFile = { fileLauncher.launch(arrayOf("application/pdf", "image/*")) }
            )
            is CreateResumeStep.Importing -> ImportingContent()
            is CreateResumeStep.ImportError -> ImportErrorContent(
                message = current.message,
                onRetry = { step = CreateResumeStep.ChooseSource }
            )
        }
    }
}

@Composable
private fun ChooseModeContent(onSelectNew: () -> Unit, onSelectExisting: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(stringResource(R.string.create_resume_choose_mode_heading), style = MaterialTheme.typography.titleLarge)
        ModeOptionRow(
            icon = Icons.Filled.Add,
            title = stringResource(R.string.create_resume_mode_new_title),
            description = stringResource(R.string.create_resume_mode_new_desc),
            onClick = onSelectNew,
            modifier = Modifier.padding(top = 20.dp)
        )
        ModeOptionRow(
            icon = Icons.Filled.UploadFile,
            title = stringResource(R.string.create_resume_mode_existing_title),
            description = stringResource(R.string.create_resume_mode_existing_desc),
            onClick = onSelectExisting,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun ChooseSourceContent(onBack: () -> Unit, onPickGallery: () -> Unit, onPickFile: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }
            Text(
                stringResource(R.string.import_resume_choose_source_heading),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        ModeOptionRow(
            icon = Icons.Filled.PhotoLibrary,
            title = stringResource(R.string.import_resume_source_gallery_title),
            description = stringResource(R.string.import_resume_source_gallery_desc),
            onClick = onPickGallery,
            modifier = Modifier.padding(top = 20.dp)
        )
        ModeOptionRow(
            icon = Icons.Filled.InsertDriveFile,
            title = stringResource(R.string.import_resume_source_file_title),
            description = stringResource(R.string.import_resume_source_file_desc),
            onClick = onPickFile,
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun ImportingContent() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            stringResource(R.string.import_resume_importing_message),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

@Composable
private fun ImportErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp)
        )
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp)
        )
        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.btn_try_again))
        }
    }
}

@Composable
private fun ModeOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CreateResumeFormContent(onCreate: (name: String, designation: String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }

    ImeLocaleHint()
    Column(modifier = Modifier.padding(24.dp)) {
        Text(stringResource(R.string.create_resume_heading), style = MaterialTheme.typography.titleLarge)

        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_full_name)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        AppTextField(
            value = designation,
            onValueChange = { designation = it },
            label = { Text(stringResource(R.string.label_designation)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )

        Button(
            onClick = { onCreate(name.trim(), designation.trim()) },
            enabled = name.isNotBlank() && designation.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(stringResource(R.string.btn_create))
        }

/*        AppCard(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(28.dp)
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        stringResource(R.string.home_did_you_know),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(R.string.home_did_you_know_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }*/
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateResumeSheetPreview() {
    ResumeBuilderTheme {
        AppBottomSheet(onDismissRequest = {}) {
            CreateResumeFormContent(onCreate = { _, _ -> })
        }
    }
}
