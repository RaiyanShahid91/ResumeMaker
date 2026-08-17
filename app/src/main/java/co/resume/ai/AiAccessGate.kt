package co.resume.ai

/** Shared kill switch for AI features — [co.resume.ai.AiAccessRepository] keeps [isDisabled] in
 *  sync with `users/{uid}.aiDisabled` in Firestore, and [AiClient] refuses every request while
 *  it's true. A plain object (not Hilt-injected) because [AiClient] itself is a plain object
 *  called from many places without DI — this is the simplest way for it to see the current flag. */
object AiAccessGate {
    @Volatile
    var isDisabled: Boolean = false

    const val DISABLED_MESSAGE =
        "AI features have been temporarily paused due to an internal issue. We're working on it — please check back soon."
}

/** Thrown by [AiClient] when [AiAccessGate.isDisabled] is true, instead of attempting the
 *  network call at all. UI catch blocks should prefer this exception's message over their usual
 *  generic "AI failed" text, since this one is meant to be shown verbatim. */
class AiDisabledException(message: String = AiAccessGate.DISABLED_MESSAGE) : Exception(message)

/** Picks what a UI catch block should show: [AiDisabledException]'s specific message when
 *  that's what happened, otherwise the caller's usual generic fallback text (a real network/API
 *  error isn't something the app should show verbatim to the user). */
fun aiErrorMessage(error: Throwable, fallback: String): String =
    if (error is AiDisabledException) error.message ?: fallback else fallback
