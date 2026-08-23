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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import co.resume.ai.AiClient
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.LanguagePickerSheet
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.SettingsViewModel
import co.resume.utils.AppLanguage
import co.resumeai.R

@Composable
fun SettingsTab(
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val versionName = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
            .getOrDefault("—")
    }

    SettingsContent(
        currentLanguage = viewModel.currentLanguage,
        availableLanguages = viewModel.availableLanguages,
        versionName = versionName ?: "—",
        onLanguageSelected = onLanguageSelected,
        onOpenPrivacy = onOpenPrivacy,
        onOpenAbout = onOpenAbout,
        onOpenGuide = onOpenGuide,
        onRateApp = { rateApp(context) },
        onWriteUs = { sendFeedbackEmail(context) },
        onShareApp = { shareChooserTitle, appName, tagline ->
            shareApp(context, appName, tagline, shareChooserTitle)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    currentLanguage: AppLanguage,
    availableLanguages: List<AppLanguage>,
    versionName: String,
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    onRateApp: () -> Unit,
    onWriteUs: () -> Unit,
    onShareApp: (chooserTitle: String, appName: String, tagline: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showLanguageSheet by remember { mutableStateOf(false) }

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

                // ── Language section ─────────────────────────────────────────────
                item {
                    SettingsSectionCard(title = stringResource(R.string.settings_language)) {
                        SettingsRow(
                            icon = Icons.Filled.Language,
                            iconGradient = listOf(primary, tertiary),
                            title = currentLanguage.nativeName,
                            subtitle = currentLanguage.displayName,
                            onClick = { showLanguageSheet = true },
                            showDivider = false
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
        BannerAdView()
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
        data = Uri.parse("mailto:contact@brynflow.com")
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
