package co.resume.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import co.resume.analytics.Analytics;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class ResumeApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Analytics.init(this);
        MobileAds.initialize(this, initializationStatus -> {});
    }
}
