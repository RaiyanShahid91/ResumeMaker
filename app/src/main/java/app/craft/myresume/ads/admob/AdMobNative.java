package app.craft.myresume.ads.admob;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class AdMobNative {

    private static AdMobNative mInstance;
    public NativeAd nativeAd;

    public static AdMobNative getInstance() {
        if (mInstance == null) {
            mInstance = new AdMobNative();
        }
        return mInstance;
    }

    //350dp fix size native
    public void showNativeBigFixOne(final Activity activity, final FrameLayout frameLayout) {
        frameLayout.setVisibility(View.VISIBLE);
        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance2);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_big_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                AdMobNative.this.showNativeBigFixTwo(activity, frameLayout);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void showNativeBigFixTwo(final Activity activity, final FrameLayout frameLayout) {
        frameLayout.setVisibility(View.VISIBLE);
        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_big_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                AdMobNative.this.showNativeBigFixThree(activity, frameLayout);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void showNativeBigFixThree(final Activity activity, final FrameLayout frameLayout) {
        frameLayout.setVisibility(View.VISIBLE);
        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance2);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_big_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {



            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void showNativeSmallOne(final Activity activity, final FrameLayout frameLayout) {

        frameLayout.setVisibility(View.VISIBLE);

        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_small_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                AdMobNative.this.showNativeSmallTwo(activity, frameLayout);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void showNativeSmallTwo(final Activity activity, final FrameLayout frameLayout) {

        frameLayout.setVisibility(View.VISIBLE);

        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance2);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_small_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                AdMobNative.this.showNativeSmallThree(activity, frameLayout);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void showNativeSmallThree(final Activity activity, final FrameLayout frameLayout) {

        frameLayout.setVisibility(View.VISIBLE);

        AdLoader.Builder builder = new AdLoader.Builder(activity, MyApplication.AdMob_NativeAdvance);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if ((Build.VERSION.SDK_INT >= 17 ? activity.isDestroyed() : false) || activity.isFinishing() || activity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (AdMobNative.this.nativeAd != null) {
                    AdMobNative.this.nativeAd.destroy();
                }
                NativeAd unused = AdMobNative.this.nativeAd = nativeAd;
                NativeAdView nativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.google_small_native, (ViewGroup) null);
                AdMobNative.this.populateUnifiedNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build());
        builder.withAdListener(new AdListener() {
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                frameLayout.setVisibility(View.GONE);
                //new FailedSmallNative(activity, frameLayout);
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }

    public void populateUnifiedNativeAdView(NativeAd nativeAd2, NativeAdView nativeAdView) {
        nativeAdView.setMediaView((MediaView) nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd2.getHeadline());
        if (nativeAd2.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(4);
        } else {
            nativeAdView.getBodyView().setVisibility(0);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd2.getBody());
        }
        if (nativeAd2.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(4);
        } else {
            nativeAdView.getCallToActionView().setVisibility(0);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd2.getCallToAction());
        }
        if (nativeAd2.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(8);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd2.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(0);
        }
        if (nativeAd2.getPrice() == null) {
            nativeAdView.getPriceView().setVisibility(4);
        } else {
            nativeAdView.getPriceView().setVisibility(0);
            ((TextView) nativeAdView.getPriceView()).setText(nativeAd2.getPrice());
        }
        if (nativeAd2.getStore() == null) {
            nativeAdView.getStoreView().setVisibility(4);
        } else {
            nativeAdView.getStoreView().setVisibility(0);
            ((TextView) nativeAdView.getStoreView()).setText(nativeAd2.getStore());
        }
        if (nativeAd2.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(4);
        } else {
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd2.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(0);
        }
        if (nativeAd2.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(4);
        } else {
            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd2.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(0);
        }
        nativeAdView.setNativeAd(nativeAd2);
    }

    public void populateUnifiedNativeAdView100(NativeAd nativeAd2, NativeAdView nativeAdView, Activity activity) {
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd2.getHeadline());
        if (nativeAd2.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(4);
        } else {
            nativeAdView.getBodyView().setVisibility(0);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd2.getBody());
        }
        if (nativeAd2.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(4);
        } else {
            nativeAdView.getCallToActionView().setVisibility(0);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd2.getCallToAction());
        }
        if (nativeAd2.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(8);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd2.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(0);
        }
        if (nativeAd2.getStarRating() == null) {
            nativeAdView.getStarRatingView().setVisibility(4);
        } else {
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd2.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(0);
        }
        nativeAdView.setNativeAd(nativeAd2);
    }

}
