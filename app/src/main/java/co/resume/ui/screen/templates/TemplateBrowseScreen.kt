package co.resume.ui.screen.templates

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.ResumeWebPreview
import co.resume.utils.ImeLocaleHint
import co.resume.ui.screen.editor.CategoryFilterRow
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
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    var previewTemplate by remember { mutableStateOf<TemplateUiModel?>(null) }
    var createWithTemplateId by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
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
                onCategorySelected = viewModel::setCategory
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
                        TemplateCard(
                            template = template,
                            isSelected = false,
                            onClick = { previewTemplate = template }
                        )
                    }
                }
            }
        }
    }

    previewTemplate?.let { template ->
        BrowsePreviewDialog(
            template = template,
            onDismiss = { previewTemplate = null },
            onCreateResume = {
                previewTemplate = null
                createWithTemplateId = template.option.id
            }
        )
    }

    if (createWithTemplateId != -1) {
        CreateWithTemplateSheet(
            onDismiss = { createWithTemplateId = -1 },
            onCreated = { name, designation ->
                viewModel.createResumeWithTemplate(name, designation, createWithTemplateId) { id ->
                    createWithTemplateId = -1
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
    onDismiss: () -> Unit,
    onCreateResume: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
                    Button(
                        onClick = onCreateResume,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(stringResource(R.string.create_with_template))
                    }
                }
            ) { innerPadding ->
                ResumeWebPreview(
                    html = template.previewHtml,
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
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(stringResource(R.string.create_resume_heading), style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
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
