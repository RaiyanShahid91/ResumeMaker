package co.resume.ui.screen.info

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.resumeai.R

/** The privacy policy's source of truth now lives on the web (so it can be updated without an
 *  app release) — this screen is just a chrome-less viewer for it. Never surface this URL in the
 *  UI (no address bar, no "open in browser" text); it's an implementation detail, not something
 *  the user needs to see or copy. */
private const val PRIVACY_POLICY_URL = "https://brynflow.com/fileforge/privacy"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var loadFailed by remember { mutableStateOf(false) }
    // Bumped to force AndroidView's factory to run again on retry — a fresh WebView is simpler
    // and more reliable than trying to re-trigger a load on one that already failed.
    var loadAttempt by remember { mutableStateOf(0) }

    // Unlike every other screen, this one's content is a full-bleed white web page with no
    // margin/gap around it — the app's usual transparent chrome (which lets the shared gradient
    // background show through) reads as a jarring mismatched seam right above it instead. Solid
    // white top bar + Scaffold background instead, so the whole screen reads as one continuous
    // surface with the page it's showing.
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = { Text(stringResource(R.string.settings_privacy)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (!loadFailed) {
                key(loadAttempt) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView, url: String?) {
                                        isLoading = false
                                    }

                                    override fun onReceivedError(
                                        view: WebView,
                                        request: WebResourceRequest,
                                        error: WebResourceError
                                    ) {
                                        // Only the main page load failing should show the error
                                        // state — a single failed sub-resource (an image, a font)
                                        // shouldn't blank out an otherwise-working page.
                                        if (request.isForMainFrame) {
                                            isLoading = false
                                            loadFailed = true
                                        }
                                    }
                                }
                                settings.javaScriptEnabled = true
                                loadUrl(PRIVACY_POLICY_URL)
                            }
                        },
                        onRelease = { it.destroy() }
                    )
                }
            }

            if (isLoading && !loadFailed) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (loadFailed) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        stringResource(R.string.privacy_load_failed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        modifier = Modifier.padding(top = 16.dp),
                        onClick = {
                            loadFailed = false
                            isLoading = true
                            loadAttempt++
                        }
                    ) { Text(stringResource(R.string.privacy_load_retry)) }
                }
            }
        }
    }
}
