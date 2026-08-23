package co.resume.ui.screen.info

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.theme.ResumeBuilderTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context: Context = LocalContext.current
    val versionName = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrDefault("—")

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.about_title)) },
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

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // ── App header ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(primary, tertiary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        stringResource(R.string.about_app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        stringResource(R.string.about_version, versionName ?: ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        stringResource(R.string.about_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // ── What is this app ────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_what_title)) {
                    Text(
                        stringResource(R.string.about_what_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Core features ───────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_features_title)) {
                    val features = listOf(
                        FeatureItem(Icons.Filled.Person,      stringResource(R.string.about_feat_personal_title),    stringResource(R.string.about_feat_personal_desc)),
                        FeatureItem(Icons.Filled.Psychology,  stringResource(R.string.about_feat_objective_title),   stringResource(R.string.about_feat_objective_desc)),
                        FeatureItem(Icons.Filled.School,      stringResource(R.string.about_feat_education_title),   stringResource(R.string.about_feat_education_desc)),
                        FeatureItem(Icons.Filled.Work,        stringResource(R.string.about_feat_work_title),        stringResource(R.string.about_feat_work_desc)),
                        FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_feat_skills_title),      stringResource(R.string.about_feat_skills_desc)),
                        FeatureItem(Icons.Filled.Description, stringResource(R.string.about_feat_projects_title),    stringResource(R.string.about_feat_projects_desc)),
                        FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_feat_achievements_title), stringResource(R.string.about_feat_achievements_desc)),
                        FeatureItem(Icons.Filled.Language,    stringResource(R.string.about_feat_languages_title),   stringResource(R.string.about_feat_languages_desc)),
                        FeatureItem(Icons.Filled.Person,      stringResource(R.string.about_feat_photo_title),       stringResource(R.string.about_feat_photo_desc)),
                    )
                    val gradients = listOf(listOf(primary, tertiary), listOf(secondary, primary), listOf(tertiary, secondary))
                    features.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── Cover letters ───────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_coverletter_title), icon = Icons.AutoMirrored.Filled.Article) {
                    val items = listOf(
                        FeatureItem(Icons.Filled.AutoAwesome, stringResource(R.string.about_coverletter_create_title), stringResource(R.string.about_coverletter_create_desc)),
                        FeatureItem(Icons.AutoMirrored.Filled.Article, stringResource(R.string.about_coverletter_templates_title), stringResource(R.string.about_coverletter_templates_desc)),
                    )
                    val gradients = listOf(listOf(tertiary, secondary), listOf(primary, tertiary))
                    items.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── Document scanner ────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_scan_title), icon = Icons.Filled.DocumentScanner) {
                    val items = listOf(
                        FeatureItem(Icons.Filled.DocumentScanner, stringResource(R.string.about_scan_capture_title), stringResource(R.string.about_scan_capture_desc)),
                        FeatureItem(Icons.Filled.TextSnippet,     stringResource(R.string.about_scan_ocr_title),     stringResource(R.string.about_scan_ocr_desc)),
                    )
                    val gradients = listOf(listOf(secondary, tertiary), listOf(tertiary, primary))
                    items.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── PDF tools ────────────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_convert_title), icon = Icons.Filled.MergeType) {
                    val items = listOf(
                        FeatureItem(Icons.Filled.Image,      stringResource(R.string.about_convert_image_title),    stringResource(R.string.about_convert_image_desc)),
                        FeatureItem(Icons.Filled.MergeType,  stringResource(R.string.about_convert_merge_title),    stringResource(R.string.about_convert_merge_desc)),
                        FeatureItem(Icons.Filled.Compress,   stringResource(R.string.about_convert_compress_title), stringResource(R.string.about_convert_compress_desc)),
                        FeatureItem(Icons.Filled.Image,      stringResource(R.string.about_convert_topdf_title),    stringResource(R.string.about_convert_topdf_desc)),
                    )
                    val gradients = listOf(listOf(primary, secondary), listOf(secondary, tertiary), listOf(tertiary, primary), listOf(primary, tertiary))
                    items.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── Templates & Design ──────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_templates_title)) {
                    val items = listOf(
                        FeatureItem(Icons.Filled.GridView,   stringResource(R.string.about_templates13_title), stringResource(R.string.about_templates13_desc)),
                        FeatureItem(Icons.Filled.ColorLens,  stringResource(R.string.about_accent_title),      stringResource(R.string.about_accent_desc)),
                        FeatureItem(Icons.Filled.Download,   stringResource(R.string.about_pdf_title),         stringResource(R.string.about_pdf_desc)),
                    )
                    val gradients = listOf(listOf(secondary, tertiary), listOf(primary, secondary), listOf(tertiary, primary))
                    items.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── AI features ─────────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_ai_title), icon = Icons.Filled.AutoAwesome) {
                    Text(
                        stringResource(R.string.about_ai_intro),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    val aiFeatures = listOf(
                        FeatureItem(Icons.Filled.AutoAwesome, stringResource(R.string.about_ai_objective_title),   stringResource(R.string.about_ai_objective_desc)),
                        FeatureItem(Icons.Filled.Work,        stringResource(R.string.about_ai_job_title),         stringResource(R.string.about_ai_job_desc)),
                        FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_ai_skills_title),      stringResource(R.string.about_ai_skills_desc)),
                        FeatureItem(Icons.Filled.Description, stringResource(R.string.about_ai_projects_title),    stringResource(R.string.about_ai_projects_desc)),
                        FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_ai_achievements_title), stringResource(R.string.about_ai_achievements_desc)),
                        FeatureItem(Icons.Filled.Chat,        stringResource(R.string.about_ai_chat_title),        stringResource(R.string.about_ai_chat_desc)),
                    )
                    val gradients = listOf(listOf(primary, tertiary), listOf(secondary, primary), listOf(tertiary, secondary))
                    aiFeatures.forEachIndexed { index, item -> FeatureRow(item, gradients[index % gradients.size]) }
                }
            }

            // ── Technology ──────────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_tech_title)) {
                    val tech = listOf(
                        stringResource(R.string.about_tech_built_label) to stringResource(R.string.about_tech_built_value),
                        stringResource(R.string.about_tech_ui_label) to stringResource(R.string.about_tech_ui_value),
                        stringResource(R.string.about_tech_storage_label) to stringResource(R.string.about_tech_storage_value),
                        stringResource(R.string.about_tech_model_label) to stringResource(R.string.about_tech_model_value),
                        stringResource(R.string.about_tech_provider_label) to stringResource(R.string.about_tech_provider_value),
                        stringResource(R.string.about_tech_pdf_label) to stringResource(R.string.about_tech_pdf_value),
                    )
                    tech.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(110.dp)
                            )
                            Text(
                                value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Privacy ─────────────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_privacy_title), icon = Icons.Filled.Lock) {
                    Text(
                        stringResource(R.string.about_privacy_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Contact ─────────────────────────────────────────────────────
            item {
                AboutSectionCard(title = stringResource(R.string.about_contact_title)) {
                    Text(
                        stringResource(R.string.about_contact_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    ResumeBuilderTheme { AboutScreen(onBack = {}) }
}

private data class FeatureItem(val icon: ImageVector, val title: String, val description: String)

/** A titled card grouping related About content, matching the Settings screen's card language. */
@Composable
private fun AboutSectionCard(
    title: String,
    icon: ImageVector? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun FeatureRow(item: FeatureItem, gradient: List<androidx.compose.ui.graphics.Color>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(
                item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
