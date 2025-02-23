package com.craft.resumebuilder.resume;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import app.craft.myresume.R;
import app.craft.myresume.databinding.ResumeActivityDeclarationBinding;
import co.resume.utils.Constants;

public class DeclarationActivity extends Fragment {
    private ResumeActivityDeclarationBinding binding; // Declare the binding instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResumeActivityDeclarationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        final TinyDB tinyDB = new TinyDB(requireContext());
        String resumeId = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        binding.edittextResumeDeclaration.setText(tinyDB.getString(resumeId + ":declaration"));
        binding.edittextResumeDeclarationPlace.setText(tinyDB.getString(resumeId + ":declaration_place"));
        binding.edittextResumeDeclarationDate.setText(tinyDB.getString(resumeId + ":declaration_date"));
        if (tinyDB.getString(resumeId + ":declaration_date").isEmpty()) {
            binding.edittextResumeDeclarationDate.setText(getCurrentDate());
        }
        binding.buttonSaveDetails.setOnClickListener(view1 -> {
            String obj = binding.edittextResumeDeclaration.getText().toString();
            String obj2 = binding.edittextResumeDeclarationPlace.getText().toString();
            String obj3 = getCurrentDate();
            if (obj.isEmpty() || obj3.isEmpty() || obj2.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
                return;
            }
            tinyDB.putString(resumeId + ":declaration", obj);
            tinyDB.putString(resumeId + ":declaration_place", obj2);
            tinyDB.putString(resumeId + ":declaration_date", obj3);
            Toast.makeText(requireContext(), getString(R.string.details_saved_successfully), Toast.LENGTH_SHORT).show();
        });

        binding.edittextResumeDeclarationDate.setOnClickListener(view2 -> showDatePickerDialog(binding.edittextResumeDeclarationDate));

        return view;
    }

    private void showDatePickerDialog(TextView textView) {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format date as DD/MM/YYYY
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    textView.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
}
