package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.data.local.entity.SkillEntity
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppDialog
import co.resume.ui.component.AppTextField
import kotlinx.coroutines.launch

private val skillLevels = listOf("Beginner", "Intermediate", "Advanced", "Expert")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SkillsSection(
    items: List<SkillEntity>,
    resumeId: Long,
    designation: String = "",
    onSave: (List<SkillEntity>) -> Unit
) {
    val context = LocalContext.current
    val fillMandatoryMsg = stringResource(R.string.msg_fill_mandatory)
    val deletedMsg = stringResource(R.string.msg_deleted_successfully)
    val addedMsg = stringResource(R.string.skills_msg_added)
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var skillName by remember { mutableStateOf("") }
    var skillLevel by remember { mutableStateOf("") }
    var levelExpanded by remember { mutableStateOf(false) }
    var showSuggestSheet by remember { mutableStateOf(false) }

    fun openAdd() {
        editingIndex = -1; skillName = ""; skillLevel = ""
        showDialog = true
    }

    fun openEdit(index: Int) {
        val item = items[index]
        editingIndex = index; skillName = item.skillName; skillLevel = item.skillLevel
        showDialog = true
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (AiClient.isConfigured) {
                    SmallFloatingActionButton(
                        onClick = { showSuggestSheet = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = stringResource(R.string.skills_cd_suggest_ai))
                    }
                }
                FloatingActionButton(onClick = { openAdd() }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.skills_cd_add))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.msg_nothing_to_show), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    itemsIndexed(items) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.skillName, style = MaterialTheme.typography.titleMedium)
                                    Text(item.skillLevel, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = {
                                    if (index > 0) {
                                        val updated = items.toMutableList()
                                        val tmp = updated[index - 1]; updated[index - 1] = updated[index]; updated[index] = tmp
                                        onSave(reindex(updated))
                                    }
                                }, enabled = index > 0) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = stringResource(R.string.cd_move_up)) }
                                IconButton(onClick = {
                                    if (index < items.size - 1) {
                                        val updated = items.toMutableList()
                                        val tmp = updated[index + 1]; updated[index + 1] = updated[index]; updated[index] = tmp
                                        onSave(reindex(updated))
                                    }
                                }, enabled = index < items.size - 1) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = stringResource(R.string.cd_move_down)) }
                                IconButton(onClick = { openEdit(index) }) { Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit)) }
                                IconButton(onClick = {
                                    onSave(reindex(items.toMutableList().apply { removeAt(index) }))
                                    Toast.makeText(context, deletedMsg, Toast.LENGTH_SHORT).show()
                                }) { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete)) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AppDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingIndex >= 0) stringResource(R.string.skills_title_edit) else stringResource(R.string.skills_title_add)) },
            text = {
                Column {
                    AppTextField(value = skillName, onValueChange = { skillName = it }, label = { Text(stringResource(R.string.skills_label_name)) }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    ExposedDropdownMenuBox(expanded = levelExpanded, onExpandedChange = { levelExpanded = it }) {
                        AppTextField(
                            value = skillLevel, onValueChange = {}, readOnly = true, label = { Text(stringResource(R.string.skills_label_level)) },
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = levelExpanded, onDismissRequest = { levelExpanded = false }) {
                            skillLevels.forEach { level ->
                                DropdownMenuItem(text = { Text(level) }, onClick = { skillLevel = level; levelExpanded = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (skillName.isBlank() || skillLevel.isBlank()) {
                        Toast.makeText(context, fillMandatoryMsg, Toast.LENGTH_SHORT).show()
                    } else {
                        val entry = SkillEntity(
                            id = if (editingIndex >= 0) items[editingIndex].id else 0,
                            resumeId = resumeId,
                            orderIndex = if (editingIndex >= 0) editingIndex else items.size,
                            skillName = skillName, skillLevel = skillLevel
                        )
                        val updated = items.toMutableList()
                        if (editingIndex >= 0) updated[editingIndex] = entry else updated.add(entry)
                        onSave(reindex(updated))
                        showDialog = false
                        Toast.makeText(context, addedMsg, Toast.LENGTH_SHORT).show()
                    }
                }) { Text(stringResource(R.string.btn_save)) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.btn_cancel)) } }
        )
    }

    if (showSuggestSheet) {
        AiSkillSuggestSheet(
            designation = designation,
            existingSkillNames = items.map { it.skillName },
            resumeId = resumeId,
            nextOrderIndex = items.size,
            onAddSkills = { newSkills ->
                onSave(reindex(items + newSkills))
                Toast.makeText(context, context.getString(R.string.skills_msg_count_added, newSkills.size), Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showSuggestSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AiSkillSuggestSheet(
    designation: String,
    existingSkillNames: List<String>,
    resumeId: Long,
    nextOrderIndex: Int,
    onAddSkills: (List<SkillEntity>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val enterRoleMsg = stringResource(R.string.skills_msg_enter_role)
    val aiFailedMsg = stringResource(R.string.skills_msg_ai_failed)
    val selectOneMsg = stringResource(R.string.skills_msg_select_one)

    var role by remember { mutableStateOf(designation) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isLoading by remember { mutableStateOf(false) }

    AppBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(
                    "  " + stringResource(R.string.skills_ai_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.skills_ai_sheet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text(stringResource(R.string.skills_label_role)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                TextButton(
                    onClick = {
                        if (role.isBlank()) {
                            Toast.makeText(context, enterRoleMsg, Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        isLoading = true
                        selected = emptySet()
                        scope.launch {
                            try {
                                suggestions = ResumeAiService.suggestSkills(role)
                            } catch (e: Exception) {
                                Toast.makeText(context, aiFailedMsg, Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    else Text(stringResource(R.string.skills_btn_generate))
                }
            }

            if (suggestions.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.skills_tap_to_select), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    suggestions.forEach { skill ->
                        val alreadyAdded = existingSkillNames.any { it.equals(skill, ignoreCase = true) }
                        FilterChip(
                            selected = skill in selected,
                            onClick = {
                                if (!alreadyAdded) {
                                    selected = if (skill in selected) selected - skill else selected + skill
                                }
                            },
                            label = { Text(if (alreadyAdded) "$skill ✓" else skill) },
                            enabled = !alreadyAdded
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                TextButton(
                    onClick = {
                        val newSkills = selected.mapIndexed { i, name ->
                            SkillEntity(resumeId = resumeId, orderIndex = nextOrderIndex + i, skillName = name, skillLevel = "Intermediate")
                        }
                        if (newSkills.isEmpty()) {
                            Toast.makeText(context, selectOneMsg, Toast.LENGTH_SHORT).show()
                        } else {
                            onAddSkills(newSkills)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selected.isEmpty()) stringResource(R.string.skills_btn_add_selected_none)
                        else stringResource(R.string.skills_btn_add_selected_count, selected.size)
                    )
                }
            }
        }
    }
}

private fun reindex(items: List<SkillEntity>): List<SkillEntity> =
    items.mapIndexed { index, item -> item.copy(orderIndex = index) }
