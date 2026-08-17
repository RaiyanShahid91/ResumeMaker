package co.resume.ai

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Fire-and-forget write to Firestore whenever a real Groq request fails (bad/expired key, Groq
 * outage, rate limit, etc.) — never called for [AiDisabledException], since that's an
 * intentional, already-known state rather than something to alert on. Check
 * Firebase Console → Firestore → `system/ai_status` to see the most recent failure without
 * needing an in-app admin dashboard; update the key via Remote Config's
 * `groq_api_key_obfuscated` parameter (see [RemoteApiKeyFetcher]) once you've rotated it.
 */
object AiFailureReporter {
    fun report(errorMessage: String, httpCode: Int? = null) {
        runCatching {
            Firebase.firestore.collection("system").document("ai_status").set(
                mapOf(
                    "lastError" to errorMessage.take(500),
                    "lastErrorCode" to httpCode,
                    "lastErrorAt" to FieldValue.serverTimestamp()
                )
            )
        }
    }
}
