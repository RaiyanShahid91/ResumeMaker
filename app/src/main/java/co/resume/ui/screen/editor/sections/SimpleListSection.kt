package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import co.resume.ui.component.AppDialog
import co.resume.ui.component.AppTextField
import kotlinx.coroutines.launch

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
                LazyColumn(contentPadding = PaddingValues(20.dp)) {
                    itemsIndexed(items) { index, value ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(value, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    if (index > 0) {
                                        val updated = items.toMutableList()
                                        val tmp = updated[index - 1]
                                        updated[index - 1] = updated[index]
                                        updated[index] = tmp
                                        onSave(updated)
                                    }
                                }, enabled = index > 0) {
                                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = stringResource(R.string.cd_move_up))
                                }
                                IconButton(onClick = {
                                    if (index < items.size - 1) {
                                        val updated = items.toMutableList()
                                        val tmp = updated[index + 1]
                                        updated[index + 1] = updated[index]
                                        updated[index] = tmp
                                        onSave(updated)
                                    }
                                }, enabled = index < items.size - 1) {
                                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = stringResource(R.string.cd_move_down))
                                }
                                IconButton(onClick = {
                                    editingIndex = index
                                    fieldValue = value
                                    showDialog = true
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.cd_edit))
                                }
                                IconButton(onClick = {
                                    val updated = items.toMutableList().apply { removeAt(index) }
                                    onSave(updated)
                                    Toast.makeText(context, deletedMsg, Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_delete))
                                }
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
            title = { Text(if (editingIndex >= 0) editTitle else addTitle) },
            text = {
                Column {
                    AppTextField(
                        value = fieldValue,
                        onValueChange = { fieldValue = it },
                        placeholder = { Text(placeholder) },
                        minLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = if (onAiGenerate != null) {
                            {
                                if (isGenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp).padding(2.dp), strokeWidth = 2.dp)
                                } else {
                                    IconButton(onClick = {
                                        isGenerating = true
                                        scope.launch {
                                            try {
                                                val result = onAiGenerate(fieldValue)
                                                if (result.isNotBlank()) fieldValue = result
                                            } catch (e: Exception) {
                                                Toast.makeText(context, aiFailedMsg, Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isGenerating = false
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = stringResource(R.string.proj_cd_generate_ai), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        } else null
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (fieldValue.isBlank()) {
                        Toast.makeText(context, fillMandatoryMsg, Toast.LENGTH_SHORT).show()
                    } else {
                        val updated = items.toMutableList()
                        if (editingIndex >= 0) updated[editingIndex] = fieldValue else updated.add(fieldValue)
                        onSave(updated)
                        showDialog = false
                        Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                    }
                }) { Text(stringResource(R.string.btn_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }
}
