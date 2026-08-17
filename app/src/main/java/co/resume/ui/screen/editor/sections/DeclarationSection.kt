package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.ui.component.AiActionButton
import co.resume.ui.component.AppTextField
import co.resume.ui.component.RichTextField
import co.resume.ui.theme.ResumeBuilderTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclarationSection(
    initialDeclaration: String,
    initialPlace: String,
    initialDate: String,
    onBack: () -> Unit,
    onSave: (declaration: String, place: String, date: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.msg_details_saved)
    val aiFailedMsg = stringResource(R.string.work_msg_ai_failed)
    var declaration by remember { mutableStateOf(initialDeclaration) }
    var place by remember { mutableStateOf(initialPlace) }
    var date by remember { mutableStateOf(initialDate.ifBlank { displayFormat.format(Date()) }) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(initialDeclaration, initialPlace, initialDate) {
        declaration = initialDeclaration
        place = initialPlace
        if (initialDate.isNotBlank()) date = initialDate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        RichTextField(
            value = declaration,
            onValueChange = { declaration = it },
            label = { Text(stringResource(R.string.decl_label_declaration)) },
            placeholder = { Text(stringResource(R.string.decl_placeholder)) },
            minLines = 4,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            trailingIcon = if (AiClient.isConfigured) {
                {
                    AiActionButton(
                        isGenerating = isGenerating,
                        contentDescription = stringResource(R.string.proj_cd_generate_ai),
                        onClick = {
                            isGenerating = true
                            scope.launch {
                                try {
                                    val result = ResumeAiService.generateDeclaration(declaration)
                                    if (result.isNotBlank()) declaration = result
                                } catch (e: Exception) {
                                    Toast.makeText(context, co.resume.ai.aiErrorMessage(e, aiFailedMsg), Toast.LENGTH_SHORT).show()
                                } finally {
                                    isGenerating = false
                                }
                            }
                        }
                    )
                }
            } else null
        )
        AppTextField(
            value = place, onValueChange = { place = it }, label = { Text(stringResource(R.string.decl_label_place)) },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        AppTextField(
            value = date,
            onValueChange = {},
            label = { Text(stringResource(R.string.decl_label_date)) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text(stringResource(R.string.btn_change)) } }
        )
        val isDirty = declaration != initialDeclaration || place != initialPlace || date != initialDate
        val isValid = declaration.isNotBlank() && place.isNotBlank() && date.isNotBlank()

        Button(
            onClick = {
                onSave(declaration, place, date)
                Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                onBack()
            },
            enabled = isDirty && isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_save))
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { date = displayFormat.format(Date(it)) }
                    showDatePicker = false
                }) { Text(stringResource(R.string.btn_ok)) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_cancel)) } }
        ) {
            DatePicker(state = state)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DeclarationSectionPreview() {
    ResumeBuilderTheme {
        DeclarationSection(
            initialDeclaration = "I hereby declare that the information provided above is true to the best of my knowledge.",
            initialPlace = "San Francisco",
            initialDate = "01/01/2026",
            onBack = {},
            onSave = { _, _, _ -> }
        )
    }
}
