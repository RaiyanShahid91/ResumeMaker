package app.craft.myresume.ads.max;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import app.craft.myresume.ads.MyApplication;

public class MAXInterstitialAds {

    public static MaxInterstitialAd maxInterstitialAd;

    public static void MAXInterstitialAd(Activity context, Dialog dialog, Intent intent) {

        maxInterstitialAd = new MaxInterstitialAd(MyApplication.MAX_Int, context);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                dialog.dismiss();
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                dialog.dismiss();
                context.startActivity(intent);
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                dialog.dismiss();
                context.startActivity(intent);

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        };
        maxInterstitialAd.setListener(adListener);
        maxInterstitialAd.loadAd();

    }


    public static void MAXInterstitialAdFinish(Activity context, Dialog dialog, Intent intent) {

        maxInterstitialAd = new MaxInterstitialAd(MyApplication.MAX_Int, context);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                dialog.dismiss();
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                dialog.dismiss();
                context.startActivity(intent);
                context.finish();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                dialog.dismiss();
                context.startActivity(intent);
                context.finish();

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        };
        maxInterstitialAd.setListener(adListener);
        maxInterstitialAd.loadAd();

    }


    public static void MAXInterstitialAdBackAds(Activity context, Dialog dialog) {

        maxInterstitialAd = new MaxInterstitialAd(MyApplication.MAX_Int, context);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                dialog.dismiss();
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                dialog.dismiss();
                context.finish();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                dialog.dismiss();
                context.finish();

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        };
        maxInterstitialAd.setListener(adListener);
        maxInterstitialAd.loadAd();

    }


    public static void MAXInterstitialAdOnlyAds(Activity context) {

        maxInterstitialAd = new MaxInterstitialAd(MyApplication.MAX_Int, context);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                if (maxInterstitialAd.isReady()) {
                    maxInterstitialAd.showAd();
                }
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
        };
        maxInterstitialAd.setListener(adListener);
        maxInterstitialAd.loadAd();

    }

}
