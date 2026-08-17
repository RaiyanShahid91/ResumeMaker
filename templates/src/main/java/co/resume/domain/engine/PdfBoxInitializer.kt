package co.resume.domain.engine

import android.content.Context
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

/** PDFBoxResourceLoader.init() must run once before any font/PDF work; guards against the easy-to-forget crash if it doesn't. */
object PdfBoxInitializer {
    @Volatile private var initialized = false

    @Synchronized
    fun ensureInitialized(context: Context) {
        if (initialized) return
        PDFBoxResourceLoader.init(context.applicationContext)
        initialized = true
    }
}
