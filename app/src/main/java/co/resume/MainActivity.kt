package co.resume

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import co.resume.ads.AdManager
import co.resume.analytics.Analytics
import co.resume.data.migration.TinyDbImporter
import co.resume.navigation.RootNavHost
import co.resume.navigation.Screen
import co.resume.ui.component.FloatingBlobsBackground
import co.resume.ui.theme.AppBackgroundGradient
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.utils.Constants
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tinyDbImporter: TinyDbImporter
    @Inject lateinit var adManager: AdManager
    @Inject lateinit var sharedPref: SharedPref

    /**
     * Called before onCreate — wraps the base context with the saved locale so that
     * all string resources and Compose stringResource() calls resolve in the right language.
     */
    override fun attachBaseContext(newBase: Context) {
        val code = LanguageManager.getSavedLanguageCode(newBase)
        super.attachBaseContext(LanguageManager.wrap(newBase, code))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch { tinyDbImporter.importIfNeeded() }
        adManager.preload()

        val onboardingDone = sharedPref.getBoolean(Constants.ONBOARDING_DONE, false)
        val startDestination = if (onboardingDone) Screen.Dashboard.route else Screen.Onboarding.route

        setContent {
            ResumeBuilderTheme {
                // Single continuous gradient + slowly floating blobs painted once behind the
                // whole nav host, so they never scroll, reset, or tile per-screen — every screen
                // sits on top of the same, continuously-animating background.
                Box(modifier = Modifier.fillMaxSize().background(AppBackgroundGradient)) {
                    FloatingBlobsBackground()
                    RootNavHost(
                        startDestination = startDestination,
                        onLanguageSelected = { code -> applyLanguageAndRestart(code) },
                        onOnboardingFinished = {
                            sharedPref.saveBoolean(Constants.ONBOARDING_DONE, true)
                            Analytics.logEvent(Analytics.Event.ONBOARDING_COMPLETED)
                        }
                    )
                }
            }
        }
    }

    private fun applyLanguageAndRestart(languageCode: String) {
        Analytics.logEvent(Analytics.Event.LANGUAGE_CHANGED, mapOf(Analytics.Param.LANGUAGE_CODE to languageCode))
        getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .edit()
            .putString(Constants.APP_LANG, languageCode)
            .apply()
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finishAffinity()
    }
}
