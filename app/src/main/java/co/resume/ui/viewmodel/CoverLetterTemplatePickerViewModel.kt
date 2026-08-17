package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.analytics.Analytics
import co.resume.data.repository.CoverLetterRepository
import co.resume.domain.export.CoverLetterHtmlRenderer
import co.resume.domain.export.CoverLetterTemplateCatalog
import co.resume.domain.export.CoverLetterTemplateOption
import co.resume.domain.export.SampleCoverLetterData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class CoverLetterTemplateUiModel(val option: CoverLetterTemplateOption, val previewHtml: String)

data class CoverLetterTemplatePickerUiState(
    val templates: List<CoverLetterTemplateUiModel> = emptyList(),
    val selectedTemplateId: Int? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class CoverLetterTemplatePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repository: CoverLetterRepository
) : ViewModel() {

    private val coverLetterId: Long = savedStateHandle.get<Long>("coverLetterId") ?: -1L

    private val allPreviews = MutableStateFlow<List<CoverLetterTemplateUiModel>>(emptyList())

    val uiState: StateFlow<CoverLetterTemplatePickerUiState> = combine(
        allPreviews,
        repository.observeCoverLetter(coverLetterId)
    ) { previews, letter ->
        CoverLetterTemplatePickerUiState(
            templates = previews,
            selectedTemplateId = letter?.templateId,
            isLoading = previews.isEmpty()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoverLetterTemplatePickerUiState())

    init {
        viewModelScope.launch {
            allPreviews.value = withContext(Dispatchers.Default) {
                CoverLetterTemplateCatalog.templates.map { option ->
                    CoverLetterTemplateUiModel(
                        option = option,
                        previewHtml = CoverLetterHtmlRenderer.render(context, SampleCoverLetterData.forTemplate(option.id))
                    )
                }
            }
        }
    }

    fun selectTemplate(templateId: Int, accentColorHex: String?) {
        viewModelScope.launch {
            val current = repository.observeCoverLetter(coverLetterId).first() ?: return@launch
            repository.updateCoverLetter(current.copy(templateId = templateId, accentColorHex = accentColorHex))
            Analytics.logEvent(Analytics.Event.COVER_LETTER_TEMPLATE_SELECTED, mapOf(Analytics.Param.TEMPLATE_ID to templateId))
        }
    }
}
