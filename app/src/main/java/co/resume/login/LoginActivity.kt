package co.resume.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import app.craft.myresume.databinding.ActivityLoginBinding
import co.resume.google.GoogleSignInHelper
import co.resume.main.DashboardActivity
import co.resume.register.RegisterActivity
import co.resume.resume.CreateResumeBottomSheet
import co.resume.resume.ForgetPasswordBottomSheet
import co.resume.utils.BaseActivity
import co.resume.utils.Constants
import co.resume.utils.CustomLoader
import co.resume.utils.CustomTextChangedListener
import co.resume.utils.FirebaseAuthRepository
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import co.resume.utils.SnackbarUtil
import co.resume.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var authViewModel: AuthViewModel
    private lateinit var customLoader: CustomLoader
    private val firebaseAuthRepository = FirebaseAuthRepository()
    private val RC_SIGN_IN = 9001
    private lateinit var sharedPref: SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadLanguage(this);

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        googleSignInHelper = GoogleSignInHelper(this)
        customLoader = CustomLoader(this)
        sharedPref = SharedPref(this)

        binding.btnGoogleLogin.setOnClickListener {
            googleSignInHelper.signInWithGoogle()
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.login.setOnClickListener {
            login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                customLoader.showLoader()
            } else {
                customLoader.hideLoader()
            }
        }

        authViewModel.authState.observe(this) { message ->
            authViewModel.dismissLoader()
            SnackbarUtil.showSnackbar(this, message.toString())
            sharedPref.saveString(Constants.EMAIL, binding.etEmail.text.toString().trim())
            startActivity(Intent(this, DashboardActivity::class.java))
            finishAffinity()
        }

        authViewModel.authError.observe(this) { error ->
            authViewModel.dismissLoader()
            SnackbarUtil.showSnackbar(this, error.toString())
        }

        addTextChanged()

        binding.forgetPasswordTv.setOnClickListener {
            val bottomSheet = ForgetPasswordBottomSheet()
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }

        binding.skipLogin.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finishAffinity()
        }
    }

    private fun addTextChanged() {
        binding.etEmail.addTextChangedListener(CustomTextChangedListener {
            validateForm()
        })
        binding.etPassword.addTextChangedListener(CustomTextChangedListener {
            validateForm()
        })
    }


    private fun validateForm() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        binding.login.isEnabled =
            email.isNotEmpty() && password.isNotEmpty()
    }

    private fun login(email: String, password: String) {
        if (email.isEmpty()) {
            SnackbarUtil.showSnackbar(this, Constants.EMAIL_CAN_NOT_BE_EMPTY)
            return
        }
        if (!isValidEmail(email)) {
            SnackbarUtil.showSnackbar(this, Constants.INVALID_EMAIL_FORMAT)
            return
        }
        if (password.isEmpty()) {
            SnackbarUtil.showSnackbar(this, Constants.PASSWORD_CAN_NOT_BE_EMPTY)
            return
        }
        authViewModel.loginWithEmail(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                val idToken = account.idToken
                val googleEmail = account.email
                if (idToken != null) {
                    firebaseAuthRepository.firebaseAuthWithGoogle(idToken) { success, message, isNewUser ->
                        if (success) {
                            if (isNewUser) {
                                // New user, save data
                                SnackbarUtil.showSnackbar(
                                    this,
                                    message ?: "Google Sign-Up Successful"
                                )
                                sharedPref.saveString(Constants.EMAIL, googleEmail)
                            } else {
                                SnackbarUtil.showSnackbar(this, "Google Sign-In Successful")
                            }
                            sharedPref.saveString(Constants.EMAIL, googleEmail)
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        } else {
                            SnackbarUtil.showSnackbar(this, message ?: "Google sign-in failed")
                        }
                    }
                }
            } else {
                SnackbarUtil.showSnackbar(this, "Google sign-in failed")
            }
        }
    }

    private fun loadLanguage(context: Context) {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("App_Lang", "en") ?: "en"
        LanguageManager.setLocale(context, language)
    }

}