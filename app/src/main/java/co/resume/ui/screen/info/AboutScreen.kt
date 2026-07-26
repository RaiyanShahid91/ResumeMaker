package co.resume.ui.screen.info

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.resumeai.R

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
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── App header ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
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
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // ── What is this app ────────────────────────────────────────────
            item {
                AboutSection(title = stringResource(R.string.about_what_title)) {
                    Text(
                        stringResource(R.string.about_what_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Core features ───────────────────────────────────────────────
            item {
                AboutSection(title = stringResource(R.string.about_features_title)) {
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
                    features.forEach { FeatureRow(it) }
                }
            }

            // ── Templates & Design ──────────────────────────────────────────
            item {
                AboutSection(title = stringResource(R.string.about_templates_title)) {
                    val items = listOf(
                        FeatureItem(Icons.Filled.GridView,   stringResource(R.string.about_templates13_title), stringResource(R.string.about_templates13_desc)),
                        FeatureItem(Icons.Filled.ColorLens,  stringResource(R.string.about_accent_title),      stringResource(R.string.about_accent_desc)),
                        FeatureItem(Icons.Filled.Download,   stringResource(R.string.about_pdf_title),         stringResource(R.string.about_pdf_desc)),
                    )
                    items.forEach { FeatureRow(it) }
                }
            }

            // ── AI features ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.about_ai_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            stringResource(R.string.about_ai_intro),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(12.dp))
                        val aiFeatures = listOf(
                            FeatureItem(Icons.Filled.AutoAwesome, stringResource(R.string.about_ai_objective_title),   stringResource(R.string.about_ai_objective_desc)),
                            FeatureItem(Icons.Filled.Work,        stringResource(R.string.about_ai_job_title),         stringResource(R.string.about_ai_job_desc)),
                            FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_ai_skills_title),      stringResource(R.string.about_ai_skills_desc)),
                            FeatureItem(Icons.Filled.Description, stringResource(R.string.about_ai_projects_title),    stringResource(R.string.about_ai_projects_desc)),
                            FeatureItem(Icons.Filled.Star,        stringResource(R.string.about_ai_achievements_title), stringResource(R.string.about_ai_achievements_desc)),
                            FeatureItem(Icons.Filled.Chat,        stringResource(R.string.about_ai_chat_title),        stringResource(R.string.about_ai_chat_desc)),
                        )
                        aiFeatures.forEach { item ->
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        item.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Technology ──────────────────────────────────────────────────
            item {
                AboutSection(title = stringResource(R.string.about_tech_title)) {
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
                AboutSection(title = stringResource(R.string.about_privacy_title)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.about_privacy_body),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Contact ─────────────────────────────────────────────────────
            item {
                AboutSection(title = stringResource(R.string.about_contact_title)) {
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

private data class FeatureItem(val icon: ImageVector, val title: String, val description: String)

@Composable
private fun AboutSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun FeatureRow(item: FeatureItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(6.dp)
            )
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
