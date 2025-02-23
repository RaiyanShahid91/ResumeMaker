package com.craft.resumebuilder.resume;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EducationalDetailsActivity extends Fragment implements BottomSheetListener {

    ImageView buttonAddEducationalDetails;
    LinearLayout layoutMain, layoutNoDataFound;
    String resume_id;
    ImageView moreOption;
    TinyDB tinyDB;
    ArrayList<String> listString;
    String selectedId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_educational_details, container, false);

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);
        buttonAddEducationalDetails = view.findViewById(R.id.button_add_educational_details);

        tinyDB = new TinyDB(requireContext());
        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        listString = tinyDB.getListString(this.resume_id + ":educational_details");

        buttonAddEducationalDetails.setOnClickListener(view1 -> promptCreateOrEditDetails(0));

        loadEducationalDetails();

        return view;
    }

    private void loadEducationalDetails() {
        layoutMain.removeAllViews();

        for (int i = 0; i < listString.size(); i++) {
            ArrayList<String> detailData = tinyDB.getListString(listString.get(i));
            View itemView = getLayoutInflater().inflate(R.layout.resume_layout_educational_details, layoutMain, false);

            ((TextView) itemView.findViewById(R.id.textview_education_course)).setText(detailData.get(0));
            ((TextView) itemView.findViewById(R.id.textview_education_university)).setText(detailData.get(1));
            ((TextView) itemView.findViewById(R.id.textview_education_grade)).setText("Percentage: " + detailData.get(2));
            ((TextView) itemView.findViewById(R.id.textview_education_duration_from)).setText(detailData.get(3));
            ((TextView) itemView.findViewById(R.id.textview_education_duration_end)).setText(detailData.get(4));

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
                    tinyDB.putListString(resume_id + ":educational_details", listString);
                    loadEducationalDetails();
                }
            });

            ImageView buttonMoveDown = itemView.findViewById(R.id.button_move_down);
            buttonMoveDown.setVisibility(i == listString.size() - 1 ? View.GONE : View.VISIBLE);
            buttonMoveDown.setOnClickListener(view -> {
                if (finalI < listString.size() - 1) {
                    Collections.swap(listString, finalI, finalI + 1);
                    tinyDB.putListString(resume_id + ":educational_details", listString);
                    loadEducationalDetails();
                }
            });

            layoutMain.addView(itemView);
        }

        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void promptCreateOrEditDetails(int mode) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_educational_details, null);
        EditText editTextCourse = dialogView.findViewById(R.id.edittext_education_course);
        EditText editTextUniversity = dialogView.findViewById(R.id.edittext_education_university);
        EditText editTextGrade = dialogView.findViewById(R.id.edittext_education_grade);
        TextView editTextDurationFrom = dialogView.findViewById(R.id.edittext_education_duration_from);
        TextView editTextDurationEnd = dialogView.findViewById(R.id.edittext_education_duration_to);

        if (mode == 1 && !selectedId.isEmpty()) {
            ArrayList<String> detailData = tinyDB.getListString(selectedId);
            editTextCourse.setText(detailData.get(0));
            editTextUniversity.setText(detailData.get(1));
            editTextGrade.setText(detailData.get(2));
            editTextDurationFrom.setText(detailData.get(3));
            editTextDurationEnd.setText(detailData.get(4));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        editTextDurationFrom.setOnClickListener(view -> {
            showMonthYearPicker(editTextDurationFrom);
        });

        editTextDurationEnd.setOnClickListener(view -> {
            showMonthYearPicker(editTextDurationEnd);
        });

        dialogView.findViewById(R.id.button_save_details).setOnClickListener(view -> {
            if (!editTextCourse.getText().toString().trim().isEmpty() &&
                    !editTextUniversity.getText().toString().trim().isEmpty() &&
                    !editTextDurationFrom.getText().toString().trim().isEmpty() &&
                    !editTextDurationEnd.getText().toString().trim().isEmpty() &&
                    !editTextGrade.getText().toString().trim().isEmpty()) {

                ArrayList<String> newData = new ArrayList<>();
                newData.add(editTextCourse.getText().toString());
                newData.add(editTextUniversity.getText().toString());
                newData.add(editTextGrade.getText().toString());
                newData.add(editTextDurationFrom.getText().toString());
                newData.add(editTextDurationEnd.getText().toString());

                if (mode == 1 && !selectedId.isEmpty()) {
                    tinyDB.putListString(selectedId, newData);
                } else {
                    String newId = "educational_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    listString.add(newId);
                    tinyDB.putListString(resume_id + ":educational_details", listString);
                    tinyDB.putListString(newId, newData);
                }

                Toast.makeText(requireContext(), getString(R.string.educational_details_saved), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadEducationalDetails();
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
            tinyDB.putListString(resume_id + ":educational_details", listString);
            loadEducationalDetails();
        }
    }

    private void showMonthYearPicker(TextView textView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, dayOfMonth) -> {
                    String formattedDate = String.format("%02d/%04d", selectedMonth + 1, selectedYear);
                    textView.setText(formattedDate);
                },
                year, month, calendar.get(Calendar.DAY_OF_MONTH)
        );

        try {
            Field[] fields = datePickerDialog.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mDatePicker".equals(field.getName())) {
                    field.setAccessible(true);
                    Object picker = field.get(datePickerDialog);
                    Field dayField = picker.getClass().getDeclaredField("mDaySpinner");
                    dayField.setAccessible(true);
                    Object dayPicker = dayField.get(picker);
                    if (dayPicker instanceof View) {
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }
}
