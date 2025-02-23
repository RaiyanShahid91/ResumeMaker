package co.resume.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.craft.myresume.R
import app.craft.myresume.databinding.FragmentSettingsBinding
import app.craft.myresume.databinding.LayoutTermsPrivacyBinding
import co.resume.login.LoginActivity
import co.resume.resume.ForgetPasswordBottomSheet
import co.resume.utils.Constants
import co.resume.utils.Constants.Companion.ALL_RESUME
import co.resume.utils.FirebaseAuthRepository
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import co.resume.utils.SnackbarUtil
import co.resume.welcome.WelcomeScreen
import com.craft.resumebuilder.tinydb.TinyDB
import com.google.android.material.bottomsheet.BottomSheetDialog

class SettingsFragment : Fragment() {
    private var binding: FragmentSettingsBinding? = null
    private lateinit var sharedPref: SharedPref
    private lateinit var tinyDB: TinyDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = SharedPref(requireContext())
        tinyDB = TinyDB(requireActivity())

        clickListener()
        binding?.appVersion?.text =
            "${getString(R.string.app_version)} ${getAppVersion(requireContext())}"

        val sharedPreferences = context?.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getString("App_Lang", "en") ?: "en"
        val fetchSelectedLangauge =
            when (language) {
                "en" -> "English"
                "es" -> "Spanish"
                "ja" -> "Japanese"
                else -> "English"
            }
        binding?.selectedLanguage?.text = fetchSelectedLangauge

        fetchUserInfo()

    }

    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName // e.g., "1.0.0"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    private fun clickListener() {
        binding?.termOfUseLable?.setOnClickListener { termOfUser() }
        binding?.privacyLable?.setOnClickListener { termOfUser() }
        binding?.termOfUseForward?.setOnClickListener { termOfUser() }
        binding?.privacyForward?.setOnClickListener { termOfUser() }

        binding?.writeEmailLabel?.setOnClickListener { sendEmail() }
        binding?.writeMailForward?.setOnClickListener { sendEmail() }

        binding?.rateLabel?.setOnClickListener { rateApp() }
        binding?.rateForward?.setOnClickListener { rateApp() }

        selectLanguage()

        binding?.logoutLabel?.setOnClickListener { confirmLogout() }
        binding?.logoutForwardImg?.setOnClickListener { confirmLogout() }

        binding?.changePasswordLabel?.setOnClickListener {
            showChangePassword()
        }
        binding?.passwordForwardImg?.setOnClickListener {
            showChangePassword()
        }

        binding?.deleteLabel?.setOnClickListener {
            confirmDeleteAccount()
        }

        binding?.deleteForwardImg?.setOnClickListener {
            confirmDeleteAccount()
        }


    }

    private fun showChangePassword(){
        val bottomSheet = ForgetPasswordBottomSheet.newInstance(sharedPref.getString(Constants.EMAIL).toString())
        bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
    }

    private fun termOfUser() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)

        val binding = LayoutTermsPrivacyBinding.inflate(bottomSheetDialog.layoutInflater)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundResource(android.R.color.transparent) // Remove the white background
        }


        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.show()
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:rssphere0@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "Feedback / Support")
            putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
        }

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rateApp() {
        val packageName = "app.craft.myresume"
        try {
            // Try opening the Play Store app
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$packageName")
            )
            intent.setPackage("com.android.vending")
            startActivity(intent)
        } catch (e: Exception) {
            // If Play Store app is not available, open Play Store in the browser
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            startActivity(intent)
        }
    }

    private fun selectLanguage() {
        val languages = mapOf("Select" to "", "English" to "en", "Spanish" to "es", "Japanese" to "ja")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            languages.keys.toList()
        )
        binding?.langaugeTxt?.adapter = adapter

        binding?.langaugeTxt?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                selectedLanguage?.let { changeLanguage(requireContext(), it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun changeLanguage(context: Context, languageCode: String) {
        LanguageManager.setLocale(context, languageCode)
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
            .putString(Constants.APP_LANG, languageCode)
            .apply()

        val intent = (context as? android.app.Activity)?.intent
        activity?.finishAffinity()
        context.startActivity(intent)
    }

    private fun logout() {
        sharedPref.remove(Constants.EMAIL)
        sharedPref.clear()
        tinyDB.clear()
        FirebaseAuthRepository().firebaseLogout {  }
        FirebaseAuthRepository().googleLogout(requireContext()){ }
        startActivity(Intent(requireContext(), WelcomeScreen::class.java))
        activity?.finishAffinity()
    }

    private fun fetchUserInfo(){
        if (!sharedPref.getString(Constants.EMAIL).isNullOrEmpty()){
            binding?.loginEmail?.text = sharedPref.getString(Constants.EMAIL)
            binding?.logoutImg?.visibility = View.VISIBLE
            binding?.logoutForwardImg?.visibility = View.VISIBLE
            binding?.logoutLabel?.visibility = View.VISIBLE
            binding?.changePasswordLabel?.visibility = View.VISIBLE
            binding?.passwordForwardImg?.visibility = View.VISIBLE
            binding?.pwImg?.visibility = View.VISIBLE
            binding?.deleteLabel?.visibility = View.VISIBLE
            binding?.deleteForwardImg?.visibility = View.VISIBLE
            binding?.deleteImg?.visibility = View.VISIBLE
        } else{
            binding?.loginEmail?.text = ""
            binding?.myAccountLabel?.text = getString(R.string.login)
            binding?.logoutImg?.visibility = View.GONE
            binding?.logoutForwardImg?.visibility = View.GONE
            binding?.logoutLabel?.visibility = View.GONE
            binding?.changePasswordLabel?.visibility = View.GONE
            binding?.passwordForwardImg?.visibility = View.GONE
            binding?.pwImg?.visibility = View.GONE
            binding?.deleteLabel?.visibility = View.GONE
            binding?.deleteForwardImg?.visibility = View.GONE
            binding?.deleteImg?.visibility = View.GONE
            binding?.myAccountLabel?.setOnClickListener {
              if (binding?.loginEmail?.text?.trim().toString().isEmpty()){
                  startActivity(Intent(requireContext(), LoginActivity::class.java))
                  activity?.finishAffinity()
              }
            }
        }
    }

    private fun confirmLogout() {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_logout_confirm, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.show()

        dialogView.findViewById<Button>(R.id.prompt_button_yes).setOnClickListener {
            logout()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.prompt_button_no).setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun confirmDeleteAccount() {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_delete_promt, null)

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        dialog.show()

        dialogView.findViewById<Button>(R.id.prompt_button_yes).setOnClickListener {
            FirebaseAuthRepository().deleteUserAccount { success, message ->
                if (success) {
                    Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    logout()
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finishAffinity()
                } else {
                    Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.prompt_button_no).setOnClickListener {
            dialog.dismiss()
        }
    }

}