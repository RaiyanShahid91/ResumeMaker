package co.resume.ui.screen.auth

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import co.resume.ui.component.AppDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.resumeai.R
import co.resume.auth.EmailPolicy
import co.resume.auth.GoogleAuthConfig
import co.resume.auth.requestGoogleIdToken
import co.resume.ui.component.AppButton
import co.resume.ui.component.AppOutlinedButton
import co.resume.ui.component.AppTextField
import co.resume.ui.component.LanguagePickerSheet
import co.resume.ui.component.RequirementRow
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AuthViewModel
import co.resume.utils.LanguageManager
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    onGoToRegister: () -> Unit,
    onLanguageSelected: (String) -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val currentLanguageCode = remember { LanguageManager.getSavedLanguageCode(context) }

    LaunchedEffect(Unit) { viewModel.clearError() }

    AuthScaffold(
        title = stringResource(R.string.auth_login_title),
        subtitle = stringResource(R.string.auth_login_subtitle),
        errorMessage = viewModel.errorMessage,
        isLoading = viewModel.isLoading,
        onLanguageClick = { showLanguageSheet = true }
    ) {
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(
                onClick = { viewModel.clearResetState(); showForgotPassword = true },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) { Text(stringResource(R.string.auth_forgot_password), style = MaterialTheme.typography.bodySmall) }
        }

        Spacer(Modifier.height(8.dp))

        AppButton(
            text = if (viewModel.isLoading) stringResource(R.string.auth_logging_in) else stringResource(R.string.auth_login_button),
            enabled = !viewModel.isLoading && email.isNotBlank() && password.isNotBlank(),
            onClick = { viewModel.login(email, password, onSuccess = onLoggedIn) },
            modifier = Modifier.fillMaxWidth()
        )

        if (GoogleAuthConfig.isGoogleSignInConfigured) {
            GoogleDivider()
            GoogleSignInButton(enabled = !viewModel.isLoading) {
                val currentActivity = activity ?: return@GoogleSignInButton
                scope.launch {
                    runCatching { requestGoogleIdToken(currentActivity) }
                        .onSuccess { token ->
                            if (token != null) viewModel.signInWithGoogle(token, onSuccess = onLoggedIn)
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
                stringResource(R.string.auth_no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            TextButton(
                onClick = onGoToRegister,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
            ) { Text(stringResource(R.string.auth_sign_up), maxLines = 1) }
        }
    }

    if (showForgotPassword) {
        ForgotPasswordDialog(
            initialEmail = email,
            isSending = viewModel.isSendingReset,
            sent = viewModel.resetSent,
            error = viewModel.resetError,
            onSend = { resetEmail -> viewModel.sendPasswordReset(resetEmail) },
            onDismiss = { showForgotPassword = false; viewModel.clearResetState() }
        )
    }

    if (showLanguageSheet) {
        LanguagePickerSheet(
            title = stringResource(R.string.settings_select_language),
            currentLanguageCode = currentLanguageCode,
            availableLanguages = LanguageManager.supportedLanguages,
            onLanguageSelected = { code ->
                showLanguageSheet = false
                onLanguageSelected(code)
            },
            onDismissRequest = { showLanguageSheet = false }
        )
    }
}

/** Lets the user request a password-reset email without leaving the app — Firebase sends the
 *  actual email, but the request itself (typing the address, seeing it was sent) all happens
 *  in this in-app dialog rather than bouncing out to a browser. */
@Composable
private fun ForgotPasswordDialog(
    initialEmail: String,
    isSending: Boolean,
    sent: Boolean,
    error: String?,
    onSend: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var resetEmail by remember { mutableStateOf(initialEmail) }
    AppDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.auth_reset_title)) },
        text = {
            Column {
                if (sent) {
                    Text(stringResource(R.string.auth_reset_sent))
                } else {
                    Text(
                        stringResource(R.string.auth_reset_instruction),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    AppTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text(stringResource(R.string.auth_email_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error != null) {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (!sent) TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) }
        },
        confirmButton = {
            if (sent) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.auth_reset_done)) }
            } else {
                TextButton(
                    enabled = !isSending,
                    onClick = { onSend(resetEmail) }
                ) { Text(stringResource(if (isSending) R.string.auth_reset_sending else R.string.auth_reset_send)) }
            }
        }
    )
}

