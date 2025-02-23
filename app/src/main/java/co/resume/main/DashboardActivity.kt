package co.resume.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.craft.myresume.R
import app.craft.myresume.databinding.ActivityDashboardBinding
import co.resume.resume.CreateResumeBottomSheet
import co.resume.resume.UpdateAppBottomSheet
import co.resume.utils.Constants
import co.resume.utils.FirebaseAuthRepository
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import co.resume.utils.VersionCheckUtils
import kotlinx.coroutines.launch


class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setUpNavController(navController)
        setUpClickListener()
        observeFragmentChanges(navController)
        loadLanguage(this)
        fetchUserData()
        checkForAppUpdate()
    }

    private fun setUpNavController(navController: NavController) {
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun setUpClickListener() {
        binding.createFab.setOnClickListener {
            showCreateResumeBottomSheet()
        }
    }

    private fun showCreateResumeBottomSheet() {
        val bottomSheet = CreateResumeBottomSheet()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    private fun observeFragmentChanges(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.settingsFragment || destination.id == R.id.homeFragment) {
                binding.createFab.visibility = View.GONE
            } else {
                binding.createFab.visibility = View.VISIBLE
            }
        }
    }

    private fun loadLanguage(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("App_Lang", "en") ?: "en"
        LanguageManager.setLocale(context, language)
    }

    private fun fetchUserData(){
        FirebaseAuthRepository().fetchUserData { success, name, email, password ->
            if (success) {
                sharedPref.saveString(Constants.CURRENT_USER_NAME,name)
                sharedPref.saveString(Constants.CURRENT_USER_EMAIL,name )
            }
        }
    }

    private fun checkForAppUpdate() {
        lifecycleScope.launch {
            val isUpdateAvailable = VersionCheckUtils.isUpdateAvailable(this@DashboardActivity)
            if (isUpdateAvailable) {
                val bottomSheet = UpdateAppBottomSheet()
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }
    }

}