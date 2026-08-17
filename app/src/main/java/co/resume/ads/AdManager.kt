package co.resume.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import co.resume.billing.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Minimum time between rewarded "support the app" prompts, regardless of which save action
 *  triggered it — the resume editor alone has ~10 independent save points (one per section), so
 *  without a shared cooldown here a user filling in their resume would get offered an ad after
 *  every single field they save, which is exactly the "frustrated by ads" outcome to avoid. */
private const val REWARDED_PROMPT_COOLDOWN_MS = 5 * 60 * 1000L

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val subscriptionRepository: SubscriptionRepository
) {
    private var interstitial: InterstitialAd? = null
    private var rewarded: RewardedAd? = null
    private var lastRewardedPromptAt = 0L

    private val isPremium: Boolean get() = subscriptionRepository.isPremium.value

    fun preload() {
        if (isPremium) return
        if (interstitial != null) return
        InterstitialAd.load(
            context,
            AdConstants.INTERSTITIAL,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
                override fun onAdFailedToLoad(e: LoadAdError) { interstitial = null }
            }
        )
        preloadRewarded()
    }

    fun showInterstitial(activity: Activity, onComplete: () -> Unit) {
        if (isPremium) { onComplete(); return }
        val ad = interstitial
        if (ad == null) { onComplete(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitial = null
                preload()
                onComplete()
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { onComplete() }
        }
        ad.show(activity)
    }

    private fun preloadRewarded() {
        if (rewarded != null) return
        RewardedAd.load(
            context,
            AdConstants.REWARDED,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
                override fun onAdFailedToLoad(e: LoadAdError) { rewarded = null }
            }
        )
    }

    /** True at most once every [REWARDED_PROMPT_COOLDOWN_MS] — callers should only offer the
     *  "watch an ad to support us" prompt when this is true, and must call [markRewardedPromptShown]
     *  once they actually show it (not just when the user agrees) so a declined prompt still
     *  starts the cooldown rather than being re-offered on the very next save. */
    fun canOfferRewardedPrompt(): Boolean =
        !isPremium && rewarded != null && System.currentTimeMillis() - lastRewardedPromptAt > REWARDED_PROMPT_COOLDOWN_MS

    fun markRewardedPromptShown() {
        lastRewardedPromptAt = System.currentTimeMillis()
    }

    /** Shows the loaded rewarded ad if there is one; [onFinished] fires once either way (reward
     *  earned, ad dismissed without finishing, or failed to show) since watching it is always
     *  optional and never blocks whatever the user was already doing. */
    fun showRewarded(activity: Activity, onFinished: () -> Unit) {
        if (isPremium) { onFinished(); return }
        val ad = rewarded
        if (ad == null) { onFinished(); return }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewarded = null
                preloadRewarded()
                onFinished()
            }
            override fun onAdFailedToShowFullScreenContent(e: AdError) { onFinished() }
        }
        ad.show(activity) { /* reward earned — no in-app currency to grant, watching it through is the whole point */ }
    }
}
