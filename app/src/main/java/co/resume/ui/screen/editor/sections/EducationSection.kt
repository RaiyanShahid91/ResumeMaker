package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.resume.data.local.entity.EducationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val monthYearFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationSection(items: List<EducationEntity>, resumeId: Long, onSave: (List<EducationEntity>) -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var editingIndex by remember { mutableStateOf(-1) }
    var course by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }
    var durationFrom by remember { mutableStateOf("") }
    var durationTo by remember { mutableStateOf("") }
    var showDatePickerFor by remember { mutableStateOf<Int?>(null) }

    fun openAdd() {
        editingIndex = -1
        course = ""; university = ""; grade = ""; durationFrom = ""; durationTo = ""
        showDialog = true
    }

    fun openEdit(index: Int) {
        val item = items[index]
        editingIndex = index
        course = item.course; university = item.university; grade = item.grade
        durationFrom = item.durationFrom; durationTo = item.durationTo
        showDialog = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { openAdd() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add education")
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
                    itemsIndexed(items) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(item.course, style = MaterialTheme.typography.titleMedium)
                                Text(item.university, style = MaterialTheme.typography.bodyMedium)
                                Text("${item.durationFrom} - ${item.durationTo}", style = MaterialTheme.typography.bodySmall)
                                Text(item.grade, style = MaterialTheme.typography.bodySmall)
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                    IconButton(onClick = {
                                        if (index > 0) {
                                            val updated = items.toMutableList()
                                            val tmp = updated[index - 1]; updated[index - 1] = updated[index]; updated[index] = tmp
                                            onSave(reindex(updated))
                                        }
                                    }, enabled = index > 0) { Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up") }
                                    IconButton(onClick = {
                                        if (index < items.size - 1) {
                                            val updated = items.toMutableList()
                                            val tmp = updated[index + 1]; updated[index + 1] = updated[index]; updated[index] = tmp
                                            onSave(reindex(updated))
                                        }
                                    }, enabled = index < items.size - 1) { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down") }
                                    IconButton(onClick = { openEdit(index) }) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }
                                    IconButton(onClick = {
                                        onSave(reindex(items.toMutableList().apply { removeAt(index) }))
                                        Toast.makeText(context, "Deleted successfully.", Toast.LENGTH_SHORT).show()
                                    }) { Icon(Icons.Filled.Delete, contentDescription = "Delete") }
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
            title = { Text(if (editingIndex >= 0) "Edit Education" else "Add Education") },
            text = {
                Column {
                    OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Degree / Course") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = university, onValueChange = { university = it }, label = { Text("University / School") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade / Score") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = durationFrom, onValueChange = {}, readOnly = true, label = { Text("From") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        trailingIcon = { TextButton(onClick = { showDatePickerFor = 0 }) { Text("Pick") } }
                    )
                    OutlinedTextField(
                        value = durationTo, onValueChange = {}, readOnly = true, label = { Text("To") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { TextButton(onClick = { showDatePickerFor = 1 }) { Text("Pick") } }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (course.isBlank() || university.isBlank() || grade.isBlank() || durationFrom.isBlank() || durationTo.isBlank()) {
                        Toast.makeText(context, "Please fill the mandatory details.", Toast.LENGTH_SHORT).show()
                    } else {
                        val entry = EducationEntity(
                            id = if (editingIndex >= 0) items[editingIndex].id else 0,
                            resumeId = resumeId,
                            orderIndex = if (editingIndex >= 0) editingIndex else items.size,
                            course = course, university = university, grade = grade,
                            durationFrom = durationFrom, durationTo = durationTo
                        )
                        val updated = items.toMutableList()
                        if (editingIndex >= 0) updated[editingIndex] = entry else updated.add(entry)
                        onSave(reindex(updated))
                        showDialog = false
                        Toast.makeText(context, "Educational details saved.", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    showDatePickerFor?.let { which ->
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerFor = null },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val formatted = monthYearFormat.format(Date(millis))
                        if (which == 0) durationFrom = formatted else durationTo = formatted
                    }
                    showDatePickerFor = null
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePickerFor = null }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }
}

private fun reindex(items: List<EducationEntity>): List<EducationEntity> =
    items.mapIndexed { index, item -> item.copy(orderIndex = index) }
