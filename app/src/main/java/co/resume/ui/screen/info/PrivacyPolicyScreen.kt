package co.resume.ui.screen.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class PolicySection(val heading: String, val body: String)

private val sections = listOf(
    PolicySection(
        "No account, no cloud",
        "ResumeAI works entirely on your device. There is no sign-in, no account, and no " +
            "server that your resumes are sent to."
    ),
    PolicySection(
        "What's stored, and where",
        "Everything you enter — personal details, education, work experience, skills, " +
            "projects, photos, and your signature — is saved only in this app's private " +
            "storage on your device, never uploaded anywhere."
    ),
    PolicySection(
        "Nothing leaves your device",
        "We don't collect, transmit, sell, or share your resume data with anyone. There is no " +
            "analytics or tracking of what you type."
    ),
    PolicySection(
        "Permissions we ask for",
        "Camera and photo access are used only so you can add a profile photo or signature " +
            "image, and that image is stored locally like the rest of your resume."
    ),
    PolicySection(
        "Deleting your data",
        "Deleting a resume in the app removes it and its photos from your device. Uninstalling " +
            "the app removes everything, since nothing is kept anywhere else."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = PaddingValues(20.dp), modifier = Modifier.padding(padding)) {
            items(sections) { section ->
                Text(
                    section.heading,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
                Text(section.body, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
