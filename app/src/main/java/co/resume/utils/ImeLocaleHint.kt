package co.resume.utils

import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Forces the IME to restart input, allowing it to re-detect the current locale
 * from the app's Configuration (which is set via attachBaseContext after a language change).
 * Most modern keyboards (Gboard, Samsung) will switch to the matching language automatically.
 */
@Composable
fun ImeLocaleHint() {
    val context = LocalContext.current
    val view = LocalView.current
    val localeTag = LocalConfiguration.current.locales[0].toLanguageTag()

    LaunchedEffect(localeTag) {
        val imm = context.getSystemService(InputMethodManager::class.java)
        imm?.restartInput(view)
    }
}
