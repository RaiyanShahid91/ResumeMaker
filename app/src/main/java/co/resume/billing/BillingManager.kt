package co.resume.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "BillingManager"

data class SubscriptionPlan(
    val basePlanId: String,
    val offerToken: String,
    val formattedPrice: String
)

/**
 * Thin wrapper around [BillingClient] for the single "premium" subscription product (monthly +
 * yearly base plans — see [BillingConstants]). Owns the connection lifecycle, exposes the
 * purchasable plans with Play-localized prices, and keeps [SubscriptionRepository] truthful by
 * re-querying active purchases on every (re)connect — that's what makes premium state survive
 * reinstalls/new devices on the same Google account without a backend.
 */
@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository
) {
    private val _plans = MutableStateFlow<List<SubscriptionPlan>>(emptyList())
    val plans: StateFlow<List<SubscriptionPlan>> = _plans

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { handlePurchase(it) }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            com.android.billingclient.api.PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    fun startConnection() {
        if (billingClient.isReady) return
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryActivePurchases()
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Play auto-retries transient disconnects on the next call that needs the
                // client; nothing to do here beyond leaving stale local state as-is.
            }
        })
    }

    private fun queryProductDetails() {
        val product = QueryProductDetailsParams.Product.newBuilder()
            .setProductId(BillingConstants.PREMIUM_PRODUCT_ID)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(product))
            .build()

        billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "queryProductDetails failed: ${result.debugMessage}")
                return@queryProductDetailsAsync
            }
            val details = productDetailsList.firstOrNull() ?: return@queryProductDetailsAsync
            _plans.value = details.subscriptionOfferDetails.orEmpty().mapNotNull { offer ->
                val basePlanId = offer.basePlanId
                val pricingPhase = offer.pricingPhases.pricingPhaseList.firstOrNull() ?: return@mapNotNull null
                SubscriptionPlan(
                    basePlanId = basePlanId,
                    offerToken = offer.offerToken,
                    formattedPrice = pricingPhase.formattedPrice
                )
            }
            productDetailsCache = details
        }
    }

    private var productDetailsCache: ProductDetails? = null

    // Play's Purchase object doesn't carry back which base plan (monthly/yearly) was bought —
    // remembering which offer the user just tapped is the only client-side way to know, since
    // this app has no backend to do proper server-side receipt validation.
    private var lastSelectedBasePlanId: String? = null

    /** Launches Play's native purchase sheet for the given base plan (see [BillingConstants]). */
    fun launchPurchase(activity: Activity, offerToken: String) {
        val details = productDetailsCache ?: return
        lastSelectedBasePlanId = _plans.value.find { it.offerToken == offerToken }?.basePlanId
        val offerParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .setOfferToken(offerToken)
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(offerParams))
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    /** Re-checks Play for an active subscription; call on app start and from a "Restore" action. */
    fun queryActivePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            val hasActive = purchases.any { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            subscriptionRepository.setPremium(hasActive)
            purchases.forEach { handlePurchase(it) }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        subscriptionRepository.setPremiumWithDetails(
            active = true,
            basePlanId = lastSelectedBasePlanId,
            orderId = purchase.orderId,
            purchaseTimeMillis = purchase.purchaseTime
        )
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(params) { /* best-effort; next queryActivePurchases retries */ }
        }
    }
}
