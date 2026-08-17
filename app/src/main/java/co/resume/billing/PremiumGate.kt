package co.resume.billing

/**
 * Every download/share/export action in the app routes through this instead of calling straight
 * through to the export code. Non-premium users are hard-gated — the paywall opens and [action]
 * never runs — so there's no "skip" path back to a free export; premium users pass straight
 * through with no interruption.
 */
inline fun requirePremium(isPremium: Boolean, onOpenPaywall: () -> Unit, action: () -> Unit) {
    if (isPremium) action() else onOpenPaywall()
}
