package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.CoverLetterRepository
import co.resume.domain.export.CoverLetterHtmlRenderer
import co.resumeai.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CoverLetterPreviewUiState(val html: String?, val fileTitle: String)

@HiltViewModel
class CoverLetterPreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    repository: CoverLetterRepository
) : ViewModel() {

    private val coverLetterId: Long = savedStateHandle.get<Long>("coverLetterId") ?: -1L

    val uiState: StateFlow<CoverLetterPreviewUiState> = repository.observeCoverLetter(coverLetterId)
        .map { letter ->
            val defaultTitle = context.getString(R.string.cover_letter_default_title)
            if (letter == null) {
                CoverLetterPreviewUiState(html = null, fileTitle = defaultTitle)
            } else {
                val title = letter.senderName.ifBlank { defaultTitle }
                CoverLetterPreviewUiState(
                    html = CoverLetterHtmlRenderer.render(context, letter),
                    fileTitle = "$title's Cover Letter"
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoverLetterPreviewUiState(null, "Cover Letter"))
}
