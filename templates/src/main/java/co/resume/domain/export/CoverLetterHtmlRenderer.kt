package co.resume.domain.export

import android.content.Context
import co.resume.data.local.entity.CoverLetterEntity

/**
 * Renders a [CoverLetterEntity] into the HTML contract expected by each cover-letter template's
 * `index.html`. Every template file (see each folder under `cover_letter_templates`) is authored as a
 * bare `<html><body>...</body></html>` block followed by its own trailing `<script>` and `<style>`
 * — those files were never meant to be loaded standalone; historically this renderer just
 * concatenated them raw after a handful of `sample_*` placeholder `<p>` tags, producing a single
 * loaded "document" with multiple `<html>`/`<body>` tags and a `<script>`/`<style>` sitting after
 * `</html>`. Chromium's forgiving HTML5 parser papers over that (which is why it visibly worked),
 * but it's invalid markup and fragile — outerHTML capture (used by the Word export) in particular
 * has no well-defined single root to serialize. [TemplateFragment] pulls each template's body/
 * script/style out of that raw file once, and [render] assembles them into one real, valid
 * `<!DOCTYPE html>` document instead.
 */
object CoverLetterHtmlRenderer {

    private data class TemplateFragment(val bodyHtml: String, val scriptJs: String, val styleCss: String)

    private val fragmentCache = mutableMapOf<String, TemplateFragment>()

    private fun fragment(context: Context, assetFolder: String): TemplateFragment = fragmentCache.getOrPut(assetFolder) {
        val raw = context.assets.open("cover_letter_templates/$assetFolder/index.html").bufferedReader().use { it.readText() }
        TemplateFragment(
            bodyHtml = extractBetween(raw, "<body>", "</body>"),
            scriptJs = extractBetween(raw, "<script>", "</script>"),
            styleCss = extractBetween(raw, "<style>", "</style>"),
        )
    }

    private fun extractBetween(source: String, startTag: String, endTag: String): String {
        val start = source.indexOf(startTag).let { if (it == -1) return "" else it + startTag.length }
        val end = source.indexOf(endTag, start).let { if (it == -1) source.length else it }
        return source.substring(start, end)
    }

    fun render(context: Context, letter: CoverLetterEntity): String {
        val template = CoverLetterTemplateCatalog.resolve(letter.templateId)
        val fragment = fragment(context, template.assetFolder)

        val placeholders = StringBuilder()
        placeholders.append(p("sample_sender_name", letter.senderName))
        placeholders.append(p("sample_sender_contact", listOf(letter.senderEmail, letter.senderPhone).filter { it.isNotBlank() }.joinToString(" | ")))
        placeholders.append(p("sample_date", letter.date))
        placeholders.append(p("sample_recipient_block", listOf(letter.recipientName, letter.companyName).filter { it.isNotBlank() }.joinToString("<br>")))
        placeholders.append(p("sample_job_title", letter.jobTitle))
        placeholders.append(p("sample_salutation", letter.salutation))
        placeholders.append(p("sample_body", RichTextRenderer.toHtml(letter.bodyText)))
        placeholders.append(p("sample_closing", nl2br(letter.closing)))

        val palette = letter.accentColorHex?.let { ResumePalettes.fromHex(it) }
        val colorOne = palette?.primary ?: letter.accentColorHex ?: "#1d4ed8"
        val colorTwo = palette?.secondary ?: "#93c5fd"

        return """
            <!DOCTYPE html>
            <html>
            <head>
            <meta charset="utf-8">
            <style>${fragment.styleCss}</style>
            <style>:root{--color-one: $colorOne;--color-two: $colorTwo;}</style>
            $RichTextListStyle
            <style>
                /* Page-card look, matching the resume preview's `.page` treatment — added last so
                   it wins the cascade, but only for the properties it actually sets. Each template
                   already defines its own body margin for internal text breathing room (varies per
                   template: some rely on full-bleed color bands with margin:0 instead, others use a
                   nonzero margin like `body{margin:32px}` designed to visually blend with a plain
                   white canvas around the page). `html`'s background MUST match `body`'s (white),
                   not a tray gray — a gray `html` background would otherwise show through any
                   template's own top/bottom body margin as an unwanted gray band, both on screen and
                   in the PDF/Word export capture below (which screenshot this exact DOM, not just
                   the Compose-level tray). The on-screen "page on a tray" look instead comes entirely
                   from CoverLetterPagePreview's Compose-level gray padding OUTSIDE the WebView, which
                   this html/css never needs to duplicate. */
                html { background: #ffffff; overflow-x: hidden; }
                body { background: #ffffff; width: 210mm; min-height: 297mm; margin-left: auto; margin-right: auto; overflow-x: hidden; }
            </style>
            <!-- Kept as its own togglable stylesheet (see DocumentExporter's Hide/ShowShadowScript)
                 so PDF/Word export can strip the drop shadow before capturing — a shadow baked into
                 an exported document reads as a stray gray smudge around the page rather than the
                 on-screen depth cue it's meant to be. -->
            <style id="__page_shadow_style">body { box-shadow: 0 2px 10px rgba(0,0,0,0.15); }</style>
            </head>
            <body>
            $placeholders
            ${fragment.bodyHtml}
            <script>${fragment.scriptJs}</script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun p(cls: String, value: String) = "<p class='$cls'>$value</p>"

    private fun nl2br(value: String) = value.replace("\n", "<br>")
}
