package co.resume.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import co.resume.ads.AdConstants

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
