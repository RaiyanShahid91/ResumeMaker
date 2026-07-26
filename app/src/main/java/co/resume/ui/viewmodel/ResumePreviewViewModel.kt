package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.export.ResumeHtmlRenderer
import co.resumeai.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ResumePreviewUiState(val html: String?, val fileTitle: String)

@HiltViewModel
class ResumePreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    repository: ResumeRepository
) : ViewModel() {

    private val resumeId: Long = savedStateHandle.get<Long>("resumeId") ?: -1L

    val uiState: StateFlow<ResumePreviewUiState> = repository.observeResume(resumeId)
        .map { details ->
            val defaultTitle = context.getString(R.string.editor_default_title)
            if (details == null) {
                ResumePreviewUiState(html = null, fileTitle = defaultTitle)
            } else {
                val title = details.resume.name.ifBlank { defaultTitle }
                ResumePreviewUiState(html = ResumeHtmlRenderer.render(context, details), fileTitle = "$title's Resume")
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumePreviewUiState(null, "Resume"))
}
