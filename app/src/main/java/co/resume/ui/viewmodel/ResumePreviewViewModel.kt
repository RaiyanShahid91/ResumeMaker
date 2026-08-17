package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.data.repository.ResumeRepository
import co.resume.domain.engine.ResumeLayout
import co.resume.domain.engine.ResumeLayoutBuilder
import co.resume.domain.export.ResumeDocumentExporter
import co.resume.domain.export.ResumeLayoutHtmlRenderer
import co.resumeai.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResumePreviewUiState(val html: String?, val layout: ResumeLayout?, val fileTitle: String)

@HiltViewModel
class ResumePreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    repository: ResumeRepository,
    private val layoutBuilder: ResumeLayoutBuilder,
    private val htmlRenderer: ResumeLayoutHtmlRenderer,
    private val documentExporter: ResumeDocumentExporter,
) : ViewModel() {

    private val resumeId: Long = savedStateHandle.get<Long>("resumeId") ?: -1L

    val uiState: StateFlow<ResumePreviewUiState> = repository.observeResume(resumeId)
        .map { details ->
            val defaultTitle = context.getString(R.string.editor_default_title)
            if (details == null) {
                ResumePreviewUiState(html = null, layout = null, fileTitle = defaultTitle)
            } else {
                val title = details.resume.name.ifBlank { defaultTitle }
                // Built once here (not per-renderer) so the WebView preview and the PDF
                // exporter all draw from the exact same ResumeLayout — see LayoutEngine's contract.
                val layout = layoutBuilder.build(details)
                ResumePreviewUiState(html = htmlRenderer.render(layout), layout = layout, fileTitle = "$title's Resume")
            }
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumePreviewUiState(null, null, "Resume"))

    // uiContext must be the calling screen's own Context (e.g. LocalContext.current), not this
    // ViewModel's injected @ApplicationContext — see ResumeDocumentExporter for why.
    fun exportPdf(layout: ResumeLayout, fileTitle: String, uiContext: Context) {
        viewModelScope.launch(Dispatchers.IO) { documentExporter.exportAsPdf(layout, fileTitle, uiContext) }
    }

    fun sharePdf(layout: ResumeLayout, fileTitle: String, chooserTitle: String, uiContext: Context) {
        viewModelScope.launch(Dispatchers.IO) { documentExporter.sharePdf(layout, fileTitle, chooserTitle, uiContext) }
    }
}
