package co.resume.ui.component

import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Cover-letter-only wrapper around [ResumeWebPreview] that mirrors ResumePreviewScreen's own
 * page-preview treatment: a plain 16dp-inset, centered box sitting directly on whatever background
 * the screen itself already has — no separate gray "card" panel behind it. An earlier version
 * painted its own gray tray box behind the page; a later one fixed that but still assumed a FIXED
 * one-page A4 ratio (595:842) for the container. Cover letters are a single continuous flow, not
 * ResumeLayoutHtmlRenderer's fixed-height `.page` divs, so a short letter's real rendered height is
 * very often shorter than one full page — a fixed-ratio container left the WebView's own native
 * white background exposed below the real (shorter) content, visible as what looked like a second,
 * blank page nested inside the first, EVEN THOUGH the container itself was correctly A4-shaped.
 *
 * The fix: start at the A4 ratio (so there's a sane box before anything has loaded) and then, once
 * [ResumeWebPreview] reports the document's real measured CSS-px size via `onContentMeasured`,
 * switch the container to exactly that ratio — the container and the document it holds can then
 * never disagree, regardless of how tall any given letter actually renders.
 */
@Composable
fun CoverLetterPagePreview(
    html: String?,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    onWebViewReady: (WebView) -> Unit = {},
) {
    var aspectRatio by remember { mutableFloatStateOf(595f / 842f) }
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        ResumeWebPreview(
            html = html,
            modifier = Modifier.fillMaxHeight().aspectRatio(aspectRatio),
            interactive = interactive,
            onWebViewReady = onWebViewReady,
            onContentMeasured = { width, height -> aspectRatio = width / height },
        )
    }
}
