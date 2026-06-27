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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import co.resumeai.R

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.ui.component.BannerAdView
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
    EditorSectionType.INTERESTS,
    EditorSectionType.HOBBIES,
    EditorSectionType.DECLARATION,
    EditorSectionType.SIGNATURE
)

private fun isSectionComplete(type: EditorSectionType, details: ResumeWithDetails): Boolean = when (type) {
    EditorSectionType.PERSONAL -> details.resume.name.isNotBlank() && details.resume.email.isNotBlank() &&
        details.resume.phone.isNotBlank() && details.resume.address.isNotBlank()
    EditorSectionType.PHOTO -> !details.resume.profilePhotoPath.isNullOrBlank()
    EditorSectionType.OBJECTIVE -> details.resume.objective.isNotBlank()
    EditorSectionType.EDUCATION -> details.education.isNotEmpty()
    EditorSectionType.WORK_EXPERIENCE -> details.workExperience.isNotEmpty()
    EditorSectionType.SKILLS -> details.skills.isNotEmpty()
    EditorSectionType.PROJECTS -> details.projects.isNotEmpty()
    EditorSectionType.ACHIEVEMENTS -> details.achievements.isNotEmpty()
    EditorSectionType.LANGUAGES -> details.languages.isNotEmpty()
    EditorSectionType.INTERESTS -> details.interests.isNotEmpty()
    EditorSectionType.HOBBIES -> details.hobbies.isNotEmpty()
    EditorSectionType.DECLARATION -> details.resume.declaration.isNotBlank()
    EditorSectionType.SIGNATURE -> !details.resume.signaturePath.isNullOrBlank()
}

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
    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.resume?.name?.ifBlank { "Resume" } ?: "Resume") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_accent_color)) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = { showColorPicker = true }) {
                            Icon(Icons.Filled.FormatColorFill, contentDescription = stringResource(R.string.tooltip_accent_color))
                        }
                    }
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
        Surface(modifier = Modifier.fillMaxSize().padding(padding), color = MaterialTheme.colorScheme.background) {
            if (resumeDetails == null) {
                Box(modifier = Modifier.fillMaxSize())
            } else {
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
                                        contentDescription = "Profile photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    resumeDetails.resume.name.ifBlank { "Untitled resume" },
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

                    items(hubSections) { type ->
                        val complete = isSectionComplete(type, resumeDetails)
                        Card(
                            onClick = { onOpenSection(type.key) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                        tint = if (complete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
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

    if (showColorPicker) {
        ColorPickerSheet(
            currentHex = details?.resume?.accentColorHex,
            onColorSelected = { hex -> viewModel.setColors(hex) },
            onDismiss = { showColorPicker = false }
        )
    }
}
