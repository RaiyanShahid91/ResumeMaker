package app.craft.myresume.ads.max;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;


public class MAXNativeBannerAds {

    public static void MAXNativeBanner(Context context, FrameLayout adContainer) {

        adContainer.setVisibility(View.VISIBLE);

        MaxAdView adView = new MaxAdView(MyApplication.MAX_Banner, context);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {


            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = context.getResources().getDimensionPixelSize(R.dimen.banner_height_90);
        adView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        adContainer.addView(adView);
        adView.loadAd();

    }

}
