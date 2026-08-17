package co.resume.ui.screen.info

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

private data class GuideStep(val title: String, val description: String)

@Composable
private fun guideSteps(): List<GuideStep> = listOf(
    GuideStep(stringResource(R.string.guide_s1_title), stringResource(R.string.guide_s1_desc)),
    GuideStep(stringResource(R.string.guide_s2_title), stringResource(R.string.guide_s2_desc)),
    GuideStep(stringResource(R.string.guide_s3_title), stringResource(R.string.guide_s3_desc)),
    GuideStep(stringResource(R.string.guide_s4_title), stringResource(R.string.guide_s4_desc)),
    GuideStep(stringResource(R.string.guide_s5_title), stringResource(R.string.guide_s5_desc))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(onBack: () -> Unit) {
    val steps = guideSteps()
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.settings_user_guide)) },
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
            itemsIndexed(steps) { index, step ->
                AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(gradients[index % gradients.size])),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            Text(step.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
}

@Preview(showBackground = true)
@Composable
private fun UserGuideScreenPreview() {
    ResumeBuilderTheme { UserGuideScreen(onBack = {}) }
}
