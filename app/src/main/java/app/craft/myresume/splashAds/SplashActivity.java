package app.craft.myresume.splashAds;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;
import app.craft.myresume.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.Dark_Green));


        MyApplication.setuser_balance(0);

        //Full Ads
        new Handler().postDelayed(this::goNext, 3000);


    }

    private void goNext() {
        loadOpenApp();
    }

    private void loadOpenApp() {
        if (MyApplication.getuser_onetime() == 0) {
            Intent i = new Intent(SplashActivity.this, PrivacyTermsActivity.class);
            startActivity(i);
        }else {
            Intent i = new Intent(SplashActivity.this, FirstPageMainActivity.class);
            startActivity(i);
        }
    }


}
