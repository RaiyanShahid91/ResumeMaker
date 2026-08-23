package co.resume.ui.screen.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ui.component.ResumeWebPreview
import co.resume.ui.component.SupportWithAdDialog
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.ResumePreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumePreviewScreen(
    onBack: () -> Unit,
    viewModel: ResumePreviewViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val localContext = LocalContext.current
    val activity = localContext as? android.app.Activity
    val shareChooserTitle = stringResource(R.string.preview_share_chooser_title)
    val estimatedPages = uiState.layout?.pages?.size ?: 1
    val adManager = adViewModel.adManager
    var showAdPrompt by remember { mutableStateOf(false) }
    fun offerAdAfter() {
        if (adManager.canOfferRewardedPrompt()) showAdPrompt = true
    }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
                title = { Text(stringResource(R.string.preview_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FloatingActionButton(
                    onClick = {
                        val layout = uiState.layout ?: return@FloatingActionButton
                        viewModel.sharePdf(layout, uiState.fileTitle, shareChooserTitle, localContext)
                        offerAdAfter()
                    }
                ) {
                    Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.preview_cd_share))
                }
                FloatingActionButton(
                    onClick = {
                        val layout = uiState.layout ?: return@FloatingActionButton
                        viewModel.exportPdf(layout, uiState.fileTitle, localContext)
                        offerAdAfter()
                    }
                ) {
                    Icon(Icons.Filled.Download, contentDescription = stringResource(R.string.preview_cd_download))
                }
            }
        },
        bottomBar = { co.resume.ui.component.BannerAdView() }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (estimatedPages > 1) {
                    MultiPageNotice(pageCount = estimatedPages, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Constrained to real A4 proportions (595:842 pt) instead of an arbitrary
                    // phone-shaped box — a tall, narrow phone screen made a single on-screen
                    // "page" look deceptively short/long compared to the actual paper size the
                    // PDF export renders against, which is what made long resumes silently
                    // exporting as several pages so surprising.
                    ResumeWebPreview(
                        html = uiState.html,
                        modifier = Modifier.fillMaxHeight().aspectRatio(595f / 842f)
                    )
                }
            }
        }
    }

    if (showAdPrompt) {
        SupportWithAdDialog(
            onWatchAd = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
                activity?.let { adManager.showRewarded(it) {} }
            },
            onSkip = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
            }
        )
    }
}

/** Warns the user upfront, before they export, that their content is long enough to span multiple A4 pages. */
@Composable
private fun MultiPageNotice(pageCount: Int, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Icon(
                Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Text(
                stringResource(R.string.preview_multi_page_notice, pageCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}