/** Shared header/body chrome for [LoginScreen] and [RegisterScreen] — same icon, spacing,
 *  entrance motion, and error-message slot so the two screens read as one flow rather than two
 *  different designs. */
@Composable
internal fun AuthScaffold(
    title: String,
    subtitle: String,
    errorMessage: String?,
    isLoading: Boolean = false,
    onBack: (() -> Unit)? = null,
    onLanguageClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var headerVisible by remember { mutableStateOf(false) }
    var formVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        headerVisible = true
        kotlinx.coroutines.delay(120)
        formVisible = true
    }

    // Shakes the form horizontally whenever a new error comes in — a quick, decaying wobble
    // reads as "that didn't work" far faster than the eye has to find and read red text.
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -12f at 50
                    12f at 110
                    -8f at 180
                    8f at 250
                    -4f at 320
                    0f at 400
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // The app-wide floating blobs (see MainActivity) already show through this screen's
        // transparent background — nothing to paint here anymore.

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isLoading) Modifier.blur(10.dp) else Modifier)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            AnimatedVisibility(
                visible = headerVisible,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420, easing = FastOutSlowInEasing)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AuthIconBadge()
                    Spacer(Modifier.height(20.dp))
                    Text(title, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = formVisible,
                enter = fadeIn(tween(420)) + slideInVertically(tween(420, easing = FastOutSlowInEasing)) { it / 4 }
            ) {
                // A solid card behind the fields/buttons — without this, the floating blobs
                // behind it (see FloatingBlobsBackground) could wash out text-field borders and
                // labels depending on where a blob happened to be mid-animation.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(x = shakeOffset.value.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.96f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                        .padding(20.dp)
                ) {
                    content()

                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(150))
                    ) {
                        Text(
                            errorMessage.orEmpty(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Placed after the scrollable form Column (not before) so these sit on top for both
        // painting AND hit-testing — Compose gives touch priority to whichever Box child was
        // composed last, so a same-sized transparent Column composed after these would silently
        // swallow every tap in the corners where they live, even though it paints nothing there.
        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(4.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
            }
        }

        if (onLanguageClick != null) {
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(4.dp)
            ) {
                Icon(Icons.Filled.Language, contentDescription = stringResource(R.string.auth_change_language_cd))
            }
        }

        // Composed last so it paints and hit-tests on top of everything else — the tapGestures
        // block with no callbacks is what actually swallows clicks/drags rather than letting
        // them fall through to the (blurred but still technically present) content underneath.
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .pointerInput(Unit) { detectTapGestures { } },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

/** Gradient circle badge with a slow pulsing halo and a springy pop-in — the same visual
 *  language as the paywall's icon badge, so login feels like part of the same app, not a
 *  bolted-on flow. */
@Composable
private fun AuthIconBadge() {
    val pulse = rememberInfiniteTransition(label = "auth_icon_pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse_scale"
    )
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse_alpha"
    )
    val pop = remember { Animatable(0.6f) }
    LaunchedEffect(Unit) {
        pop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale; alpha = pulseAlpha }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .graphicsLayer { scaleX = pop.value; scaleY = pop.value }
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Description, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
        }
    }
}

@Composable
internal fun GoogleDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            stringResource(R.string.auth_or_divider),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun GoogleSignInButton(enabled: Boolean, onClick: () -> Unit) {
    AppOutlinedButton(
        text = stringResource(R.string.auth_google_continue),
        enabled = enabled,
        onClick = onClick,
        icon = {
            Icon(
                painterResource(co.resumeai.R.drawable.ic_google_logo),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp).padding(end = 10.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    ResumeBuilderTheme {
        LoginScreen(onLoggedIn = {}, onGoToRegister = {})
    }
}
