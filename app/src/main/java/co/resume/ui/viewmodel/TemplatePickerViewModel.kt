package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.export.ResumeHtmlRenderer
import co.resume.domain.export.SampleResumeData
import co.resume.domain.export.TemplateCatalog
import co.resume.domain.export.TemplateCategory
import co.resume.domain.export.TemplateOption
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class TemplateUiModel(val option: TemplateOption, val previewHtml: String)

data class TemplatePickerUiState(
    val templates: List<TemplateUiModel> = emptyList(),
    val selectedTemplateId: Int? = null,
    val selectedCategory: TemplateCategory = TemplateCategory.All,
    val isLoading: Boolean = true
)

@HiltViewModel
class TemplatePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repository: ResumeRepository
) : ViewModel() {

    private val resumeId: Long = savedStateHandle.get<Long>("resumeId") ?: -1L

    private val allPreviews = MutableStateFlow<List<TemplateUiModel>>(emptyList())
    private val selectedCategory = MutableStateFlow(TemplateCategory.All)

    val uiState: StateFlow<TemplatePickerUiState> = combine(
        allPreviews,
        selectedCategory,
        repository.observeResume(resumeId)
    ) { previews, category, details ->
        val filtered = if (category == TemplateCategory.All) previews
                       else previews.filter { it.option.category == category }
        TemplatePickerUiState(
            templates = filtered,
            selectedTemplateId = details?.resume?.templateId,
            selectedCategory = category,
            isLoading = previews.isEmpty()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemplatePickerUiState())

    init {
        viewModelScope.launch {
            allPreviews.value = withContext(Dispatchers.Default) {
                TemplateCatalog.templates.map { option ->
                    TemplateUiModel(
                        option = option,
                        previewHtml = ResumeHtmlRenderer.render(context, SampleResumeData.forTemplate(option.id))
                    )
                }
            }
        }
    }

    fun selectCategory(category: TemplateCategory) {
        selectedCategory.value = category
    }

    fun selectTemplate(templateId: Int) {
        viewModelScope.launch {
            val current = repository.getResumeWithDetails(resumeId)?.resume ?: return@launch
            repository.updateResume(current.copy(templateId = templateId))
        }
    }
}
