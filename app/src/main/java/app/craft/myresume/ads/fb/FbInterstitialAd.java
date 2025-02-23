package app.craft.myresume.ads.fb;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import app.craft.myresume.ads.MyApplication;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

public class FbInterstitialAd {

    public static InterstitialAd interstitialAd;
    private static String TAG = "";
    private static Activity mContext;
    private static FbInterstitialAd mInstance;
    private Dialog mDialogue;
    private Intent mIntent;

    public static FbInterstitialAd getInstance() {
        if (mInstance == null) {
            mInstance = new FbInterstitialAd();
        }
        return mInstance;
    }

    public void loadFbInter(Activity context, Dialog dialog, Intent intent) {

        mContext = context;
        mDialogue = dialog;
        mIntent = intent;

        AudienceNetworkAds.initialize(context);

        interstitialAd = new InterstitialAd(context, MyApplication.FbInter);


        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mContext.startActivity(intent);
                mContext.finish();

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                mDialogue.dismiss();

                mContext.startActivity(intent);

                mContext.finish();


//                loadFbInter(mContext, mDialogue, mIntent);

            }


            @Override
            public void onAdLoaded(Ad ad) {
                mDialogue.dismiss();
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }



    public void loadFbInter3(Activity context, Dialog dialog, Intent intent) {

        mContext = context;
        mDialogue = dialog;
        mIntent = intent;

        AudienceNetworkAds.initialize(context);

        interstitialAd = new InterstitialAd(context, MyApplication.FbInter);


        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mContext.startActivity(intent);
//                mContext.finish();

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                mDialogue.dismiss();

                mContext.startActivity(intent);

//                mContext.finish();


//                loadFbInter(mContext, mDialogue, mIntent);

            }


            @Override
            public void onAdLoaded(Ad ad) {
                mDialogue.dismiss();
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }



    public void loadFbInter1(Activity context, Dialog dialog) {

        mContext = context;
        mDialogue = dialog;
//        mIntent = intent;

        AudienceNetworkAds.initialize(context);

        interstitialAd = new InterstitialAd(context, MyApplication.FbInter);


        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
//                mContext.startActivity(intent);
                mDialogue.dismiss();
                mContext.finish();

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                mDialogue.dismiss();

//                mContext.startActivity(intent);//
                mContext.finish();

//                loadFbInter1(mContext, mDialogue, mIntent);

            }


            @Override
            public void onAdLoaded(Ad ad) {
                mDialogue.dismiss();
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }

    public void loadFbInter123(Activity context, Dialog dialog) {

        mContext = context;
        mDialogue = dialog;
//        mIntent = intent;

        AudienceNetworkAds.initialize(context);

        interstitialAd = new InterstitialAd(context, MyApplication.FbInter);


        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
//                mContext.startActivity(intent);
                mDialogue.dismiss();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

                mDialogue.dismiss();

//                mContext.startActivity(intent);//

//                loadFbInter1(mContext, mDialogue, mIntent);

            }


            @Override
            public void onAdLoaded(Ad ad) {
                mDialogue.dismiss();
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }


    public void loadFbInter2(Activity context, Dialog dialog) {

        mContext = context;
        mDialogue = dialog;
//        mIntent = intent;

        AudienceNetworkAds.initialize(context);

        interstitialAd = new InterstitialAd(context, MyApplication.FbInter);

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
//                mContext.startActivity(intent);
                mDialogue.dismiss();
//                mContext.finish();

            }

            @Override
            public void onError(Ad ad, AdError adError) {

                mDialogue.dismiss();

//                mContext.startActivity(intent);//
//                mContext.finish();

//                loadFbInter1(mContext, mDialogue, mIntent);

            }

            @Override
            public void onAdLoaded(Ad ad) {
                mDialogue.dismiss();
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        interstitialAd.loadAd(
                interstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }


}
