package co.resume.auth

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mirrors account + subscription facts to Cloud Firestore under `users/{uid}`, keyed by the
 * Firebase Auth UID. Deliberately never writes the password here — Firebase Authentication
 * already stores it (hashed, never in plain text) and re-storing it ourselves would just be a
 * second, redundant place for a credential leak to happen. Only identity fields (name, email,
 * uid, created-at) and subscription status are written.
 */
@Singleton
class UserProfileRepository @Inject constructor() {
    private val db = Firebase.firestore

    /** Called once, right after a brand-new account is created (email/password registration,
     *  or the first time a Google account signs in) — never on every login, so `createdAt`
     *  reflects when the account was actually created, not when it was last used. */
    suspend fun createUserProfile(uid: String, name: String?, email: String?, provider: SignInProvider): Result<Unit> = runCatching {
        val data = hashMapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "signInProvider" to provider.name,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    suspend fun updateSubscriptionStatus(uid: String, isPremium: Boolean): Result<Unit> = runCatching {
        val data = hashMapOf(
            "isPremium" to isPremium,
            "subscriptionUpdatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    /** Records the actual purchase facts (which base plan, Play's order ID, when) alongside the
     *  boolean flag, so a support agent looking at Firebase Console can see not just "is this
     *  user premium" but what they actually bought and when — needed to investigate "I paid but
     *  didn't get unlocked" reports. Payment mode is always "Google Play Billing" since that's
     *  the only payment method this app integrates (see Terms & Conditions). */
    suspend fun updateSubscriptionDetails(
        uid: String,
        isPremium: Boolean,
        basePlanId: String?,
        orderId: String?,
        purchaseTimeMillis: Long?
    ): Result<Unit> = runCatching {
        val data = hashMapOf(
            "isPremium" to isPremium,
            "subscriptionUpdatedAt" to FieldValue.serverTimestamp(),
            "subscription" to hashMapOf(
                "paymentMode" to "Google Play Billing",
                "basePlanId" to basePlanId,
                "orderId" to orderId,
                "purchaseTimeMillis" to purchaseTimeMillis
            )
        )
        db.collection("users").document(uid).set(data, SetOptions.merge()).await()
    }

    /** Live stream of Firebase Console's `isPremium` field for this user — this is what lets
     *  support manually grant Premium to someone whose Play purchase didn't get picked up
     *  locally (billing hiccup, etc.) by flipping this field in the console, without needing a
     *  new app release. Combined (OR'd) with the real Play verification in
     *  [co.resume.billing.SubscriptionRepository] rather than replacing it. */
    fun observeIsPremium(uid: String): Flow<Boolean> = callbackFlow {
        val registration = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.getBoolean("isPremium") ?: false)
            }
        awaitClose { registration.remove() }
    }

    /** Live stream of Firebase Console's `aiDisabled` field for this specific user — this is the
     *  per-user AI kill switch: set it to `true` on a `users/{uid}` document in the console to
     *  stop AI working for just that account, no app update needed. Defaults to false (AI stays
     *  on) for every user who doesn't have the field set at all. */
    fun observeAiDisabled(uid: String): Flow<Boolean> = callbackFlow {
        val registration = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.getBoolean("aiDisabled") ?: false)
            }
        awaitClose { registration.remove() }
    }
}
