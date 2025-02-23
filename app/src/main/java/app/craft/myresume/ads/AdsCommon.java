package app.craft.myresume.ads;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import app.craft.myresume.R;
import app.craft.myresume.ads.admob.AdMobBanner;
import app.craft.myresume.ads.admob.AdMobInterstitialClick;
import app.craft.myresume.ads.admob.AdMobInterstitialClickBack;
import app.craft.myresume.ads.admob.AdMobNative;
import app.craft.myresume.ads.fb.FbInterstitialAd;
import app.craft.myresume.ads.fb.FbNativeBannerAd;
import app.craft.myresume.ads.fb.FbNativeFullAds;
import app.craft.myresume.ads.max.MAXBannerAds;
import app.craft.myresume.ads.max.MAXInterstitialAds;
import app.craft.myresume.ads.max.MAXNativeAds;
import app.craft.myresume.ads.max.MAXNativeBannerAds;
import com.facebook.ads.AdSize;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;

public class AdsCommon {

    public static void OneTimeCall(Context context) {

        if (MyApplication.getuser_balance() == 0) {
            AdMobInterstitialClick.getInstance().loadInterOne((Activity) context);
            AdMobInterstitialClick.getInstance().loadInterTwo((Activity) context);
            AdMobInterstitialClick.getInstance().loadInterThree((Activity) context);

            AdMobInterstitialClickBack.getInstance().loadInterOne((Activity) context);
            AdMobInterstitialClickBack.getInstance().loadInterTwo((Activity) context);
            AdMobInterstitialClickBack.getInstance().loadInterThree((Activity) context);

            AppLovinSdk.getInstance( context ).setMediationProvider( "max" );
            AppLovinSdk.initializeSdk( context, new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
                {
                    // AppLovin SDK is initialized, start loading ads
                }
            } );

        }
    }

    //click to play game

    public static void InterstitialAd(Context context, Intent intent) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                context.startActivity(intent);

                AdMobInterstitialClick.getInstance().showInter((Activity) context, new AdMobInterstitialClick.MyCallback() {
                    public void callbackCall() {

                    }
                });

            } else if (MyApplication.Type.contains("fb")) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                dialog.show();

                FbInterstitialAd.getInstance().loadFbInter3((Activity) context, dialog, intent);

            } else if(MyApplication.Type.contains(MyApplication.strMAX)){

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                dialog.show();

                MAXInterstitialAds.MAXInterstitialAd((Activity) context, dialog, intent);

            } else if (MyApplication.Type.contains("mix")) {


                if(!TextUtils.isEmpty(MyApplication.AdMob_Int)){

                    context.startActivity(intent);

                    AdMobInterstitialClick.getInstance().showInter((Activity) context, new AdMobInterstitialClick.MyCallback() {
                        public void callbackCall() {

                        }
                    });

                } else if(!TextUtils.isEmpty(MyApplication.FbInter)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    FbInterstitialAd.getInstance().loadFbInter3((Activity) context, dialog, intent);

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Int)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    MAXInterstitialAds.MAXInterstitialAd((Activity) context, dialog, intent);

                } else {
                    context.startActivity(intent);
                }

            }

        } else {
            context.startActivity(intent);
        }

    }

    public static void InterstitialAdFinish(Activity context, Intent intent) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                context.startActivity(intent);
                context.finish();

                AdMobInterstitialClick.getInstance().showInter((Activity) context, new AdMobInterstitialClick.MyCallback() {
                    public void callbackCall() {

                    }
                });

            } else if (MyApplication.Type.contains("fb")) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                dialog.show();

                FbInterstitialAd.getInstance().loadFbInter3((Activity) context, dialog, intent);

            } else if(MyApplication.Type.contains(MyApplication.strMAX)){

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                dialog.show();

                MAXInterstitialAds.MAXInterstitialAdFinish((Activity) context, dialog, intent);

            } else if (MyApplication.Type.contains("mix")) {

                if(!TextUtils.isEmpty(MyApplication.AdMob_Int)){

                    context.startActivity(intent);
                    context.finish();

                    AdMobInterstitialClick.getInstance().showInter((Activity) context, new AdMobInterstitialClick.MyCallback() {
                        public void callbackCall() {

                        }
                    });

                } else if(!TextUtils.isEmpty(MyApplication.FbInter)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    FbInterstitialAd.getInstance().loadFbInter3((Activity) context, dialog, intent);

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Int)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    MAXInterstitialAds.MAXInterstitialAdFinish((Activity) context, dialog, intent);

                } else {

                    context.startActivity(intent);
                    context.finish();
                }

            }
        } else {
            context.startActivity(intent);
            context.finish();
        }

    }

    public static void InterstitialAdBackClick(Activity context) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                context.finish();

                AdMobInterstitialClickBack.getInstance().showInter(context, new AdMobInterstitialClickBack.MyCallback() {
                    public void callbackCall() {

                    }
                });

            } else if (MyApplication.Type.contains("fb")) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                //dialog.show();

                FbInterstitialAd.getInstance().loadFbInter1(context, dialog);

            } else if (MyApplication.Type.contains(MyApplication.strMAX)) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                //dialog.show();

                MAXInterstitialAds.MAXInterstitialAdBackAds((Activity) context, dialog);

            } else if (MyApplication.Type.contains("mix")) {


                if(!TextUtils.isEmpty(MyApplication.AdMob_Int)){

                    context.finish();

                    AdMobInterstitialClickBack.getInstance().showInter(context, new AdMobInterstitialClickBack.MyCallback() {
                        public void callbackCall() {

                        }
                    });

                } else if(!TextUtils.isEmpty(MyApplication.FbInter)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    //dialog.show();

                    FbInterstitialAd.getInstance().loadFbInter1(context, dialog);

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Int)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    //dialog.show();

                    MAXInterstitialAds.MAXInterstitialAdBackAds((Activity) context, dialog);

                } else {
                    context.finish();
                }

            }

        } else {
            context.finish();
        }

    }

    public static void InterstitialAdsOnly(Activity context) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                AdMobInterstitialClickBack.getInstance().showInter(context, new AdMobInterstitialClickBack.MyCallback() {
                    public void callbackCall() {

                    }
                });

            } else if (MyApplication.Type.contains("fb")) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.ads_dialog);
                dialog.setCancelable(false);
                dialog.show();

                FbInterstitialAd.getInstance().loadFbInter123(context, dialog);

            } else if (MyApplication.Type.contains(MyApplication.strMAX)) {

                MAXInterstitialAds.MAXInterstitialAdOnlyAds((Activity) context);

            } else if (MyApplication.Type.contains("mix")) {

                if(!TextUtils.isEmpty(MyApplication.AdMob_Int)){

                    AdMobInterstitialClickBack.getInstance().showInter(context, new AdMobInterstitialClickBack.MyCallback() {
                        public void callbackCall() {

                        }
                    });

                } else if(!TextUtils.isEmpty(MyApplication.FbInter)){

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.ads_dialog);
                    dialog.setCancelable(false);
                    dialog.show();

                    FbInterstitialAd.getInstance().loadFbInter123(context, dialog);

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Int)){

                    MAXInterstitialAds.MAXInterstitialAdOnlyAds((Activity) context);

                } else {

                }

            }
        } else {

        }

    }

    public static void RegulerBigNative(Context context, FrameLayout admob_native_frame, NativeAdLayout nativeAdLayout, FrameLayout nativeMax) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                AdMobNative.getInstance().showNativeBigFixOne((Activity) context, admob_native_frame);

            } else if (MyApplication.Type.contains("fb")) {

                com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(context, MyApplication.Fbnative);
                nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(new FbNativeFullAds(nativeAd, context, nativeAdLayout)).build());

            } else if(MyApplication.Type.contains(MyApplication.strMAX)){

                MAXNativeAds.NativeAdsMAX(context, nativeMax);

            } else if (MyApplication.Type.contains("mix")) {

                if(!TextUtils.isEmpty(MyApplication.AdMob_NativeAdvance)){

                    AdMobNative.getInstance().showNativeBigFixOne((Activity) context, admob_native_frame);

                } else if(!TextUtils.isEmpty(MyApplication.Fbnative)){

                    com.facebook.ads.NativeAd nativeAd = new com.facebook.ads.NativeAd(context, MyApplication.Fbnative);
                    nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(new FbNativeFullAds(nativeAd, context, nativeAdLayout)).build());

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Native)){

                    MAXNativeAds.NativeAdsMAX(context, nativeMax);

                } else {
                    admob_native_frame.setVisibility(View.GONE);
                    nativeAdLayout.setVisibility(View.GONE);
                }

            }

        } else {
            admob_native_frame.setVisibility(View.GONE);
            nativeAdLayout.setVisibility(View.GONE);
        }

    }

    public static void RegulerBanner(Context context, RelativeLayout admob_banner, LinearLayout adContainer) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                admob_banner.setVisibility(View.VISIBLE);
                new AdMobBanner().showAd((Activity) context, admob_banner, new AdMobBanner.adMobSmallAdCallback() {
                    @Override
                    public void onAdLoaded() {
                    }

                    @Override
                    public void onAdError(String error) {
                    }
                });

            } else if (MyApplication.Type.contains("fb")) {

                adContainer.setVisibility(View.VISIBLE);
                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, MyApplication.FbBanner, AdSize.BANNER_HEIGHT_50);
                adContainer.addView(adView);
                adView.loadAd();

            } else if(MyApplication.Type.contains(MyApplication.strMAX)){

                MAXBannerAds.MAXBanner(context, admob_banner);

            } else if (MyApplication.Type.contains("mix")) {

                if(!TextUtils.isEmpty(MyApplication.AdMob_Banner)){

                    admob_banner.setVisibility(View.VISIBLE);
                    new AdMobBanner().showAd((Activity) context, admob_banner, new AdMobBanner.adMobSmallAdCallback() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdError(String error) {
                        }
                    });

                } else if(!TextUtils.isEmpty(MyApplication.FbBanner)){

                    adContainer.setVisibility(View.VISIBLE);
                    com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, MyApplication.FbBanner, AdSize.BANNER_HEIGHT_50);
                    adContainer.addView(adView);
                    adView.loadAd();

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Banner)) {

                    MAXBannerAds.MAXBanner(context, admob_banner);

                } else {
                    admob_banner.setVisibility(View.GONE);
                    adContainer.setVisibility(View.GONE);
                }

            }

        } else {
            admob_banner.setVisibility(View.GONE);
            adContainer.setVisibility(View.GONE);
        }

    }

    public static void SmallNative(Context context, FrameLayout admob_small_native, NativeAdLayout native_banner_ad_container) {

        if (MyApplication.getuser_balance() == 0) {

            if (MyApplication.Type.contains("admob")) {

                AdMobNative.getInstance().showNativeSmallOne((Activity) context, admob_small_native);

            } else if (MyApplication.Type.contains("fb")) {

                NativeBannerAd nativeBannerAd = new NativeBannerAd(context, MyApplication.FbNativeB);
                nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).withAdListener(new FbNativeBannerAd(nativeBannerAd, context, new LinearLayout[1], native_banner_ad_container)).build());

            } else if(MyApplication.Type.contains(MyApplication.strMAX)){

                MAXNativeBannerAds.MAXNativeBanner(context, admob_small_native);

            }  else if (MyApplication.Type.contains("mix")) {

                if(!TextUtils.isEmpty(MyApplication.AdMob_NativeAdvance)){

                    AdMobNative.getInstance().showNativeSmallOne((Activity) context, admob_small_native);

                } else if(!TextUtils.isEmpty(MyApplication.Fbnative)){

                    NativeBannerAd nativeBannerAd = new NativeBannerAd(context, MyApplication.FbNativeB);
                    nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL).withAdListener(new FbNativeBannerAd(nativeBannerAd, context, new LinearLayout[1], native_banner_ad_container)).build());

                } else if(!TextUtils.isEmpty(MyApplication.MAX_Banner)) {

                    MAXNativeBannerAds.MAXNativeBanner(context, admob_small_native);

                } else {

                    admob_small_native.setVisibility(View.GONE);
                    native_banner_ad_container.setVisibility(View.GONE);

                }

            }

        } else {
            admob_small_native.setVisibility(View.GONE);
            native_banner_ad_container.setVisibility(View.GONE);
        }

    }

}
