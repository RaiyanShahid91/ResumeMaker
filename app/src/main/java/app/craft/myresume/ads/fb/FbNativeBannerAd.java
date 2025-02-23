package app.craft.myresume.ads.fb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.craft.myresume.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;

import java.util.ArrayList;
import java.util.List;

public class FbNativeBannerAd implements NativeAdListener {

    final NativeBannerAd f16313a;

    final Context f16314b;

    final LinearLayout[] f16315c;

    final NativeAdLayout f16316d;

    public FbNativeBannerAd(NativeBannerAd nativeBannerAd, Context context, LinearLayout[] linearLayoutArr, NativeAdLayout nativeAdLayout) {
        this.f16313a = nativeBannerAd;
        this.f16314b = context;
        this.f16315c = linearLayoutArr;
        this.f16316d = nativeAdLayout;
    }

    public void onAdClicked(Ad ad) {
    }

    public void onAdLoaded(Ad ad) {

        f16316d.setVisibility(View.VISIBLE);
        Log.e("divrsity", "onAdLoaded: " + ad);
        NativeBannerAd nativeBannerAd = this.f16313a;

        nativeBannerAd.unregisterView();
        this.f16315c[0] = (LinearLayout) LayoutInflater.from(this.f16314b).inflate(R.layout.fb_banner_native, this.f16316d, false);
        this.f16316d.removeAllViews();
        this.f16316d.addView(this.f16315c[0]);
        RelativeLayout relativeLayout = (RelativeLayout) this.f16315c[0].findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this.f16314b, this.f16313a, this.f16316d, AdOptionsView.Orientation.HORIZONTAL, 20);
        relativeLayout.removeAllViews();
        relativeLayout.addView(adOptionsView, 0);
        TextView textView = (TextView) this.f16315c[0].findViewById(R.id.native_ad_title);
        TextView textView2 = (TextView) this.f16315c[0].findViewById(R.id.native_ad_social_context);
        TextView textView3 = (TextView) this.f16315c[0].findViewById(R.id.native_ad_sponsored_label);
        MediaView mediaView = (MediaView) this.f16315c[0].findViewById(R.id.native_icon_view);
        Button button = (Button) this.f16315c[0].findViewById(R.id.native_ad_call_to_action);
        button.setText(this.f16313a.getAdCallToAction());
        button.setVisibility(this.f16313a.hasCallToAction() ? 0 : 4);
        textView.setText(this.f16313a.getAdvertiserName());
        textView2.setText(this.f16313a.getAdSocialContext());
        textView3.setText("Sponsored");
        ArrayList arrayList = new ArrayList();
        arrayList.add(button);
        this.f16313a.registerViewForInteraction((View) this.f16315c[0], mediaView, (List<View>) arrayList);
//            if (nativeBannerAd != null && nativeBannerAd == ad) {
//            }
    }

    public void onError(Ad ad, AdError adError) {
        Log.e("diversity", "onError: " + adError.getErrorMessage());
    }

    public void onLoggingImpression(Ad ad) {
    }

    public void onMediaDownloaded(Ad ad) {
    }
}