package co.resume.ui.component

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.resume.ui.theme.ResumeBuilderTheme

/**
 * Renders [html] as a resume thumbnail scaled to fit [modifier]'s bounds, both width (via
 * loadWithOverviewMode in ResumeWebPreview) and height (via fitToContainer, which shrinks the
 * whole page with a CSS transform so it's never clipped, however tall the resume is).
 */
@Composable
fun ResumeTemplateThumbnail(html: String?, modifier: Modifier = Modifier) {
    ResumeWebPreview(
        html = html,
        interactive = false,
        fitToContainer = true,
        modifier = modifier
    )
}

// Backed by an AndroidView(WebView), which Android Studio's static preview renderer does not
// draw — this shows the loading spinner state (html = null) rather than real HTML content.
@Preview(showBackground = true)
@Composable
private fun ResumeTemplateThumbnailPreview() {
    ResumeBuilderTheme {
        ResumeTemplateThumbnail(html = null, modifier = Modifier.fillMaxWidth().aspectRatio(0.7f))
    }
}
