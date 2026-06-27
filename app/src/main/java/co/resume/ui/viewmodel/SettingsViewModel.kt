package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import co.resume.utils.AppLanguage
import co.resume.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    val availableLanguages: List<AppLanguage> = LanguageManager.supportedLanguages

    val currentLanguage: AppLanguage = LanguageManager.getLanguage(
        LanguageManager.getSavedLanguageCode(context)
    )
}
