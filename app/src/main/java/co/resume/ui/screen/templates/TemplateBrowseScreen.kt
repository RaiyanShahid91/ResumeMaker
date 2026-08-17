package co.resume.ui.screen.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.tooling.preview.Preview
import co.resumeai.R
import co.resume.domain.export.TemplateCategory
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppTextField
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.OneTimeCoachMark
import co.resume.ui.component.ResumeWebPreview
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.utils.ImeLocaleHint
import co.resume.ui.screen.editor.CategoryFilterRow
import co.resume.ui.screen.editor.InlineColorSwatchRow
import co.resume.ui.screen.editor.TemplateCard
import co.resume.ui.viewmodel.TemplateBrowseViewModel
import co.resume.ui.viewmodel.TemplateUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateBrowseScreen(
    onBack: () -> Unit,
    onOpenEditor: (resumeId: Long) -> Unit,
    viewModel: TemplateBrowseViewModel = hiltViewModel()
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val colorPreviewHtml by viewModel.colorPreview.collectAsStateWithLifecycle()

    TemplateBrowseContent(
        templates = templates,
        selectedCategory = selectedCategory,
        colorPreviewHtml = colorPreviewHtml,
        onCategorySelected = viewModel::setCategory,
        onRequestPreview = viewModel::renderPreview,
        onVariantSelected = { option, layoutId, hex -> viewModel.renderColorPreview(option, hex, layoutId) },
        onClearColorPreview = viewModel::clearColorPreview,
        onBack = onBack,
        onCreateWithTemplate = { name, designation, layoutId, themeId, accentColorHex, onCreated ->
            viewModel.createResumeWithTemplate(name, designation, layoutId, themeId, accentColorHex, onCreated)
        },
        onOpenEditor = onOpenEditor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateBrowseContent(
    templates: List<TemplateUiModel>,
    selectedCategory: TemplateCategory,
    colorPreviewHtml: String?,
    onCategorySelected: (TemplateCategory) -> Unit,
    onRequestPreview: (co.resume.domain.layout.CuratedTemplateOption) -> Unit,
    onVariantSelected: (co.resume.domain.layout.CuratedTemplateOption, layoutId: String, accentColorHex: String?) -> Unit,
    onClearColorPreview: () -> Unit,
    onBack: () -> Unit,
    onCreateWithTemplate: (name: String, designation: String, layoutId: String, themeId: String, accentColorHex: String?, onCreated: (Long) -> Unit) -> Unit,
    onOpenEditor: (resumeId: Long) -> Unit
) {
    var previewTemplate by remember { mutableStateOf<TemplateUiModel?>(null) }
    var createWithTemplate by remember { mutableStateOf<TemplateUiModel?>(null) }
    var createLayoutId by remember { mutableStateOf<String?>(null) }
    var createAccentColorHex by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.browse_templates)) },
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
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            OneTimeCoachMark(
                id = "coach_browse_templates",
                message = stringResource(R.string.coach_browse_templates),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(templates, key = { it.option.id }) { template ->
                    TemplateCard(
                        template = template,
                        isSelected = false,
                        onClick = { previewTemplate = template },
                        onVisible = { onRequestPreview(template.option) }
                    )
                }
            }
        }
    }

    previewTemplate?.let { template ->
        BrowsePreviewDialog(
            template = template,
            colorPreviewHtml = colorPreviewHtml,
            onVariantSelected = { layoutId, hex -> onVariantSelected(template.option, layoutId, hex) },
            onDismiss = {
                previewTemplate = null
                onClearColorPreview()
            },
            onCreateResume = { layoutId, accentColorHex ->
                previewTemplate = null
                onClearColorPreview()
                createLayoutId = layoutId
                createAccentColorHex = accentColorHex
                createWithTemplate = template
            }
        )
    }

    createWithTemplate?.let { template ->
        CreateWithTemplateSheet(
            onDismiss = { createWithTemplate = null },
            onCreated = { name, designation ->
                onCreateWithTemplate(
                    name, designation,
                    createLayoutId ?: template.option.layoutId, template.option.themeId, createAccentColorHex,
                ) { id ->
                    createWithTemplate = null
                    onOpenEditor(id)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrowsePreviewDialog(
    template: TemplateUiModel,
    colorPreviewHtml: String?,
    onVariantSelected: (layoutId: String, accentColorHex: String?) -> Unit,
    onDismiss: () -> Unit,
    onCreateResume: (layoutId: String, accentColorHex: String?) -> Unit
) {
    var selectedColorHex by remember(template.option.id) { mutableStateOf<String?>(null) }
    var selectedLayoutId by remember(template.option.id) { mutableStateOf(template.option.layoutId) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text(template.option.title)
                                Text(
                                    stringResource(template.option.category.labelRes),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = "Close")
                            }
                        }
                    )
                },
                bottomBar = {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        Column(modifier = Modifier.navigationBarsPadding().padding(top = 12.dp, bottom = 16.dp)) {
                            template.option.mirrorLayoutId?.let { mirrorId ->
                                Text(
                                    stringResource(R.string.template_preview_position_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedLayoutId == template.option.layoutId,
                                        onClick = {
                                            selectedLayoutId = template.option.layoutId
                                            onVariantSelected(selectedLayoutId, selectedColorHex)
                                        },
                                        label = { Text(stringResource(R.string.template_position_left)) }
                                    )
                                    FilterChip(
                                        selected = selectedLayoutId == mirrorId,
                                        onClick = {
                                            selectedLayoutId = mirrorId
                                            onVariantSelected(selectedLayoutId, selectedColorHex)
                                        },
                                        label = { Text(stringResource(R.string.template_position_right)) }
                                    )
                                }
                            }
                            Text(
                                stringResource(R.string.template_preview_color_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
                            )
                            InlineColorSwatchRow(
                                selectedHex = selectedColorHex,
                                onColorSelected = {
                                    selectedColorHex = it
                                    onVariantSelected(selectedLayoutId, it)
                                },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Button(
                                onClick = { onCreateResume(selectedLayoutId, selectedColorHex) },
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
                ResumeWebPreview(
                    html = colorPreviewHtml ?: template.previewHtml,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateWithTemplateSheet(
    onDismiss: () -> Unit,
    onCreated: (name: String, designation: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }

    ImeLocaleHint()
    AppBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.create_resume_heading), style = MaterialTheme.typography.titleLarge)

            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            AppTextField(
                value = designation,
                onValueChange = { designation = it },
                label = { Text(stringResource(R.string.label_designation)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )

            Button(
                onClick = { onCreated(name.trim(), designation.trim()) },
                enabled = name.isNotBlank() && designation.isNotBlank(),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 24.dp)
            ) {
                Text(stringResource(R.string.btn_create))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplateBrowseScreenPreview() {
    ResumeBuilderTheme {
        TemplateBrowseContent(
            templates = emptyList(),
            selectedCategory = TemplateCategory.All,
            colorPreviewHtml = null,
            onCategorySelected = {},
            onRequestPreview = {},
            onVariantSelected = { _, _, _ -> },
            onClearColorPreview = {},
            onBack = {},
            onCreateWithTemplate = { _, _, _, _, _, _ -> },
            onOpenEditor = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CreateWithTemplateSheetPreview() {
    ResumeBuilderTheme {
        CreateWithTemplateSheet(onDismiss = {}, onCreated = { _, _ -> })
    }
}
