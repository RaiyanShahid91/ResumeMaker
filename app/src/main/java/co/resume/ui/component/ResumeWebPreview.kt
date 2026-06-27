package co.resume.ui.component

import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Renders resume HTML in a WebView. Used for the template grid thumbnails, the enlarged
 * template preview, and the final resume preview/export screen, so all three are guaranteed
 * to show exactly the same output.
 */
@Composable
fun ResumeWebPreview(
    html: String?,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    onWebViewReady: (WebView) -> Unit = {}
) {
    Box(modifier = modifier) {
        if (html == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    object : WebView(context) {
                        override fun dispatchTouchEvent(ev: MotionEvent?) =
                            if (interactive) super.dispatchTouchEvent(ev) else false
                    }.apply {
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                // Open any tapped link in the external browser, not inside the WebView
                                runCatching {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.url.toString()))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    view.context.startActivity(intent)
                                }
                                return true
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        @Suppress("DEPRECATION")
                        settings.allowFileAccess = false
                        isVerticalScrollBarEnabled = interactive
                        isHorizontalScrollBarEnabled = interactive
                        onWebViewReady(this)
                    }
                },
                // loadData() truncates content at the first literal '#' (treated as a URL
                // fragment); templates use '#rrggbb' colors, so loadDataWithBaseURL is required.
                update = { it.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null) }
            )
        }
    }
}
