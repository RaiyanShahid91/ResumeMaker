package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AiActionButton
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppTextField
import co.resume.ui.component.DragHandle
import co.resume.ui.component.DragReorderColumn
import co.resume.ui.component.OneTimeCoachMark
import co.resume.ui.component.SwipeEditableCard
import co.resume.ui.theme.ResumeBuilderTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleListSection(
    items: List<String>,
    fieldLabel: String,
    placeholder: String,
    onAiGenerate: (suspend (String) -> String)? = null,
    onSave: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fillMandatoryMsg = stringResource(R.string.msg_fill_mandatory)
    val deletedMsg = stringResource(R.string.msg_deleted_successfully)
    val aiFailedMsg = stringResource(R.string.work_msg_ai_failed)
    val savedMsg = stringResource(R.string.list_msg_saved)
    val addCd = stringResource(R.string.list_cd_add, fieldLabel)
    val editTitle = stringResource(R.string.list_title_edit, fieldLabel)
    val addTitle = stringResource(R.string.list_title_add, fieldLabel)
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var fieldValue by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingIndex = -1
                fieldValue = ""
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = addCd)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.msg_nothing_to_show), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    OneTimeCoachMark(
                        id = "coach_tap_edit_swipe_delete",
                        message = stringResource(R.string.coach_tap_edit_swipe_delete),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if (items.size > 1) {
                        OneTimeCoachMark(
                            id = "coach_drag_reorder",
                            message = stringResource(R.string.coach_drag_reorder),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                    DragReorderColumn(
                        items = items,
                        onMove = { from, to ->
                            val updated = items.toMutableList()
                            val moved = updated.removeAt(from)
                            updated.add(to, moved)
                            onSave(updated)
                        }
                    ) { index, value, dragHandleModifier ->
                        SwipeEditableCard(
                            onEdit = {
                                editingIndex = index
                                fieldValue = value
                                showDialog = true
                            },
                            onDelete = {
                                val updated = items.toMutableList().apply { removeAt(index) }
                                onSave(updated)
                                Toast.makeText(context, deletedMsg, Toast.LENGTH_SHORT).show()
                            },
                            dragHandleModifier = dragHandleModifier,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Text(value, modifier = Modifier.weight(1f).padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AppBottomSheet(
            sheetState = rememberModalBottomSheetState(),
            onDismissRequest = { showDialog = false }
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                Text(
                    if (editingIndex >= 0) editTitle else addTitle,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                AppTextField(
                    value = fieldValue,
                    onValueChange = { fieldValue = it },
                    placeholder = { Text(placeholder) },
                    minLines = 1,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    trailingIcon = if (onAiGenerate != null) {
                        {
                            AiActionButton(
                                isGenerating = isGenerating,
                                contentDescription = stringResource(R.string.proj_cd_generate_ai),
                                onClick = {
                                    isGenerating = true
                                    scope.launch {
                                        try {
                                            val result = onAiGenerate(fieldValue)
                                            if (result.isNotBlank()) fieldValue = result
                                        } catch (e: Exception) {
                                            Toast.makeText(context, co.resume.ai.aiErrorMessage(e, aiFailedMsg), Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isGenerating = false
                                        }
                                    }
                                }
                            )
                        }
                    } else null
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { showDialog = false }, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                    Button(
                        onClick = {
                            if (fieldValue.isBlank()) {
                                Toast.makeText(context, fillMandatoryMsg, Toast.LENGTH_SHORT).show()
                            } else {
                                val updated = items.toMutableList()
                                if (editingIndex >= 0) updated[editingIndex] = fieldValue else updated.add(fieldValue)
                                onSave(updated)
                                showDialog = false
                                Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text(stringResource(R.string.btn_save)) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SimpleListSectionPreview() {
    ResumeBuilderTheme {
        SimpleListSection(
            items = listOf("UI/UX Design", "Figma", "Design Systems"),
            fieldLabel = "Skill",
            placeholder = "e.g. Figma",
            onSave = {}
        )
    }
}
