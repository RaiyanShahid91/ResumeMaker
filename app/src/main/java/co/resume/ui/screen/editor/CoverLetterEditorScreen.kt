package co.resume.ui.screen.editor

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.data.local.entity.CoverLetterEntity
import co.resume.domain.export.SampleCoverLetterData
import co.resume.ui.component.AiActionButton
import co.resume.ui.component.AiAssistantFab
import co.resume.ui.component.AiAssistantPopup
import co.resume.ui.component.AppCard
import co.resume.ui.component.AppTextField
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.RichTextField
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.CoverLetterEditorViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val defaultDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverLetterEditorScreen(
    coverLetterId: Long,
    onBack: () -> Unit,
    onOpenTemplatePicker: () -> Unit,
    onOpenPreview: () -> Unit,
    viewModel: CoverLetterEditorViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val coverLetter by viewModel.coverLetter.collectAsStateWithLifecycle()

    LaunchedEffect(coverLetterId) { viewModel.prefillFromLatestResumeIfBlank() }

    CoverLetterEditorContent(
        coverLetterId = coverLetterId,
        coverLetter = coverLetter,
        onBack = onBack,
        onOpenTemplatePicker = onOpenTemplatePicker,
        onOpenPreview = onOpenPreview,
        onSave = viewModel::save,
        adManager = adViewModel.adManager
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoverLetterEditorContent(
    coverLetterId: Long,
    coverLetter: CoverLetterEntity?,
    onBack: () -> Unit,
    onOpenTemplatePicker: () -> Unit,
    onOpenPreview: () -> Unit,
    onSave: (CoverLetterEntity) -> Unit,
    adManager: co.resume.ads.AdManager
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val scope = rememberCoroutineScope()
    var showAiAssistant by remember { mutableStateOf(false) }
    var showAdPrompt by remember { mutableStateOf(false) }

    var senderName by remember { mutableStateOf("") }
    var senderEmail by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var salutation by remember { mutableStateOf("") }
    var bodyText by remember { mutableStateOf("") }
    var closing by remember { mutableStateOf("") }
    var isGeneratingBody by remember { mutableStateOf(false) }

    LaunchedEffect(coverLetter?.id) {
        val letter = coverLetter ?: return@LaunchedEffect
        senderName = letter.senderName
        senderEmail = letter.senderEmail
        senderPhone = letter.senderPhone
        recipientName = letter.recipientName
        companyName = letter.companyName
        jobTitle = letter.jobTitle
        date = letter.date.ifBlank { defaultDateFormat.format(Date()) }
        salutation = letter.salutation.ifBlank { "Dear Hiring Manager," }
        bodyText = letter.bodyText
        closing = letter.closing.ifBlank { "Sincerely,\n$senderName" }
    }

    val savedMsg = stringResource(R.string.msg_details_saved)
    val aiFailedMsg = stringResource(R.string.work_msg_ai_failed)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(R.string.cover_letter_editor_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_change_template)) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = onOpenTemplatePicker) {
                            Icon(Icons.AutoMirrored.Filled.Article, contentDescription = stringResource(R.string.tooltip_change_template))
                        }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.tooltip_preview)) } },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = onOpenPreview) {
                            Icon(Icons.Filled.Visibility, contentDescription = stringResource(R.string.tooltip_preview))
                        }
                    }
                }
            )
        },
        floatingActionButton = { AiAssistantFab(onClick = { showAiAssistant = true }) },
        bottomBar = { BannerAdView() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                CoverLetterSectionCard(
                    icon = Icons.Filled.Person,
                    title = stringResource(R.string.cover_letter_section_sender)
                ) {
                    AppTextField(value = senderName, onValueChange = { senderName = it }, label = { Text(stringResource(R.string.label_full_name)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    AppTextField(value = senderEmail, onValueChange = { senderEmail = it }, label = { Text(stringResource(R.string.cover_letter_label_email)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    AppTextField(value = senderPhone, onValueChange = { senderPhone = it }, label = { Text(stringResource(R.string.cover_letter_label_phone)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                }

                CoverLetterSectionCard(
                    icon = Icons.Filled.Business,
                    title = stringResource(R.string.cover_letter_section_recipient)
                ) {
                    AppTextField(value = recipientName, onValueChange = { recipientName = it }, label = { Text(stringResource(R.string.cover_letter_label_recipient)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    AppTextField(value = companyName, onValueChange = { companyName = it }, label = { Text(stringResource(R.string.cover_letter_label_company)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    AppTextField(value = jobTitle, onValueChange = { jobTitle = it }, label = { Text(stringResource(R.string.cover_letter_label_job_title)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    AppTextField(value = date, onValueChange = { date = it }, label = { Text(stringResource(R.string.cover_letter_label_date)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                }

                CoverLetterSectionCard(
                    icon = Icons.Filled.Description,
                    title = stringResource(R.string.cover_letter_section_letter)
                ) {
                    AppTextField(value = salutation, onValueChange = { salutation = it }, label = { Text(stringResource(R.string.cover_letter_label_salutation)) }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                    RichTextField(
                        value = bodyText,
                        onValueChange = { bodyText = it },
                        label = { Text(stringResource(R.string.cover_letter_label_body)) },
                        minLines = 8,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        trailingIcon = if (AiClient.isConfigured) {
                            {
                                AiActionButton(
                                    isGenerating = isGeneratingBody,
                                    contentDescription = stringResource(R.string.cover_letter_cd_generate_ai),
                                    onClick = {
                                        isGeneratingBody = true
                                        scope.launch {
                                            try {
                                                val result = ResumeAiService.generateCoverLetter(
                                                    designation = senderName,
                                                    companyName = companyName,
                                                    jobTitle = jobTitle,
                                                    skills = emptyList()
                                                )
                                                if (result.isNotBlank()) bodyText = result
                                            } catch (e: Exception) {
                                                Toast.makeText(context, co.resume.ai.aiErrorMessage(e, aiFailedMsg), Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isGeneratingBody = false
                                            }
                                        }
                                    }
                                )
                            }
                        } else null
                    )
                    AppTextField(value = closing, onValueChange = { closing = it }, label = { Text(stringResource(R.string.cover_letter_label_closing)) }, minLines = 2, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
                }

                Button(
                    onClick = {
                        val current = coverLetter ?: return@Button
                        onSave(
                            current.copy(
                                senderName = senderName, senderEmail = senderEmail, senderPhone = senderPhone,
                                recipientName = recipientName, companyName = companyName, jobTitle = jobTitle,
                                date = date, salutation = salutation, bodyText = bodyText, closing = closing
                            )
                        )
                        Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                        if (adManager.canOfferRewardedPrompt()) showAdPrompt = true
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            }
        }
    }

    if (showAiAssistant) {
        AiAssistantPopup(coverLetterId = coverLetterId, onDismiss = { showAiAssistant = false })
    }

    if (showAdPrompt) {
        co.resume.ui.component.SupportWithAdDialog(
            onWatchAd = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
                activity?.let { adManager.showRewarded(it) {} }
            },
            onSkip = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
            }
        )
    }
}

@Composable
private fun CoverLetterSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoverLetterEditorScreenPreview() {
    ResumeBuilderTheme {
        CoverLetterEditorContent(
            coverLetterId = 1L,
            coverLetter = SampleCoverLetterData.letter,
            onBack = {},
            onOpenTemplatePicker = {},
            onOpenPreview = {},
            onSave = {},
            adManager = co.resume.ads.AdManager(
                androidx.compose.ui.platform.LocalContext.current,
                co.resume.billing.SubscriptionRepository(
                    co.resume.utils.SharedPref(androidx.compose.ui.platform.LocalContext.current),
                    co.resume.auth.AuthRepository(co.resume.auth.UserProfileRepository()),
                    co.resume.auth.UserProfileRepository()
                )
            )
        )
    }
}
