package co.resume.ui.screen.dashboard

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resume.ai.AiClient
import co.resume.auth.AuthUser
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.component.AppDialog
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.LanguagePickerSheet
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.AuthViewModel
import co.resume.ui.viewmodel.SettingsViewModel
import co.resume.utils.AppLanguage
import co.resumeai.R

@Composable
fun SettingsTab(
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenPaywall: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrDefault("—")
    }
    val isPremium by adViewModel.isPremium.collectAsStateWithLifecycle()
    val subscriptionDetails by adViewModel.subscriptionDetails.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()

    SettingsContent(
        currentLanguage = viewModel.currentLanguage,
        availableLanguages = viewModel.availableLanguages,
        versionName = versionName ?: "—",
        isPremium = isPremium,
        subscriptionDetails = subscriptionDetails,
        currentUser = currentUser,
        onLanguageSelected = onLanguageSelected,
        onOpenPrivacy = onOpenPrivacy,
        onOpenAbout = onOpenAbout,
        onOpenGuide = onOpenGuide,
        onOpenPaywall = onOpenPaywall,
        onRateApp = { rateApp(context) },
        onWriteUs = { sendFeedbackEmail(context) },
        onShareApp = { shareChooserTitle, appName, tagline ->
            shareApp(context, appName, tagline, shareChooserTitle)
        },
        onLogout = {
            authViewModel.signOut()
            onLoggedOut()
        },
        onDeleteAccount = {
            authViewModel.deleteAccount(
                onSuccess = onLoggedOut,
                onFailure = { message ->
                    Toast.makeText(context, "Couldn't delete account: $message. Try logging in again first.", Toast.LENGTH_LONG).show()
                }
            )
        },
        authViewModel = authViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    currentLanguage: AppLanguage,
    availableLanguages: List<AppLanguage>,
    versionName: String,
    isPremium: Boolean = false,
    subscriptionDetails: co.resume.billing.SubscriptionDetails? = null,
    currentUser: AuthUser? = null,
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenPaywall: () -> Unit = {},
    onRateApp: () -> Unit,
    onWriteUs: () -> Unit,
    onShareApp: (chooserTitle: String, appName: String, tagline: String) -> Unit,
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    authViewModel: AuthViewModel? = null
) {
    val sheetState = rememberModalBottomSheetState()
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.weight(1f), color = Color.Transparent) {
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)) {
                // ── Header ────────────────────────────────────────────────────
                item {
                    Column(modifier = Modifier.padding(top = 24.dp, bottom = 20.dp)) {
                        Text(
                            stringResource(R.string.nav_settings),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // ── Account section ──────────────────────────────────────────────
                item {
                    SettingsSectionCard(title = "Account") {
                        AccountRow(currentUser, onClick = { showProfileSheet = true })
                        SettingsRow(
                            icon = Icons.Filled.Logout,
                            iconGradient = listOf(secondary, tertiary),
                            title = "Log out",
                            onClick = { showLogoutConfirm = true },
                            showDivider = false
                        )
                    }
                }

                // ── Premium status ───────────────────────────────────────────────
                item {
                    if (isPremium) PremiumActiveCard() else PremiumPromoCard(onClick = onOpenPaywall)
                }

                // ── Language section ─────────────────────────────────────────────
                item {
                    SettingsSectionCard(title = stringResource(R.string.settings_language)) {
                        SettingsRow(
                            icon = Icons.Filled.Language,
                            iconGradient = listOf(primary, tertiary),
                            title = currentLanguage.nativeName,
                            subtitle = currentLanguage.displayName,
                            onClick = { showLanguageSheet = true }
                        )
                    }
                }

                // ── Help section ─────────────────────────────────────────────────
                item {
                    SettingsSectionCard(title = stringResource(R.string.settings_help)) {
                        SettingsRow(
                            icon = Icons.Filled.MenuBook,
                            iconGradient = listOf(secondary, primary),
                            title = stringResource(R.string.settings_user_guide),
                            onClick = onOpenGuide
                        )
                        SettingsRow(
                            icon = Icons.Filled.Info,
                            iconGradient = listOf(secondary, primary),
                            title = stringResource(R.string.settings_about),
                            onClick = onOpenAbout
                        )
                        SettingsRow(
                            icon = Icons.Filled.Policy,
                            iconGradient = listOf(secondary, primary),
                            title = stringResource(R.string.settings_privacy),
                            onClick = onOpenPrivacy,
                            showDivider = false
                        )
                    }
                }

                // ── AI section ───────────────────────────────────────────────────
                item {
                    SettingsSectionCard(title = stringResource(R.string.settings_ai)) {
                        AiInfoCard()
                    }
                }

                // ── Contact section ──────────────────────────────────────────────
                item {
                    val shareChooserTitle = stringResource(R.string.share_chooser_title)
                    val appName = stringResource(R.string.about_app_name)
                    val tagline = stringResource(R.string.about_tagline)
                    SettingsSectionCard(title = stringResource(R.string.settings_contact)) {
                        SettingsRow(
                            icon = Icons.Filled.RateReview,
                            iconGradient = listOf(tertiary, secondary),
                            title = stringResource(R.string.settings_rate_app),
                            onClick = onRateApp
                        )
                        SettingsRow(
                            icon = Icons.Filled.Send,
                            iconGradient = listOf(tertiary, secondary),
                            title = stringResource(R.string.settings_write_us),
                            onClick = onWriteUs
                        )
                        SettingsRow(
                            icon = Icons.Filled.Share,
                            iconGradient = listOf(tertiary, secondary),
                            title = stringResource(R.string.settings_share_app),
                            onClick = { onShareApp(shareChooserTitle, appName, tagline) },
                            showDivider = false
                        )
                    }
                }

                // ── Version ──────────────────────────────────────────────────────
                item {
                    Text(
                        stringResource(R.string.settings_version, versionName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        if (!isPremium) BannerAdView()
    }

    // ── Language picker bottom sheet ─────────────────────────────────────────
    if (showLanguageSheet) {
        LanguagePickerSheet(
            title = stringResource(R.string.settings_select_language),
            currentLanguageCode = currentLanguage.code,
            availableLanguages = availableLanguages,
            onLanguageSelected = { code ->
                showLanguageSheet = false
                onLanguageSelected(code)
            },
            onDismissRequest = { showLanguageSheet = false },
            sheetState = sheetState
        )
    }

    // ── Logout confirmation (bottom sheet) ───────────────────────────────────
    if (showLogoutConfirm) {
        AppBottomSheet(onDismissRequest = { showLogoutConfirm = false }) {
            Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
                Text("Log out?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "You'll need to log back in to access your account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )
                Button(
                    onClick = { showLogoutConfirm = false; onLogout() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Log out") }
                TextButton(
                    onClick = { showLogoutConfirm = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) { Text("Cancel") }
            }
        }
    }

    // ── Profile sheet: name/email, change password, delete account ──────────
    if (showProfileSheet) {
        AppBottomSheet(onDismissRequest = { showProfileSheet = false }) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
                AccountRow(currentUser, onClick = null)
                SubscriptionStatusRow(isPremium, subscriptionDetails)
                if (authViewModel?.hasPasswordProvider != false) {
                    SettingsRow(
                        icon = Icons.Filled.Lock,
                        iconGradient = listOf(secondary, primary),
                        title = "Change password",
                        onClick = {
                            showProfileSheet = false
                            authViewModel?.clearChangePasswordState()
                            showChangePassword = true
                        }
                    )
                }
                SettingsRow(
                    icon = Icons.Filled.DeleteForever,
                    iconGradient = listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error),
                    title = "Delete account",
                    onClick = { showProfileSheet = false; showDeleteConfirm = true },
                    showDivider = false
                )
            }
        }
    }

    // ── Change password dialog ───────────────────────────────────────────────
    if (showChangePassword && authViewModel != null) {
        ChangePasswordDialog(
            isSending = authViewModel.isChangingPassword,
            error = authViewModel.changePasswordError,
            success = authViewModel.changePasswordSuccess,
            onSend = { current, new -> authViewModel.changePassword(current, new) },
            onDismiss = { showChangePassword = false; authViewModel.clearChangePasswordState() }
        )
    }

    // ── Delete account confirmation (bottom sheet, agree checkbox required) ─
    if (showDeleteConfirm) {
        DeleteAccountSheet(
            onDismissRequest = { showDeleteConfirm = false },
            onConfirmDelete = { showDeleteConfirm = false; onDeleteAccount() }
        )
    }
}

/** Shows whether the user currently has an active subscription — and if so, what plan, when it
 *  was purchased, the payment method (always Google Play Billing, see Terms & Conditions), and
 *  Play's order ID, so a support conversation about a specific purchase has something concrete
 *  to reference. */
@Composable
private fun SubscriptionStatusRow(isPremium: Boolean, details: co.resume.billing.SubscriptionDetails?) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isPremium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
            )
            Text(
                if (isPremium) "Subscription: Active" else "Subscription: No active subscription",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        if (isPremium && details != null) {
            val planLabel = when (details.basePlanId) {
                co.resume.billing.BillingConstants.YEARLY_BASE_PLAN_ID -> "Yearly"
                co.resume.billing.BillingConstants.MONTHLY_BASE_PLAN_ID -> "Monthly"
                else -> null
            }
            Column(modifier = Modifier.padding(start = 18.dp, top = 6.dp)) {
                if (planLabel != null) {
                    Text("Plan: $planLabel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Payment via: Google Play Billing", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                details.purchaseTimeMillis?.let { millis ->
                    val formatted = remember(millis) {
                        java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM).format(java.util.Date(millis))
                    }
                    Text("Purchased: $formatted", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                details.orderId?.let { orderId ->
                    Text("Order ID: $orderId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

/** Name/email row shown at the top of the Account section, with an initials avatar. */
@Composable
private fun AccountRow(user: AuthUser?, onClick: (() -> Unit)?) {
    val label = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email ?: "Signed in"
    val initial = (user?.displayName?.trim()?.firstOrNull() ?: user?.email?.firstOrNull() ?: '?').uppercaseChar()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text(initial.toString(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
            if (user?.email != null && user.email != label) {
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (user != null) {
                Text(
                    when (user.provider) {
                        co.resume.auth.SignInProvider.GOOGLE -> "Signed in with Google"
                        co.resume.auth.SignInProvider.EMAIL -> "Signed in with Email"
                        co.resume.auth.SignInProvider.UNKNOWN -> "Signed in"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        if (onClick != null) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 66.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

/** Bottom sheet warning of everything account deletion costs the user, gated behind an explicit
 *  "I understand" checkbox before Delete becomes tappable — this is permanent and destroys
 *  locally-stored resumes/cover letters/scans, so a plain confirm button was too easy to hit
 *  by reflex. Deliberately does NOT claim to cancel a Play Store subscription: a Firebase
 *  account delete has no way to reach into Google Play Billing, so saying otherwise would be
 *  misleading — the user still has to cancel that separately in Play. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountSheet(onDismissRequest: () -> Unit, onConfirmDelete: () -> Unit) {
    var understood by remember { mutableStateOf(false) }
    AppBottomSheet(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text("Delete your account?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "This is permanent and can't be undone. Deleting your account will:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp, bottom = 12.dp)
            )
            DeleteConsequenceRow("Permanently sign you out and remove access to this account")
            DeleteConsequenceRow("Delete all resumes, cover letters, and scanned documents stored on this device")
            DeleteConsequenceRow("NOT cancel an active Play Store subscription — cancel that separately in Google Play, or you'll keep being charged")

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable { understood = !understood }
            ) {
                Checkbox(checked = understood, onCheckedChange = { understood = it })
                Text(
                    "I understand this action is permanent",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onConfirmDelete,
                enabled = understood,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Delete my account") }
            TextButton(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Text("Cancel")
            }
        }
    }
}

@Composable
private fun DeleteConsequenceRow(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("•", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(end = 8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Reauthenticate-with-current-password + set-new-password dialog. Firebase requires a recent
 *  sign-in before it allows a password change, which is why the current password is asked for
 *  here rather than just the new one — see [co.resume.auth.AuthRepository.changePassword]. */
@Composable
private fun ChangePasswordDialog(
    isSending: Boolean,
    error: String?,
    success: Boolean,
    onSend: (current: String, new: String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    AppDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change password") },
        text = {
            Column {
                if (success) {
                    Text("Your password has been changed.")
                } else {
                    co.resume.ui.component.AppTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Current password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    co.resume.ui.component.AppTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New password") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (newPassword.isNotBlank()) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            co.resume.ui.component.RequirementRow(
                                "At least ${co.resume.auth.PasswordPolicy.MIN_LENGTH} characters",
                                co.resume.auth.PasswordPolicy.hasMinLength(newPassword)
                            )
                            co.resume.ui.component.RequirementRow(
                                "At least 1 number",
                                co.resume.auth.PasswordPolicy.hasDigit(newPassword)
                            )
                            co.resume.ui.component.RequirementRow(
                                "At least 1 special character (e.g. @ # !)",
                                co.resume.auth.PasswordPolicy.hasSpecialChar(newPassword)
                            )
                        }
                    }
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
            if (!success) TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        confirmButton = {
            if (success) {
                TextButton(onClick = onDismiss) { Text("Done") }
            } else {
                TextButton(
                    enabled = !isSending,
                    onClick = { onSend(currentPassword, newPassword) }
                ) { Text(if (isSending) "Saving…" else "Save") }
            }
        }
    )
}

/** Upsell card shown at the top of Settings for non-subscribers; opens the paywall. */
@Composable
private fun PremiumPromoCard(onClick: () -> Unit) {
    AppCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        containerColor = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    "Go Premium",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Remove ads & unlock everything",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
        }
    }
}

/** Shown instead of [PremiumPromoCard] once the user has an active subscription. */
@Composable
private fun PremiumActiveCard() {
    val context = LocalContext.current
    AppCard(
        onClick = { openManageSubscription(context) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        containerColor = Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                )
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(
                    "Premium Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Manage subscription",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.White)
        }
    }
}

private fun openManageSubscription(context: android.content.Context) {
    val url = "https://play.google.com/store/account/subscriptions?sku=${co.resume.billing.BillingConstants.PREMIUM_PRODUCT_ID}&package=${context.packageName}"
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}

/** A titled card grouping related settings rows, matching the rest of the app's card language. */
@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconGradient: List<Color>,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showDivider: Boolean = true
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(iconGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 66.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun AiInfoCard() {
    val isActive = AiClient.isConfigured
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isActive) listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
                            else listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outlineVariant)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Text(
                if (isActive) stringResource(R.string.settings_ai_active)
                else stringResource(R.string.settings_ai_not_configured),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 14.dp)
            )
        }

        Spacer(Modifier.height(14.dp))

        AiInfoRow(
            stringResource(R.string.settings_ai_model),
            stringResource(R.string.settings_ai_model_name)
        )
        AiInfoRow(
            stringResource(R.string.settings_ai_provider),
            stringResource(R.string.settings_ai_provider_name)
        )
        AiInfoRow(
            stringResource(R.string.settings_ai_available_in),
            stringResource(R.string.settings_ai_available_in_value)
        )
    }
}

@Composable
private fun AiInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun rateApp(context: android.content.Context) {
    val packageName = context.packageName
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        intent.setPackage("com.android.vending")
        context.startActivity(intent)
    } catch (e: Exception) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

private fun shareApp(
    context: android.content.Context,
    appName: String,
    tagline: String,
    chooserTitle: String
) {
    val playStoreLink = "https://play.google.com/store/apps/details?id=${context.packageName}"
    val message = "$appName — $tagline\n$playStoreLink"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

private fun sendFeedbackEmail(context: android.content.Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:rssphere0@gmail.com")
        putExtra(Intent.EXTRA_SUBJECT, "Feedback / Support")
        putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}

private val previewLanguages = listOf(
    AppLanguage("en", "English", "English"),
    AppLanguage("de", "German", "Deutsch"),
    AppLanguage("hi", "Hindi", "हिन्दी")
)

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    ResumeBuilderTheme {
        SettingsContent(
            currentLanguage = previewLanguages.first(),
            availableLanguages = previewLanguages,
            versionName = "1.0.0",
            onLanguageSelected = {},
            onOpenPrivacy = {},
            onOpenAbout = {},
            onOpenGuide = {},
            onRateApp = {},
            onWriteUs = {},
            onShareApp = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AiInfoCardPreview() {
    ResumeBuilderTheme {
        AiInfoCard()
    }
}
