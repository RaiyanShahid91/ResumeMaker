package com.craft.resumebuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import app.craft.myresume.R;
import app.craft.myresume.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // Declare the binding instance
    private static int SPLASH_SCREEN_TIME_OUT = 1500;
    private int REQUEST_CODE = 11;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences.Editor edit = getSharedPreferences("MyUserPrefs", 0).edit();
        edit.putString("first_time", "1");
        edit.apply();
        binding.versionName.setText("Version: 1.0");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, CheckSubscriptionActivity.class);
                intent.setFlags(603979776);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        }, (long) SPLASH_SCREEN_TIME_OUT);
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == this.REQUEST_CODE) {
            Toast.makeText(this, "Start Download", 0).show();
            if (i2 != -1) {
                Log.d("mmm", "Update flow failed " + i2);
            }
        }
    }

}
