package co.resume.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** One line of a live validation checklist (password rules, email format, …) — a small gray
 *  dot that swaps for a green check the instant its condition is met, so the user sees what's
 *  still missing while typing instead of finding out only after submitting. */
@Composable
fun RequirementRow(label: String, met: Boolean) {
    val color = if (met) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        AnimatedContent(targetState = met, label = "requirement_icon") { isMet ->
            Icon(
                if (isMet) Icons.Filled.Check else Icons.Filled.Circle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (isMet) 14.dp else 6.dp)
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
