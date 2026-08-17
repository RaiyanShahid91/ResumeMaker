package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.engine.LayoutEngine
import co.resume.domain.export.ResumeLayoutHtmlRenderer
import co.resume.domain.export.SampleResumeData
import co.resume.domain.export.TemplateCategory
import co.resume.domain.layout.CuratedTemplateCatalog
import co.resume.domain.layout.LayoutCatalog
import co.resume.domain.theme.FontCatalog
import co.resume.domain.theme.ThemeCatalog
import co.resume.domain.theme.withAccentOverride
import co.resume.domain.theme.withFontOverride
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

data class TemplatePickerUiState(
    val templates: List<TemplateUiModel> = emptyList(),
    val selectedLayoutId: String? = null,
    val selectedThemeId: String? = null,
    val selectedAccentColorHex: String? = null,
    val selectedFontFamilyId: String? = null,
    val selectedFontSizeScale: Float? = null,
    val selectedCategory: TemplateCategory = TemplateCategory.All,
)

@HiltViewModel
class TemplatePickerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repository: ResumeRepository,
    private val layoutEngine: LayoutEngine,
    private val htmlRenderer: ResumeLayoutHtmlRenderer,
) : ViewModel() {

    private val resumeId: Long = savedStateHandle.get<Long>("resumeId") ?: -1L

    private val previews = MutableStateFlow<Map<String, String>>(emptyMap())
    private val selectedCategory = MutableStateFlow(TemplateCategory.All)

    val uiState: StateFlow<TemplatePickerUiState> = combine(
        previews,
        selectedCategory,
        repository.observeResume(resumeId),
    ) { rendered, category, details ->
        val filtered = CuratedTemplateCatalog.templates
            .filter { category == TemplateCategory.All || it.category == category }
            .map { option -> TemplateUiModel(option, rendered[option.id]) }
        TemplatePickerUiState(
            templates = filtered,
            selectedLayoutId = details?.resume?.layoutId,
            selectedThemeId = details?.resume?.themeId,
            selectedAccentColorHex = details?.resume?.accentColorHex,
            selectedFontFamilyId = details?.resume?.fontFamilyId,
            selectedFontSizeScale = details?.resume?.fontSizeScale,
            selectedCategory = category,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemplatePickerUiState())

    private val variantPreviewHtml = MutableStateFlow<String?>(null)
    val colorPreview: StateFlow<String?> = variantPreviewHtml

    fun selectCategory(category: TemplateCategory) {
        selectedCategory.value = category
    }

    fun renderPreview(option: co.resume.domain.layout.CuratedTemplateOption) {
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

    /** Re-renders the open preview dialog's sample with a candidate accent color, font family,
     * font size, and/or a candidate layoutId (the mirrored Left/Right position for templates that
     * offer one), without touching the stored resume — lets the user see the combination against
     * this template before applying it. */
    fun renderColorPreview(
        option: co.resume.domain.layout.CuratedTemplateOption,
        accentColorHex: String?,
        layoutId: String = option.layoutId,
        fontFamilyId: String? = null,
        fontSizeScale: Float? = null,
    ) {
        viewModelScope.launch {
            val html = withContext(Dispatchers.Default) {
                val sample = SampleResumeData.forLayoutAndTheme(context, layoutId, option.themeId)
                val theme = ThemeCatalog.resolve(option.themeId)
                    .withAccentOverride(accentColorHex)
                    .withFontOverride(FontCatalog.resolve(fontFamilyId)?.pairing)
                val layout = layoutEngine.build(sample, LayoutCatalog.resolve(layoutId), theme, sizeScale = fontSizeScale ?: 1f)
                htmlRenderer.render(layout)
            }
            variantPreviewHtml.value = html
        }
    }

    fun clearColorPreview() {
        variantPreviewHtml.value = null
    }

    fun selectTemplate(
        layoutId: String,
        themeId: String,
        accentColorHex: String?,
        fontFamilyId: String? = null,
        fontSizeScale: Float? = null,
    ) {
        viewModelScope.launch {
            val current = repository.getResumeWithDetails(resumeId)?.resume ?: return@launch
            repository.updateResume(
                current.copy(
                    layoutId = layoutId,
                    themeId = themeId,
                    accentColorHex = accentColorHex,
                    fontFamilyId = fontFamilyId,
                    fontSizeScale = fontSizeScale,
                )
            )
        }
    }
}
