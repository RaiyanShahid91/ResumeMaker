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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var fieldValue by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingIndex = -1
                fieldValue = ""
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add $fieldLabel")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nothing to show", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up")
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
                                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down")
                                }
                                IconButton(onClick = {
                                    editingIndex = index
                                    fieldValue = value
                                    showDialog = true
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    val updated = items.toMutableList().apply { removeAt(index) }
                                    onSave(updated)
                                    Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingIndex >= 0) "Edit $fieldLabel" else "Add $fieldLabel") },
            text = {
                Column {
                    OutlinedTextField(
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
                                                Toast.makeText(context, "AI failed. Try again.", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isGenerating = false
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.AutoAwesome, contentDescription = "Generate with AI", tint = MaterialTheme.colorScheme.primary)
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
                        Toast.makeText(context, "Please fill the mandatory details.", Toast.LENGTH_SHORT).show()
                    } else {
                        val updated = items.toMutableList()
                        if (editingIndex >= 0) updated[editingIndex] = fieldValue else updated.add(fieldValue)
                        onSave(updated)
                        showDialog = false
                        Toast.makeText(context, "Saved successfully.", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}
