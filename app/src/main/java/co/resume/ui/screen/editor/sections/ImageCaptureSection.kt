package co.resume.ui.screen.editor.sections

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import co.resumeai.R
import co.resume.domain.ImageStorage
import co.resume.ui.component.AppDialog
import co.resume.ui.theme.ResumeBuilderTheme
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun ImageCaptureSection(
    title: String,
    currentPath: String?,
    storageFileName: String,
    jpegQuality: Int,
    onImageSaved: (path: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val saveErrorMsg = stringResource(R.string.img_msg_save_error)
    val cameraDeniedMsg = stringResource(R.string.img_msg_camera_denied)
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPickerDialog by remember { mutableStateOf(false) }

    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                val savedPath = ImageStorage.recompressAndSave(context, resultUri, storageFileName, jpegQuality)
                if (savedPath != null) {
                    onImageSaved(savedPath)
                    onBack()
                } else {
                    Toast.makeText(context, saveErrorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun launchCrop(sourceUri: Uri) {
        val destination = Uri.fromFile(File(context.cacheDir, "cropped_$storageFileName"))
        val intent = UCrop.of(sourceUri, destination)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .getIntent(context)
        cropLauncher.launch(intent)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) launchCrop(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) pendingCameraUri?.let { launchCrop(it) }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = ImageStorage.createImageCaptureUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, cameraDeniedMsg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(PaddingValues(20.dp))) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (currentPath.isNullOrBlank()) {
                androidx.compose.material3.Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                AsyncImage(
                    model = currentPath,
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Button(
            onClick = { showPickerDialog = true },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        ) {
            Text(stringResource(R.string.img_btn_add_change, title))
        }
    }

    if (showPickerDialog) {
        AppDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text(stringResource(R.string.img_dialog_title)) },
            text = { Text(stringResource(R.string.img_dialog_text, title)) },
            confirmButton = {
                TextButton(onClick = {
                    showPickerDialog = false
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) { Text(stringResource(R.string.img_btn_camera)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPickerDialog = false
                    galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text(stringResource(R.string.img_btn_gallery)) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageCaptureSectionPreview() {
    ResumeBuilderTheme {
        ImageCaptureSection(
            title = "Profile photo",
            currentPath = null,
            storageFileName = "profile.jpg",
            jpegQuality = 85,
            onImageSaved = {},
            onBack = {}
        )
    }
}
