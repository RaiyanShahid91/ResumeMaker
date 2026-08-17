package co.resume.auth

/** Single source of truth for the password rules — used both by [AuthViewModel][co.resume.ui.viewmodel.AuthViewModel]'s
 *  validation before calling Firebase, and by the live requirement checklist shown on the register
 *  screen, so the two can never drift out of sync. */
object PasswordPolicy {
    const val MIN_LENGTH = 8

    fun hasMinLength(password: String) = password.length >= MIN_LENGTH
    fun hasDigit(password: String) = password.any { it.isDigit() }
    fun hasSpecialChar(password: String) = password.any { !it.isLetterOrDigit() }

    fun isValid(password: String) = hasMinLength(password) && hasDigit(password) && hasSpecialChar(password)
}
