package co.resume.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

enum class SignInProvider { EMAIL, GOOGLE, UNKNOWN }

data class AuthUser(val displayName: String?, val email: String?, val provider: SignInProvider)

private fun FirebaseUser.toAuthUser(): AuthUser {
    // providerData almost always has 2 entries here: "firebase" (always present) plus the real
    // sign-in method ("password" or "google.com") — filtering that out is what tells the two apart.
    val providerId = providerData.firstOrNull { it.providerId != "firebase" }?.providerId
    val provider = when (providerId) {
        EmailAuthProvider.PROVIDER_ID -> SignInProvider.EMAIL
        GoogleAuthProvider.PROVIDER_ID -> SignInProvider.GOOGLE
        else -> SignInProvider.UNKNOWN
    }
    return AuthUser(displayName = displayName, email = email, provider = provider)
}

/**
 * Owns the single [FirebaseAuth] instance used for account sign-in. Firebase Auth persists
 * the signed-in session to disk itself — `auth.currentUser` is already restored by the time this
 * is constructed, so no extra local flag is needed to know "stay logged in across app restarts".
 */
@Singleton
class AuthRepository @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    private val auth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow(auth.currentUser?.toAuthUser())
    val currentUser: StateFlow<AuthUser?> = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser?.toAuthUser()
        }
    }

    val isLoggedIn: Boolean get() = auth.currentUser != null
    val currentUid: String? get() = auth.currentUser?.uid

    /** False for accounts signed in only via Google — there's no password to change. */
    val hasPasswordProvider: Boolean
        get() = auth.currentUser?.providerData.orEmpty().any { it.providerId == EmailAuthProvider.PROVIDER_ID }

    suspend fun signInWithEmail(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await()
        Unit
    }

    suspend fun registerWithEmail(name: String, email: String, password: String): Result<Unit> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("Account created but no user returned")
        if (name.isNotBlank()) {
            user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        }
        // updateProfile doesn't update the cached FirebaseUser synchronously in all SDK versions —
        // push the name into our own state right away instead of waiting for the next auth event.
        _currentUser.value = AuthUser(displayName = name.ifBlank { null }, email = user.email, provider = SignInProvider.EMAIL)
        userProfileRepository.createUserProfile(user.uid, name.ifBlank { null }, user.email, SignInProvider.EMAIL)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    /** Firebase requires a recent sign-in before allowing a password change — reauthenticating
     *  with the current password here is what satisfies that, rather than surfacing a confusing
     *  "requires recent login" error and forcing the user to log out and back in. */
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No signed-in user")
        val email = user.email ?: throw IllegalStateException("Account has no email")
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<Unit> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        // Only write a profile document the first time this Google account signs in — on every
        // later login this stays false, so `createdAt` isn't overwritten on each sign-in.
        if (result.additionalUserInfo?.isNewUser == true) {
            val user = result.user
            if (user != null) userProfileRepository.createUserProfile(user.uid, user.displayName, user.email, SignInProvider.GOOGLE)
        }
        Unit
    }

    fun signOut() {
        auth.signOut()
    }

    /** Permanently deletes the Firebase account. Recent-login errors bubble up as a [Result] failure
     *  so the caller can prompt the user to re-authenticate rather than silently no-op. */
    suspend fun deleteAccount(): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("No signed-in user to delete")
        user.delete().await()
    }
}
