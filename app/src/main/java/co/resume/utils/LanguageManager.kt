package co.resume.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object LanguageManager {
    private val supportedLanguages = listOf("en", "fr", "es", "ja")

    fun setLocale(context: Context, languageCode: String) {
        if (languageCode !in supportedLanguages) return

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }

        resources.updateConfiguration(config, resources.displayMetrics)
        context.applicationContext.resources.updateConfiguration(config, resources.displayMetrics)
    }
}