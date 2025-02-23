package com.craft.resumebuilder.resume;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import app.craft.myresume.R;
import co.resume.listener.BottomSheetListener;
import co.resume.utils.BottomSheetOptionsUtil;
import co.resume.utils.Constants;

public class WorkExperienceActivity extends Fragment implements BottomSheetListener {

    ImageView buttonAddWorkExperience;
    LinearLayout layoutMain, layoutNoDataFound;
    String resume_id;
    ImageView moreOption;
    TinyDB tinyDB;
    ArrayList<String> listString;
    String selectedId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_work_experience, container, false);

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);
        buttonAddWorkExperience = view.findViewById(R.id.fl_add_work_exp);

        tinyDB = new TinyDB(requireContext());
        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        listString = tinyDB.getListString(resume_id + ":work_experience");

        buttonAddWorkExperience.setOnClickListener(view1 -> promptCreateOrEditDetails(0));

        loadWorkExperience();

        return view;
    }

    private void loadWorkExperience() {
        layoutMain.removeAllViews();

        for (int i = 0; i < listString.size(); i++) {
            ArrayList<String> detailData = tinyDB.getListString(listString.get(i));
            View itemView = getLayoutInflater().inflate(R.layout.resume_layout_work_experience, layoutMain, false);

            ((TextView) itemView.findViewById(R.id.textview_work_experience_job)).setText(detailData.get(0));
            ((TextView) itemView.findViewById(R.id.textview_work_experience_company)).setText(detailData.get(1));
            ((TextView) itemView.findViewById(R.id.textview_work_experience_duration)).setText(detailData.get(2)+" - "+ detailData.get(3));
            ((TextView) itemView.findViewById(R.id.textview_work_experience_description)).setText(detailData.get(4));

            int finalI = i;

            moreOption = itemView.findViewById(R.id.img_option);
            moreOption.setOnClickListener(view -> {
                selectedId = listString.get(finalI);
                BottomSheetOptionsUtil.INSTANCE.showBottomSheetDialog(requireContext(), this);
            });

            ImageView buttonMoveUp = itemView.findViewById(R.id.button_move_up);
            buttonMoveUp.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            buttonMoveUp.setOnClickListener(view -> {
                if (finalI > 0) {
                    Collections.swap(listString, finalI, finalI - 1);
                    tinyDB.putListString(resume_id + ":work_experience", listString);
                    loadWorkExperience();
                }
            });

            ImageView buttonMoveDown = itemView.findViewById(R.id.button_move_down);
            buttonMoveDown.setVisibility(i == listString.size() - 1 ? View.GONE : View.VISIBLE);
            buttonMoveDown.setOnClickListener(view -> {
                if (finalI < listString.size() - 1) {
                    Collections.swap(listString, finalI, finalI + 1);
                    tinyDB.putListString(resume_id + ":work_experience", listString);
                    loadWorkExperience();
                }
            });

            layoutMain.addView(itemView);
        }

        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void promptCreateOrEditDetails(int mode) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_work_experience, null);
        EditText editTextJob = dialogView.findViewById(R.id.edittext_work_experience_job);
        EditText editTextCompany = dialogView.findViewById(R.id.edittext_work_experience_company);
        TextView durationfrom = dialogView.findViewById(R.id.duration_from);
        TextView durationend = dialogView.findViewById(R.id.duration_to);
        CheckBox current = dialogView.findViewById(R.id.ongoing_check);
        EditText editTextDescription = dialogView.findViewById(R.id.edittext_work_experience_description);

        if (mode == 1 && !selectedId.isEmpty()) {
            ArrayList<String> detailData = tinyDB.getListString(selectedId);
            editTextJob.setText(detailData.get(0));
            editTextCompany.setText(detailData.get(1));
            durationfrom.setText(detailData.get(2));
            durationend.setText(detailData.get(3));
            editTextDescription.setText(detailData.get(4));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        durationfrom.setOnClickListener(view -> {
            showMonthYearPicker(durationfrom);
        });

        durationend.setOnClickListener(view -> {
            showMonthYearPicker(durationend);
        });

        current.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                durationend.setVisibility(View.GONE);
            } else {
                durationend.setVisibility(View.VISIBLE);
            }
        });

        dialogView.findViewById(R.id.button_save_details).setOnClickListener(view -> {
            String end;
            if (current.isChecked()) {
                end = "ongoing";
            } else {
                end = durationend.getText().toString();
            }
            if (!editTextJob.getText().toString().trim().isEmpty() &&
                    !editTextCompany.getText().toString().trim().isEmpty() &&
                    !durationfrom.getText().toString().trim().isEmpty() &&
                    !end.trim().isEmpty() &&
                    !editTextDescription.getText().toString().trim().isEmpty()) {
                ArrayList<String> newData = new ArrayList<>();
                newData.add(editTextJob.getText().toString());
                newData.add(editTextCompany.getText().toString());
                newData.add(durationfrom.getText().toString());
                newData.add(end);
                newData.add(editTextDescription.getText().toString());

                if (mode == 1 && !selectedId.isEmpty()) {
                    tinyDB.putListString(selectedId, newData);
                } else {
                    String newId = "work_experience:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    listString.add(newId);
                    tinyDB.putListString(resume_id + ":work_experience", listString);
                    tinyDB.putListString(newId, newData);
                }

                Toast.makeText(requireContext(), getString(R.string.work_experience_saved), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadWorkExperience();
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
            }
        });

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(view -> alertDialog.dismiss());
    }

    @Override
    public void onEditClicked() {
        if (!selectedId.isEmpty()) {
            promptCreateOrEditDetails(1);
        }
    }

    @Override
    public void onDeleteClicked() {
        if (!selectedId.isEmpty()) {
            listString.remove(selectedId);
            tinyDB.putListString(resume_id + ":work_experience", listString);
            loadWorkExperience();
        }
    }

    private void showMonthYearPicker(TextView textView) {
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
}