package co.resume.ui.screen.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import co.resumeai.R

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.domain.export.SampleResumeData
import co.resume.domain.score.AtsScoreCalculator
import co.resume.ui.component.AiAssistantFab
import co.resume.ui.component.AiAssistantPopup
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.GradientProgressBar
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.theme.SuccessGreen
import co.resume.ui.viewmodel.ResumeEditorViewModel

private val hubSections = listOf(
    EditorSectionType.PERSONAL,
    EditorSectionType.PHOTO,
    EditorSectionType.OBJECTIVE,
    EditorSectionType.EDUCATION,
    EditorSectionType.WORK_EXPERIENCE,
    EditorSectionType.SKILLS,
    EditorSectionType.PROJECTS,
    EditorSectionType.ACHIEVEMENTS,
    EditorSectionType.LANGUAGES,
    EditorSectionType.DECLARATION
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeEditorScreen(
    resumeId: Long,
    onBack: () -> Unit,
    onOpenSection: (sectionKey: String) -> Unit,
    onOpenTemplatePicker: () -> Unit,
    onOpenPreview: () -> Unit,
    viewModel: ResumeEditorViewModel = hiltViewModel()
) {
    val details by viewModel.resume.collectAsStateWithLifecycle()
    ResumeEditorContent(
        resumeId = resumeId,
        details = details,
        onBack = onBack,
        onOpenSection = onOpenSection,
        onOpenTemplatePicker = onOpenTemplatePicker,
        onOpenPreview = onOpenPreview
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResumeEditorContent(
    resumeId: Long,
    details: ResumeWithDetails?,
    onBack: () -> Unit,
    onOpenSection: (sectionKey: String) -> Unit,
    onOpenTemplatePicker: () -> Unit,
    onOpenPreview: () -> Unit
) {
    var showAiAssistant by remember { mutableStateOf(false) }
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = { AiAssistantFab(onClick = { showAiAssistant = true }) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(details?.resume?.name?.ifBlank { stringResource(R.string.editor_default_title) } ?: stringResource(R.string.editor_default_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_change_template)) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = onOpenTemplatePicker) {
                            Icon(Icons.AutoMirrored.Filled.Article, contentDescription = stringResource(R.string.tooltip_change_template))
                        }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_preview)) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = onOpenPreview) {
                            Icon(Icons.Filled.Visibility, contentDescription = stringResource(R.string.tooltip_preview))
                        }
                    }
                }
            )
        },
        bottomBar = { BannerAdView() }
    ) { padding ->
        val resumeDetails = details
        Surface(modifier = Modifier.fillMaxSize().padding(padding), color = Color.Transparent) {
            if (resumeDetails == null) {
                Box(modifier = Modifier.fillMaxSize())
            } else {
                val sectionCompletion = AtsScoreCalculator.sectionCompletion(resumeDetails)
                val score = AtsScoreCalculator.score(resumeDetails)
                val missing = AtsScoreCalculator.missingSections(resumeDetails)
                LazyColumn(contentPadding = PaddingValues(20.dp)) {

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                val photoPath = resumeDetails.resume.profilePhotoPath
                                if (photoPath.isNullOrBlank()) {
                                    Icon(Icons.Filled.Person, contentDescription = null)
                                } else {
                                    AsyncImage(
                                        model = photoPath,
                                        contentDescription = stringResource(R.string.editor_cd_profile_photo),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    resumeDetails.resume.name.ifBlank { stringResource(R.string.editor_untitled) },
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    resumeDetails.resume.designation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(R.string.editor_ats_score_title), style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        stringResource(R.string.editor_ats_score_percent, score),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                GradientProgressBar(
                                    progress = score / 100f,
                                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                                )
                                if (missing.isNotEmpty()) {
                                    val missingTitles = missing.take(3).map { stringResource(it.titleRes) }
                                    Text(
                                        stringResource(R.string.editor_ats_score_missing, missingTitles.joinToString(", ")),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }
                        }
                    }

                    itemsIndexed(hubSections) { index, type ->
                        val complete = sectionCompletion[type] == true
                        StaggeredEntrance(index = index) {
                            AppCard(
                                onClick = { onOpenSection(type.key) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (complete) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (complete) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(end = 16.dp)
                                        )
                                        Text(stringResource(type.titleRes), style = MaterialTheme.typography.titleMedium)
                                    }
                                    Icon(Icons.Filled.ChevronRight, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAiAssistant) {
        AiAssistantPopup(resumeId = resumeId, onDismiss = { showAiAssistant = false })
    }
}

@Preview(showBackground = true)
@Composable
private fun ResumeEditorScreenPreview() {
    ResumeBuilderTheme {
        ResumeEditorContent(
            resumeId = 1L,
            details = SampleResumeData.details,
            onBack = {},
            onOpenSection = {},
            onOpenTemplatePicker = {},
            onOpenPreview = {}
        )
    }
}
