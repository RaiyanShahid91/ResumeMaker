package co.resume.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.analytics.Analytics
import co.resume.data.local.entity.DocumentKind
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.data.repository.ResumeRepository
import co.resume.domain.convert.ConverterStorage
import co.resume.domain.convert.PdfConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

enum class ConverterTool(val analyticsName: String) {
    IMAGE_TO_PDF("image_to_pdf"),
    MERGE_PDF("merge_pdf"),
    COMPRESS_PDF("compress_pdf"),
    PDF_TO_JPG("pdf_to_jpg")
}

data class ConverterUiState(
    val isProcessing: Boolean = false,
    val isDone: Boolean = false,
    val resultTitle: String = "",
    /** Set for PDF-producing tools (image-to-PDF, merge, compress) — the saved PDF's path. */
    val resultPdfPath: String? = null,
    /** Set for PDF-to-JPG — one path per rasterized page. */
    val resultImagePaths: List<String> = emptyList(),
    val isDownloading: Boolean = false,
    val downloadedMessage: String? = null,
    val errorMessage: String? = null
)

private data class ConversionOutput(val pdfPath: String? = null, val imagePaths: List<String> = emptyList())

/** Owns the PDF-converter toolkit's file-format work (image-to-PDF, merge, compress, PDF-to-JPG) and saves results into Documents. */
@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState: StateFlow<ConverterUiState> = _uiState

    fun reset() {
        _uiState.value = ConverterUiState()
    }

    fun convertImagesToPdf(context: Context, imageUris: List<Uri>, title: String) {
        if (imageUris.isEmpty()) return
        run(ConverterTool.IMAGE_TO_PDF, title) {
            val outputFile = File(ConverterStorage.documentsDir(context), safeFileName(title, "pdf"))
            PdfConverter.imagesToPdf(context, imageUris, outputFile)
            repository.saveScannedDocument(
                ScannedDocumentEntity(
                    title = title.ifBlank { defaultTitle("Converted") },
                    pdfPath = outputFile.absolutePath,
                    pageCount = imageUris.size,
                    ocrText = "",
                    needsReview = false,
                    kind = DocumentKind.CONVERTED_PDF
                )
            )
            ConversionOutput(pdfPath = outputFile.absolutePath)
        }
    }

    fun mergePdfs(context: Context, pdfUris: List<Uri>, title: String) {
        if (pdfUris.size < 2) {
            _uiState.value = ConverterUiState(errorMessage = "Pick at least 2 PDFs to merge")
            return
        }
        run(ConverterTool.MERGE_PDF, title) {
            val sourceFiles = pdfUris.mapIndexed { index, uri ->
                ConverterStorage.copyToCache(context, uri, "merge_src_${index}_${System.nanoTime()}.pdf")
                    ?: error("Couldn't read one of the selected PDFs")
            }
            val outputFile = File(ConverterStorage.documentsDir(context), safeFileName(title, "pdf"))
            try {
                PdfConverter.mergePdfs(context, sourceFiles, outputFile)
            } finally {
                sourceFiles.forEach { it.delete() }
            }
            repository.saveScannedDocument(
                ScannedDocumentEntity(
                    title = title.ifBlank { defaultTitle("Merged") },
                    pdfPath = outputFile.absolutePath,
                    pageCount = PdfConverter.pageCount(outputFile),
                    ocrText = "",
                    needsReview = false,
                    kind = DocumentKind.CONVERTED_PDF
                )
            )
            ConversionOutput(pdfPath = outputFile.absolutePath)
        }
    }

    fun compressPdf(context: Context, pdfUri: Uri, title: String) {
        run(ConverterTool.COMPRESS_PDF, title) {
            val sourceFile = ConverterStorage.copyToCache(context, pdfUri, "compress_src_${System.nanoTime()}.pdf")
                ?: error("Couldn't read the selected PDF")
            val outputFile = File(ConverterStorage.documentsDir(context), safeFileName(title, "pdf"))
            try {
                PdfConverter.compressPdf(context, sourceFile, outputFile)
            } finally {
                sourceFile.delete()
            }
            repository.saveScannedDocument(
                ScannedDocumentEntity(
                    title = title.ifBlank { defaultTitle("Compressed") },
                    pdfPath = outputFile.absolutePath,
                    pageCount = PdfConverter.pageCount(outputFile),
                    ocrText = "",
                    needsReview = false,
                    kind = DocumentKind.CONVERTED_PDF
                )
            )
            ConversionOutput(pdfPath = outputFile.absolutePath)
        }
    }

    /** Rasterizes every page of the picked PDF into its own JPG, each saved as a separate Documents entry. */
    fun pdfToJpegs(context: Context, pdfUri: Uri, title: String) {
        run(ConverterTool.PDF_TO_JPG, title) {
            val sourceFile = ConverterStorage.copyToCache(context, pdfUri, "topng_src_${System.nanoTime()}.pdf")
                ?: error("Couldn't read the selected PDF")
            val baseName = safeFileName(title, "").removeSuffix(".")
            val pages = try {
                PdfConverter.pdfToJpegs(sourceFile, ConverterStorage.imagesDir(context), baseName)
            } finally {
                sourceFile.delete()
            }
            val singlePage = pages.size == 1
            pages.forEachIndexed { index, pageFile ->
                repository.saveScannedDocument(
                    ScannedDocumentEntity(
                        title = if (singlePage) title.ifBlank { defaultTitle("Page") } else "${title.ifBlank { defaultTitle("Page") }} (${index + 1}/${pages.size})",
                        pdfPath = "",
                        pageCount = 1,
                        ocrText = "",
                        needsReview = false,
                        kind = DocumentKind.CONVERTED_IMAGE,
                        imagePath = pageFile.absolutePath
                    )
                )
            }
            ConversionOutput(imagePaths = pages.map { it.absolutePath })
        }
    }

    /** Copies the just-produced result (PDF or JPG pages) into the device's Downloads folder. */
    fun downloadResult(context: Context) {
        val state = _uiState.value
        if (state.isDownloading) return
        _uiState.value = state.copy(isDownloading = true, downloadedMessage = null)
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                if (state.resultImagePaths.isNotEmpty()) {
                    state.resultImagePaths.all { path ->
                        ConverterStorage.saveFileToDownloads(context, File(path), "image/jpeg") != null
                    }
                } else {
                    state.resultPdfPath?.let {
                        ConverterStorage.saveFileToDownloads(context, File(it), "application/pdf") != null
                    } ?: false
                }
            }
            val messageRes = if (success) co.resumeai.R.string.converter_download_success else co.resumeai.R.string.converter_download_failed
            _uiState.value = _uiState.value.copy(
                isDownloading = false,
                downloadedMessage = context.getString(messageRes)
            )
        }
    }

    private fun run(tool: ConverterTool, title: String, block: suspend () -> ConversionOutput) {
        _uiState.value = ConverterUiState(isProcessing = true)
        viewModelScope.launch {
            try {
                val output = withContext(Dispatchers.IO) { block() }
                _uiState.value = ConverterUiState(
                    isDone = true,
                    resultTitle = title,
                    resultPdfPath = output.pdfPath,
                    resultImagePaths = output.imagePaths
                )
                Analytics.logEvent(Analytics.Event.CONVERT_COMPLETED, mapOf(Analytics.Param.FORMAT to tool.analyticsName))
            } catch (e: Exception) {
                Analytics.logError("ConverterViewModel.${tool.analyticsName}", e)
                Analytics.logEvent(Analytics.Event.CONVERT_FAILED, mapOf(Analytics.Param.FORMAT to tool.analyticsName))
                _uiState.value = ConverterUiState(errorMessage = e.message ?: "Conversion failed")
            }
        }
    }

    private fun safeFileName(title: String, extension: String): String {
        val base = title.ifBlank { defaultTitle("Converted") }.replace(Regex("[^a-zA-Z0-9_\\- ]"), "_")
        return if (extension.isBlank()) base else "$base.$extension"
    }

    private fun defaultTitle(prefix: String): String =
        "${prefix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
}
