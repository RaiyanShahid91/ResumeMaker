package co.resume.domain

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object ImageStorage {

    fun createImageCaptureUri(context: Context): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "NewPic")
            put(MediaStore.Images.Media.DESCRIPTION, "Captured photo")
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw IllegalStateException("Unable to create image capture URI")
    }

    fun recompressAndSave(context: Context, sourceUri: Uri, fileName: String, quality: Int): String? {
        return try {
            val bitmap = context.contentResolver.openInputStream(sourceUri)?.use { BitmapFactory.decodeStream(it) }
                ?: return null
            val outputDir = File(context.filesDir, "resume_images").apply { mkdirs() }
            val outputFile = File(outputDir, fileName)
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
