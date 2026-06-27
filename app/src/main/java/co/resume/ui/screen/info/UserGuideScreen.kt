package co.resume.ui.screen.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class GuideStep(val title: String, val description: String)

private val steps = listOf(
    GuideStep("Create a resume", "From the Home or My Resume tab, tap \"Create Resume\" and enter a name and designation."),
    GuideStep("Fill in your details", "Open the resume and work through each section — Personal Details, Objective, Education, Work Experience, Skills, and more."),
    GuideStep("Add a photo or signature", "Use the Profile Photo and Signature sections to capture or pick an image."),
    GuideStep("Choose a template", "Tap the palette icon to pick from several professional designs — switching templates never loses your entered data."),
    GuideStep("Preview and download", "Tap the eye icon to see exactly how your resume will look, then tap the print icon to save it as a PDF or print it.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Guide") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(20.dp), modifier = Modifier.padding(padding)) {
            itemsIndexed(steps) { index, step ->
                Row(modifier = Modifier.padding(bottom = 20.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(step.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            step.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
