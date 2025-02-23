package com.craft.resumebuilder.resume;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import app.craft.myresume.R;
import app.craft.myresume.databinding.ResumeActivityLanguagesBinding;
import app.craft.myresume.databinding.ResumeLayoutLanguagesBinding;
import app.craft.myresume.databinding.ResumePromptLanguagesBinding;
import co.resume.listener.BottomSheetListener;
import co.resume.utils.BottomSheetOptionsUtil;
import co.resume.utils.Constants;

public class LanguagesActivity extends Fragment implements BottomSheetListener {
    private String resume_id;
    private ResumeActivityLanguagesBinding binding;
    private TinyDB tinyDB;
    private ArrayList<String> listString;
    private String selectedLanguageId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResumeActivityLanguagesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize TinyDB and load language details
        tinyDB = new TinyDB(requireContext());
        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        listString = tinyDB.getListString(resume_id + ":languages_details");

        // Set up Add button listener
        binding.buttonAddLanguagesDetails.setOnClickListener(view1 -> promptCreateOrEditDetails(0, ""));

        // Load the language details
        loadLanguageDetails();

        return view;
    }

    private void loadLanguageDetails() {
        binding.layoutMain.removeAllViews();

        for (int i = 0; i < listString.size(); i++) {
            final int index = i; // Declare a final variable to hold the value of i
            ResumeLayoutLanguagesBinding resumeLayoutBinding = ResumeLayoutLanguagesBinding.inflate(getLayoutInflater(), binding.layoutMain, false);
            resumeLayoutBinding.textviewLanguageName.setText(tinyDB.getListString(listString.get(index)).get(0));

            // Edit button logic
            resumeLayoutBinding.imgOption.setOnClickListener(view -> {
                selectedLanguageId = listString.get(index); // Use the final variable `index`
                BottomSheetOptionsUtil.INSTANCE.showBottomSheetDialog(requireContext(), this);
            });

            // Move Up button logic
            if (index > 0) {
                resumeLayoutBinding.buttonMoveUp.setVisibility(View.VISIBLE);
                resumeLayoutBinding.buttonMoveUp.setOnClickListener(view -> {
                    Collections.swap(listString, index, index - 1);
                    tinyDB.putListString(resume_id + ":languages_details", listString);
                    loadLanguageDetails();
                });
            } else {
                resumeLayoutBinding.buttonMoveUp.setVisibility(View.GONE);
            }

            // Move Down button logic
            if (index < listString.size() - 1) {
                resumeLayoutBinding.buttonMoveDown.setVisibility(View.VISIBLE);
                resumeLayoutBinding.buttonMoveDown.setOnClickListener(view -> {
                    Collections.swap(listString, index, index + 1);
                    tinyDB.putListString(resume_id + ":languages_details", listString);
                    loadLanguageDetails();
                });
            } else {
                resumeLayoutBinding.buttonMoveDown.setVisibility(View.GONE);
            }

            binding.layoutMain.addView(resumeLayoutBinding.getRoot());
        }

        // Show "No Data Found" if the list is empty
        binding.layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }


    public void promptCreateOrEditDetails(int mode, String languageId) {
        ResumePromptLanguagesBinding resumePromptBinding = ResumePromptLanguagesBinding.inflate(LayoutInflater.from(requireContext()));

        if (mode == 1) {
            // Editing existing language
            resumePromptBinding.edittextLanguageName.setText(tinyDB.getListString(languageId).get(0));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(resumePromptBinding.getRoot());
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        resumePromptBinding.buttonSaveDetails.setOnClickListener(view -> {
            String languageName = resumePromptBinding.edittextLanguageName.getText().toString();
            if (!languageName.isEmpty()) {
                ArrayList<String> languageDetails = new ArrayList<>();
                languageDetails.add(languageName);
                String languageKey = mode == 1 ? languageId : "languages_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

                // Save new or edited language details
                tinyDB.putListString(languageKey, languageDetails);
                if (mode == 0) {
                    listString.add(languageKey);
                    tinyDB.putListString(resume_id + ":languages_details", listString);
                }

                Toast.makeText(requireContext(), getString(R.string.language_details_saved_successfully), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadLanguageDetails();
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
            }
        });

        resumePromptBinding.buttonCancel.setOnClickListener(view -> alertDialog.dismiss());
    }

    @Override
    public void onEditClicked() {
        if (!selectedLanguageId.isEmpty()) {
            promptCreateOrEditDetails(1, selectedLanguageId);
        }
    }

    @Override
    public void onDeleteClicked() {
        if (!selectedLanguageId.isEmpty()) {
            listString.remove(selectedLanguageId);
            tinyDB.putListString(resume_id + ":languages_details", listString);
            loadLanguageDetails();
            Toast.makeText(requireContext(), getString(R.string.language_deleted_successfully), Toast.LENGTH_SHORT).show();
        }
    }
}
