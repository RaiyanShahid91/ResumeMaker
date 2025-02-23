package com.craft.resumebuilder.resume;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProjectsActivity extends Fragment implements BottomSheetListener {

    ImageView buttonAddProject;
    LinearLayout layoutMain, layoutNoDataFound;
    String resume_id;
    ImageView moreOption;
    TinyDB tinyDB;
    ArrayList<String> listString;
    String selectedId = "";

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_projects, container, false);

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);
        buttonAddProject = view.findViewById(R.id.button_add_project_details);

        tinyDB = new TinyDB(requireContext());
        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        listString = tinyDB.getListString(this.resume_id + ":projects_details");

        buttonAddProject.setOnClickListener(view1 -> promptCreateOrEditDetails(0));

        loadProjectDetails();

        return view;
    }

    private void loadProjectDetails() {
        layoutMain.removeAllViews();

        for (int i = 0; i < listString.size(); i++) {
            ArrayList<String> detailData = tinyDB.getListString(listString.get(i));
            View itemView = getLayoutInflater().inflate(R.layout.resume_layout_projects, layoutMain, false);

            ((TextView) itemView.findViewById(R.id.textview_project_name)).setText(detailData.get(0));
            ((TextView) itemView.findViewById(R.id.textview_project_description)).setText(detailData.get(1));
            ((TextView) itemView.findViewById(R.id.textview_project_duration)).setText(detailData.get(2)+" - "+ detailData.get(3));
            ((TextView) itemView.findViewById(R.id.textview_project_link)).setText(detailData.get(4));

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
                    tinyDB.putListString(resume_id + ":projects_details", listString);
                    loadProjectDetails();
                }
            });

            ImageView buttonMoveDown = itemView.findViewById(R.id.button_move_down);
            buttonMoveDown.setVisibility(i == listString.size() - 1 ? View.GONE : View.VISIBLE);
            buttonMoveDown.setOnClickListener(view -> {
                if (finalI < listString.size() - 1) {
                    Collections.swap(listString, finalI, finalI + 1);
                    tinyDB.putListString(resume_id + ":projects_details", listString);
                    loadProjectDetails();
                }
            });

            layoutMain.addView(itemView);
        }

        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void promptCreateOrEditDetails(int mode) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_projects, null);
        EditText editTextName = dialogView.findViewById(R.id.edittext_project_name);
        EditText editTextDescription = dialogView.findViewById(R.id.edittext_project_description);
        TextView startDate = dialogView.findViewById(R.id.duration_from);
        TextView endDate = dialogView.findViewById(R.id.duration_to);
        EditText editTextLink = dialogView.findViewById(R.id.edittext_project_link);

        if (mode == 1 && !selectedId.isEmpty()) {
            ArrayList<String> detailData = tinyDB.getListString(selectedId);
            editTextName.setText(detailData.get(0));
            editTextDescription.setText(detailData.get(1));
            startDate.setText(detailData.get(2));
            endDate.setText(detailData.get(3));
            editTextLink.setText(detailData.get(4));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        startDate.setOnClickListener(view -> showMonthYearPicker(startDate));

        endDate.setOnClickListener(view -> showMonthYearPicker(endDate));

        dialogView.findViewById(R.id.button_save_details).setOnClickListener(view -> {
            if (!editTextName.getText().toString().trim().isEmpty() && !startDate.getText().toString().trim().isEmpty() && !endDate.getText().toString().trim().isEmpty()){


                ArrayList<String> newData = new ArrayList<>();
                newData.add(editTextName.getText().toString());
                newData.add(editTextDescription.getText().toString());
                newData.add(startDate.getText().toString());
                newData.add(endDate.getText().toString());
                newData.add(editTextLink.getText().toString());

                if (mode == 1 && !selectedId.isEmpty()) {
                    tinyDB.putListString(selectedId, newData);
                } else {
                    String newId = "projects_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    listString.add(newId);
                    tinyDB.putListString(resume_id + ":projects_details", listString);
                    tinyDB.putListString(newId, newData);
                }

                Toast.makeText(requireContext(), getString(R.string.project_details_saved), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadProjectDetails();
            }else {
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
            tinyDB.putListString(resume_id + ":projects_details", listString);
            loadProjectDetails();
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