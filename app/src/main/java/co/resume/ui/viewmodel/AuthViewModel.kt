package co.resume.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.auth.AccountDataWiper
import co.resume.auth.AuthRepository
import co.resume.auth.AuthUser
import co.resume.auth.EmailPolicy
import co.resume.auth.PasswordPolicy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountDataWiper: AccountDataWiper
) : ViewModel() {

    val currentUser: StateFlow<AuthUser?> = authRepository.currentUser
    val isLoggedIn: Boolean get() = authRepository.isLoggedIn
    val hasPasswordProvider: Boolean get() = authRepository.hasPasswordProvider

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isSendingReset by mutableStateOf(false)
        private set
    var resetError by mutableStateOf<String?>(null)
        private set
    var resetSent by mutableStateOf(false)
        private set

    fun clearError() { errorMessage = null }

    /** For failures that happen outside a repository call (e.g. the Credential Manager /
     *  Google sign-in picker itself failing before we ever reach [signInWithGoogle]) — lets
     *  the same error banner + shake in [co.resume.ui.screen.auth.AuthScaffold] show it,
     *  instead of the failure being silently swallowed. */
    fun reportError(message: String) { errorMessage = message }

    fun clearResetState() {
        resetError = null
        resetSent = false
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            resetError = "Enter your email first"
            return
        }
        isSendingReset = true
        resetError = null
        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(email.trim())
                .onSuccess { resetSent = true }
                .onFailure { resetError = it.message ?: "Couldn't send reset email" }
            isSendingReset = false
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Enter your email and password"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.signInWithEmail(email.trim(), password)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Sign in failed" }
            isLoading = false
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Fill in all fields"
            return
        }
        if (!EmailPolicy.isValid(email)) {
            errorMessage = "Enter a valid email address"
            return
        }
        if (!PasswordPolicy.isValid(password)) {
            errorMessage = "Password doesn't meet the requirements below"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.registerWithEmail(name.trim(), email.trim(), password)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Account creation failed" }
            isLoading = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            authRepository.signInWithGoogleIdToken(idToken)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message ?: "Google sign-in failed" }
            isLoading = false
        }
    }

    var isChangingPassword by mutableStateOf(false)
        private set
    var changePasswordError by mutableStateOf<String?>(null)
        private set
    var changePasswordSuccess by mutableStateOf(false)
        private set

    fun clearChangePasswordState() {
        changePasswordError = null
        changePasswordSuccess = false
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        if (currentPassword.isBlank()) {
            changePasswordError = "Enter your current password"
            return
        }
        if (!PasswordPolicy.isValid(newPassword)) {
            changePasswordError = "New password doesn't meet the requirements below"
            return
        }
        isChangingPassword = true
        changePasswordError = null
        viewModelScope.launch {
            authRepository.changePassword(currentPassword, newPassword)
                .onSuccess { changePasswordSuccess = true }
                .onFailure { changePasswordError = it.message ?: "Couldn't change password" }
            isChangingPassword = false
        }
    }

    fun signOut() = authRepository.signOut()

    fun deleteAccount(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        isLoading = true
        viewModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess {
                    accountDataWiper.wipeAll()
                    onSuccess()
                }
                .onFailure { onFailure(it.message ?: "Couldn't delete account") }
            isLoading = false
        }
    }
}
