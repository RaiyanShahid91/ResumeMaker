package co.resume.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.resumeai.R

/**
 * The shared row style for every editable list item across the resume editor's sections (work
 * experience, education, projects, skills, achievements, languages): tap the card to edit it,
 * swipe left to delete it, and — if [dragHandleModifier] is supplied — drag the handle icon to
 * reorder. Replaces the old pattern of separate Edit/Delete [androidx.compose.material3.IconButton]s
 * on every row, which took up real row space and wasn't how any other list in the app (Resumes,
 * Cover Letters, Documents) already worked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeEditableCard(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    dragHandleModifier: Modifier? = null,
    content: @Composable RowScope.() -> Unit
) {
    // Unlike the resume/cover-letter/document lists (which confirm before deleting, since
    // losing a whole document is a much bigger deal), these are small in-editor entries with an
    // existing "delete confirmed by nothing but its own undo-less immediacy" precedent — so the
    // swipe itself completes the delete rather than snapping back to show a confirm sheet.
    val haptics = LocalHapticFeedback.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            // Centered against the whole row's height (not top-anchored with a fixed offset) —
            // a fixed top padding on the handle only lined up with the first line of tall,
            // multi-line cards (Education, Work Experience). Short single-line rows (Skills,
            // Languages, Achievements) have far less height, so that same fixed offset pushed
            // the handle visibly below-center relative to the text next to it.
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (dragHandleModifier != null) {
                    DragHandle(modifier = dragHandleModifier.padding(start = 4.dp, end = 10.dp))
                }
                content()
            }
        }
    }
}
