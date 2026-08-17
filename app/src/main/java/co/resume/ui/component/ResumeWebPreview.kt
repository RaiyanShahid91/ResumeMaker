package co.resume.ui.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.resume.ui.theme.ResumeBuilderTheme

// Every template exposes its body text size as the CSS custom property --font-size (default
// 12px), with everything else derived from it via calc(). Rather than a fixed shrink, this binary
// searches for the LARGEST --font-size that still keeps the rendered content within MAX_PAGES
// worth of A4-page height (same 1.4142 width:height ratio DocumentExporter's own pagination uses),
// down to a MIN_FONT_PX floor below which text would stop being legible/printable. Content that
// still overflows MAX_PAGES even at the floor size is left at the floor size rather than shrunk
// further — the exporter will then simply produce more than MAX_PAGES pages for that edge case,
// rather than losing data or becoming unreadable. Runs once per load, on the same WebView/DOM that
// PDF/Word export later reuses, so what the user sees is exactly what gets exported.
private const val AutoFitPagesScript = """
    (function() {
        try {
            var MAX_PAGES = 3;
            var MIN_FONT_PX = 8;
            var root = document.documentElement;
            function pageHeight() { return root.scrollWidth * 1.4142; }
            function contentHeight() { return root.scrollHeight; }

            var computed = parseFloat(getComputedStyle(root).getPropertyValue('--font-size'));
            var startSize = (computed && computed > 0) ? computed : 12;
            var maxHeight = MAX_PAGES * pageHeight();

            if (contentHeight() > maxHeight && startSize > MIN_FONT_PX) {
                var lo = MIN_FONT_PX, hi = startSize, best = MIN_FONT_PX;
                for (var i = 0; i < 10; i++) {
                    var mid = (lo + hi) / 2;
                    root.style.setProperty('--font-size', mid + 'px');
                    if (contentHeight() <= maxHeight) {
                        best = mid;
                        lo = mid;
                    } else {
                        hi = mid;
                    }
                }
                root.style.setProperty('--font-size', best + 'px');
            }

            // ResumeLayoutHtmlRenderer's output emits one real, separately-backgrounded `.page` div
            // per page already, with a visible gap between them — that IS the page boundary, visible
            // without any synthetic overlay. Cover letter templates are one continuous flow with no
            // `.page` elements; a dashed divider + floating "Page N" label used to be synthesized
            // here to approximate where a continuous document would break, but it was a guess (a
            // uniform pageHeight() slice, unrelated to where content naturally breaks) that read as
            // a stray label rather than useful information — removed so the preview just scrolls
            // continuously, the same as any other flowed document.
            var realPages = document.querySelectorAll('.page').length;
            var finalPageHeight = pageHeight();
            var pageCount = realPages > 0 ? realPages : Math.max(1, Math.ceil(contentHeight() / finalPageHeight));
            return JSON.stringify({ pageCount: pageCount, width: root.scrollWidth, height: contentHeight() });
        } catch (e) {
            return JSON.stringify({ pageCount: 1, width: 0, height: 0 });
        }
    })();
"""

/**
 * Renders resume HTML in a WebView. Used for the template grid thumbnails, the enlarged
 * template preview, and the final resume preview/export screen, so all three are guaranteed
 * to show exactly the same output.
 */
