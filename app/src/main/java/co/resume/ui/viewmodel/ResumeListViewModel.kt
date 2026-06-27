package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeListViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    val resumes: StateFlow<List<ResumeEntity>> = repository.observeAllResumes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createResume(name: String, designation: String, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createResume(name, designation)
            onCreated(id)
        }
    }

    fun deleteResume(id: Long) {
        viewModelScope.launch { repository.softDeleteResume(id) }
    }

    fun duplicateResume(id: Long) {
        viewModelScope.launch { repository.duplicateResume(id) }
    }
}
