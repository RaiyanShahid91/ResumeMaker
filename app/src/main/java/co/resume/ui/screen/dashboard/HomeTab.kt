package co.resume.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.ListRowSkeleton
import co.resume.ui.component.ResumeTemplateThumbnail
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.component.TemplateThumbnailSkeleton
import co.resume.ui.component.rememberShimmerGate
import co.resume.ui.viewmodel.TemplateBrowseViewModel
import co.resume.ui.viewmodel.TemplateUiModel

private data class HomeAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

private data class ResumeTip(val icon: ImageVector, val title: String, val body: String)

@Composable
fun HomeTab(
    onCreateResume: () -> Unit,
    onViewResumes: () -> Unit,
    onBrowseTemplates: () -> Unit,
    onOpenAiChat: () -> Unit = {},
    viewModel: TemplateBrowseViewModel = hiltViewModel()
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val templatesFetching by viewModel.isLoading.collectAsStateWithLifecycle()
    val shimmerGate = rememberShimmerGate()
    val isLoading = templatesFetching || shimmerGate

    val actions = listOf(
        HomeAction(Icons.Filled.AddCircle,   stringResource(R.string.home_action_create),    stringResource(R.string.home_action_create_desc),    onCreateResume),
        HomeAction(Icons.Filled.Description, stringResource(R.string.home_action_resumes),   stringResource(R.string.home_action_resumes_desc),   onViewResumes),
        HomeAction(Icons.Filled.GridView,    stringResource(R.string.home_action_templates), stringResource(R.string.home_action_templates_desc), onBrowseTemplates),
        HomeAction(Icons.Filled.AutoAwesome, stringResource(R.string.home_action_ai_chat),   stringResource(R.string.home_action_ai_chat_desc),   onOpenAiChat)
    )

    val tips = listOf(
        ResumeTip(Icons.Filled.CheckCircle,       stringResource(R.string.tip_one_page_title), stringResource(R.string.tip_one_page_body)),
        ResumeTip(Icons.Filled.TrendingUp,        stringResource(R.string.tip_quantify_title), stringResource(R.string.tip_quantify_body)),
        ResumeTip(Icons.Filled.Edit,              stringResource(R.string.tip_tailor_title),   stringResource(R.string.tip_tailor_body)),
        ResumeTip(Icons.Filled.FormatListBulleted, stringResource(R.string.tip_verbs_title),   stringResource(R.string.tip_verbs_body)),
        ResumeTip(Icons.Filled.Star,              stringResource(R.string.tip_skills_title),   stringResource(R.string.tip_skills_body))
    )

    // No background fill here — the single app-wide gradient (painted once behind the nav
    // host in MainActivity) shows through this whole screen, glass cards included.
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {

            // ── Hero header ─────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        stringResource(R.string.home_welcome),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            // ── Featured templates carousel ──────────────────────────────────
            if (isLoading || templates.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_featured_templates),
                        actionLabel = stringResource(R.string.home_see_all),
                        onAction = onBrowseTemplates
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isLoading) {
                            items(4) { TemplateThumbnailSkeleton() }
                        } else {
                            items(templates.take(8)) { template ->
                                TemplateThumbnailCard(template = template, onClick = onBrowseTemplates)
                            }
                        }
                    }
                }
            }

            // ── Quick actions ────────────────────────────────────────────────
            item { SectionHeader(title = stringResource(R.string.home_quick_actions)) }

            itemsIndexed(actions) { index, action ->
                StaggeredEntrance(index = index) {
                AppCard(
                    onClick = if (shimmerGate) null else action.onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                ) {
                    if (shimmerGate) {
                        ListRowSkeleton()
                    } else {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                action.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                            Text(action.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                action.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    }
                }
                }
            }

            // ── Resume tips ──────────────────────────────────────────────────
            item { SectionHeader(title = stringResource(R.string.home_tips_title)) }

            itemsIndexed(tips) { index, tip ->
                StaggeredEntrance(index = index) {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                ) {
                    if (shimmerGate) {
                        ListRowSkeleton(iconSize = 20.dp)
                    } else {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            tip.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(tip.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                tip.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                    }
                }
                }
            }

            // ── AI features banner ───────────────────────────────────────────
            item {
                AppCard(
                    onClick = if (shimmerGate) null else onOpenAiChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 14.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    restingElevation = 2.dp
                ) {
                    if (shimmerGate) {
                        ListRowSkeleton()
                    } else {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                            Text(
                                stringResource(R.string.home_ai_banner_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                stringResource(R.string.home_ai_banner_body),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    }
                }
            }

            // ── Banner ad ────────────────────────────────────────────────────
            item { BannerAdView(modifier = Modifier.padding(top = 8.dp)) }

            // ── Insight / motivational card ──────────────────────────────────
            item {
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 14.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    if (shimmerGate) {
                        ListRowSkeleton(iconSize = 28.dp)
                    } else {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                stringResource(R.string.home_did_you_know),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                stringResource(R.string.home_did_you_know_body),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 22.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionLabel, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun TemplateThumbnailCard(template: TemplateUiModel, onClick: () -> Unit) {
    AppCard(
        onClick = onClick,
        modifier = Modifier.width(130.dp),
        restingElevation = 2.dp
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.7f)) {
                ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())
            }
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(template.option.title, style = MaterialTheme.typography.labelMedium, maxLines = 1)
                Text(
                    stringResource(template.option.category.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