// Scales the document so the FIRST page alone fills the thumbnail viewport, then relies on the
// AndroidView's clipToBounds() (see below) to crop away anything past it — a card is a small,
// fixed-aspect-ratio square, and squeezing every page of a multi-page resume into that same box
// (the earlier approach) shrank text to the point of being unreadable and, worse, could measure
// as an inconsistent height across a grid row when a very long document's natural scrollHeight
// changed between layout passes, visibly breaking the 2-column grid. A template card only needs to
// sell the design at a glance — page one is what a user actually judges it by.
// Re-run from a native OnLayoutChangeListener (see below), since Compose can deliver more than one
// layout pass (an intermediate size, then the final aspectRatio-derived one) before settling.
// Earlier revisions set body.style.width directly from the freshly-computed scale WITHOUT first
// undoing the previous run's width — width is layout-affecting (unlike the transform, which is
// purely visual), so a second invocation measured scrollHeight against an already-widened/narrowed
// body, compounding the scale further off with each call. Two consecutive layout passes could
// converge on values so far off that content ended up scaled down to nothing (blank thumbnail,
// unresponsive to taps). Resetting width/transform to the unscaled baseline before measuring makes
// every call idempotent — same inputs in, same output, regardless of how many times it runs.
private const val FitToContainerScript = """
    (function() {
        document.body.style.transform = 'none';
        document.body.style.width = '100%';
        // ResumeLayoutHtmlRenderer emits one real `.page` div per page — key off the first one so a
        // 2+ page resume still shows a properly-filled, legible page one instead of the whole spread
        // shrunk down. Continuous-flow documents (cover letter templates, which have no `.page`
        // elements) fall back to an A4-ratio slice of the current (already-widened-to-100%) width.
        var pages = document.querySelectorAll('.page');
        var rect = pages.length > 0 ? pages[0].getBoundingClientRect() : null;
        var pageWidth = rect ? rect.width : document.documentElement.scrollWidth;
        var pageHeight = rect ? rect.height : pageWidth * 1.4142;
        // .page has a fixed physical (mm) width, unaffected by body.style.width above, so its
        // rendered width in CSS px doesn't necessarily match window.innerWidth even though the card's
        // aspect ratio is chosen to match A4. Scaling by height alone (the previous approach) left
        // that mismatch as visible letterboxing down one side — only ~75% of the card actually
        // covered by the page. Taking the LARGER of the width-fit and height-fit scale factors
        // guarantees both axes are fully covered (a "cover" fit, cropped by clipToBounds below)
        // instead of a "contain" fit that can underfill either dimension.
        var scaleW = pageWidth > 0 ? window.innerWidth / pageWidth : 0;
        var scaleH = pageHeight > 0 ? window.innerHeight / pageHeight : 0;
        var scale = Math.max(scaleW, scaleH);
        if (scale > 0) {
            document.body.style.transformOrigin = 'top left';
            document.body.style.transform = 'scale(' + scale + ')';
            document.body.style.width = (100 / scale) + '%';
        }
        document.documentElement.style.visibility = 'visible';
    })();
"""

// Prepended only when fitToContainer is true, so the page stays invisible for the brief window
// between initial paint and FitToContainerScript running — otherwise the thumbnail flashes at
// full (unscaled) size for a frame before snapping down, which reads as flicker/jitter during a
// horizontal scroll where carousel items are freshly composed. No !important, so the script's
// plain inline-style assignment (always higher precedence than a stylesheet rule) reliably wins.
private const val HideUntilScaledStyle = "<style>html{visibility:hidden;}</style>"

/**
 * Tracks the last HTML string actually loaded so [AndroidView]'s `update` callback — which
 * Compose re-invokes on every recomposition of this node, not just when [html] changes — doesn't
 * re-parse/re-render identical content on every unrelated recomposition (e.g. a scroll-driven
 * recomposition of a parent LazyRow/LazyColumn item). That was previously wasted WebView work on
 * every scroll frame in the template thumbnail carousels.
 */
private class ResumeWebViewImpl(context: Context, private val interactive: Boolean) : WebView(context) {
    var loadedHtml: String? = null

    override fun dispatchTouchEvent(ev: MotionEvent?) =
        if (interactive) super.dispatchTouchEvent(ev) else false
}

