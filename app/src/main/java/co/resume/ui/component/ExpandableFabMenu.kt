package co.resume.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme

/** One row inside an [ExpandableFabMenu]'s expanded option list. */
data class FabMenuOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

/**
 * A single [FloatingActionButton] that expands upward into a labeled stack of [options] when
 * tapped — the same "tap main FAB, reveal mini FAB options with labels" interaction already
 * used for the resume/cover-letter download menu, generalized so other entry points (e.g. the
 * dashboard's "create" FAB) can reuse it with their own option list and toggle icon.
 */
@Composable
fun ExpandableFabMenu(
    expanded: Boolean,
    onToggle: () -> Unit,
    options: List<FabMenuOption>,
    modifier: Modifier = Modifier,
    toggleIcon: @Composable () -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    FabMenuOptionRow(option = option)
                }
                Spacer(Modifier.height(4.dp))
            }
        }
        FloatingActionButton(onClick = onToggle) {
            toggleIcon()
        }
    }
}

@Composable
private fun FabMenuOptionRow(option: FabMenuOption) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp
        ) {
            Text(
                option.label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }
        SmallFloatingActionButton(onClick = option.onClick) {
            Icon(option.icon, contentDescription = option.label)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableFabMenuPreview() {
    ResumeBuilderTheme {
        ExpandableFabMenu(
            expanded = true,
            onToggle = {},
            options = listOf(
                FabMenuOption(Icons.Filled.PictureAsPdf, "PDF", {}),
                FabMenuOption(Icons.Filled.PictureAsPdf, "Word", {})
            ),
            toggleIcon = { Icon(Icons.Filled.Close, contentDescription = null) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpandableFabMenuCollapsedPreview() {
    ResumeBuilderTheme {
        ExpandableFabMenu(
            expanded = false,
            onToggle = {},
            options = emptyList(),
            toggleIcon = { Icon(Icons.Filled.Add, contentDescription = null) }
        )
    }
}
