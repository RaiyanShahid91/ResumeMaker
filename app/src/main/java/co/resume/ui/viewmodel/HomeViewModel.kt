package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val recentResume: ResumeEntity? = null,
    val resumeCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: ResumeRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.observeAllResumes()
        .map { resumes ->
            HomeUiState(
                recentResume = resumes.maxByOrNull { it.updatedAt },
                resumeCount = resumes.size
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}
