package com.craft.resumebuilder.resume;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.craft.resumebuilder.tinydb.TinyDB;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;

public class ProfilePhotoActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int STORAGE_REQUEST_CODE = 400;
    Button buttonUploadPhoto;
    String[] cameraPermission;
    Uri image_uri;
    ImageView mPreviewIv;
    String resume_id;
    String[] storagePermission;

    @SuppressLint("WrongThread")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.resume_activity_profile_photo);


        //Reguler Banner Ads
        RelativeLayout admob_banner = findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admob_banner, adContainer);

        this.resume_id = getIntent().getStringExtra("resume_id");
        this.mPreviewIv = findViewById(R.id.imageIv);
        String string = new TinyDB(this).getString(this.resume_id + ":profile_photo");
        if (string.equals("")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BitmapFactory.decodeResource(getResources(), R.drawable.icon_resume_profile_photo).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] decode = Base64.decode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0), 0);
            this.mPreviewIv.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        } else {
            new ByteArrayOutputStream().toByteArray();
            byte[] decode2 = Base64.decode(string, 0);
            this.mPreviewIv.setImageBitmap(BitmapFactory.decodeByteArray(decode2, 0, decode2.length));
        }
        Button button = (Button) findViewById(R.id.button_upload_profile_photo);
        this.buttonUploadPhoto = button;
        button.setOnClickListener(new ProfilePhotoActivity$$ExternalSyntheticLambda0(this));
        this.cameraPermission = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
        this.storagePermission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};

        findViewById(R.id.back).setOnClickListener(view -> {
            onBackPressed();
        });

    }


    public void showImageImportDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Select Image");
        builder.setItems((CharSequence[]) new String[]{"Camera", "Gallery"}, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (!ProfilePhotoActivity.this.checkCameraPermission()) {
                        ProfilePhotoActivity.this.requestCameraPermission();
                    } else {
                        ProfilePhotoActivity.this.pickCamera();
                    }
                }
                if (i != 1) {
                    return;
                }
                if (!ProfilePhotoActivity.this.checkStoragePermission()) {
                    ProfilePhotoActivity.this.requestStoragePermission();
                } else {
                    ProfilePhotoActivity.this.pickGallery();
                }
            }
        });
        builder.create().show();
    }


    public void pickGallery() {
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }


    public void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", "NewPic");
        contentValues.put("description", "Image To Text");
        this.image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.image_uri);
        startActivityForResult(intent, 1001);
    }


    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, this.storagePermission, STORAGE_REQUEST_CODE);
    }


    public boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }


    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, this.cameraPermission, 200);
    }


    public boolean checkCameraPermission() {
        boolean z = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0;
        boolean z2 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
        return z && z2;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        boolean z = true;
        if (i != 200) {
            if (i == STORAGE_REQUEST_CODE && iArr.length > 0) {
                if (iArr[0] != 0) {
                    z = false;
                }
                if (z) {
                    pickGallery();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (iArr.length > 0) {
            boolean z2 = iArr[0] == 0;
            if (iArr[0] != 0) {
                z = false;
            }
            if (!z2 || !z) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            } else {
                pickCamera();
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                Uri selectedImageUri = data.getData();
                // Use UCrop for cropping the selected image from gallery
                UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), "cropped.jpg")))
                        .withAspectRatio(1, 1)  // Optional: Set aspect ratio
                        .withMaxResultSize(1000, 1000)  // Optional: Set max result size
                        .start(this);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // Use UCrop for cropping the captured image
                UCrop.of(image_uri, Uri.fromFile(new File(getCacheDir(), "cropped.jpg")))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(1000, 1000)
                        .start(this);
            }
        }

        // Handle the result from UCrop
        if (requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                mPreviewIv.setImageURI(resultUri);  // Display the cropped image
                Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());

                // Convert the cropped image to a byte array and save it
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                // Save the profile photo
                new TinyDB(this).putString(resume_id + ":profile_photo", encodedImage);
                Toast.makeText(this, "Profile Photo updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error cropping the image", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
