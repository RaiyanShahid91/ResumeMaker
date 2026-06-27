package co.resume.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interstitial: InterstitialAd? = null

    fun preload() {
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
    }

    fun showInterstitial(activity: Activity, onComplete: () -> Unit) {
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
}
