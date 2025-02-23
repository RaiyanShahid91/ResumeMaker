package app.craft.myresume.splashAds;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;
import app.craft.myresume.databinding.ActivityPrivacyTermsBinding;


public class PrivacyTermsActivity extends AppCompatActivity {

    Activity activity;

    private ActivityPrivacyTermsBinding binding; // Declare the binding instance

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyTermsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = PrivacyTermsActivity.this;

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent));

        binding.acceptButton.setOnClickListener(new android.view.View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(android.view.View v) {
                if (!binding.firstCheck.isChecked() || !binding.secondCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Check above options to continue", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    MyApplication.setuser_onetime(1);
                    finish();
                }
            }
        });

        binding.back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            binding.acceptButton.setText("Get Started");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}