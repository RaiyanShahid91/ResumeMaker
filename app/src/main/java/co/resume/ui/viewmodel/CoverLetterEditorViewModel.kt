package co.resume.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.local.entity.CoverLetterEntity
import co.resume.data.repository.CoverLetterRepository
import co.resume.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoverLetterEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: CoverLetterRepository,
    private val resumeRepository: ResumeRepository
) : ViewModel() {

    private val coverLetterId: Long = savedStateHandle.get<Long>("coverLetterId") ?: -1L

    val coverLetter: StateFlow<CoverLetterEntity?> = repository.observeCoverLetter(coverLetterId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** One-time prefill of sender info from the user's most recent resume, only for a blank letter. */
    fun prefillFromLatestResumeIfBlank() {
        viewModelScope.launch {
            val current = coverLetter.value ?: repository.observeCoverLetter(coverLetterId).first() ?: return@launch
            if (current.senderName.isNotBlank()) return@launch
            val latestResume = resumeRepository.observeAllResumes().first().firstOrNull() ?: return@launch
            repository.updateCoverLetter(
                current.copy(
                    senderName = latestResume.name,
                    senderEmail = latestResume.email,
                    senderPhone = latestResume.phone
                )
            )
        }
    }

    fun save(coverLetter: CoverLetterEntity) {
        viewModelScope.launch {
            repository.updateCoverLetter(coverLetter)
        }
    }

}
