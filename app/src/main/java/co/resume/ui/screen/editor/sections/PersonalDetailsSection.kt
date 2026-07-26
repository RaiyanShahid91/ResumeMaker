package co.resume.ui.screen.editor.sections

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.data.local.entity.ResumeEntity
import co.resume.ui.component.AppTextField

@Composable
fun PersonalDetailsSection(
    resume: ResumeEntity,
    onBack: () -> Unit,
    onSave: (name: String, designation: String, email: String, phone: String, address: String) -> Unit
) {
    val context = LocalContext.current
    val savedMsg = stringResource(R.string.msg_details_saved)
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
        AppTextField(
            value = name, onValueChange = { name = it }, label = { Text(stringResource(R.string.personal_label_name)) },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        AppTextField(
            value = designation, onValueChange = { designation = it }, label = { Text(stringResource(R.string.label_designation)) },
            singleLine = true, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
        AppTextField(
            value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.personal_label_email)) },
            singleLine = true,
            isError = !isEmailValid,
            supportingText = if (!isEmailValid) {
                { Text(stringResource(R.string.personal_error_email_invalid)) }
            } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        AppTextField(
            value = phone,
            onValueChange = { input -> phone = input.filter { it.isDigit() } },
            label = { Text(stringResource(R.string.personal_label_phone)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        AppTextField(
            value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.personal_label_address)) },
            minLines = 3, modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
        )
        val isDirty = name != resume.name || designation != resume.designation ||
            email != resume.email || phone != resume.phone || address != resume.address
        val isValid = name.isNotBlank() && address.isNotBlank() &&
            email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            phone.isNotBlank() && phone.all { it.isDigit() }

        Button(
            onClick = {
                onSave(name, designation, email, phone, address)
                Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                onBack()
            },
            enabled = isDirty && isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_save))
        }
    }
}
