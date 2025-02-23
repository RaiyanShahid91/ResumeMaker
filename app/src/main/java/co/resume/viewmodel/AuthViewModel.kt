package co.resume.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.resume.utils.FirebaseAuthRepository

class AuthViewModel : ViewModel() {

    private val firebaseAuthRepository = FirebaseAuthRepository()
    val authState = MutableLiveData<String?>()
    val authError = MutableLiveData<String?>()
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _emailExists = MutableLiveData<Boolean>()
    val emailExists: LiveData<Boolean> get() = _emailExists

    fun checkIfEmailExists(email: String) {
        firebaseAuthRepository.checkIfEmailExists(email) { exists ->
            _emailExists.value = exists
        }
    }


    fun registerWithEmail(email: String, password: String, name: String) {
        firebaseAuthRepository.registerWithEmail(email, password, name) { isSuccess, message ->
            _isLoading.value = true
            if (isSuccess) {
                dismissLoader()
                authState.value = message
            } else {
                dismissLoader()
                authError.value = message
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        firebaseAuthRepository.loginWithEmail(email, password) { isSuccess, message ->
            _isLoading.value = true
            if (isSuccess) {
                authState.value = message
            } else {
                authError.value = message
            }
        }
    }


    fun dismissLoader() {
        _isLoading.value = false
    }
}
