package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.engine.LayoutEngine
import co.resume.domain.export.ResumeLayoutHtmlRenderer
import co.resume.domain.export.SampleResumeData
import co.resume.domain.export.TemplateCategory
import co.resume.domain.layout.CuratedTemplateCatalog
import co.resume.domain.layout.CuratedTemplateOption
import co.resume.domain.layout.LayoutCatalog
import co.resume.domain.theme.ThemeCatalog
import co.resume.domain.theme.withAccentOverride
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

/** [previewHtml] is null until [TemplateBrowseViewModel.renderPreview] has been called for this card — rendered lazily as cards scroll into view, not all 24 at once. */
data class TemplateUiModel(val option: CuratedTemplateOption, val previewHtml: String? = null)

@HiltViewModel
class TemplateBrowseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ResumeRepository,
    private val layoutEngine: LayoutEngine,
    private val htmlRenderer: ResumeLayoutHtmlRenderer,
) : ViewModel() {

    private val previews = MutableStateFlow<Map<String, String>>(emptyMap())
    private val _selectedCategory = MutableStateFlow(TemplateCategory.All)

    val selectedCategory: StateFlow<TemplateCategory> = _selectedCategory

    val templates: StateFlow<List<TemplateUiModel>> = combine(previews, _selectedCategory) { rendered, cat ->
        CuratedTemplateCatalog.templates
            .filter { cat == TemplateCategory.All || it.category == cat }
            .map { option -> TemplateUiModel(option, rendered[option.id]) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategory(category: TemplateCategory) {
        _selectedCategory.value = category
    }

    /** Renders one template's preview HTML on demand (called as its card scrolls into view), cached for the session. */
    fun renderPreview(option: CuratedTemplateOption) {
        if (previews.value.containsKey(option.id)) return
        viewModelScope.launch {
            val html = withContext(Dispatchers.Default) {
                val sample = SampleResumeData.forLayoutAndTheme(context, option.layoutId, option.themeId)
                val layout = layoutEngine.build(sample, LayoutCatalog.resolve(option.layoutId), ThemeCatalog.resolve(option.themeId))
                htmlRenderer.render(layout)
            }
            previews.value = previews.value + (option.id to html)
        }
    }

    private val variantPreviewHtml = MutableStateFlow<String?>(null)
    val colorPreview: StateFlow<String?> = variantPreviewHtml

    /** Re-renders the open preview dialog's sample with a candidate accent color and/or a candidate
     * layoutId (the mirrored Left/Right position for templates that offer one) — same variant-preview
     * capability TemplatePickerViewModel offers, so the Dashboard's "Browse Templates" preview dialog
     * isn't missing the color/position pickers the "Choose Template" flow already has. */
    fun renderColorPreview(option: CuratedTemplateOption, accentColorHex: String?, layoutId: String = option.layoutId) {
        viewModelScope.launch {
            val html = withContext(Dispatchers.Default) {
                val sample = SampleResumeData.forLayoutAndTheme(context, layoutId, option.themeId)
                val theme = ThemeCatalog.resolve(option.themeId).withAccentOverride(accentColorHex)
                val layout = layoutEngine.build(sample, LayoutCatalog.resolve(layoutId), theme)
                htmlRenderer.render(layout)
            }
            variantPreviewHtml.value = html
        }
    }

    fun clearColorPreview() {
        variantPreviewHtml.value = null
    }

    fun createResumeWithTemplate(
        name: String,
        designation: String,
        layoutId: String,
        themeId: String,
        accentColorHex: String?,
        onCreated: (Long) -> Unit,
    ) {
        viewModelScope.launch {
            val id = repository.createResume(name, designation)
            val resume = repository.getResumeWithDetails(id)?.resume ?: return@launch
            repository.updateResume(resume.copy(layoutId = layoutId, themeId = themeId, accentColorHex = accentColorHex))
            onCreated(id)
        }
    }
}
