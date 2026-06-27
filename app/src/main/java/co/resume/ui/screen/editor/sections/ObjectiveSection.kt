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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import kotlinx.coroutines.launch

@Composable
fun ObjectiveSection(
    initialObjective: String,
    designation: String = "",
    onSave: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var objective by remember { mutableStateOf(initialObjective) }
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(initialObjective) { objective = initialObjective }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = objective,
            onValueChange = { objective = it },
            label = { Text("Resume Objective") },
            placeholder = { Text("I am seeking employment with a company where I can grow professionally and personally") },
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
                            Toast.makeText(context, "AI error: ${e.message?.take(120)}", Toast.LENGTH_LONG).show()
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
                    Text("Generating…")
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (objective.isBlank()) "Generate with AI" else "Improve with AI")
                }
            }
        }

        Text("Suggested objectives", style = MaterialTheme.typography.titleMedium)
        suggestedObjectives.forEach { suggestion ->
            Surface(
                onClick = { objective = suggestion },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(suggestion, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(12.dp))
            }
        }

        Button(
            onClick = {
                if (objective.isBlank()) {
                    Toast.makeText(context, "Please enter the objective.", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(objective)
                    Toast.makeText(context, "Details saved successfully.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text("Save")
        }
    }
}

private val suggestedObjectives = listOf(
    "Data-driven professional skilled in Python, SQL, and data visualization tools, seeking to leverage analytical expertise to drive business insights.",
    "Results-driven project manager with 5 years of experience in agile software development, seeking to lead high-performing teams toward delivering impactful products.",
    "Creative and detail-oriented UI/UX designer passionate about crafting intuitive, user-centered digital experiences.",
    "Motivated computer science graduate with a strong foundation in full stack development, eager to contribute to innovative engineering teams.",
    "Seeking a software developer position to leverage my skills in Java, Kotlin, and Android development to build high-quality mobile applications."
)
