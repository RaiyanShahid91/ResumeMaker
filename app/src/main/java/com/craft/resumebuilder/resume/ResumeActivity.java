package com.craft.resumebuilder.resume;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.craft.resumebuilder.tinydb.TinyDB;
import com.google.android.material.tabs.TabLayout;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;
import co.resume.adapter.ViewPagerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResumeActivity extends AppCompatActivity {

    private ImageView buttonViewResume, editImage, backArrow;
    private CircleImageView imageViewProfilePhoto;
    private TextView textViewPersonName;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String resumeId;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.resume_activity_main);

        initViews();
        setupAds();
        setupViewPager();
        setupListeners();
        loadProfileData();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        editImage = findViewById(R.id.edit_img);
        buttonViewResume = findViewById(R.id.button_view_resume);
        imageViewProfilePhoto = findViewById(R.id.imageview_profile_photo);
        textViewPersonName = findViewById(R.id.textview_person_name);
        backArrow = findViewById(R.id.back_arrow);

        tinyDB = new TinyDB(this);
        resumeId = getIntent().getStringExtra("resume_id");
    }

    private void setupAds() {
        RelativeLayout admobBanner = findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admobBanner, adContainer);
    }

    private void setupViewPager() {
        adjustViewPagerHeight();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.getText() != null) {
                tab.setText(tab.getText().toString().toLowerCase());
            }
        }
    }

    private void adjustViewPagerHeight() {
        viewPager.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int height = tabLayout.getHeight();
            int remainingHeight = getResources().getDisplayMetrics().heightPixels - height;
            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
            params.height = remainingHeight;
            viewPager.setLayoutParams(params);
        });
    }

    private void setupListeners() {
        buttonViewResume.setOnClickListener(view -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("resume_id", resumeId);
            showAds(intent);
        });

        View.OnClickListener profileClickListener = view -> {
            Intent intent = new Intent(this, ProfilePhotoActivity.class);
            intent.putExtra("resume_id", resumeId);
            startActivity(intent);
        };

        imageViewProfilePhoto.setOnClickListener(profileClickListener);
        editImage.setOnClickListener(profileClickListener);

        backArrow.setOnClickListener(view -> onBackPressed());
    }

    private void loadProfileData() {
        textViewPersonName.setText(tinyDB.getString(resumeId + ":name"));
    }

    private void showAds(Intent intent) {
        AdsCommon.InterstitialAd(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileImage();
    }

    private void loadProfileImage() {
        String encodedImage = tinyDB.getString(resumeId + ":profile_photo");
        Bitmap profileBitmap;

        if (encodedImage.isEmpty()) {
            profileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_new_man);
        } else {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            profileBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

        imageViewProfilePhoto.setImageBitmap(profileBitmap);
    }
}
