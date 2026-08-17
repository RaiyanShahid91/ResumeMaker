package co.resume.ui.screen.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.analytics.Analytics
import co.resume.domain.export.TemplateCategory
import co.resume.domain.layout.CuratedTemplateOption
import co.resume.domain.theme.FontCatalog
import co.resume.domain.theme.FontSizeOption
import co.resume.ui.component.ResumeTemplateThumbnail
import co.resume.ui.component.ResumeWebPreview
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.TemplatePickerUiState
import co.resume.ui.viewmodel.TemplateUiModel
import co.resume.ui.viewmodel.TemplatePickerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerScreen(
    onBack: () -> Unit,
    viewModel: TemplatePickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colorPreviewHtml by viewModel.colorPreview.collectAsStateWithLifecycle()
    TemplatePickerContent(
        uiState = uiState,
        colorPreviewHtml = colorPreviewHtml,
        onBack = onBack,
        onCategorySelected = viewModel::selectCategory,
        onRequestPreview = viewModel::renderPreview,
        onVariantSelected = { option, layoutId, hex, fontFamilyId, fontSizeScale ->
            viewModel.renderColorPreview(option, hex, layoutId, fontFamilyId, fontSizeScale)
        },
        onClearColorPreview = viewModel::clearColorPreview,
        onSelectTemplate = { layoutId, themeId, accentColorHex, fontFamilyId, fontSizeScale ->
            Analytics.logEvent(Analytics.Event.TEMPLATE_SELECTED, mapOf(Analytics.Param.TEMPLATE_ID to layoutId))
            viewModel.selectTemplate(layoutId, themeId, accentColorHex, fontFamilyId, fontSizeScale)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplatePickerContent(
    uiState: TemplatePickerUiState,
    colorPreviewHtml: String?,
    onBack: () -> Unit,
    onCategorySelected: (TemplateCategory) -> Unit,
    onRequestPreview: (CuratedTemplateOption) -> Unit,
    onVariantSelected: (CuratedTemplateOption, layoutId: String, accentColorHex: String?, fontFamilyId: String?, fontSizeScale: Float?) -> Unit,
    onClearColorPreview: () -> Unit,
    onSelectTemplate: (layoutId: String, themeId: String, accentColorHex: String?, fontFamilyId: String?, fontSizeScale: Float?) -> Unit
) {
    var previewTemplate by remember { mutableStateOf<TemplateUiModel?>(null) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.choose_template)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            CategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = onCategorySelected
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.templates, key = { it.option.id }) { template ->
                    val isSelected = (template.option.layoutId == uiState.selectedLayoutId || template.option.mirrorLayoutId == uiState.selectedLayoutId) &&
                        template.option.themeId == uiState.selectedThemeId
                    TemplateCard(
                        template = template,
                        isSelected = isSelected,
                        onClick = { previewTemplate = template },
                        onVisible = { onRequestPreview(template.option) }
                    )
                }
            }
        }
    }

    previewTemplate?.let { template ->
        val templateIsApplied = template.option.themeId == uiState.selectedThemeId
        val appliedLayoutId = if (templateIsApplied) uiState.selectedLayoutId else null
        val appliedFontFamilyId = if (templateIsApplied) uiState.selectedFontFamilyId else null
        val appliedFontSizeScale = if (templateIsApplied) uiState.selectedFontSizeScale else null
        TemplatePreviewDialog(
            template = template,
            appliedLayoutId = appliedLayoutId,
            appliedFontFamilyId = appliedFontFamilyId,
            appliedFontSizeScale = appliedFontSizeScale,
            colorPreviewHtml = colorPreviewHtml,
            onVariantSelected = { layoutId, hex, fontFamilyId, fontSizeScale ->
                onVariantSelected(template.option, layoutId, hex, fontFamilyId, fontSizeScale)
            },
            onDismiss = {
                previewTemplate = null
                onClearColorPreview()
            },
            onApply = { layoutId, accentColorHex, fontFamilyId, fontSizeScale ->
                onSelectTemplate(layoutId, template.option.themeId, accentColorHex, fontFamilyId, fontSizeScale)
                previewTemplate = null
                onClearColorPreview()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TemplatePickerScreenPreview() {
    ResumeBuilderTheme {
        TemplatePickerContent(
            uiState = TemplatePickerUiState(),
            colorPreviewHtml = null,
            onBack = {},
            onCategorySelected = {},
            onRequestPreview = {},
            onVariantSelected = { _, _, _, _, _ -> },
            onClearColorPreview = {},
            onSelectTemplate = { _, _, _, _, _ -> }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterRow(
    selectedCategory: TemplateCategory,
    onCategorySelected: (TemplateCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(TemplateCategory.entries.toList()) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(stringResource(category.labelRes), style = MaterialTheme.typography.labelMedium) }
            )
        }
    }
}

@Composable
fun TemplateCard(template: TemplateUiModel, isSelected: Boolean, onClick: () -> Unit, onVisible: () -> Unit = {}) {
    LaunchedEffect(template.option.id) { onVisible() }

    val shape = RoundedCornerShape(18.dp)
    Column {
        // Glass-style thumbnail: translucent surface + soft border over the WebView instead of an
        // opaque Card, so the thumbnail itself reads as the focal point. The whole document (every
        // page) is scaled to fit here — see FitToContainerScript — rather than cropped to page one.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f))
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    shape = shape,
                )
                .clickable(onClick = onClick)
        ) {
            ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.45f),
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Text(
                    stringResource(template.option.category.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = stringResource(R.string.template_cd_selected),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(3.dp).size(16.dp)
                    )
                }
            }
        }
        // Title lives outside the glass card now — plain caption text under the thumbnail rather
        // than a label baked into the card surface.
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
private fun TemplatePreviewDialog(
    template: TemplateUiModel,
    /** The layoutId actually applied to the resume right now, or null if this template's theme
     * isn't even the one applied — used to tell "Left" from "Right" being the current pick, not
     * just "this template family, either position." */
    appliedLayoutId: String?,
    appliedFontFamilyId: String?,
    appliedFontSizeScale: Float?,
    colorPreviewHtml: String?,
    onVariantSelected: (layoutId: String, accentColorHex: String?, fontFamilyId: String?, fontSizeScale: Float?) -> Unit,
    onDismiss: () -> Unit,
    onApply: (layoutId: String, accentColorHex: String?, fontFamilyId: String?, fontSizeScale: Float?) -> Unit
) {
    var selectedColorHex by remember(template.option.id) { mutableStateOf<String?>(null) }
    var selectedLayoutId by remember(template.option.id) { mutableStateOf(appliedLayoutId ?: template.option.layoutId) }
    var selectedFontFamilyId by remember(template.option.id) { mutableStateOf(appliedFontFamilyId) }
    var selectedFontSizeScale by remember(template.option.id) { mutableStateOf(appliedFontSizeScale) }
    val isSelected = selectedLayoutId == appliedLayoutId && selectedColorHex == null &&
        selectedFontFamilyId == appliedFontFamilyId && selectedFontSizeScale == appliedFontSizeScale

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
                                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.template_cd_close))
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
                                            onVariantSelected(selectedLayoutId, selectedColorHex, selectedFontFamilyId, selectedFontSizeScale)
                                        },
                                        label = { Text(stringResource(R.string.template_position_left)) }
                                    )
                                    FilterChip(
                                        selected = selectedLayoutId == mirrorId,
                                        onClick = {
                                            selectedLayoutId = mirrorId
                                            onVariantSelected(selectedLayoutId, selectedColorHex, selectedFontFamilyId, selectedFontSizeScale)
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
                                    onVariantSelected(selectedLayoutId, it, selectedFontFamilyId, selectedFontSizeScale)
                                },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Text(
                                "Font",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    FilterChip(
                                        selected = selectedFontFamilyId == null,
                                        onClick = {
                                            selectedFontFamilyId = null
                                            onVariantSelected(selectedLayoutId, selectedColorHex, null, selectedFontSizeScale)
                                        },
                                        label = { Text("Default") }
                                    )
                                }
                                items(FontCatalog.all, key = { it.id }) { fontOption ->
                                    FilterChip(
                                        selected = selectedFontFamilyId == fontOption.id,
                                        onClick = {
                                            selectedFontFamilyId = fontOption.id
                                            onVariantSelected(selectedLayoutId, selectedColorHex, fontOption.id, selectedFontSizeScale)
                                        },
                                        label = { Text(fontOption.displayName) }
                                    )
                                }
                            }
                            Text(
                                "Text size",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 8.dp)
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                FontSizeOption.entries.forEach { sizeOption ->
                                    val isThisSizeSelected = (selectedFontSizeScale ?: 1f) == sizeOption.scale
                                    FilterChip(
                                        selected = isThisSizeSelected,
                                        onClick = {
                                            selectedFontSizeScale = sizeOption.scale
                                            onVariantSelected(selectedLayoutId, selectedColorHex, selectedFontFamilyId, sizeOption.scale)
                                        },
                                        label = { Text(sizeOption.displayName) }
                                    )
                                }
                            }
                            Button(
                                onClick = { onApply(selectedLayoutId, selectedColorHex, selectedFontFamilyId, selectedFontSizeScale) },
                                enabled = !isSelected,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(stringResource(if (isSelected) R.string.already_using_template else R.string.use_this_template))
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
