package app.craft.myresume.ads.admob;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.RelativeLayout;

import app.craft.myresume.ads.MyApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

public class AdMobBanner {

    private Activity activity;
    private adMobSmallAdCallback listener;
    private RelativeLayout adContainer;
    String TAG = "BannerAdClass";
    private AdView adView;

    public interface adMobSmallAdCallback {
        void onAdLoaded();

        void onAdError(String error);
    }

    public void showAd(Activity context, RelativeLayout adContainer, adMobSmallAdCallback adMobSmallAdCallback) {
        this.activity = context;
        this.listener = adMobSmallAdCallback;
        this.adContainer = adContainer;
        if (!isOnline()) {
            listener.onAdError("No Internet Connection");
            return;
        }
        loadAd();
    }

    private void loadAd() {
        adView = new AdView(activity);
        adView.setAdSize(getAdSize());
        adView.setAdUnitId(MyApplication.AdMob_Banner);
        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                listener.onAdLoaded();
                adContainer.removeAllViews();
                RelativeLayout.LayoutParams bannerParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
                adContainer.addView(adView, bannerParameters);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.e(TAG, "onAdFailedToLoad: " + adError.toString());
            }

        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    private AdSize getAdSize() {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public boolean isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            return false;
        }
        return true;
    }

}
