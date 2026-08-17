package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.domain.export.CoverLetterHtmlRenderer
import co.resume.domain.export.CoverLetterTemplateCatalog
import co.resume.domain.export.SampleCoverLetterData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Renders every cover-letter template's preview HTML once, for the Home screen's "Featured Cover Letters" carousel. */
@HiltViewModel
class CoverLetterBrowseViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val allTemplates = MutableStateFlow<List<CoverLetterTemplateUiModel>>(emptyList())

    val templates: StateFlow<List<CoverLetterTemplateUiModel>> = allTemplates
    val isLoading: StateFlow<Boolean> = allTemplates.map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        viewModelScope.launch {
            allTemplates.value = withContext(Dispatchers.Default) {
                CoverLetterTemplateCatalog.templates.map { option ->
                    CoverLetterTemplateUiModel(
                        option = option,
                        previewHtml = CoverLetterHtmlRenderer.render(context, SampleCoverLetterData.forTemplate(option.id))
                    )
                }
            }
        }
    }
}
