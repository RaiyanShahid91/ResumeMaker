package co.resume.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.HobbyEntity
import co.resume.data.local.entity.InterestEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity
import co.resume.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ResumeRepository
) : ViewModel() {

    val resumeId: Long = savedStateHandle.get<Long>("resumeId") ?: -1L

    val resume: StateFlow<ResumeWithDetails?> = repository.observeResume(resumeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updatePersonalDetails(name: String, designation: String, email: String, phone: String, address: String) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch {
            repository.updateResume(
                current.copy(name = name, designation = designation, email = email, phone = phone, address = address)
            )
        }
    }

    fun updateObjective(objective: String) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch { repository.updateResume(current.copy(objective = objective)) }
    }

    fun updateDeclaration(declaration: String, place: String, date: String) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch {
            repository.updateResume(
                current.copy(declaration = declaration, declarationPlace = place, declarationDate = date)
            )
        }
    }

    fun setProfilePhoto(path: String?) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch { repository.updateResume(current.copy(profilePhotoPath = path)) }
    }

    fun setSignature(path: String?) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch { repository.updateResume(current.copy(signaturePath = path)) }
    }

    fun setTemplate(templateId: Int) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch { repository.updateResume(current.copy(templateId = templateId)) }
    }

    fun setColors(primaryHex: String) {
        val current = resume.value?.resume ?: return
        viewModelScope.launch { repository.updateResume(current.copy(accentColorHex = primaryHex)) }
    }

    fun saveEducation(items: List<EducationEntity>) {
        viewModelScope.launch { repository.setEducation(resumeId, items) }
    }

    fun saveWorkExperience(items: List<WorkExperienceEntity>) {
        viewModelScope.launch { repository.setWorkExperience(resumeId, items) }
    }

    fun saveSkills(items: List<SkillEntity>) {
        viewModelScope.launch { repository.setSkills(resumeId, items) }
    }

    fun saveProjects(items: List<ProjectEntity>) {
        viewModelScope.launch { repository.setProjects(resumeId, items) }
    }

    fun saveAchievements(items: List<AchievementEntity>) {
        viewModelScope.launch { repository.setAchievements(resumeId, items) }
    }

    fun saveLanguages(items: List<LanguageEntity>) {
        viewModelScope.launch { repository.setLanguages(resumeId, items) }
    }

    fun saveInterests(items: List<InterestEntity>) {
        viewModelScope.launch { repository.setInterests(resumeId, items) }
    }

    fun saveHobbies(items: List<HobbyEntity>) {
        viewModelScope.launch { repository.setHobbies(resumeId, items) }
    }
}
