package co.resume.ui.screen.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R

private data class PolicySection(val heading: String, val body: String)

@Composable
private fun policySections(): List<PolicySection> = listOf(
    PolicySection(stringResource(R.string.privacy_s1_heading), stringResource(R.string.privacy_s1_body)),
    PolicySection(stringResource(R.string.privacy_s2_heading), stringResource(R.string.privacy_s2_body)),
    PolicySection(stringResource(R.string.privacy_s3_heading), stringResource(R.string.privacy_s3_body)),
    PolicySection(stringResource(R.string.privacy_s4_heading), stringResource(R.string.privacy_s4_body)),
    PolicySection(stringResource(R.string.privacy_s5_heading), stringResource(R.string.privacy_s5_body))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val sections = policySections()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.settings_privacy)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
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
