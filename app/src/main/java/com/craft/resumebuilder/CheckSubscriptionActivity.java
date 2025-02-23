package com.craft.resumebuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import app.craft.myresume.R;

public class CheckSubscriptionActivity extends AppCompatActivity {
    public String subscribed_or_not = "0";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_check_subscription);
        SharedPreferences.Editor edit = getSharedPreferences("MyUserPrefs", 0).edit();
        edit.putString("subscribed_or_not", this.subscribed_or_not);
        edit.commit();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(603979776);
        startActivity(intent);
        finish();
    }
}
