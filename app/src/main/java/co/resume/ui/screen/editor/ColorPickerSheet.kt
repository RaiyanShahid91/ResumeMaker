package co.resume.ui.screen.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.domain.export.ResumePalettes

/**
 * Compact inline color-swatch row (default + every [ResumePalettes]) used inside a template
 * preview dialog so the user can try an accent color against *that specific template* before
 * committing to it. `null` means "template default" — no custom accent color persisted.
 */
@Composable
fun InlineColorSwatchRow(
    selectedHex: String?,
    onColorSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            if (selectedHex == null) 3.dp else 1.dp,
                            if (selectedHex == null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape
                        )
                        .clickable { onColorSelected(null) }
                ) {
                    if (selectedHex == null) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    stringResource(R.string.color_picker_default),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(ResumePalettes.all) { palette ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(parseHex(palette.primary))
                        .then(
                            if (selectedHex == palette.primary) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            else Modifier
                        )
                        .clickable { onColorSelected(palette.primary) }
                ) {
                    if (selectedHex == palette.primary) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(palette.name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun parseHex(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}
