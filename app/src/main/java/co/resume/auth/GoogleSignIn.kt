package co.resume.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * Launches the system "Sign in with Google" account picker and returns the Google ID token to
 * hand to [AuthRepository.signInWithGoogleIdToken] — or null if the returned credential wasn't
 * actually a Google ID token (shouldn't happen given we only requested that option, but the
 * Credential Manager API always returns the supertype). Throws on cancel/no-accounts/etc.,
 * left for the caller to catch since the right UI response (silent vs. a toast) is caller-specific.
 */
suspend fun requestGoogleIdToken(context: Context): String? {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(GoogleAuthConfig.WEB_CLIENT_ID)
        .build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    val result = credentialManager.getCredential(context, request)
    val credential = result.credential
    return if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        GoogleIdTokenCredential.createFrom(credential.data).idToken
    } else null
}

/** Maps a [requestGoogleIdToken] failure to UI-facing text, or null if it shouldn't be shown as
 *  an error at all (the user just closed the account picker — that's not a failure). The
 *  "framework"/"unknown" case below is what a missing or mismatched SHA-1 certificate
 *  fingerprint in Firebase looks like from here — Credential Manager can't tell us that
 *  directly, it just fails, so the message points at the most common real cause. */
fun googleSignInErrorMessage(error: Throwable): String? = when (error) {
    is GetCredentialCancellationException -> null
    is NoCredentialException -> "No Google account found on this device. Add one in Settings, or sign in with email instead."
    is GetCredentialException -> "Google sign-in isn't available right now. If this keeps happening, the app's SHA-1 fingerprint may not be registered in Firebase yet."
    else -> "Google sign-in failed: ${error.message ?: "please try again"}"
}
