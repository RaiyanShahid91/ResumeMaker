package co.resume.ui.screen.preview

import android.webkit.WebView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.billing.requirePremium
import co.resume.domain.export.DocumentExporter
import co.resume.ui.component.CoverLetterPagePreview
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.CoverLetterPreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterPreviewScreen(
    onBack: () -> Unit,
    onOpenPaywall: () -> Unit = {},
    viewModel: CoverLetterPreviewViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var webView by remember { mutableStateOf<WebView?>(null) }
    val context = LocalContext.current
    val isPremium by adViewModel.isPremium.collectAsStateWithLifecycle()
    val shareChooserTitle = stringResource(R.string.cover_letter_share_chooser_title)

    CoverLetterPreviewContent(
        html = uiState.html,
        onBack = onBack,
        onWebViewReady = { webView = it },
        onShare = {
            val wv = webView ?: return@CoverLetterPreviewContent
            requirePremium(isPremium, onOpenPaywall) {
                DocumentExporter.sharePdf(context, wv, uiState.fileTitle, shareChooserTitle, "cover_letter")
            }
        },
        onDownload = {
            val wv = webView ?: return@CoverLetterPreviewContent
            requirePremium(isPremium, onOpenPaywall) {
                DocumentExporter.exportAsPdf(context, wv, uiState.fileTitle, "cover_letter")
            }
        }
    )
}

/**
 * Stateless split-out of [CoverLetterPreviewScreen]'s UI so it can be exercised from a `@Preview`
 * without a Hilt ViewModel/Activity/AdManager in the graph — none of those are reachable from
 * Android Studio's static preview renderer. Note the WebView itself still won't paint any actual
 * page content in that renderer (see ResumeWebPreview's own preview, which only shows the loading
 * spinner state) — this only lets the surrounding Scaffold/FAB chrome be checked visually.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterPreviewContent(
    html: String?,
    onBack: () -> Unit,
    onWebViewReady: (WebView) -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
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
                FloatingActionButton(onClick = onShare) {
                    Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.preview_cd_share))
                }
                FloatingActionButton(onClick = onDownload) {
                    Icon(Icons.Filled.Download, contentDescription = stringResource(R.string.preview_cd_download))
                }
            }
        }
    ) { padding ->
        // CoverLetterPagePreview gives every cover letter preview surface (this screen, plus the
        // template-browse and template-picker preview dialogs) an identical, reliable 40dp gap on
        // every side. Resume's own preview screen is untouched — it keeps using ResumeWebPreview
        // directly, with no gap wrapper.
        Box(modifier = Modifier.fillMaxSize()) {
            CoverLetterPagePreview(
                html = html,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    // Scaffold's `padding` only reserves room for the top/bottom bars, never for a
                    // floatingActionButton — FABs are meant to float ON TOP of content by design.
                    // CoverLetterPagePreview now sizes its page to fill all the height it's given
                    // (see its own doc for why), so without this the page grew tall enough to run
                    // underneath the share/download FAB stack, with the FAB circles visibly
                    // overlapping the letter's last lines of text. This reserves the FAB column's
                    // real footprint (two 56dp FABs + 12dp spacing + Scaffold's own edge margin) so
                    // the page always stays clear of it.
                    .padding(bottom = 140.dp),
                onWebViewReady = onWebViewReady
            )
        }
    }
}

@Preview(showBackground = true, name = "Cover letter preview")
@Composable
private fun CoverLetterPreviewScreenPreview() {
    ResumeBuilderTheme {
        CoverLetterPreviewContent(
            html = null,
            onBack = {},
            onWebViewReady = {},
            onShare = {},
            onDownload = {}
        )
    }
}
