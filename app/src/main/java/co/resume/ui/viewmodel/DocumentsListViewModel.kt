package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.data.repository.ResumeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentsListViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    val documents: StateFlow<List<ScannedDocumentEntity>> = repository.observeScannedDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** True until the first emission arrives from the database, used to drive a shimmer skeleton. */
    val isLoading: StateFlow<Boolean> = documents
        .map { false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun deleteDocument(id: Long) {
        viewModelScope.launch { repository.deleteScannedDocument(id) }
    }
}
