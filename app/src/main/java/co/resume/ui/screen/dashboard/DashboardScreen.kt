package co.resume.ui.screen.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.resumeai.R
import co.resume.ui.component.DraggableFabGroup
import co.resume.ui.component.DraggableFabItem
import co.resume.ui.component.ExpandableFabMenu
import co.resume.ui.component.FabMenuOption
import co.resume.ui.screen.scan.DocumentScannerHost
import co.resume.ui.viewmodel.CoverLetterListViewModel
import co.resume.ui.viewmodel.ScannerViewModel

private enum class DashboardTab(@StringRes val labelRes: Int) {
    Home(R.string.nav_home),
    Documents(R.string.nav_documents),
    Settings(R.string.nav_settings)
}

@Composable
fun DashboardScreen(
    scannerViewModel: ScannerViewModel,
    onOpenResumeEditor: (resumeId: Long) -> Unit,
    onOpenCoverLetterEditor: (coverLetterId: Long) -> Unit,
    onBrowseTemplates: () -> Unit,
    onBrowseCoverLetterTemplates: () -> Unit = {},
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenAiChat: () -> Unit = {},
    onScanReady: () -> Unit = {},
    onOpenPdfViewer: (path: String, title: String) -> Unit = { _, _ -> },
    onOpenConverterTool: (co.resume.ui.viewmodel.ConverterTool) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var documentsSubTab by rememberSaveable { mutableIntStateOf(0) }
    var requestedDocumentsPage by remember { mutableIntStateOf(0) }
    var showCreateSheet by remember { mutableIntStateOf(0) }
    var showConverterSheet by remember { mutableStateOf(false) }
    var homeFabExpanded by remember { mutableStateOf(false) }
    var showUpdateSheet by remember { mutableStateOf(false) }
    val coverLetterViewModel: CoverLetterListViewModel = hiltViewModel()
    // Injecting this is the trigger — see AiBootstrapViewModel's doc. Starts the Groq key
    // Remote Config fetch and the per-user AI-disabled listener as soon as the home screen loads.
    hiltViewModel<co.resume.ui.viewmodel.AiBootstrapViewModel>()
    val updateCheckContext = androidx.compose.ui.platform.LocalContext.current

    // Checked once when the dashboard first loads (not on every recomposition) — a stale-version
    // nag on every screen visit would get old fast; once per session is enough to get someone to
    // update without being naggy about it.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        if (co.resume.utils.VersionCheckUtils.isUpdateAvailable(updateCheckContext)) {
            showUpdateSheet = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                NavigationBar(
                    modifier = Modifier.height(48.dp),
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    DashboardTab.entries.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                homeFabExpanded = false
                            },
                            icon = {
                                Icon(
                                    when (tab) {
                                        DashboardTab.Home -> Icons.Filled.Home
                                        DashboardTab.Documents -> Icons.Filled.Folder
                                        DashboardTab.Settings -> Icons.Filled.Settings
                                    },
                                    contentDescription = stringResource(tab.labelRes)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                        )
                    }
                }
            }
        }
        // No floatingActionButton slot here — Scaffold pins FABs to a fixed corner, which
        // can't be dragged. The create-resume FAB is laid out manually below instead, so it
        // can be dragged anywhere within the content area.
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (homeFabExpanded) Modifier.blur(20.dp) else Modifier)
            ) {
                when (DashboardTab.entries[selectedTab]) {
                    DashboardTab.Home -> HomeTab(
                        onCreateResume = { homeFabExpanded = false; showCreateSheet++ },
                        onViewResumes = {
                            homeFabExpanded = false
                            requestedDocumentsPage = DocumentsPage.Resumes.ordinal
                            selectedTab = DashboardTab.Documents.ordinal
                        },
                        onViewCoverLetters = {
                            homeFabExpanded = false
                            requestedDocumentsPage = DocumentsPage.CoverLetters.ordinal
                            selectedTab = DashboardTab.Documents.ordinal
                        },
                        onViewScannedDocuments = {
                            homeFabExpanded = false
                            requestedDocumentsPage = DocumentsPage.Scans.ordinal
                            selectedTab = DashboardTab.Documents.ordinal
                        },
                        onScanDocument = { homeFabExpanded = false; scannerViewModel.requestScan() },
                        onOpenConverter = { homeFabExpanded = false; showConverterSheet = true },
                        onBrowseTemplates = { homeFabExpanded = false; onBrowseTemplates() },
                        onBrowseCoverLetterTemplates = { homeFabExpanded = false; onBrowseCoverLetterTemplates() },
                        onOpenAiChat = { homeFabExpanded = false; onOpenAiChat() },
                        onCreateCoverLetter = { id -> homeFabExpanded = false; onOpenCoverLetterEditor(id) }
                    )
                    DashboardTab.Documents -> DocumentsHubTab(
                        onOpenResume = onOpenResumeEditor,
                        onOpenCoverLetter = onOpenCoverLetterEditor,
                        onOpenPdfViewer = onOpenPdfViewer,
                        initialPage = requestedDocumentsPage,
                        onPageChanged = { documentsSubTab = it }
                    )
                    DashboardTab.Settings -> SettingsTab(
                        onLanguageSelected = onLanguageSelected,
                        onOpenPrivacy = onOpenPrivacy,
                        onOpenAbout = onOpenAbout,
                        onOpenGuide = onOpenGuide
                    )
                }
            }

            if (homeFabExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { homeFabExpanded = false }
                )
            }

            val createResumeLabel = stringResource(R.string.nav_create_resume)
            val createCoverLetterLabel = stringResource(R.string.home_action_cover_letter)
            val scanLabel = stringResource(R.string.nav_scan_document)

            when (DashboardTab.entries[selectedTab]) {
                DashboardTab.Home -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.BottomEnd) {
                        ExpandableFabMenu(
                            expanded = homeFabExpanded,
                            onToggle = { homeFabExpanded = !homeFabExpanded },
                            options = listOf(
                                FabMenuOption(Icons.Filled.Description, createResumeLabel) {
                                    homeFabExpanded = false
                                    showCreateSheet++
                                },
                                FabMenuOption(Icons.AutoMirrored.Filled.Article, createCoverLetterLabel) {
                                    homeFabExpanded = false
                                    coverLetterViewModel.createCoverLetter(onCreated = onOpenCoverLetterEditor)
                                },
                                FabMenuOption(Icons.Filled.DocumentScanner, scanLabel) {
                                    homeFabExpanded = false
                                    scannerViewModel.requestScan()
                                }
                            ),
                            toggleIcon = {
                                Icon(
                                    if (homeFabExpanded) Icons.Filled.Close else Icons.Filled.Add,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
                DashboardTab.Documents -> {
                    val fab = when (DocumentsPage.entries[documentsSubTab]) {
                        DocumentsPage.Resumes -> DraggableFabItem(key = "create_resume", onClick = { showCreateSheet++ }) {
                            Icon(Icons.Filled.Add, contentDescription = createResumeLabel)
                        }
                        DocumentsPage.CoverLetters -> DraggableFabItem(
                            key = "create_cover_letter",
                            onClick = { coverLetterViewModel.createCoverLetter(onCreated = onOpenCoverLetterEditor) }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = createCoverLetterLabel)
                        }
                        DocumentsPage.Scans -> DraggableFabItem(key = "scan", onClick = { scannerViewModel.requestScan() }) {
                            Icon(Icons.Filled.Add, contentDescription = scanLabel)
                        }
                    }
                    DraggableFabGroup(items = listOf(fab))
                }
                DashboardTab.Settings -> Unit
            }
        }
    }

    DocumentScannerHost(viewModel = scannerViewModel, onScanReady = onScanReady)

    if (showUpdateSheet) {
        UpdateAppSheet(onDismiss = { showUpdateSheet = false })
    }

    if (showCreateSheet > 0) {
        CreateResumeSheet(
            onDismiss = { showCreateSheet = 0 },
            onCreated = { id ->
                showCreateSheet = 0
                onOpenResumeEditor(id)
            }
        )
    }

    if (showConverterSheet) {
        co.resume.ui.screen.convert.ConverterToolPickerSheet(
            onDismiss = { showConverterSheet = false },
            onSelectTool = { tool ->
                showConverterSheet = false
                onOpenConverterTool(tool)
            }
        )
    }
}
