package co.resume.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import co.resumeai.R

/**
 * A multi-line text field with a small Bold/Italic/Bullets/Numbers toolbar. The underlying stored
 * value is plain text with lightweight markdown-style markers (`**bold**`, `*italic*`, `- item`,
 * `1. item`) — [RichTextVisualTransformation] renders those markers styled live in the field, and
 * [co.resume.domain.export.RichTextRenderer] (templates module) converts the same markers to real
 * HTML (`<b>`/`<i>`/`<ul>`/`<ol>`) when the resume/cover letter is actually rendered, so formatting
 * applied here shows up in the exported document.
 */
@Composable
fun RichTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    minLines: Int = 3,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    var fieldValue by remember { mutableStateOf(TextFieldValue(value)) }

    // Picks up external changes to `value` (e.g. an "Improve with AI" button overwriting the
    // description) that didn't originate from this field's own onValueChange.
    LaunchedEffect(value) {
        if (value != fieldValue.text) {
            fieldValue = TextFieldValue(value, selection = TextRange(value.length))
        }
    }

    Column(modifier = modifier) {
        Row {
            IconButton(onClick = { fieldValue = RichTextEditingOps.toggleBold(fieldValue).also { onValueChange(it.text) } }) {
                Icon(Icons.Filled.FormatBold, contentDescription = stringResource(R.string.rich_text_bold))
            }
            IconButton(onClick = { fieldValue = RichTextEditingOps.toggleItalic(fieldValue).also { onValueChange(it.text) } }) {
                Icon(Icons.Filled.FormatItalic, contentDescription = stringResource(R.string.rich_text_italic))
            }
            IconButton(onClick = { fieldValue = RichTextEditingOps.toggleBullets(fieldValue).also { onValueChange(it.text) } }) {
                Icon(Icons.Filled.FormatListBulleted, contentDescription = stringResource(R.string.rich_text_bullets))
            }
            IconButton(onClick = { fieldValue = RichTextEditingOps.toggleNumbers(fieldValue).also { onValueChange(it.text) } }) {
                Icon(Icons.Filled.FormatListNumbered, contentDescription = stringResource(R.string.rich_text_numbers))
            }
        }
        OutlinedTextField(
            value = fieldValue,
            onValueChange = {
                fieldValue = it
                onValueChange(it.text)
            },
            label = label,
            placeholder = placeholder,
            minLines = minLines,
            trailingIcon = trailingIcon,
            visualTransformation = RichTextVisualTransformation,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Renders `**bold**`/`*italic*` markers styled in place, without hiding or removing them. */
object RichTextVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val builder = AnnotatedString.Builder()
        var i = 0
        while (i < raw.length) {
            when {
                raw.startsWith("**", i) -> {
                    val end = raw.indexOf("**", i + 2)
                    if (end == -1) {
                        builder.append(raw[i]); i++
                    } else {
                        builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        builder.append(raw.substring(i, end + 2))
                        builder.pop()
                        i = end + 2
                    }
                }
                raw[i] == '*' -> {
                    val end = raw.indexOf('*', i + 1)
                    if (end == -1) {
                        builder.append(raw[i]); i++
                    } else {
                        builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        builder.append(raw.substring(i, end + 1))
                        builder.pop()
                        i = end + 1
                    }
                }
                else -> {
                    builder.append(raw[i]); i++
                }
            }
        }
        // No characters added/removed — every source offset maps to itself.
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}

/** Pure text-editing operations backing [RichTextField]'s toolbar buttons. */
object RichTextEditingOps {
    private val numberPrefix = Regex("^\\d+\\.\\s+")

    fun toggleBold(value: TextFieldValue): TextFieldValue = wrapOrUnwrap(value, "**")
    fun toggleItalic(value: TextFieldValue): TextFieldValue = wrapOrUnwrap(value, "*")

    fun toggleBullets(value: TextFieldValue): TextFieldValue = toggleLines(value) { lines, range ->
        val allBulleted = range.all { lines[it].startsWith("- ") || lines[it].isBlank() }
        for (idx in range) {
            if (lines[idx].isBlank()) continue
            lines[idx] = if (allBulleted) lines[idx].removePrefix("- ") else "- ${lines[idx]}"
        }
    }

    fun toggleNumbers(value: TextFieldValue): TextFieldValue = toggleLines(value) { lines, range ->
        val allNumbered = range.all { numberPrefix.containsMatchIn(lines[it]) || lines[it].isBlank() }
        var counter = 1
        for (idx in range) {
            if (lines[idx].isBlank()) continue
            val stripped = numberPrefix.replaceFirst(lines[idx], "")
            lines[idx] = if (allNumbered) stripped else "${counter++}. $stripped"
        }
    }

    private fun wrapOrUnwrap(value: TextFieldValue, marker: String): TextFieldValue {
        val text = value.text
        val sel = value.selection
        if (sel.collapsed) {
            val newText = text.substring(0, sel.start) + marker + marker + text.substring(sel.start)
            return TextFieldValue(newText, TextRange(sel.start + marker.length))
        }
        val min = sel.min
        val max = sel.max
        val selected = text.substring(min, max)
        return if (selected.startsWith(marker) && selected.endsWith(marker) && selected.length >= marker.length * 2) {
            val stripped = selected.substring(marker.length, selected.length - marker.length)
            val newText = text.substring(0, min) + stripped + text.substring(max)
            TextFieldValue(newText, TextRange(min, min + stripped.length))
        } else {
            val newText = text.substring(0, min) + marker + selected + marker + text.substring(max)
            TextFieldValue(newText, TextRange(min, min + marker.length * 2 + selected.length))
        }
    }

    private inline fun toggleLines(
        value: TextFieldValue,
        crossinline transform: (MutableList<String>, IntRange) -> Unit
    ): TextFieldValue {
        val lines = value.text.split("\n").toMutableList()
        val range = affectedLineRange(value.text, value.selection.min, value.selection.max, lines)
        transform(lines, range)
        val newText = lines.joinToString("\n")

        var pos = 0
        var selStart = 0
        var selEnd = 0
        for ((idx, line) in lines.withIndex()) {
            val lineEnd = pos + line.length
            if (idx == range.first) selStart = pos
            if (idx == range.last) selEnd = lineEnd
            pos = lineEnd + 1
        }
        return TextFieldValue(newText, TextRange(selStart, selEnd))
    }

    private fun affectedLineRange(text: String, startOffset: Int, endOffset: Int, lines: List<String>): IntRange {
        var pos = 0
        var startLine = 0
        var endLine = 0
        for ((idx, line) in lines.withIndex()) {
            val lineEnd = pos + line.length
            if (startOffset in pos..lineEnd) startLine = idx
            if (endOffset in pos..lineEnd) endLine = idx
            pos = lineEnd + 1
        }
        return startLine..endLine
    }
}
