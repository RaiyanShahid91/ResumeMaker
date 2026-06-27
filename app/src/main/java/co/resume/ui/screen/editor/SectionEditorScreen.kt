package co.resume.ui.screen.editor

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.ui.screen.editor.sections.DeclarationSection
import co.resume.ui.screen.editor.sections.EducationSection
import co.resume.ui.screen.editor.sections.ImageCaptureSection
import co.resume.ui.screen.editor.sections.ObjectiveSection
import co.resume.ui.screen.editor.sections.PersonalDetailsSection
import co.resume.ui.screen.editor.sections.ProjectsSection
import co.resume.ui.screen.editor.sections.SimpleListSection
import co.resume.ui.screen.editor.sections.SkillsSection
import co.resume.ui.screen.editor.sections.WorkExperienceSection
import co.resume.ui.viewmodel.ResumeEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionEditorScreen(
    sectionKey: String,
    onBack: () -> Unit,
    viewModel: ResumeEditorViewModel = hiltViewModel()
) {
    val type = EditorSectionType.fromKey(sectionKey)
    val details by viewModel.resume.collectAsStateWithLifecycle()
    val resumeId = viewModel.resumeId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(type.titleRes)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        val resumeDetails = details ?: return@Scaffold
        Box(modifier = Modifier.padding(padding)) {
            when (type) {
                EditorSectionType.PERSONAL -> PersonalDetailsSection(resumeDetails.resume) { name, designation, email, phone, address ->
                    viewModel.updatePersonalDetails(name, designation, email, phone, address)
                }
                EditorSectionType.PHOTO -> ImageCaptureSection(
                    title = stringResource(EditorSectionType.PHOTO.titleRes),
                    currentPath = resumeDetails.resume.profilePhotoPath,
                    storageFileName = "profile_$resumeId.jpg",
                    jpegQuality = 70,
                    onImageSaved = { path -> viewModel.setProfilePhoto(path) }
                )
                EditorSectionType.SIGNATURE -> ImageCaptureSection(
                    title = stringResource(EditorSectionType.SIGNATURE.titleRes),
                    currentPath = resumeDetails.resume.signaturePath,
                    storageFileName = "signature_$resumeId.jpg",
                    jpegQuality = 80,
                    onImageSaved = { path -> viewModel.setSignature(path) }
                )
                EditorSectionType.OBJECTIVE -> ObjectiveSection(
                    initialObjective = resumeDetails.resume.objective,
                    designation = resumeDetails.resume.designation,
                    onSave = { viewModel.updateObjective(it) }
                )
                EditorSectionType.DECLARATION -> DeclarationSection(
                    resumeDetails.resume.declaration,
                    resumeDetails.resume.declarationPlace,
                    resumeDetails.resume.declarationDate
                ) { declaration, place, date -> viewModel.updateDeclaration(declaration, place, date) }
                EditorSectionType.EDUCATION -> EducationSection(resumeDetails.education, resumeId) { viewModel.saveEducation(it) }
                EditorSectionType.WORK_EXPERIENCE -> WorkExperienceSection(resumeDetails.workExperience, resumeId) { viewModel.saveWorkExperience(it) }
                EditorSectionType.SKILLS -> SkillsSection(
                    items = resumeDetails.skills,
                    resumeId = resumeId,
                    designation = resumeDetails.resume.designation,
                    onSave = { viewModel.saveSkills(it) }
                )
                EditorSectionType.PROJECTS -> ProjectsSection(resumeDetails.projects, resumeId) { viewModel.saveProjects(it) }
                EditorSectionType.ACHIEVEMENTS -> SimpleListSection(
                    items = resumeDetails.achievements.map { it.achievementName },
                    fieldLabel = "Achievement",
                    placeholder = "Secured first place in the national level hackathon",
                    onAiGenerate = if (AiClient.isConfigured) { current ->
                        ResumeAiService.suggestAchievement(resumeDetails.resume.designation, current)
                    } else null
                ) { updated ->
                    viewModel.saveAchievements(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.AchievementEntity(resumeId = resumeId, orderIndex = index, achievementName = value)
                    })
                }
                EditorSectionType.LANGUAGES -> SimpleListSection(
                    items = resumeDetails.languages.map { it.languageName },
                    fieldLabel = "Language",
                    placeholder = "English"
                ) { updated ->
                    viewModel.saveLanguages(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.LanguageEntity(resumeId = resumeId, orderIndex = index, languageName = value)
                    })
                }
                EditorSectionType.INTERESTS -> SimpleListSection(
                    items = resumeDetails.interests.map { it.interestName },
                    fieldLabel = "Interest",
                    placeholder = "Full stack application development"
                ) { updated ->
                    viewModel.saveInterests(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.InterestEntity(resumeId = resumeId, orderIndex = index, interestName = value)
                    })
                }
                EditorSectionType.HOBBIES -> SimpleListSection(
                    items = resumeDetails.hobbies.map { it.hobbyName },
                    fieldLabel = "Hobby",
                    placeholder = "Reading books"
                ) { updated ->
                    viewModel.saveHobbies(updated.mapIndexed { index, value ->
                        co.resume.data.local.entity.HobbyEntity(resumeId = resumeId, orderIndex = index, hobbyName = value)
                    })
                }
            }
        }
    }
}
