package com.craft.resumebuilder;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.resume.ResumeActivity;
import com.craft.resumebuilder.tinydb.TinyDB;

import java.util.ArrayList;
import java.util.stream.Collectors;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;
import app.craft.myresume.databinding.FragmentResumeBinding;
import co.resume.utils.Constants;


public class ResumeFragment extends Fragment {

    private FragmentResumeBinding binding;
    ArrayList<String> allResumes;
    private TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentResumeBinding.inflate(layoutInflater, viewGroup, false);
        View view = binding.getRoot();

//        setupSearchView();
//
//        updateResume();

        return view;
    }

//    public void updateResume() {
//        tinyDB = new TinyDB(requireActivity());
//        allResumes = tinyDB.getListString(Constants.ALL_RESUME);
//
//        if (!allResumes.isEmpty()) {
//            binding.layoutYesResume.setVisibility(View.VISIBLE);
//            binding.search.setVisibility(View.VISIBLE);
//            binding.layoutNoResume.setVisibility(View.GONE);
//            displayResumes(allResumes);
//        } else {
//            binding.layoutNoResume.setVisibility(View.VISIBLE);
//            binding.layoutYesResume.setVisibility(View.GONE);
//            binding.search.setVisibility(View.GONE);
//        }
//    }
//
//    private void setupSearchView() {
//        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filterResumes(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterResumes(newText);
//                return true;
//            }
//        });
//    }
//
//    private void filterResumes(String query) {
//        ArrayList<String> filteredResumes = (ArrayList<String>) allResumes.stream()
//                .filter(resumeId -> {
//                    String name = tinyDB.getString(resumeId + ":name");
//                    return name.toLowerCase().contains(query.toLowerCase());
//                })
//                .collect(Collectors.toList());
//
//        binding.layoutYesResume.removeAllViews();
//        displayResumes(filteredResumes);
//    }
//
//    private void displayResumes(ArrayList<String> resumes) {
//        float density = getResources().getDisplayMetrics().density;
//        int margin5 = (int) (5 * density);
//        int margin10 = (int) (10 * density);
//
//        for (String resumeId : resumes) {
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            layoutParams.setMargins(margin5, margin5, margin5, margin5);
//
//            CardView cardView = new CardView(requireActivity());
//            cardView.setLayoutParams(layoutParams);
//            cardView.setRadius(30.0f);
//            cardView.setClickable(true);
//
//            LinearLayout linearLayout = new LinearLayout(requireActivity());
//            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            ));
//            linearLayout.setOrientation(LinearLayout.VERTICAL);
//            linearLayout.setPadding(margin10, margin10, margin10, margin10);
//            linearLayout.setGravity(17);
//
//            // Image
//            ImageView imageView = new ImageView(requireActivity());
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    200
//            ));
//            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imageView.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_profile_resume));
//            linearLayout.addView(imageView);
//
//            // Name
//            TextView textView = new TextView(requireActivity());
//            textView.setText(tinyDB.getString(resumeId + ":name"));
//            textView.setTextSize(14.0f);
//            textView.setTypeface(null, Typeface.BOLD);
//            textView.setPadding(0, 25, 0, 25);
//            textView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black));
//            textView.setGravity(17);
//            linearLayout.addView(textView);
//
//            // Buttons
//            LinearLayout buttonLayout = new LinearLayout(requireActivity());
//            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
//            buttonLayout.setGravity(17);
//
//            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
//            float f = getResources().getDisplayMetrics().density;
//            int i3 = (int) (((float) 5) * f);
//            buttonLayoutParams.setMargins(i3, i3, i3, i3);
//
//            Button viewButton = new Button(requireActivity());
//            viewButton.setLayoutParams(buttonLayoutParams);
//            viewButton.setText(getString(R.string.view));
//            viewButton.setTextSize(14.0f);
//            viewButton.setPadding(0, 30, 0, 30);
//            viewButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.button_ripple_effect));
//            viewButton.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.Dark_Green));
//            viewButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
//            viewButton.setAllCaps(false);
//            viewButton.setOnClickListener(v -> openResume(resumeId));
//            buttonLayout.addView(viewButton);
//
//            Button deleteButton = new Button(requireActivity());
//            deleteButton.setLayoutParams(buttonLayoutParams);
//            deleteButton.setText(getString(R.string.delete));
//            deleteButton.setTextSize(14.0f);
//            deleteButton.setPadding(0, 30, 0, 30);
//            deleteButton.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.button_ripple_effect));
//            deleteButton.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.red));
//            deleteButton.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
//            deleteButton.setOnClickListener(v -> deleteResume(resumeId, cardView));
//            deleteButton.setAllCaps(false);
//            buttonLayout.addView(deleteButton);
//
//            linearLayout.addView(buttonLayout);
//            cardView.addView(linearLayout);
//            binding.layoutYesResume.addView(cardView);
//        }
//    }
//
//    private void openResume(String resumeId) {
//        Intent intent = new Intent(requireActivity(), ResumeActivity.class);
//        intent.putExtra(Constants.RESUME_ID, resumeId);
//        AdsCommon.InterstitialAd(getActivity(), intent);
//    }
//
//    private void deleteResume(String resumeId, CardView cardView) {
//        View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.prompt_confirm_delete, null);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
//
//        dialog.show();
//
//        Button buttonYes = dialogView.findViewById(R.id.prompt_button_yes);
//        buttonYes.setOnClickListener(v -> {
//            ArrayList<String> updatedResumes = tinyDB.getListString(Constants.ALL_RESUME);
//            updatedResumes.remove(resumeId);
//            tinyDB.putListString(Constants.ALL_RESUME, updatedResumes);
//
//            Animation animation = AnimationUtils.loadAnimation(requireActivity(), android.R.anim.fade_out);
//            animation.setDuration(500);
//            cardView.startAnimation(animation);
//
//            new Handler().postDelayed(() -> binding.layoutYesResume.removeView(cardView), animation.getDuration());
//
//            Toast.makeText(requireActivity(), getString(R.string.resume_deleted_successfully), Toast.LENGTH_SHORT).show();
//
//            if (updatedResumes.isEmpty()) {
//                binding.layoutNoResume.setVisibility(View.VISIBLE);
//                binding.layoutYesResume.setVisibility(View.GONE);
//                binding.search.setVisibility(View.GONE);
//            }
//
//            dialog.dismiss();
//        });
//
//        Button buttonNo = dialogView.findViewById(R.id.prompt_button_no);
//        buttonNo.setOnClickListener(v -> dialog.dismiss());
//    }
//
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}