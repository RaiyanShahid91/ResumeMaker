package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.export.ResumeHtmlRenderer
import co.resume.domain.export.SampleResumeData
import co.resume.domain.export.TemplateCatalog
import co.resume.domain.export.TemplateCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TemplateBrowseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ResumeRepository
) : ViewModel() {

    private val allTemplates = MutableStateFlow<List<TemplateUiModel>>(emptyList())
    private val _selectedCategory = MutableStateFlow(TemplateCategory.All)

    val selectedCategory: StateFlow<TemplateCategory> = _selectedCategory

    val templates: StateFlow<List<TemplateUiModel>> = combine(allTemplates, _selectedCategory) { all, cat ->
        if (cat == TemplateCategory.All) all else all.filter { it.option.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isLoading: StateFlow<Boolean> = allTemplates.map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        viewModelScope.launch {
            allTemplates.value = withContext(Dispatchers.Default) {
                TemplateCatalog.templates.map { option ->
                    TemplateUiModel(
                        option = option,
                        previewHtml = ResumeHtmlRenderer.render(context, SampleResumeData.forTemplate(option.id))
                    )
                }
            }
        }
    }

    fun setCategory(category: TemplateCategory) {
        _selectedCategory.value = category
    }

    fun createResumeWithTemplate(name: String, designation: String, templateId: Int, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.createResume(name, designation)
            val resume = repository.getResumeWithDetails(id)?.resume ?: return@launch
            repository.updateResume(resume.copy(templateId = templateId))
            onCreated(id)
        }
    }
}