@Composable
fun ResumeWebPreview(
    html: String?,
    modifier: Modifier = Modifier,
    interactive: Boolean = true,
    fitToContainer: Boolean = false,
    onWebViewReady: (WebView) -> Unit = {},
    onEstimatedPageCount: (Int) -> Unit = {},
    // Real, unzoomed content size in CSS px, reported straight from AutoFitPagesScript's own
    // measurement — lets a caller size its container to the document's TRUE aspect ratio instead of
    // assuming every document is exactly one (or N whole) A4 page(s) tall. Resume content always is
    // (ResumeLayoutHtmlRenderer emits fixed-height `.page` divs), but cover letters are a single
    // continuous flow that's very often shorter than a full page — sizing a fixed-ratio container
    // around that left the WebView's own native background exposed below the real content, visible
    // as a second, blank "page" nested inside the first.
    onContentMeasured: (widthPx: Float, heightPx: Float) -> Unit = { _, _ -> }
) {
    Box(modifier = modifier) {
        if (html == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            AndroidView(
                // WebView can composite its content layer at its full (pre-scale) size and bleed
                // past the View's bounds inside a scrolling container like the template
                // thumbnail carousel — clipToBounds forces Compose to hard-clip it regardless.
                modifier = Modifier.fillMaxSize().clipToBounds(),
                factory = { context ->
                    ResumeWebViewImpl(context, interactive).apply {
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

                            override fun onPageFinished(view: WebView, url: String?) {
                                if (fitToContainer) {
                                    // Deferred one frame: right after onPageFinished, especially
                                    // when many cards in a grid load/relayout together (e.g. a
                                    // category filter change), this View's own layout pass may not
                                    // have run yet, so window.innerHeight can still read a stale
                                    // size. Posting lets Compose's pending layout pass land first;
                                    // the OnLayoutChangeListener below is the authoritative re-run
                                    // for any bounds change after that — but under a fast multi-swipe
                                    // scroll (many cards entering/leaving at once) that listener can
                                    // still end up racing with layout and never firing again with the
                                    // final size, so a single 150ms-delayed re-check acts as a last-
                                    // resort correction for whatever the frame-level attempts missed.
                                    view.post { view.evaluateJavascript(FitToContainerScript, null) }
                                    view.postDelayed({ view.evaluateJavascript(FitToContainerScript, null) }, 150)
                                } else {
                                    view.evaluateJavascript(AutoFitPagesScript) { result ->
                                        runCatching {
                                            val json = org.json.JSONObject(result)
                                            onEstimatedPageCount(json.getInt("pageCount"))
                                            val width = json.optDouble("width", 0.0).toFloat()
                                            val height = json.optDouble("height", 0.0).toFloat()
                                            if (width > 0f && height > 0f) onContentMeasured(width, height)
                                        }
                                    }
                                }
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        // Wide-viewport + overview-mode makes the WebView itself auto-zoom to fit its
                        // widest content (the .page div's fixed mm-width) to the screen — exactly what
                        // the full/interactive preview wants (see the whole A4 page on load, then pinch
                        // to zoom in). But for a thumbnail, that implicit auto-zoom fights with
                        // FitToContainerScript's own manual scale-to-fit: window.innerWidth/innerHeight
                        // end up reflecting the auto-zoomed layout viewport rather than the thumbnail's
                        // actual pixel bounds, so the script's computed scale undershoots and the page
                        // renders smaller than the card (visible empty margin instead of a full-bleed
                        // fill). Disabling both for fitToContainer makes the layout viewport equal the
                        // WebView's real, unzoomed bounds, which is exactly the deterministic baseline
                        // the script's own scale math needs.
                        settings.useWideViewPort = !fitToContainer
                        settings.loadWithOverviewMode = !fitToContainer
                        // Templates embed the profile photo and custom fonts via file:// URIs
                        // (ResumeLayoutHtmlRenderer) pointing at the app's own private storage/assets,
                        // so file access must stay enabled or those resources silently fail to load.
                        settings.allowFileAccess = true
                        isVerticalScrollBarEnabled = interactive
                        isHorizontalScrollBarEnabled = interactive
                        if (fitToContainer) {
                            // Inside a LazyVerticalGrid cell, the HTML can finish loading (firing
                            // onPageFinished) before Compose has measured this AndroidView to its
                            // final aspectRatio-derived size — window.innerHeight at that moment
                            // reads as stale/zero, so FitToContainerScript's very first run computes
                            // the wrong scale with nothing to correct it afterwards (the DOM 'resize'
                            // event does not reliably fire just because the native Android View's
                            // layout bounds changed). Re-running the same script from a native
                            // layout-change callback, once real nonzero bounds are known, fixes that
                            // regardless of what the WebView's own JS resize event does or doesn't do.
                            addOnLayoutChangeListener { view, left, top, right, bottom, _, _, _, _ ->
                                if (right - left > 0 && bottom - top > 0) {
                                    (view as WebView).evaluateJavascript(FitToContainerScript, null)
                                }
                            }
                        }
                        onWebViewReady(this)
                    }
                },
                // loadData() truncates content at the first literal '#' (treated as a URL
                // fragment); templates use '#rrggbb' colors, so loadDataWithBaseURL is required.
                update = { webView ->
                    if (webView.loadedHtml != html) {
                        val payload = if (fitToContainer) HideUntilScaledStyle + html else html
                        webView.loadDataWithBaseURL(null, payload, "text/html", "UTF-8", null)
                        webView.loadedHtml = html
                    }
                },
                // Without this, a WebView scrolled out of a LazyVerticalGrid/LazyRow (template
                // thumbnail carousels, "Choose Template" grid) is detached from its parent but
                // never destroyed — each one leaks its underlying Chromium renderer state for the
                // rest of the process's life. On a screen with two dozen+ thumbnail cards, that
                // pile-up was enough to exhaust some internal WebView/compositor resource, and the
                // symptom wasn't a crash but silent partial rendering: later cards' background
                // colors would paint but their text layer never would, and the affected WebView
                // stopped responding to touch (so tapping the card did nothing). destroy() releases
                // that instance's native resources immediately instead of waiting on GC.
                onRelease = { it.destroy() }
            )
        }
    }
}

// AndroidView(WebView) doesn't render in Android Studio's static preview — this only shows the
// loading-spinner (html = null) state, but keeps the file consistent with the rest of the app.
@Preview(showBackground = true)
@Composable
private fun ResumeWebPreviewLoadingPreview() {
    ResumeBuilderTheme {
        ResumeWebPreview(html = null, modifier = Modifier.fillMaxWidth().height(300.dp))
    }
}
