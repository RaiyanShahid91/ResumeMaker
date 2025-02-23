package co.resume.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import app.craft.myresume.R
import app.craft.myresume.databinding.ActivityRegisterBinding
import app.craft.myresume.splashAds.PrivacyTermsActivity
import co.resume.google.GoogleSignInHelper
import co.resume.login.LoginActivity
import co.resume.main.DashboardActivity
import co.resume.utils.Constants
import co.resume.utils.CustomTextChangedListener
import co.resume.utils.LanguageManager
import co.resume.utils.SharedPref
import co.resume.utils.SnackbarUtil
import co.resume.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInHelper: GoogleSignInHelper
    private lateinit var sharedPref: SharedPref
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadLanguage(this);

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        googleSignInHelper = GoogleSignInHelper(this)
        sharedPref = SharedPref(this)

        // Google Sign-In button click listener
        binding.ivGoogle.setOnClickListener {
            googleSignInHelper.signInWithGoogle()
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.registerContinue.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                SnackbarUtil.showSnackbar(this, "Please enter your name")
            } else if (email.isEmpty()) {
                SnackbarUtil.showSnackbar(this, "Please enter a valid email")
            } else if (password.isEmpty() || password.length < 6) {
                SnackbarUtil.showSnackbar(this, "Password must be at least 6 characters")
            } else {
                authViewModel.registerWithEmail(email, password, name)
            }
        }

        authViewModel.emailExists.observe(this) { exists ->
            if (exists) {
                SnackbarUtil.showSnackbar(this, "Email already exists. Please log in.")
            }
        }

        authViewModel.authState.observe(this) { message ->
            SnackbarUtil.showSnackbar(this, message.toString())
            sharedPref.saveString(Constants.EMAIL, binding.etEmail.text.toString())
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        authViewModel.authError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            println(error)
        }

        setTermsAndConditions()
        addTextChanged()
    }

    private fun setTermsAndConditions() {

        binding.termTxt.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, PrivacyTermsActivity::class.java))
        }

        /*val spannableText = SpannableString(getString(R.string.accept_term_condition))

        val greenColor = ContextCompat.getColor(this, R.color.colorAccent)

        val termsStart = spannableText.indexOf("Terms & Condition")
        val termsEnd = termsStart + "Terms & Condition".length
        spannableText.setSpan(
            ForegroundColorSpan(greenColor),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val privacyStart = spannableText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length
        spannableText.setSpan(
            ForegroundColorSpan(greenColor),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, PrivacyTermsActivity::class.java))
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RegisterActivity, PrivacyTermsActivity::class.java))
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.termTxt.text = spannableText
        binding.termTxt.movementMethod = LinkMovementMethod.getInstance()*/
    }

    private fun addTextChanged() {
        binding.etEmail.addTextChangedListener(CustomTextChangedListener {
            validateForm()
        })
        binding.etPassword.addTextChangedListener(CustomTextChangedListener {
            validateForm()
        })
        binding.etName.addTextChangedListener(CustomTextChangedListener {
            validateForm()
        })

        binding.skipLogin.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finishAffinity()
        }
    }

    private fun validateForm() {
        val email = binding.etEmail.text.toString()
        val name = binding.etName.text.toString()
        val password = binding.etPassword.text.toString()

        binding.registerContinue.isEnabled =
            email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty()
    }

    // Handle the Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                // Perform the Firebase authentication with the Google account
                val idToken = account.idToken
                val googleEmail = account.email
                if (idToken != null) {
                    googleSignInHelper.firebaseAuthWithGoogle(idToken) { success, message ->
                        if (success) {
                            sharedPref.saveString(Constants.EMAIL, googleEmail)
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finishAffinity()
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
