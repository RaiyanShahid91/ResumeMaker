package co.resume.ui.screen.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.domain.export.CoverLetterHtmlRenderer
import co.resume.domain.export.SampleCoverLetterData
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.CoverLetterPagePreview
import co.resume.ui.component.OneTimeCoachMark
import co.resume.ui.component.ResumeTemplateThumbnail
import co.resume.ui.screen.editor.InlineColorSwatchRow
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.CoverLetterBrowseViewModel
import co.resume.ui.viewmodel.CoverLetterListViewModel
import co.resume.ui.viewmodel.CoverLetterTemplateUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterTemplateBrowseScreen(
    onBack: () -> Unit,
    onOpenEditor: (coverLetterId: Long) -> Unit,
    browseViewModel: CoverLetterBrowseViewModel = hiltViewModel(),
    listViewModel: CoverLetterListViewModel = hiltViewModel()
) {
    val templates by browseViewModel.templates.collectAsStateWithLifecycle()
    val isLoading by browseViewModel.isLoading.collectAsStateWithLifecycle()

    CoverLetterTemplateBrowseContent(
        templates = templates,
        isLoading = isLoading,
        onBack = onBack,
        onCreateWithTemplate = { templateId, accentColorHex, onCreated ->
            listViewModel.createCoverLetter(templateId = templateId, accentColorHex = accentColorHex, onCreated = onCreated)
        },
        onOpenEditor = onOpenEditor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterTemplateBrowseContent(
    templates: List<CoverLetterTemplateUiModel>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onCreateWithTemplate: (templateId: Int, accentColorHex: String?, onCreated: (Long) -> Unit) -> Unit,
    onOpenEditor: (coverLetterId: Long) -> Unit
) {
    var previewTemplate by remember { mutableStateOf<CoverLetterTemplateUiModel?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.browse_cover_letter_templates)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_home))
                    }
                }
            )
        },
        bottomBar = { BannerAdView() }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OneTimeCoachMark(
                id = "coach_browse_templates",
                message = stringResource(R.string.coach_browse_templates),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(templates, key = { it.option.id }) { template ->
                        CoverLetterBrowseCard(
                            template = template,
                            onClick = { previewTemplate = template }
                        )
                    }
                }
            }
        }
    }

    previewTemplate?.let { template ->
        CoverLetterBrowsePreviewDialog(
            template = template,
            onDismiss = { previewTemplate = null },
            onCreateCoverLetter = { accentColorHex ->
                onCreateWithTemplate(template.option.id, accentColorHex) { id ->
                    previewTemplate = null
                    onOpenEditor(id)
                }
            }
        )
    }
}

// Mirrors the resume side's TemplateCard (co.resume.ui.screen.editor.TemplateCard) exactly — same
// glass-style translucent Box + border instead of an opaque Card, same clip shape, same title-below
// treatment — so tapping/browsing a cover letter template feels like the same component as browsing
// a resume template, not a visually/behaviorally distinct one-off. No category badge or selected
// checkmark here: cover letter templates aren't categorized (CoverLetterTemplateOption has no
// category field), and this grid — like the resume Dashboard's own "Browse Templates" — always
// starts a new document rather than showing which template an existing one currently uses.
@Composable
private fun CoverLetterBrowseCard(template: CoverLetterTemplateUiModel, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), shape = shape)
                .clickable(onClick = onClick)
        ) {
            ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())
        }
        Text(
            template.option.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterBrowsePreviewDialog(
    template: CoverLetterTemplateUiModel,
    onDismiss: () -> Unit,
    onCreateCoverLetter: (accentColorHex: String?) -> Unit
) {
    val context = LocalContext.current
    var selectedColorHex by remember(template.option.id) { mutableStateOf<String?>(null) }
    val previewHtml = remember(template.option.id, selectedColorHex) {
        CoverLetterHtmlRenderer.render(context, SampleCoverLetterData.forTemplate(template.option.id, selectedColorHex))
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(template.option.title) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.template_cd_close))
                            }
                        }
                    )
                },
                bottomBar = {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Column(modifier = Modifier.navigationBarsPadding().padding(top = 12.dp, bottom = 16.dp)) {
                            Text(
                                stringResource(R.string.template_preview_color_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                            )
                            InlineColorSwatchRow(
                                selectedHex = selectedColorHex,
                                onColorSelected = { selectedColorHex = it },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Button(
                                onClick = { onCreateCoverLetter(selectedColorHex) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(stringResource(R.string.create_with_template))
                            }
                        }
                    }
                }
            ) { innerPadding ->
                CoverLetterPagePreview(
                    html = previewHtml,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoverLetterTemplateBrowseScreenPreview() {
    ResumeBuilderTheme {
        CoverLetterTemplateBrowseContent(
            templates = emptyList(),
            isLoading = true,
            onBack = {},
            onCreateWithTemplate = { _, _, _ -> },
            onOpenEditor = {}
        )
    }
}
