package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.ui.component.AppTextField
import kotlinx.coroutines.launch

@Composable
fun ObjectiveSection(
    initialObjective: String,
    designation: String = "",
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val enterObjectiveMsg = stringResource(R.string.obj_msg_enter)
    val savedMsg = stringResource(R.string.msg_details_saved)
    val generateAiLabel = stringResource(R.string.obj_btn_generate_ai)
    val improveAiLabel = stringResource(R.string.obj_btn_improve_ai)
    val suggestedObjectives = listOf(
        stringResource(R.string.obj_suggestion_1),
        stringResource(R.string.obj_suggestion_2),
        stringResource(R.string.obj_suggestion_3),
        stringResource(R.string.obj_suggestion_4),
        stringResource(R.string.obj_suggestion_5)
    )
    var objective by remember { mutableStateOf(initialObjective) }
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(initialObjective) { objective = initialObjective }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        AppTextField(
            value = objective,
            onValueChange = { objective = it },
            label = { Text(stringResource(R.string.obj_label_objective)) },
            placeholder = { Text(stringResource(R.string.obj_placeholder)) },
            minLines = 4,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        if (AiClient.isConfigured) {
            OutlinedButton(
                onClick = {
                    isGenerating = true
                    scope.launch {
                        try {
                            val result = ResumeAiService.generateObjective(
                                designation = designation,
                                existingObjective = objective
                            )
                            if (result.isNotBlank()) objective = result
                        } catch (e: Exception) {
                            android.util.Log.e("AI", "generateObjective failed", e)
                            Toast.makeText(context, context.getString(R.string.obj_ai_error, e.message?.take(120)), Toast.LENGTH_LONG).show()
                        } finally {
                            isGenerating = false
                        }
                    }
                },
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.obj_btn_generating))
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (objective.isBlank()) generateAiLabel else improveAiLabel)
                }
            }
        }

        Text(stringResource(R.string.obj_suggested_title), style = MaterialTheme.typography.titleMedium)
        suggestedObjectives.forEach { suggestion ->
            Surface(
                onClick = { objective = suggestion },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(suggestion, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
            }
        }

        val isDirty = objective != initialObjective
        val isValid = objective.isNotBlank()

        Button(
            onClick = {
                onSave(objective)
                Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                onBack()
            },
            enabled = isDirty && isValid,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(stringResource(R.string.btn_save))
        }
    }
}
