package com.craft.resumebuilder.resume;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import app.craft.myresume.R;
import com.vishnusivadas.advanced_httpurlconnection.PutData;
import java.util.Objects;

public class PublishedActivity extends AppCompatActivity {
    Button buttonShareLink;
    Button buttonUnpublishResume;
    Button buttonVisitLink;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    String resume_id;
    String resume_link;
    TextView textViewPublishingInstructions;
    TextView textViewResumeAddress;
    TextView textViewResumeStatus;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.resume_activity_published);
        AppCompatDelegate.setDefaultNightMode(1);
        ((ActionBar) Objects.requireNonNull(getSupportActionBar())).setDisplayOptions(16);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView((int) R.layout.custom_action_bar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient));
        getSupportActionBar().setElevation(0.0f);
        this.resume_id = getIntent().getStringExtra("resume_id");
        this.resume_link = getResources().getString(R.string.website_address) + "?id=" + this.resume_id;
        this.progressBar = (ProgressBar) findViewById(R.id.progress);
        ProgressDialog progressDialog2 = new ProgressDialog(this);
        this.progressDialog = progressDialog2;
        progressDialog2.setMessage("Checking status...");
        this.textViewResumeStatus = (TextView) findViewById(R.id.textview_resume_status);
        this.textViewResumeAddress = (TextView) findViewById(R.id.textview_resume_address);
        this.textViewPublishingInstructions = (TextView) findViewById(R.id.textview_publishing_instructions);
        this.progressBar.setVisibility(0);
        this.progressDialog.setMessage("Please kindly wait for sometime while we are checking your resume status...");
        this.progressDialog.show();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String[] strArr = {PublishedActivity.this.resume_id};
                PutData putData = new PutData(PublishedActivity.this.getResources().getString(R.string.website_address) + "check_published_status.php", "POST", new String[]{"resume_id"}, strArr);
                if (putData.startPut() && putData.onComplete()) {
                    if (putData.getResult().equals("published")) {
                        PublishedActivity.this.textViewResumeStatus.setText("Published Online");
                        PublishedActivity.this.textViewResumeAddress.setText(PublishedActivity.this.resume_link);
                        PublishedActivity.this.textViewPublishingInstructions.setVisibility(8);
                    } else {
                        PublishedActivity.this.textViewResumeStatus.setText("Unpublished");
                        PublishedActivity.this.buttonVisitLink.setVisibility(8);
                        PublishedActivity.this.buttonShareLink.setVisibility(8);
                        PublishedActivity.this.buttonUnpublishResume.setVisibility(8);
                        PublishedActivity.this.textViewPublishingInstructions.setVisibility(0);
                    }
                    PublishedActivity.this.progressBar.setVisibility(8);
                    PublishedActivity.this.progressDialog.dismiss();
                }
            }
        });
        Button button = (Button) findViewById(R.id.button_visit_link);
        this.buttonVisitLink = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishedActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(PublishedActivity.this.resume_link)));
            }
        });
        Button button2 = (Button) findViewById(R.id.button_share_link);
        this.buttonShareLink = button2;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                String str = PublishedActivity.this.resume_link;
                intent.putExtra("android.intent.extra.SUBJECT", PublishedActivity.this.resume_link);
                intent.putExtra("android.intent.extra.TEXT", str);
                PublishedActivity.this.startActivity(Intent.createChooser(intent, "Share Using"));
            }
        });
        Button button3 = (Button) findViewById(R.id.button_unpublish_resume);
        this.buttonUnpublishResume = button3;
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublishedActivity.this.progressBar.setVisibility(0);
                PublishedActivity.this.progressDialog.setMessage("Please kindly wait for sometime while we are unpublishing resume...");
                PublishedActivity.this.progressDialog.show();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        String[] strArr = {PublishedActivity.this.resume_id};
                        PutData putData = new PutData(PublishedActivity.this.getResources().getString(R.string.website_address) + "unpublish_resume.php", "POST", new String[]{"resume_id"}, strArr);
                        if (putData.startPut() && putData.onComplete()) {
                            Toast.makeText(PublishedActivity.this, "Resume unpublished successfully!!!", 0).show();
                            PublishedActivity.this.progressBar.setVisibility(8);
                            PublishedActivity.this.progressDialog.dismiss();
                            PublishedActivity.this.textViewResumeStatus.setText("Unpublished");
                            PublishedActivity.this.textViewResumeAddress.setText("-----");
                            PublishedActivity.this.buttonVisitLink.setVisibility(8);
                            PublishedActivity.this.buttonShareLink.setVisibility(8);
                            PublishedActivity.this.buttonUnpublishResume.setVisibility(8);
                            PublishedActivity.this.textViewPublishingInstructions.setVisibility(0);
                        }
                    }
                });
            }
        });
    }
}
