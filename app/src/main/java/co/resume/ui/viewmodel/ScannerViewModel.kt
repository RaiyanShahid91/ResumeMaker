package co.resume.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.analytics.Analytics
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.data.repository.ResumeRepository
import co.resume.domain.scan.ScanOcr
import co.resume.domain.scan.ScanStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/** One reviewed page from a scan session: its image, OCR text (user-editable), and flag. */
data class ScannedPageUi(
    val imageUri: Uri,
    val text: String,
    val needsReview: Boolean
)

data class ScanUiState(
    val isProcessing: Boolean = false,
    val isReady: Boolean = false,
    val pages: List<ScannedPageUi> = emptyList(),
    val pdfUri: Uri? = null,
    val errorMessage: String? = null
)

/**
 * Owns the whole document-scanner flow: shared across the Dashboard (which triggers the scan)
 * and the scan-result screen (which reviews/saves/shares it), by scoping this ViewModel to
 * the Dashboard's own NavBackStackEntry — see RootNavHost.
 */
@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    private val _scanRequestTick = MutableStateFlow(0)
    val scanRequestTick: StateFlow<Int> = _scanRequestTick

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState

    fun requestScan() {
        Analytics.logEvent(Analytics.Event.SCAN_STARTED)
        _scanRequestTick.value++
    }

    /**
     * Marks the current scan request as handled. Without this, [scanRequestTick] stays at its
     * last non-zero value forever, so if [DocumentScannerHost] ever leaves and re-enters
     * composition (e.g. navigating to the scan result screen and back), its LaunchedEffect sees
     * that same "new" key again and relaunches the camera unprompted.
     */
    fun consumeScanRequest() {
        _scanRequestTick.value = 0
    }

    fun onScanCancelled() {
        // Nothing was captured (or the user backed out mid-session) — leave any previous
        // result alone and just clear a stale error rather than resetting silently-in-progress state.
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onScanFailed(message: String?) {
        _uiState.value = ScanUiState(errorMessage = message ?: "Scan failed")
    }

    /** Runs OCR on every captured page, then marks the result ready for review. */
    fun onScanSuccess(context: Context, pageUris: List<Uri>, pdfUri: Uri?) {
        _uiState.value = ScanUiState(isProcessing = true)
        viewModelScope.launch {
            try {
                val pages = pageUris.map { uri ->
                    val ocr = ScanOcr.recognizeText(context, uri)
                    ScannedPageUi(imageUri = uri, text = ocr.text, needsReview = ocr.needsReview)
                }
                _uiState.value = ScanUiState(pages = pages, pdfUri = pdfUri, isReady = true)
                Analytics.logEvent(Analytics.Event.SCAN_COMPLETED, mapOf(Analytics.Param.PAGE_COUNT to pages.size))
            } catch (e: Exception) {
                Analytics.logError("ScannerViewModel.onScanSuccess", e)
                _uiState.value = ScanUiState(errorMessage = e.message ?: "Text recognition failed")
            }
        }
    }

    fun updatePageText(index: Int, text: String) {
        val pages = _uiState.value.pages.toMutableList()
        if (index !in pages.indices) return
        pages[index] = pages[index].copy(text = text)
        _uiState.value = _uiState.value.copy(pages = pages)
    }

    fun clearResult() {
        _uiState.value = ScanUiState()
    }

    /**
     * Saves the scanned PDF into the device's Downloads folder (or shares it on pre-Q) *and*
     * records it as a document — otherwise a PDF exported this way is visible in the system
     * file manager but never shows up in the app's own Documents tab, which only lists what's
     * in the database.
     */
    fun saveAsPdf(context: Context, title: String, onResult: (success: Boolean) -> Unit) {
        val pdfUri = _uiState.value.pdfUri
        if (pdfUri == null) {
            onResult(false)
            return
        }
        viewModelScope.launch {
            val savedToDownloads = ScanStorage.saveToDownloads(context, pdfUri, safeFileName(title)) != null
            val savedToDocuments = persistScannedDocument(context, title)
            onResult(savedToDownloads || savedToDocuments)
        }
    }

    private suspend fun persistScannedDocument(context: Context, title: String): Boolean {
        val state = _uiState.value
        val pdfUri = state.pdfUri ?: return false
        val fileName = safeFileName(title)
        val savedPath = ScanStorage.savePdf(context, pdfUri, fileName) ?: return false
        val baseName = fileName.removeSuffix(".pdf")
        val pagePaths = state.pages.mapIndexedNotNull { index, page ->
            ScanStorage.savePage(context, page.imageUri, "${baseName}_page${index + 1}.jpg")
        }
        repository.saveScannedDocument(
            ScannedDocumentEntity(
                title = title.ifBlank { defaultTitle() },
                pdfPath = savedPath,
                pageCount = state.pages.size,
                ocrText = state.pages.joinToString("\n\n") { it.text },
                needsReview = state.pages.any { it.needsReview },
                pagePaths = pagePaths.joinToString("\n")
            )
        )
        return true
    }

    private fun safeFileName(title: String): String =
        "${title.ifBlank { defaultTitle() }.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")}.pdf"

    private fun defaultTitle(): String =
        "Scan_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
}
