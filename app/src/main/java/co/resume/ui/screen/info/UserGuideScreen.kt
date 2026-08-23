package co.resume.ui.screen.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Transform
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.theme.ResumeBuilderTheme

private data class GuideStep(val title: String, val description: String)
private data class GuideSection(val title: String, val icon: ImageVector, val steps: List<GuideStep>)

/** One card per app feature (resume builder, cover letters, scanner, PDF tools, AI assistant)
 *  instead of a single flat numbered list — the guide used to only cover the resume builder,
 *  which was the only feature that existed when it was first written. */
@Composable
private fun guideSections(): List<GuideSection> = listOf(
    GuideSection(
        title = stringResource(R.string.guide_section_resume),
        icon = Icons.Filled.Description,
        steps = listOf(
            GuideStep(stringResource(R.string.guide_s1_title), stringResource(R.string.guide_s1_desc)),
            GuideStep(stringResource(R.string.guide_s2_title), stringResource(R.string.guide_s2_desc)),
            GuideStep(stringResource(R.string.guide_s3_title), stringResource(R.string.guide_s3_desc)),
            GuideStep(stringResource(R.string.guide_s4_title), stringResource(R.string.guide_s4_desc)),
            GuideStep(stringResource(R.string.guide_s5_title), stringResource(R.string.guide_s5_desc))
        )
    ),
    GuideSection(
        title = stringResource(R.string.guide_section_cover_letter),
        icon = Icons.AutoMirrored.Filled.Article,
        steps = listOf(
            GuideStep(stringResource(R.string.guide_cl_s1_title), stringResource(R.string.guide_cl_s1_desc)),
            GuideStep(stringResource(R.string.guide_cl_s2_title), stringResource(R.string.guide_cl_s2_desc)),
            GuideStep(stringResource(R.string.guide_cl_s3_title), stringResource(R.string.guide_cl_s3_desc))
        )
    ),
    GuideSection(
        title = stringResource(R.string.guide_section_scan),
        icon = Icons.Filled.DocumentScanner,
        steps = listOf(
            GuideStep(stringResource(R.string.guide_scan_s1_title), stringResource(R.string.guide_scan_s1_desc)),
            GuideStep(stringResource(R.string.guide_scan_s2_title), stringResource(R.string.guide_scan_s2_desc)),
            GuideStep(stringResource(R.string.guide_scan_s3_title), stringResource(R.string.guide_scan_s3_desc))
        )
    ),
    GuideSection(
        title = stringResource(R.string.guide_section_convert),
        icon = Icons.Filled.Transform,
        steps = listOf(
            GuideStep(stringResource(R.string.guide_convert_s1_title), stringResource(R.string.guide_convert_s1_desc)),
            GuideStep(stringResource(R.string.guide_convert_s2_title), stringResource(R.string.guide_convert_s2_desc))
        )
    ),
    GuideSection(
        title = stringResource(R.string.guide_section_ai),
        icon = Icons.Filled.AutoAwesome,
        steps = listOf(
            GuideStep(stringResource(R.string.guide_ai_s1_title), stringResource(R.string.guide_ai_s1_desc)),
            GuideStep(stringResource(R.string.guide_ai_s2_title), stringResource(R.string.guide_ai_s2_desc))
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserGuideScreen(onBack: () -> Unit) {
    val sections = guideSections()
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
        },
        bottomBar = { BannerAdView() }
    ) { padding ->
        val primary = MaterialTheme.colorScheme.primary
        val secondary = MaterialTheme.colorScheme.secondary
        val tertiary = MaterialTheme.colorScheme.tertiary
        val gradients = listOf(listOf(primary, tertiary), listOf(secondary, primary), listOf(tertiary, secondary))

        LazyColumn(
            contentPadding = PaddingValues(20.dp),
            modifier = Modifier.padding(padding).fillMaxWidth()
        ) {
            sections.forEach { section ->
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)) {
                        Icon(section.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            section.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                itemsIndexed(section.steps) { index, step ->
                    AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(gradients[index % gradients.size])),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${index + 1}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                Text(step.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
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
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UserGuideScreenPreview() {
    ResumeBuilderTheme { UserGuideScreen(onBack = {}) }
}
