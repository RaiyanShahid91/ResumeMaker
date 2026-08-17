package co.resume.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AppCard
import co.resume.ui.theme.ResumeBuilderTheme

private data class TermsSection(val heading: String, val body: String)

@Composable
private fun termsSections(): List<TermsSection> = listOf(
    TermsSection(stringResource(R.string.terms_s1_heading), stringResource(R.string.terms_s1_body)),
    TermsSection(stringResource(R.string.terms_s_login_heading), stringResource(R.string.terms_s_login_body)),
    TermsSection(stringResource(R.string.terms_s2_heading), stringResource(R.string.terms_s2_body)),
    TermsSection(stringResource(R.string.terms_s_perm_heading), stringResource(R.string.terms_s_perm_body)),
    TermsSection(stringResource(R.string.terms_s3_heading), stringResource(R.string.terms_s3_body)),
    TermsSection(stringResource(R.string.terms_s4_heading), stringResource(R.string.terms_s4_body)),
    TermsSection(stringResource(R.string.terms_s5_heading), stringResource(R.string.terms_s5_body)),
    TermsSection(stringResource(R.string.terms_s6_heading), stringResource(R.string.terms_s6_body)),
    TermsSection(stringResource(R.string.terms_s7_heading), stringResource(R.string.terms_s7_body)),
    TermsSection(stringResource(R.string.terms_s8_heading), stringResource(R.string.terms_s8_body))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBack: () -> Unit) {
    val sections = termsSections()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.auth_terms_link)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        val primary = MaterialTheme.colorScheme.primary
        val secondary = MaterialTheme.colorScheme.secondary
        val tertiary = MaterialTheme.colorScheme.tertiary
        val gradients = listOf(listOf(primary, tertiary), listOf(secondary, primary), listOf(tertiary, secondary))

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            modifier = Modifier.padding(padding).fillMaxWidth()
        ) {
            itemsIndexed(sections) { index, section ->
                AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(gradients[index % gradients.size])),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                section.heading,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Text(
                            section.body,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsScreenPreview() {
    ResumeBuilderTheme { TermsScreen(onBack = {}) }
}
