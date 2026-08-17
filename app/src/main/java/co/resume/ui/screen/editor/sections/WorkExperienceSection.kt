package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.rememberDatePickerState
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
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.data.local.entity.WorkExperienceEntity
import co.resume.ui.component.AiActionButton
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppTextField
import co.resume.ui.component.DragHandle
import co.resume.ui.component.DragReorderColumn
import co.resume.ui.component.OneTimeCoachMark
import co.resume.ui.component.RichTextField
import co.resume.ui.component.SwipeEditableCard
import co.resume.ui.theme.ResumeBuilderTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkExperienceSection(items: List<WorkExperienceEntity>, resumeId: Long, onSave: (List<WorkExperienceEntity>) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fillMandatoryMsg = stringResource(R.string.msg_fill_mandatory)
    val deletedMsg = stringResource(R.string.msg_deleted_successfully)
    val aiFailedMsg = stringResource(R.string.work_msg_ai_failed)
    val savedMsg = stringResource(R.string.work_msg_saved)
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var jobTitle by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var durationFrom by remember { mutableStateOf("") }
    var durationTo by remember { mutableStateOf("") }
    var ongoing by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var showDatePickerFor by remember { mutableStateOf<Int?>(null) }
    var isImprovingDesc by remember { mutableStateOf(false) }

    fun openAdd() {
        editingIndex = -1
        jobTitle = ""; company = ""; durationFrom = ""; durationTo = ""; ongoing = false; description = ""
        showDialog = true
    }

    fun openEdit(index: Int) {
        val item = items[index]
        editingIndex = index
        jobTitle = item.jobTitle; company = item.company; durationFrom = item.durationFrom
        ongoing = item.durationTo == "ongoing"
        durationTo = if (ongoing) "" else item.durationTo
        description = item.description
        showDialog = true
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = { openAdd() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.work_cd_add))
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
                            onSave(reindex(updated))
                        }
                    ) { index, item, dragHandleModifier ->
                        SwipeEditableCard(
                            onEdit = { openEdit(index) },
                            onDelete = {
                                onSave(reindex(items.toMutableList().apply { removeAt(index) }))
                                Toast.makeText(context, deletedMsg, Toast.LENGTH_SHORT).show()
                            },
                            dragHandleModifier = dragHandleModifier,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                                Text(item.jobTitle, style = MaterialTheme.typography.titleMedium)
                                Text(item.company, style = MaterialTheme.typography.bodyMedium)
                                Text("${item.durationFrom} - ${item.durationTo}", style = MaterialTheme.typography.bodySmall)
                                if (item.description.isNotBlank()) {
                                    Text(item.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
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
                    if (editingIndex >= 0) stringResource(R.string.work_title_edit) else stringResource(R.string.work_title_add),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                AppTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text(stringResource(R.string.work_label_job_title)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                AppTextField(value = company, onValueChange = { company = it }, label = { Text(stringResource(R.string.work_label_company)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                AppTextField(
                    value = durationFrom, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.work_label_from)) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    trailingIcon = { TextButton(onClick = { showDatePickerFor = 0 }) { Text(stringResource(R.string.btn_pick)) } }
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Checkbox(checked = ongoing, onCheckedChange = { ongoing = it; if (it) durationTo = "" })
                    Text(stringResource(R.string.work_label_ongoing))
                }
                if (!ongoing) {
                    AppTextField(
                        value = durationTo, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.work_label_to)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        trailingIcon = { TextButton(onClick = { showDatePickerFor = 1 }) { Text(stringResource(R.string.btn_pick)) } }
                    )
                }
                RichTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.work_label_description)) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    trailingIcon = if (AiClient.isConfigured) {
                        {
                            AiActionButton(
                                isGenerating = isImprovingDesc,
                                contentDescription = stringResource(R.string.work_cd_improve_ai),
                                onClick = {
                                    isImprovingDesc = true
                                    scope.launch {
                                        try {
                                            val result = ResumeAiService.improveJobDescription(jobTitle, company, description)
                                            if (result.isNotBlank()) description = result
                                        } catch (e: Exception) {
                                            Toast.makeText(context, co.resume.ai.aiErrorMessage(e, aiFailedMsg), Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isImprovingDesc = false
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
                            val effectiveTo = if (ongoing) "ongoing" else durationTo
                            if (jobTitle.isBlank() || company.isBlank() || durationFrom.isBlank() || effectiveTo.isBlank()) {
                                Toast.makeText(context, fillMandatoryMsg, Toast.LENGTH_SHORT).show()
                            } else {
                                val entry = WorkExperienceEntity(
                                    id = if (editingIndex >= 0) items[editingIndex].id else 0,
                                    resumeId = resumeId,
                                    orderIndex = if (editingIndex >= 0) editingIndex else items.size,
                                    jobTitle = jobTitle, company = company, durationFrom = durationFrom,
                                    durationTo = effectiveTo, description = description
                                )
                                val updated = items.toMutableList()
                                if (editingIndex >= 0) updated[editingIndex] = entry else updated.add(entry)
                                onSave(reindex(updated))
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

    showDatePickerFor?.let { which ->
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerFor = null },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val formatted = dateFormat.format(Date(millis))
                        if (which == 0) durationFrom = formatted else durationTo = formatted
                    }
                    showDatePickerFor = null
                }) { Text(stringResource(R.string.btn_ok)) }
            },
            dismissButton = { TextButton(onClick = { showDatePickerFor = null }) { Text(stringResource(R.string.btn_cancel)) } }
        ) {
            DatePicker(state = state)
        }
    }
}

private fun reindex(items: List<WorkExperienceEntity>): List<WorkExperienceEntity> =
    items.mapIndexed { index, item -> item.copy(orderIndex = index) }

@Preview(showBackground = true)
@Composable
private fun WorkExperienceSectionPreview() {
    ResumeBuilderTheme {
        WorkExperienceSection(
            items = listOf(
                WorkExperienceEntity(
                    resumeId = 1, orderIndex = 0,
                    jobTitle = "Senior Product Designer", company = "Northwind Labs",
                    durationFrom = "2021", durationTo = "Present",
                    description = "Led design for the core onboarding flow, increasing activation by 22%."
                )
            ),
            resumeId = 1,
            onSave = {}
        )
    }
}
