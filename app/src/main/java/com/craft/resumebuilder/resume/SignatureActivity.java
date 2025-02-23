package com.craft.resumebuilder.resume;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.craft.resumebuilder.tinydb.TinyDB;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;
import app.craft.myresume.databinding.ResumeActivitySignatureBinding;

public class SignatureActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int STORAGE_REQUEST_CODE = 400;
    String[] cameraPermission;
    Uri image_uri;
    String resume_id;
    String[] storagePermission;
    private ResumeActivitySignatureBinding binding; // Declare the binding instance

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ResumeActivitySignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Regular Banner Ads
        RelativeLayout admob_banner = findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admob_banner, adContainer);


        this.resume_id = getIntent().getStringExtra("resume_id");

        String signatureData = new TinyDB(this).getString(this.resume_id + ":signature");
        if (signatureData.equals("")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BitmapFactory.decodeResource(getResources(), R.drawable.icon_sample_signature).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] decodedBytes = Base64.decode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), Base64.DEFAULT);
            binding.imageIv.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
        } else {
            byte[] decodedData = Base64.decode(signatureData, Base64.DEFAULT);
            binding.imageIv.setImageBitmap(BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length));
        }

        binding.buttonUploadSignature.setOnClickListener(view -> showImageImportDialog(view));

        cameraPermission = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
        storagePermission = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    }

    public void showImageImportDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(new String[]{"Camera", "Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        if (!checkCameraPermission()) {
                            requestCameraPermission();
                        } else {
                            pickCamera();
                        }
                    } else if (which == 1) {
                        if (!checkStoragePermission()) {
                            requestStoragePermission();
                        } else {
                            pickGallery();
                        }
                    }
                }).create().show();
    }

    public void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    public void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "NewPic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    public boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    public boolean checkCameraPermission() {
        boolean cameraPermissionGranted = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == PackageManager.PERMISSION_GRANTED;
        boolean storagePermissionGranted = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
        return cameraPermissionGranted && storagePermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickGallery();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                Uri selectedImageUri = data.getData();
                assert selectedImageUri != null;
                UCrop.of(selectedImageUri, Uri.fromFile(new File(getCacheDir(), "cropped.jpg")))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(1000, 1000)
                        .start(this);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                UCrop.of(image_uri, Uri.fromFile(new File(getCacheDir(), "cropped.jpg")))
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(1000, 1000)
                        .start(this);
            }
        }

        if (requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                binding.imageIv.setImageURI(resultUri);
                Bitmap bitmap = BitmapFactory.decodeFile(resultUri.getPath());

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                // Save the signature
                new TinyDB(this).putString(resume_id + ":signature", encodedImage);
                Toast.makeText(this, "Signature updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error cropping the image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
