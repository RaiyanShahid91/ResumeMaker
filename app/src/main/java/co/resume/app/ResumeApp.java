package co.resume.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class ResumeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {});
    }
}
