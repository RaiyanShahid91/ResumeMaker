package com.craft.resumebuilder.resume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import app.craft.myresume.R;
import app.craft.myresume.databinding.ResumeActivityObjectiveBinding;
import co.resume.utils.Constants;

public class ObjectiveActivity extends Fragment {
    Button buttonSaveDetails;
    EditText editTextResumeObjective;

    private ResumeActivityObjectiveBinding binding; // Declare the binding instance


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResumeActivityObjectiveBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        this.editTextResumeObjective = view.findViewById(R.id.edittext_resume_objective);
        this.buttonSaveDetails = view.findViewById(R.id.button_save_details);
        final TinyDB tinyDB = new TinyDB(requireContext());
        final String resumeId = tinyDB.getString(Constants.SELECTED_RESUME_ID);

        //this.editTextResumeObjective.setText(tinyDB.getString(resumeId + ":objective"));
        this.buttonSaveDetails.setOnClickListener(view1 -> {
            String obj = ObjectiveActivity.this.editTextResumeObjective.getText().toString();
            if (!obj.isEmpty()) {
                tinyDB.putString(resumeId + ":objective", obj);
                Toast.makeText(requireContext(), getString(R.string.details_saved_successfully), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), getString(R.string.please_enter_the_objective), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickSuggestion();
    }

    private void clickSuggestion(){
        binding.suggestedTxt1.setOnClickListener(view -> {
            editTextResumeObjective.setText(binding.suggestedTxt1.getText());
        });

        binding.suggestedTxt2.setOnClickListener(view -> {
            editTextResumeObjective.setText(binding.suggestedTxt2.getText());

        });

        binding.suggestedTxt3.setOnClickListener(view -> {
            editTextResumeObjective.setText(binding.suggestedTxt3.getText());

        });

        binding.suggestedTxt4.setOnClickListener(view -> {
            editTextResumeObjective.setText(binding.suggestedTxt4.getText());
        });

        binding.suggestedTxt5.setOnClickListener(view -> {
            editTextResumeObjective.setText(binding.suggestedTxt5.getText());
        });
    }
}
