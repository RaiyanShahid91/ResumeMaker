package app.craft.myresume.splashAds;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.craft.resumebuilder.HomeActivity;
import com.facebook.ads.NativeAdLayout;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;
import app.craft.myresume.ads.MyApplication;
import app.craft.myresume.databinding.ActivityFirstPageMainBinding;
import app.craft.myresume.databinding.PopupExitDialogBinding;

public class FirstPageMainActivity extends AppCompatActivity {

    private ActivityFirstPageMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstPageMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.Dark_Green));

        //one time call & load ads
        AdsCommon.OneTimeCall(this);


        //Reguler Banner Ads
        RelativeLayout admob_banner = (RelativeLayout) findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admob_banner, adContainer);


        //Reguler Native Ads
        FrameLayout admob_native_frame = (FrameLayout) findViewById(R.id.Admob_Native_Frame);
        NativeAdLayout nativeAdLayout = (NativeAdLayout) findViewById(R.id.native_ad_container);
        FrameLayout maxNative = (FrameLayout) findViewById(R.id.max_native_ad_layout);
        AdsCommon.RegulerBigNative(this, admob_native_frame, nativeAdLayout, maxNative);


        //Small Native Ads
        FrameLayout admob_small_native = (FrameLayout) findViewById(R.id.Admob_Small_Native);
        NativeAdLayout native_banner_ad_container = (NativeAdLayout) findViewById(R.id.native_banner_ad_container);
        AdsCommon.SmallNative(this, admob_small_native, native_banner_ad_container);


        binding.createCard.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
//            AdsCommon.InterstitialAd(FirstPageMainActivity.this, intent);
        });
        binding.btnrate.setOnClickListener(view -> {
            final String rateapp = getPackageName();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + rateapp));
            startActivity(intent1);
        });
        binding.btnshare.setOnClickListener(view -> {
            String appName = getResources().getString(R.string.app_name);
            final String appPackageName = getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, appName + " : \nhttps://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
        binding.btnmore.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + MyApplication.MoreApps));
//            startActivity(intent);
        });
        binding.btnprivacy.setOnClickListener(view -> {
            Intent intentPrivacy = new Intent(Intent.ACTION_VIEW, Uri.parse(MyApplication.PrivacyPolicy));
            intentPrivacy.setPackage("com.android.chrome");
            startActivity(intentPrivacy);
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExitDialog();
    }

    private void ExitDialog() {

        final Dialog dialog = new Dialog(FirstPageMainActivity.this, R.style.DialogTheme);
        PopupExitDialogBinding binding = PopupExitDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);

        //Reguler Native Ads
        FrameLayout admob_native_frame = (FrameLayout) dialog.findViewById(R.id.Admob_Native_Frame);
        NativeAdLayout nativeAdLayout = (NativeAdLayout) dialog.findViewById(R.id.native_ad_container);
        FrameLayout maxNative = (FrameLayout) dialog.findViewById(R.id.max_native_ad_layout);
        AdsCommon.RegulerBigNative(this, admob_native_frame, nativeAdLayout, maxNative);

        binding.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        binding.rate.setOnClickListener(v -> {
            final String rateapp = getPackageName();
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + rateapp));
            startActivity(intent1);
        });

        binding.yes.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(getApplicationContext(), AppThankYouActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            AdsCommon.InterstitialAd(FirstPageMainActivity.this, intent);
        });

        dialog.show();

    }


}
