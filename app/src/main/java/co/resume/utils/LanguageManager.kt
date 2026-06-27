package co.resume.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

data class AppLanguage(val code: String, val displayName: String, val nativeName: String)

object LanguageManager {

    val supportedLanguages = listOf(
        AppLanguage("en", "English",    "English"),
        AppLanguage("de", "German",     "Deutsch"),
        AppLanguage("es", "Spanish",    "Español"),
        AppLanguage("fr", "French",     "Français"),
        AppLanguage("it", "Italian",    "Italiano"),
        AppLanguage("pt", "Portuguese", "Português"),
        AppLanguage("tr", "Turkish",    "Türkçe"),
        AppLanguage("ar", "Arabic",     "العربية"),
        AppLanguage("iw", "Hebrew",     "עברית"),
        AppLanguage("hi", "Hindi",      "हिन्दी"),
        AppLanguage("ja", "Japanese",   "日本語")
    )

    private val supportedCodes = supportedLanguages.map { it.code }.toSet()

    fun getLanguage(code: String): AppLanguage =
        supportedLanguages.find { it.code == code } ?: supportedLanguages.first()

    /**
     * Wraps [context] with a new locale configuration using createConfigurationContext,
     * which is reliable on all API levels (unlike the deprecated updateConfiguration).
     */
    fun wrap(context: Context, languageCode: String): ContextWrapper {
        val code = if (languageCode in supportedCodes) languageCode else "en"
        val locale = if (code == "iw") Locale("iw") else Locale(code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocales(LocaleList(locale))

        val localizedContext = context.createConfigurationContext(config)
        return ContextWrapper(localizedContext)
    }

    fun getSavedLanguageCode(context: Context): String =
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString(Constants.APP_LANG, "en") ?: "en"
}
