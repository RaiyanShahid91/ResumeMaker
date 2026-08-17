package co.resume.domain.scan

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Result of running OCR on a single scanned page. */
data class OcrPageResult(val text: String, val needsReview: Boolean)

/**
 * Runs on-device text recognition (ML Kit Text Recognition v2, Latin script) on scanned pages.
 *
 * Note: the on-device recognizer does not expose a confidence score — `Text`/`TextBlock`/
 * `Line`/`Element` have no `confidence` field in this API (that's a Cloud Vision-only
 * feature). [needsReview] is therefore a heuristic, not a true confidence measure: a page is
 * flagged when very little text was recognized, or when the output looks mostly non-text
 * (e.g. noise), since those are the failure modes an offline recognizer actually exhibits.
 */
object ScanOcr {
    private const val MinReasonableLength = 20
    private const val MinAlphanumericRatio = 0.4

    suspend fun recognizeText(context: Context, imageUri: Uri): OcrPageResult {
        val image = InputImage.fromFilePath(context, imageUri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val text = suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { result -> continuation.resume(result.text) }
                .addOnFailureListener { error -> continuation.resumeWithException(error) }
        }
        return OcrPageResult(text = text, needsReview = looksUnreliable(text))
    }

    private fun looksUnreliable(text: String): Boolean {
        val trimmed = text.trim()
        if (trimmed.length < MinReasonableLength) return true
        val alnumCount = trimmed.count { it.isLetterOrDigit() }
        return alnumCount.toDouble() / trimmed.length < MinAlphanumericRatio
    }
}
