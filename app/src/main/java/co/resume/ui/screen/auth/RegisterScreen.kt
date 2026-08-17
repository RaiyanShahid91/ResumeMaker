package co.resume.ui.screen.auth

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.resumeai.R
import co.resume.auth.EmailPolicy
import co.resume.auth.GoogleAuthConfig
import co.resume.auth.PasswordPolicy
import co.resume.auth.requestGoogleIdToken
import co.resume.ui.component.AppButton
import co.resume.ui.component.AppTextField
import co.resume.ui.component.RequirementRow
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onGoToLogin: () -> Unit,
    onOpenTerms: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    // rememberSaveable (not remember) — Terms & Conditions is a separate destination pushed on
    // top of this one, which disposes this composable's plain `remember` state. Since this
    // screen's NavBackStackEntry itself isn't destroyed (just navigated away from), the saved
    // state registry it's scoped to survives the round trip and restores everything typed here.
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var agreedToTerms by rememberSaveable { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.clearError() }

    AuthScaffold(
        title = stringResource(R.string.auth_register_title),
        subtitle = stringResource(R.string.auth_register_subtitle),
        errorMessage = viewModel.errorMessage,
        isLoading = viewModel.isLoading,
        onBack = onGoToLogin
    ) {
        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.auth_full_name_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.auth_email_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (email.isNotBlank()) {
            RequirementRow(stringResource(R.string.auth_valid_email), EmailPolicy.isValid(email))
        }
        Spacer(Modifier.height(12.dp))
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.auth_password_label)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    AnimatedContent(targetState = passwordVisible, label = "password_visibility_icon") { visible ->
                        Icon(
                            if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (password.isNotBlank()) {
            PasswordRequirements(password)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = agreedToTerms, onCheckedChange = { agreedToTerms = it })
            Text(stringResource(R.string.auth_agree_prefix), style = MaterialTheme.typography.bodySmall)
            TextButton(
                onClick = onOpenTerms,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text(
                    stringResource(R.string.auth_terms_link),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        AppButton(
            text = if (viewModel.isLoading) stringResource(R.string.auth_creating_account) else stringResource(R.string.auth_create_account_button),
            enabled = !viewModel.isLoading && name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && agreedToTerms,
            onClick = { viewModel.register(name, email, password, onSuccess = onRegistered) },
            modifier = Modifier.fillMaxWidth()
        )

        if (GoogleAuthConfig.isGoogleSignInConfigured) {
            GoogleDivider()
            GoogleSignInButton(enabled = !viewModel.isLoading && agreedToTerms) {
                val currentActivity = activity ?: return@GoogleSignInButton
                scope.launch {
                    runCatching { requestGoogleIdToken(currentActivity) }
                        .onSuccess { token ->
                            if (token != null) viewModel.signInWithGoogle(token, onSuccess = onRegistered)
                        }
                        .onFailure { error ->
                            co.resume.auth.googleSignInErrorMessage(error)?.let { viewModel.reportError(it) }
                        }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.auth_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            TextButton(
                onClick = onGoToLogin,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
            ) { Text(stringResource(R.string.auth_login_link), maxLines = 1) }
        }
    }
}

/** Live requirement checklist for the password field — each row turns green with a check the
 *  moment its rule is met, so the user finds out what's missing before hitting submit rather
 *  than from a rejected-password error afterward. */
@Composable
private fun PasswordRequirements(password: String) {
    Column(modifier = Modifier.padding(top = 8.dp, start = 4.dp)) {
        RequirementRow(stringResource(R.string.auth_pw_req_length, PasswordPolicy.MIN_LENGTH), PasswordPolicy.hasMinLength(password))
        RequirementRow(stringResource(R.string.auth_pw_req_number), PasswordPolicy.hasDigit(password))
        RequirementRow(stringResource(R.string.auth_pw_req_special), PasswordPolicy.hasSpecialChar(password))
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    ResumeBuilderTheme {
        RegisterScreen(onRegistered = {}, onGoToLogin = {})
    }
}
