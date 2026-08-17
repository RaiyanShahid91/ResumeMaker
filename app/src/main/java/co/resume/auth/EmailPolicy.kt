package co.resume.auth

import android.util.Patterns

/** Client-side email format check — same idea as [PasswordPolicy]: one place both the live
 *  on-screen checklist and the submit-time validation in `AuthViewModel` read from, so they
 *  can't disagree with each other. */
object EmailPolicy {
    fun isValid(email: String): Boolean =
        email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
