package co.resume.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resume.ui.theme.ResumeBuilderTheme

/**
 * The app's single text-field style — rounded corners, subtle border that highlights on
 * focus, no fill behind the text — used everywhere instead of a bare M3 [OutlinedTextField]
 * so every form in the app looks and feels consistent.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    ResumeBuilderTheme {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            AppTextField(
                value = "Alex Morgan",
                onValueChange = {},
                label = { Text("Full name") },
                singleLine = true
            )
            AppTextField(
                value = "",
                onValueChange = {},
                label = { Text("Email") },
                placeholder = { Text("you@example.com") },
                singleLine = true,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
