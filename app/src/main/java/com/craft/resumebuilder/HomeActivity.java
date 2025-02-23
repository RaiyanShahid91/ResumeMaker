package com.craft.resumebuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;
import app.craft.myresume.databinding.ActivityHomeBinding;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    public Fragment mHomeFragment = new ResumeFragment();
    private ActivityHomeBinding binding;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.Dark_Green));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, this.mHomeFragment).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        View inflate = LayoutInflater.from(this).inflate(R.layout.prompt_confirm_exit, (ViewGroup) null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(inflate);
//        builder.setCancelable(true);
//        final AlertDialog create = builder.create();
//        create.show();
//        inflate.findViewById(R.id.prompt_button_yes).setOnClickListener(view -> {
//            finishAffinity();
//            HomeActivity.this.finish();
//        });
//        inflate.findViewById(R.id.prompt_button_no).setOnClickListener(view -> create.cancel());
    }
}
