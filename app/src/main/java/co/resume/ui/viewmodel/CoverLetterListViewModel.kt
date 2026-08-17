package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.analytics.Analytics
import co.resume.data.local.entity.CoverLetterEntity
import co.resume.data.repository.CoverLetterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoverLetterListViewModel @Inject constructor(
    private val repository: CoverLetterRepository
) : ViewModel() {

    val coverLetters: StateFlow<List<CoverLetterEntity>> = repository.observeAllCoverLetters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isLoading: StateFlow<Boolean> = coverLetters
        .map { false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun createCoverLetter(templateId: Int? = null, accentColorHex: String? = null, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createCoverLetter()
            if (templateId != null) {
                val current = repository.observeCoverLetter(id).first() ?: return@launch onCreated(id)
                repository.updateCoverLetter(current.copy(templateId = templateId, accentColorHex = accentColorHex))
            }
            Analytics.logEvent(Analytics.Event.COVER_LETTER_CREATED)
            onCreated(id)
        }
    }

    fun deleteCoverLetter(id: Long) {
        viewModelScope.launch { repository.deleteCoverLetter(id) }
    }
}
