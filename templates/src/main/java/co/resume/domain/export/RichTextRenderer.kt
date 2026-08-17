package co.resume.domain.export

// Templates have no ul/ol/li rules of their own (bullets/numbers didn't exist before this
// feature) — one global, low-specificity rule covers every template without editing ~20 HTML files.
const val RichTextListStyle = "<style>ul,ol{margin:6px 0;padding-left:20px;}li{margin:2px 0;}</style>"

/** A run of text with formatting flags, the unit the layout engine measures and wraps. */
data class RichSpan(val text: String, val bold: Boolean = false, val italic: Boolean = false)

/** One paragraph or list item's worth of spans — what the layout engine treats as a wrappable unit. */
sealed class RichBlock {
    data class Paragraph(val spans: List<RichSpan>) : RichBlock()
    data class BulletItem(val spans: List<RichSpan>) : RichBlock()
    data class NumberedItem(val index: Int, val spans: List<RichSpan>) : RichBlock()
}

/**
 * Converts the lightweight markdown-style markup produced by the editor's rich-text field
 * (`**bold**`, `*italic*`, `- bullet` lines, `1. numbered` lines) into real HTML, since the
 * template renderer already injects field values into the resume HTML unescaped. Plain lines fall
 * back to the same newline-to-`<br>` behavior as before.
 */
object RichTextRenderer {
    private val bulletPrefix = Regex("^-\\s+")
    private val numberPrefix = Regex("^\\d+\\.\\s+")
    private val boldMarker = Regex("\\*\\*(.+?)\\*\\*")
    private val italicMarker = Regex("\\*(.+?)\\*")

    fun toHtml(raw: String): String {
        val lines = escapeHtml(raw).split("\n")
        val out = StringBuilder()
        var i = 0
        while (i < lines.size) {
            when {
                bulletPrefix.containsMatchIn(lines[i]) -> {
                    out.append("<ul>")
                    while (i < lines.size && bulletPrefix.containsMatchIn(lines[i])) {
                        out.append("<li>").append(inline(bulletPrefix.replaceFirst(lines[i], ""))).append("</li>")
                        i++
                    }
                    out.append("</ul>")
                }
                numberPrefix.containsMatchIn(lines[i]) -> {
                    out.append("<ol>")
                    while (i < lines.size && numberPrefix.containsMatchIn(lines[i])) {
                        out.append("<li>").append(inline(numberPrefix.replaceFirst(lines[i], ""))).append("</li>")
                        i++
                    }
                    out.append("</ol>")
                }
                else -> {
                    out.append(inline(lines[i]))
                    i++
                    val nextIsList = i < lines.size &&
                        (bulletPrefix.containsMatchIn(lines[i]) || numberPrefix.containsMatchIn(lines[i]))
                    if (i < lines.size && !nextIsList) out.append("<br>")
                }
            }
        }
        return out.toString()
    }

    private fun inline(text: String): String {
        val bolded = boldMarker.replace(text) { "<b>${it.groupValues[1]}</b>" }
        return italicMarker.replace(bolded) { "<i>${it.groupValues[1]}</i>" }
    }

    private fun escapeHtml(value: String): String =
        value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    /**
     * Parses the same markup into a span-annotated block model instead of HTML, so the layout
     * engine's entry measurement can wrap/paginate rich text itself instead of delegating to a
     * browser — the model the PDFBox renderer needs to consume directly.
     */
    fun parse(raw: String): List<RichBlock> {
        val lines = raw.split("\n")
        val out = mutableListOf<RichBlock>()
        var i = 0
        var numberIndex = 1
        while (i < lines.size) {
            when {
                bulletPrefix.containsMatchIn(lines[i]) -> {
                    out.add(RichBlock.BulletItem(inlineSpans(bulletPrefix.replaceFirst(lines[i], ""))))
                    i++
                }
                numberPrefix.containsMatchIn(lines[i]) -> {
                    out.add(RichBlock.NumberedItem(numberIndex, inlineSpans(numberPrefix.replaceFirst(lines[i], ""))))
                    numberIndex++
                    i++
                }
                lines[i].isBlank() -> i++
                else -> {
                    out.add(RichBlock.Paragraph(inlineSpans(lines[i])))
                    i++
                }
            }
        }
        return out
    }

    private fun inlineSpans(text: String): List<RichSpan> {
        val spans = mutableListOf<RichSpan>()
        var cursor = 0
        val combined = Regex("\\*\\*(.+?)\\*\\*|\\*(.+?)\\*")
        for (match in combined.findAll(text)) {
            if (match.range.first > cursor) {
                spans.add(RichSpan(text.substring(cursor, match.range.first)))
            }
            val bold = match.groupValues[1].isNotEmpty()
            val body = if (bold) match.groupValues[1] else match.groupValues[2]
            spans.add(RichSpan(body, bold = bold, italic = !bold))
            cursor = match.range.last + 1
        }
        if (cursor < text.length) spans.add(RichSpan(text.substring(cursor)))
        return if (spans.isEmpty()) listOf(RichSpan(text)) else spans
    }
}
