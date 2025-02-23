package app.craft.myresume.ads.fb;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.craft.myresume.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;

import java.util.ArrayList;
import java.util.List;

public class FbNativeFullAds implements NativeAdListener {


    final com.facebook.ads.NativeAd nativeAd;

    final Context context;

    final NativeAdLayout nativeAdLayout;

    class MediaView implements MediaViewListener {
        MediaView(FbNativeFullAds cVar) {
        }

        public void onComplete(com.facebook.ads.MediaView mediaView) {
        }

        public void onEnterFullscreen(com.facebook.ads.MediaView mediaView) {
        }

        public void onExitFullscreen(com.facebook.ads.MediaView mediaView) {
        }

        public void onFullscreenBackground(com.facebook.ads.MediaView mediaView) {
        }

        public void onFullscreenForeground(com.facebook.ads.MediaView mediaView) {
        }

        public void onPause(com.facebook.ads.MediaView mediaView) {
        }

        public void onPlay(com.facebook.ads.MediaView mediaView) {
        }

        public void onVolumeChange(com.facebook.ads.MediaView mediaView, float f) {
        }
    }

    public FbNativeFullAds(com.facebook.ads.NativeAd nativeAd, Context context, NativeAdLayout nativeAdLayout) {
        this.nativeAd = nativeAd;
        this.context = context;
        this.nativeAdLayout = nativeAdLayout;
    }

    public void onAdClicked(Ad ad) {
    }

    public void onAdLoaded(Ad ad) {
        this.nativeAd.unregisterView();
        nativeAdLayout.setVisibility(View.VISIBLE);
        int i = 0;
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this.context).inflate(R.layout.fb_big_native, this.nativeAdLayout, false);
        this.nativeAdLayout.addView(linearLayout);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this.context, this.nativeAd, this.nativeAdLayout);
        adOptionsView.setIconColor(Color.parseColor("#000000"));
        linearLayout2.removeAllViews();
        linearLayout2.addView(adOptionsView, 0);
        com.facebook.ads.MediaView mediaView = (com.facebook.ads.MediaView) linearLayout.findViewById(R.id.native_ad_icon);
        TextView textView = (TextView) linearLayout.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView mediaView2 = (com.facebook.ads.MediaView) linearLayout.findViewById(R.id.native_ad_media);
        TextView textView2 = (TextView) linearLayout.findViewById(R.id.native_ad_social_context);
        TextView textView3 = (TextView) linearLayout.findViewById(R.id.native_ad_body);
        TextView textView4 = (TextView) linearLayout.findViewById(R.id.native_ad_sponsored_label);
        Button button = (Button) linearLayout.findViewById(R.id.native_ad_call_to_action);
        mediaView2.setListener(new MediaView(this));
        textView.setText(this.nativeAd.getAdvertiserName());
        textView3.setText(this.nativeAd.getAdBodyText());
        textView2.setText(this.nativeAd.getAdSocialContext());
        if (!this.nativeAd.hasCallToAction()) {
            i = 4;
        }
        button.setVisibility(i);
        button.setText(this.nativeAd.getAdCallToAction());
        textView4.setText("Sponsored");
        ArrayList arrayList = new ArrayList();
        arrayList.add(mediaView);
        arrayList.add(mediaView2);
        arrayList.add(button);
        this.nativeAd.registerViewForInteraction((View) linearLayout, mediaView2, mediaView, (List<View>) arrayList);
        NativeAdBase.NativeComponentTag.tagView(mediaView, NativeAdBase.NativeComponentTag.AD_ICON);
        NativeAdBase.NativeComponentTag.tagView(textView, NativeAdBase.NativeComponentTag.AD_TITLE);
        NativeAdBase.NativeComponentTag.tagView(textView3, NativeAdBase.NativeComponentTag.AD_BODY);
        NativeAdBase.NativeComponentTag.tagView(textView2, NativeAdBase.NativeComponentTag.AD_SOCIAL_CONTEXT);
        NativeAdBase.NativeComponentTag.tagView(button, NativeAdBase.NativeComponentTag.AD_CALL_TO_ACTION);
    }

    public void onError(Ad ad, AdError adError) {
        Log.e("hik's", "onError:n " + adError.getErrorCode() + " " + adError.getErrorMessage());
        nativeAdLayout.setVisibility(View.GONE);
    }

    public void onLoggingImpression(Ad ad) {
    }

    public void onMediaDownloaded(Ad ad) {
    }

}