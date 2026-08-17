package co.resume.auth

/**
 * The OAuth "Web client" ID from Firebase Console → Project settings → General → Your apps
 * (or Authentication → Sign-in method → Google, once enabled). It only exists once a SHA-1
 * fingerprint has been added to the Android app and `google-services.json` has been
 * re-downloaded — until then this stays a placeholder and the Google button hides itself
 * (see [isGoogleSignInConfigured]) rather than crash on a bogus client ID.
 */
object GoogleAuthConfig {
    const val WEB_CLIENT_ID = "461034011055-qbejnlhmt262b9qu2aavv0101i2o58gr.apps.googleusercontent.com"

    val isGoogleSignInConfigured: Boolean
        get() = WEB_CLIENT_ID.isNotBlank()
}
