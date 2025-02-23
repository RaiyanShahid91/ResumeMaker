package com.craft.resumebuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import app.craft.myresume.R;
import java.util.Objects;

public class SubscriptionActivity extends AppCompatActivity {

    Button buttonDownloadPro;
    ImageView imageView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_subscription);

        Button button = (Button) findViewById(R.id.download_pro_version);
        this.buttonDownloadPro = button;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SubscriptionActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.fazil.resumebuilder")));
            }
        });
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView);
        this.imageView = imageView2;
        imageView2.setImageResource(R.drawable.benefits_of_subscription);
    }
}
