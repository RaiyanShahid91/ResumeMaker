package co.resume.ui.screen.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import co.resume.ui.component.CoverLetterPagePreview
import co.resume.ui.component.ResumeTemplateThumbnail
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.CoverLetterTemplatePickerUiState
import co.resume.ui.viewmodel.CoverLetterTemplatePickerViewModel
import co.resume.ui.viewmodel.CoverLetterTemplateUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterTemplatePickerScreen(
    onBack: () -> Unit,
    viewModel: CoverLetterTemplatePickerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CoverLetterTemplatePickerContent(
        uiState = uiState,
        onBack = onBack,
        onSelectTemplate = viewModel::selectTemplate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterTemplatePickerContent(
    uiState: CoverLetterTemplatePickerUiState,
    onBack: () -> Unit,
    onSelectTemplate: (templateId: Int, accentColorHex: String?) -> Unit
) {
    var previewTemplate by remember { mutableStateOf<CoverLetterTemplateUiModel?>(null) }

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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(uiState.templates, key = { it.option.id }) { template ->
                    CoverLetterTemplateCard(
                        template = template,
                        isSelected = template.option.id == uiState.selectedTemplateId,
                        onClick = { previewTemplate = template }
                    )
                }
            }
        }
    }

    previewTemplate?.let { template ->
        CoverLetterTemplatePreviewDialog(
            template = template,
            isSelected = template.option.id == uiState.selectedTemplateId,
            onDismiss = { previewTemplate = null },
            onApply = { accentColorHex ->
                onSelectTemplate(template.option.id, accentColorHex)
                previewTemplate = null
            }
        )
    }
}

@Composable
private fun CoverLetterTemplateCard(template: CoverLetterTemplateUiModel, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())
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
            Text(
                template.option.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterTemplatePreviewDialog(
    template: CoverLetterTemplateUiModel,
    isSelected: Boolean,
    onDismiss: () -> Unit,
    onApply: (accentColorHex: String?) -> Unit
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
                                onClick = { onApply(selectedColorHex) },
                                enabled = !isSelected || selectedColorHex != null,
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
private fun CoverLetterTemplatePickerScreenPreview() {
    ResumeBuilderTheme {
        CoverLetterTemplatePickerContent(
            uiState = CoverLetterTemplatePickerUiState(isLoading = true),
            onBack = {},
            onSelectTemplate = { _, _ -> }
        )
    }
}
