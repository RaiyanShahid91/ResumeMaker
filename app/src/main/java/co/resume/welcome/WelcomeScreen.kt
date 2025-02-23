package co.resume.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.craft.myresume.databinding.ActivityWelcomeScreenBinding
import co.resume.login.LoginActivity
import co.resume.main.DashboardActivity
import co.resume.register.RegisterActivity
import co.resume.resume.CreateResumeBottomSheet
import co.resume.resume.UpdateAppBottomSheet
import co.resume.utils.Constants
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import co.resume.utils.VersionCheckUtils
import kotlinx.coroutines.launch

class WelcomeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeScreenBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the language before setting the view
        loadLanguage(this)

        // Set the view after the language is loaded
        binding = ActivityWelcomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPref(this)

        if (!sharedPref.getString(Constants.EMAIL, "").isNullOrEmpty()) {
            startActivity(Intent(this@WelcomeScreen, DashboardActivity::class.java))
            finish()
        }

        getStarted()
        selectLanguage()

        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getString("App_Lang", "en") ?: "en"
        val fetchSelectedLangauge =
            when (language) {
                "en" -> "English"
                "es" -> "Spanish"
                "ja" -> "Japanese"
                else -> "English"
            }
        binding.selectedLanguage.text = fetchSelectedLangauge
    }

    override fun onResume() {
        super.onResume()
        // Reload the language in case it was changed while the activity was paused
        loadLanguage(this)
        checkForAppUpdate()
    }

    private fun getStarted() {
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this@WelcomeScreen, LoginActivity::class.java))
        }

        binding.btnStarted.setOnClickListener {
            startActivity(Intent(this@WelcomeScreen, RegisterActivity::class.java))
        }
    }

    private fun selectLanguage() {
        val languages = mapOf("Select" to "","English" to "en", "Spanish" to "es", "Japanese" to "ja")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            languages.keys.toList()
        )
        binding.langaugeTxt.adapter = adapter

        binding.langaugeTxt.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirstSelection = true
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }
                val selectedLanguage = languages[parent.getItemAtPosition(position).toString()]
                selectedLanguage?.let { changeLanguage(this@WelcomeScreen, it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun changeLanguage(context: Context, languageCode: String) {
        LanguageManager.setLocale(context, languageCode)
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            .putString(Constants.APP_LANG, languageCode)
            .apply()

        val intent = Intent(this, WelcomeScreen::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun loadLanguage(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("App_Lang", "en") ?: "en"
        LanguageManager.setLocale(context, language)
    }

    private fun checkForAppUpdate() {
        lifecycleScope.launch {
            val isUpdateAvailable = VersionCheckUtils.isUpdateAvailable(this@WelcomeScreen)
            if (isUpdateAvailable) {
                val bottomSheet = UpdateAppBottomSheet()
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }
    }
}