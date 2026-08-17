package co.resume.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.ai.AiClient
import co.resume.ai.ResumeAiService
import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity
import co.resume.data.repository.ResumeRepository
import co.resume.domain.importer.ExtractedResumeData
import co.resume.domain.importer.ResumeImportParser
import co.resume.domain.importer.ResumeImportSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeListViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    val resumes: StateFlow<List<ResumeWithDetails>> = repository.observeAllResumesWithDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** True until the first emission arrives from the database, used to drive a shimmer skeleton. */
    val isLoading: StateFlow<Boolean> = resumes
        .map { false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun createResume(name: String, designation: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createResume(name, designation)
            onCreated(id)
        }
    }

    fun reorderResumes(orderedIds: List<Long>) {
        viewModelScope.launch { repository.reorderResumes(orderedIds) }
    }

    fun deleteResume(id: Long) {
        viewModelScope.launch { repository.softDeleteResume(id) }
    }

    fun duplicateResume(id: Long) {
        viewModelScope.launch { repository.duplicateResume(id) }
    }

    /**
     * Creates a new resume pre-filled from an existing PDF/photo the user picked, rather than the
     * blank name/designation form `createResume` starts from. Reads the file (PDF text layer or
     * OCR — see `ResumeImportSource`), asks the AI to structure it (`ResumeAiService
     * .extractResumeData`), and persists whatever came back non-empty. Deliberately narrates each
     * failure point via `error(...)` inside `runCatching` rather than a sealed result type — the
     * caller only needs one message to show, not to distinguish "bad file" from "bad AI response".
     */
    fun importResume(context: Context, uri: Uri, mimeType: String?, onResult: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            val result = runCatching {
                check(AiClient.isConfigured) { "AI is not configured" }
                val rawText = ResumeImportSource.readText(context, uri, mimeType)
                    ?: error("Couldn't read any text from that file")
                val extracted = ResumeImportParser.parse(ResumeAiService.extractResumeData(rawText))
                    ?: error("Couldn't understand that resume's content")
                persistExtracted(extracted)
            }
            onResult(result)
        }
    }

    private suspend fun persistExtracted(extracted: ExtractedResumeData): Long {
        val designation = extracted.workExperience.firstOrNull()?.jobTitle.orEmpty()
        val id = repository.createResume(
            name = extracted.name.ifBlank { "Imported Resume" },
            designation = designation
        )
        repository.updateResume(
            ResumeEntity(
                id = id,
                name = extracted.name.ifBlank { "Imported Resume" },
                designation = designation,
                email = extracted.email,
                phone = extracted.phone,
                address = extracted.address,
                objective = extracted.objective
            )
        )
        if (extracted.workExperience.isNotEmpty()) {
            repository.setWorkExperience(id, extracted.workExperience.mapIndexed { index, item ->
                WorkExperienceEntity(
                    resumeId = id,
                    orderIndex = index,
                    jobTitle = item.jobTitle,
                    company = item.company,
                    durationFrom = item.durationFrom,
                    durationTo = item.durationTo,
                    description = item.description
                )
            })
        }
        if (extracted.education.isNotEmpty()) {
            repository.setEducation(id, extracted.education.mapIndexed { index, item ->
                EducationEntity(
                    resumeId = id,
                    orderIndex = index,
                    course = item.course,
                    university = item.university,
                    grade = item.grade,
                    durationFrom = item.durationFrom,
                    durationTo = item.durationTo
                )
            })
        }
        if (extracted.skills.isNotEmpty()) {
            repository.setSkills(id, extracted.skills.mapIndexed { index, name ->
                SkillEntity(resumeId = id, orderIndex = index, skillName = name)
            })
        }
        if (extracted.projects.isNotEmpty()) {
            repository.setProjects(id, extracted.projects.mapIndexed { index, item ->
                ProjectEntity(
                    resumeId = id,
                    orderIndex = index,
                    projectName = item.projectName,
                    description = item.description,
                    durationFrom = item.durationFrom,
                    durationTo = item.durationTo,
                    projectLink = item.projectLink
                )
            })
        }
        if (extracted.achievements.isNotEmpty()) {
            repository.setAchievements(id, extracted.achievements.mapIndexed { index, name ->
                AchievementEntity(resumeId = id, orderIndex = index, achievementName = name)
            })
        }
        if (extracted.languages.isNotEmpty()) {
            repository.setLanguages(id, extracted.languages.mapIndexed { index, name ->
                LanguageEntity(resumeId = id, orderIndex = index, languageName = name)
            })
        }
        return id
    }
}
