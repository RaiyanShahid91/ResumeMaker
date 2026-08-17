package co.resume.billing

import co.resume.auth.AuthRepository
import co.resume.auth.UserProfileRepository
import co.resume.utils.Constants
import co.resume.utils.SharedPref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Purchase facts worth showing back to the user in Settings — everything here comes straight
 *  off Play's [com.android.billingclient.api.Purchase], nothing invented locally. */
data class SubscriptionDetails(
    val basePlanId: String?,
    val orderId: String?,
    val purchaseTimeMillis: Long?
)

/**
 * Single source of truth for whether the user currently has an active premium subscription.
 * [isPremium] is the OR of two independent signals:
 *  - Play-verified: [setPremium]/[setPremiumWithDetails], called by [BillingManager] after
 *    actually querying Play — the normal path.
 *  - Remote override: `users/{uid}.isPremium` in Firestore, watched live via
 *    [UserProfileRepository.observeIsPremium] — this is what lets support manually grant
 *    Premium from the Firebase Console for a "I paid but it didn't unlock" report, without a
 *    new app release. It can only ever grant, never revoke, what Play itself reports (a false
 *    remote value just stops overriding; it never forces isPremium to false while Play says
 *    otherwise), since revocation must always come from real Play verification.
 *
 * [SharedPref] gives instant, offline-available state on app start before either signal has had
 * a chance to resolve.
 */
@Singleton
class SubscriptionRepository @Inject constructor(
    private val sharedPref: SharedPref,
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _playVerifiedActive = MutableStateFlow(sharedPref.getBoolean(Constants.PREMIUM_ACTIVE, false))
    private val _remoteOverrideActive = MutableStateFlow(false)

    val isPremium: StateFlow<Boolean> =
        combine(_playVerifiedActive, _remoteOverrideActive) { playActive, remoteActive -> playActive || remoteActive }
            .stateIn(scope, SharingStarted.Eagerly, sharedPref.getBoolean(Constants.PREMIUM_ACTIVE, false))

    val subscriptionDetails: StateFlow<SubscriptionDetails?> = MutableStateFlow(
        sharedPref.getString(Constants.SUBSCRIPTION_ORDER_ID)?.let {
            SubscriptionDetails(
                basePlanId = sharedPref.getString(Constants.SUBSCRIPTION_BASE_PLAN),
                orderId = it,
                purchaseTimeMillis = sharedPref.getLong(Constants.SUBSCRIPTION_PURCHASE_TIME).takeIf { t -> t > 0 }
            )
        }
    )

    init {
        // Re-subscribes to the right user's Firestore doc whenever who's logged in changes, and
        // stops listening (rather than leaking a listener) once nobody's logged in.
        scope.launch {
            authRepository.currentUser.collectLatest { user ->
                val uid = if (user != null) authRepository.currentUid else null
                if (uid == null) {
                    _remoteOverrideActive.value = false
                    return@collectLatest
                }
                userProfileRepository.observeIsPremium(uid).collect { remoteActive ->
                    _remoteOverrideActive.value = remoteActive
                }
            }
        }
        scope.launch {
            isPremium.collect { active -> sharedPref.saveBoolean(Constants.PREMIUM_ACTIVE, active) }
        }
    }

    fun setPremium(active: Boolean) {
        _playVerifiedActive.value = active
        val uid = authRepository.currentUid ?: return
        scope.launch { userProfileRepository.updateSubscriptionStatus(uid, active) }
    }

    /** Same as [setPremium] but also records what was actually purchased — call this from the
     *  real purchase-confirmed path (not the generic restore/re-verify path), since that's the
     *  only time these details are actually known. */
    fun setPremiumWithDetails(active: Boolean, basePlanId: String?, orderId: String?, purchaseTimeMillis: Long?) {
        _playVerifiedActive.value = active
        sharedPref.saveString(Constants.SUBSCRIPTION_BASE_PLAN, basePlanId)
        sharedPref.saveString(Constants.SUBSCRIPTION_ORDER_ID, orderId)
        purchaseTimeMillis?.let { sharedPref.saveLong(Constants.SUBSCRIPTION_PURCHASE_TIME, it) }
        val uid = authRepository.currentUid ?: return
        scope.launch { userProfileRepository.updateSubscriptionDetails(uid, active, basePlanId, orderId, purchaseTimeMillis) }
    }
}
