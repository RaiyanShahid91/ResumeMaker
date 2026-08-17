package co.resume.domain.export

import co.resume.domain.engine.PositionedBlock
import co.resume.domain.engine.ResumeLayout
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serializes the canonical ResumeLayout IR into absolutely-positioned HTML/CSS, replacing
 * ResumeHtmlRenderer's per-template string concatenation. Every TextLine/Rule/Image block becomes
 * a positioned element at its mm coordinates — the WebView preview keeps free text selection/CJK
 * font fallback while still deriving from the same layout the PDF exporter consumes, instead
 * of 18 separately hand-authored templates. Chromium's CSS box model is still an independent
 * layout engine from PDFBox, so this gives best-effort (not physically guaranteed) parity with
 * the exported PDF's page counts.
 */
@Singleton
class ResumeLayoutHtmlRenderer @Inject constructor() {

    fun render(layout: ResumeLayout): String {
        val fontFaces = collectFonts(layout).joinToString("\n") { (family, path) ->
            "@font-face { font-family: '$family'; src: url('file:///android_asset/$path'); }"
        }
        val pages = layout.pages.joinToString("\n") { page -> renderPage(page) }
        return """
            <!DOCTYPE html>
            <html><head><meta charset="utf-8">
            <style>
                $fontFaces
                * { margin:0; padding:0; box-sizing:border-box; }
                /* Width must track the screen with no horizontal scroll — loadWithOverviewMode
                   already fits the page width on load and zoom is disabled (see ResumeWebPreview),
                   but this is the hard backstop in case any content is ever wider than the .page
                   itself; only vertical scrolling should ever be possible. */
                html, body { overflow-x: hidden; }
                body { background:#e5e7eb; }
                .page {
                    position: relative;
                    width: ${layout.pageSizeMm.widthMm}mm;
                    height: ${layout.pageSizeMm.heightMm}mm;
                    background: #ffffff;
                    margin: 0 auto 8mm auto;
                    overflow: hidden;
                }
                /* overflow:hidden is a safety net, not the primary wrap mechanism — TextMeasurer
                   already wraps each line to fit its column (see WRAP_SAFETY_MARGIN_MM). Without it,
                   any residual mismatch between PDFBox's measurement and Chromium's actual glyph
                   rendering has nothing stopping it from visibly bleeding into the next column, since
                   white-space:pre disables wrapping entirely. */
                .block { position: absolute; white-space: pre; overflow: hidden; }
                .rule { position: absolute; }
                .rect { position: absolute; }
                .photo { position: absolute; object-fit: cover; }
            </style>
            </head><body>
            $pages
            </body></html>
        """.trimIndent()
    }

    private fun renderPage(page: co.resume.domain.engine.PageLayout): String {
        val blocks = page.blocks + listOfNotNull(page.footer)
        val html = blocks.joinToString("\n") { renderBlock(it) }
        return "<div class=\"page\">$html</div>"
    }

    private fun renderBlock(block: PositionedBlock): String = when (block) {
        is PositionedBlock.TextLine -> {
            val weight = if (block.style.bold) "bold" else "normal"
            val fontStyle = if (block.style.italic) "italic" else "normal"
            val decoration = if (block.style.underline) "underline" else "none"
            """<div class="block" style="left:${block.xMm}mm;top:${block.yMm}mm;width:${block.widthMm}mm;
                font-family:'${block.fontFamily}';font-size:${block.sizePt}pt;font-weight:$weight;
                font-style:$fontStyle;text-decoration:$decoration;color:${block.color.hex};">${escape(block.text)}</div>"""
        }
        is PositionedBlock.Rule -> """<div class="rule" style="left:${block.xMm}mm;top:${block.yMm}mm;
                width:${block.widthMm}mm;border-top:${block.strokePt}pt solid ${block.color.hex};"></div>"""
        is PositionedBlock.Rect -> {
            val fill = block.fill?.hex ?: "transparent"
            """<div class="rect" style="left:${block.boundsMm.xMm}mm;top:${block.boundsMm.yMm}mm;
                width:${block.boundsMm.widthMm}mm;height:${block.boundsMm.heightMm}mm;
                background:$fill;border-radius:${block.cornerRadiusMm}mm;"></div>"""
        }
        is PositionedBlock.Image -> {
            val radius = if (block.clip == co.resume.domain.engine.ClipShape.CIRCLE) "50%" else if (block.clip == co.resume.domain.engine.ClipShape.ROUNDED_RECT) "10%" else "0"
            """<img class="photo" src="file://${block.bitmapPath}" style="left:${block.boundsMm.xMm}mm;
                top:${block.boundsMm.yMm}mm;width:${block.boundsMm.widthMm}mm;height:${block.boundsMm.heightMm}mm;border-radius:$radius;"/>"""
        }
        is PositionedBlock.IconGlyph -> """<div class="rect" style="left:${block.xMm}mm;top:${block.yMm}mm;
                width:${block.sizeMm}mm;height:${block.sizeMm}mm;background:${block.color.hex};border-radius:50%;"></div>"""
    }

    private fun collectFonts(layout: ResumeLayout): Set<Pair<String, String>> =
        layout.pages.flatMap { it.blocks }.filterIsInstance<PositionedBlock.TextLine>()
            .map { it.fontFamily to it.fontAssetPath }.toSet()

    private fun escape(text: String): String =
        text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
