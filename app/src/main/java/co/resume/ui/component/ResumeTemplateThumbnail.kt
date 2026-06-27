package co.resume.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Renders [html] as a resume thumbnail scaled to fit [modifier]'s bounds.
 * loadWithOverviewMode=true in ResumeWebPreview makes the WebView auto-scale
 * the content to fill the view width, so no graphicsLayer tricks are needed.
 */
@Composable
fun ResumeTemplateThumbnail(html: String?, modifier: Modifier = Modifier) {
    ResumeWebPreview(
        html = html,
        interactive = false,
        modifier = modifier
    )
}
