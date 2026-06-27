package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeclarationSection(
    initialDeclaration: String,
    initialPlace: String,
    initialDate: String,
    onSave: (declaration: String, place: String, date: String) -> Unit
) {
    val context = LocalContext.current
    var declaration by remember { mutableStateOf(initialDeclaration) }
    var place by remember { mutableStateOf(initialPlace) }
    var date by remember { mutableStateOf(initialDate.ifBlank { displayFormat.format(Date()) }) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(initialDeclaration, initialPlace, initialDate) {
        declaration = initialDeclaration
        place = initialPlace
        if (initialDate.isNotBlank()) date = initialDate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = declaration,
            onValueChange = { declaration = it },
            label = { Text("Declaration") },
            placeholder = { Text("I do hereby confirm that the information given above is true to the best of my knowledge") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = place, onValueChange = { place = it }, label = { Text("Place") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Date") },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            trailingIcon = { TextButton(onClick = { showDatePicker = true }) { Text("Change") } }
        )
        Button(
            onClick = {
                if (declaration.isBlank() || place.isBlank() || date.isBlank()) {
                    Toast.makeText(context, "Please fill the mandatory details.", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(declaration, place, date)
                    Toast.makeText(context, "Details saved successfully.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { date = displayFormat.format(Date(it)) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = state)
        }
    }
}
