package co.resume.ui.screen.editor

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.ui.component.SupportWithAdDialog
import co.resume.ui.screen.editor.sections.DeclarationSection
import co.resume.ui.screen.editor.sections.EducationSection
import co.resume.ui.screen.editor.sections.ImageCaptureSection
import co.resume.ui.screen.editor.sections.ObjectiveSection
import co.resume.ui.screen.editor.sections.PersonalDetailsSection
import co.resume.ui.screen.editor.sections.ProjectsSection
import co.resume.ui.screen.editor.sections.SimpleListSection
import co.resume.ui.screen.editor.sections.SkillsSection
import co.resume.ui.screen.editor.sections.WorkExperienceSection
import co.resume.ui.viewmodel.AdViewModel
import co.resume.ui.viewmodel.ResumeEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionEditorScreen(
    sectionKey: String,
    onBack: () -> Unit,
    viewModel: ResumeEditorViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val type = EditorSectionType.fromKey(sectionKey)
    val details by viewModel.resume.collectAsStateWithLifecycle()
    val resumeId = viewModel.resumeId
    val adManager = adViewModel.adManager
    val activity = LocalContext.current as? Activity
    // The resume editor has ~10 independent per-section save points (one per section below) —
    // this one flag, gated by AdManager's own cooldown, is what keeps the "watch an ad to
    // support us" prompt from showing after every single section a user saves in one sitting.
    var showAdPrompt by remember { mutableStateOf(false) }
    // PERSONAL/PHOTO/OBJECTIVE/DECLARATION's own Save buttons call onSave(...) then onBack()
    // synchronously in one click (see e.g. ObjectiveSection.kt) — if we let that real onBack
    // through immediately, it navigates this whole screen away a frame after the dialog opens,
    // which is exactly the "dialog flashes for a second then the screen goes back on its own"
    // bug. guardedOnBack swallows navigation while the dialog is up; afterSave's `hasPendingBack`
    // remembers to actually run it once the user resolves the dialog (either button), so nothing
    // is silently lost, it's just deferred until there's no dialog left to tear down mid-render.
    var pendingBack by remember { mutableStateOf(false) }
    fun afterSave(hasPendingBack: Boolean = false) {
        if (adManager.canOfferRewardedPrompt()) {
            showAdPrompt = true
            pendingBack = hasPendingBack
        }
    }
    fun guardedOnBack() {
        if (!showAdPrompt) onBack()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = { Text(stringResource(type.titleRes)) },
                navigationIcon = {
                    IconButton(onClick = ::guardedOnBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(co.resumeai.R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        val resumeDetails = details ?: return@Scaffold
        Box(modifier = Modifier.padding(padding)) {
            when (type) {
                EditorSectionType.PERSONAL -> PersonalDetailsSection(resumeDetails.resume, onBack = ::guardedOnBack) { name, designation, email, phone, address ->
                    viewModel.updatePersonalDetails(name, designation, email, phone, address)
                    afterSave(hasPendingBack = true)
                }
                EditorSectionType.PHOTO -> ImageCaptureSection(
                    title = stringResource(EditorSectionType.PHOTO.titleRes),
                    currentPath = resumeDetails.resume.profilePhotoPath,
                    storageFileName = "profile_$resumeId.jpg",
                    jpegQuality = 70,
                    onImageSaved = { path -> viewModel.setProfilePhoto(path); afterSave(hasPendingBack = true) },
                    onBack = ::guardedOnBack
                )
                EditorSectionType.OBJECTIVE -> ObjectiveSection(
                    initialObjective = resumeDetails.resume.objective,
                    designation = resumeDetails.resume.designation,
                    onBack = ::guardedOnBack,
                    onSave = { viewModel.updateObjective(it); afterSave(hasPendingBack = true) }
                )
                EditorSectionType.DECLARATION -> DeclarationSection(
                    resumeDetails.resume.declaration,
                    resumeDetails.resume.declarationPlace,
                    resumeDetails.resume.declarationDate,
                    onBack = ::guardedOnBack
                ) { declaration, place, date -> viewModel.updateDeclaration(declaration, place, date); afterSave(hasPendingBack = true) }
                EditorSectionType.EDUCATION -> EducationSection(resumeDetails.education, resumeId) { viewModel.saveEducation(it); afterSave() }
                EditorSectionType.WORK_EXPERIENCE -> WorkExperienceSection(resumeDetails.workExperience, resumeId) { viewModel.saveWorkExperience(it); afterSave() }
                EditorSectionType.SKILLS -> SkillsSection(
                    items = resumeDetails.skills,
                    resumeId = resumeId,
                    designation = resumeDetails.resume.designation,
                    onSave = { viewModel.saveSkills(it); afterSave() }
                )
                EditorSectionType.PROJECTS -> ProjectsSection(resumeDetails.projects, resumeId) { viewModel.saveProjects(it); afterSave() }
                EditorSectionType.ACHIEVEMENTS -> SimpleListSection(
                    items = resumeDetails.achievements.map { it.achievementName },
                    fieldLabel = stringResource(co.resumeai.R.string.field_achievement),
                    placeholder = stringResource(co.resumeai.R.string.field_achievement_placeholder),
                    onAiGenerate = if (AiClient.isConfigured) { current ->
                        ResumeAiService.suggestAchievement(resumeDetails.resume.designation, current)
                    } else null
                ) { updated ->
                    viewModel.saveAchievements(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.AchievementEntity(resumeId = resumeId, orderIndex = index, achievementName = value)
                    })
                    afterSave()
                }
                EditorSectionType.LANGUAGES -> SimpleListSection(
                    items = resumeDetails.languages.map { it.languageName },
                    fieldLabel = stringResource(co.resumeai.R.string.field_language),
                    placeholder = stringResource(co.resumeai.R.string.field_language_placeholder)
                ) { updated ->
                    viewModel.saveLanguages(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.LanguageEntity(resumeId = resumeId, orderIndex = index, languageName = value)
                    })
                    afterSave()
                }
            }
        }
    }

    if (showAdPrompt) {
        SupportWithAdDialog(
            onWatchAd = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
                val proceedBack = pendingBack
                pendingBack = false
                // Navigate back once the ad activity itself closes, not immediately after
                // launching it — doing it immediately would pop this screen while the full-screen
                // ad is still showing on top of it, which is harmless visually (the ad covers
                // everything either way) but leaves the wrong screen underneath the instant it
                // closes if `onFinished` never fires for some reason.
                if (activity != null) {
                    adManager.showRewarded(activity) { if (proceedBack) onBack() }
                } else if (proceedBack) {
                    onBack()
                }
            },
            onSkip = {
                showAdPrompt = false
                adManager.markRewardedPromptShown()
                if (pendingBack) { pendingBack = false; onBack() }
            }
        )
    }
}
