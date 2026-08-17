package co.resume.ui.screen.scan

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ui.viewmodel.ScannerViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

/**
 * Invisible composable that owns the whole capture step: camera permission, launching the
 * native ML Kit Document Scanner UI (edge detection, crop, filters, multi-page, reorder —
 * all handled by the scanner's own screens), and feeding the result into [viewModel]. Place
 * once per screen that can trigger a scan; call [ScannerViewModel.requestScan] to start one.
 */
@Composable
fun DocumentScannerHost(
    viewModel: ScannerViewModel,
    onScanReady: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scanRequestTick by viewModel.scanRequestTick.collectAsStateWithLifecycle()
    val cameraDeniedMsg = stringResource(R.string.scan_msg_camera_denied)
    val unavailableMsg = stringResource(R.string.scan_msg_unavailable)

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
            val pageUris = result?.pages.orEmpty().map { it.imageUri }
            if (result != null && pageUris.isNotEmpty()) {
                viewModel.onScanSuccess(context, pageUris, result.pdf?.uri)
                onScanReady()
            } else {
                viewModel.onScanCancelled()
            }
        } else {
            viewModel.onScanCancelled()
        }
    }

    fun launchScanner() {
        val currentActivity = activity ?: return
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(false)
            .setPageLimit(10)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        GmsDocumentScanning.getClient(options)
            .getStartScanIntent(currentActivity)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                viewModel.onScanFailed(it.message)
                Toast.makeText(context, unavailableMsg, Toast.LENGTH_SHORT).show()
            }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchScanner()
        } else {
            Toast.makeText(context, cameraDeniedMsg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(scanRequestTick) {
        if (scanRequestTick == 0) return@LaunchedEffect
        // Consume immediately: otherwise the tick stays non-zero forever, and if this
        // composable is ever disposed and re-entered (e.g. navigating to the scan result
        // screen and back), this same "new" key would relaunch the camera unprompted.
        viewModel.consumeScanRequest()
        val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (hasCameraPermission) {
            launchScanner()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
