package co.resume.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import co.resume.ads.AdConstants
import co.resume.ui.viewmodel.AdViewModel

/** Renders nothing for premium subscribers — banner ads are the ad surface premium removes. */
@Composable
fun BannerAdView(
    adUnitId: String = AdConstants.BANNER,
    modifier: Modifier = Modifier
) {
    // Android Studio's static @Preview renderer has no Hilt ViewModelStoreOwner — calling
    // hiltViewModel() there throws and takes down the whole preview (not just this composable),
    // which is why every screen that renders a BannerAdView() lower in its tree (Home, Settings,
    // every document list, every editor's bottom bar) previously failed to preview at all.
    if (LocalInspectionMode.current) return

    val adViewModel: AdViewModel = hiltViewModel()
    val isPremium by adViewModel.isPremium.collectAsStateWithLifecycle()
    if (isPremium) return

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
