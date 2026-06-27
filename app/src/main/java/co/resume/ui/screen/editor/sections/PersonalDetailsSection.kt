package co.resume.ui.screen.editor.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.resume.data.local.entity.ResumeEntity

@Composable
fun PersonalDetailsSection(resume: ResumeEntity, onSave: (name: String, designation: String, email: String, phone: String, address: String) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(resume.name) }
    var designation by remember { mutableStateOf(resume.designation) }
    var email by remember { mutableStateOf(resume.email) }
    var phone by remember { mutableStateOf(resume.phone) }
    var address by remember { mutableStateOf(resume.address) }

    LaunchedEffect(resume.id) {
        name = resume.name
        designation = resume.designation
        email = resume.email
        phone = resume.phone
        address = resume.address
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = name, onValueChange = { name = it }, label = { Text("Name") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = designation, onValueChange = { designation = it }, label = { Text("Designation") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email ID") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = phone, onValueChange = { phone = it }, label = { Text("Phone") },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = address, onValueChange = { address = it }, label = { Text("Address") },
            minLines = 3, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )
        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || phone.isBlank() || address.isBlank()) {
                    Toast.makeText(context, "Please fill the mandatory details.", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(name, designation, email, phone, address)
                    Toast.makeText(context, "Details saved successfully.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
