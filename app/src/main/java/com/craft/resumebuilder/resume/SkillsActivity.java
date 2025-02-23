package com.craft.resumebuilder.resume;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import co.resume.listener.BottomSheetListener;
import co.resume.utils.BottomSheetOptionsUtil;
import co.resume.utils.Constants;

public class SkillsActivity extends Fragment implements AdapterView.OnItemSelectedListener, BottomSheetListener {

    ImageView buttonAddSkillsDetails;
    LinearLayout layoutMain;
    LinearLayout layoutNoDataFound;
    String resume_id;
    String selectedId = "";

    ImageView moreOption;
    TinyDB tinyDB;
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_skills, container, false);
        tinyDB = new TinyDB(requireContext());

        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        buttonAddSkillsDetails = view.findViewById(R.id.button_add_skills_details);
        buttonAddSkillsDetails.setOnClickListener(view1 -> promptCreateOrEditDetails(0, selectedId));

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);

        loadSkillsDetails();

        return view;
    }

    private void loadSkillsDetails() {
        layoutMain.removeAllViews();
        ArrayList<String> listString = tinyDB.getListString(resume_id + ":skills_details");

        for (int i = 0; i < listString.size(); i++) {
            ArrayList<String> skillDetails = tinyDB.getListString(listString.get(i));
            View itemView = getLayoutInflater().inflate(R.layout.resume_layout_skills, layoutMain, false);

            ((TextView) itemView.findViewById(R.id.textview_skill_name)).setText(skillDetails.get(0));
            ((TextView) itemView.findViewById(R.id.textview_skill_level)).setText(skillDetails.get(1));


            moreOption = itemView.findViewById(R.id.img_option);

            int finalI = i;
            moreOption.setOnClickListener(view -> {
                selectedId = listString.get(finalI);
                BottomSheetOptionsUtil.INSTANCE.showBottomSheetDialog(requireContext(), this);
            });

            ImageView buttonMoveUp = itemView.findViewById(R.id.button_move_up);
            ImageView buttonMoveDown = itemView.findViewById(R.id.button_move_down);

            if (i < 1) {
                buttonMoveUp.setVisibility(View.GONE);
                buttonMoveDown.setVisibility(View.GONE);
            } else if (i > listString.size() - 2) {
                buttonMoveDown.setVisibility(View.GONE);
            }

            buttonMoveUp.setOnClickListener(view -> {
                if (finalI > 0) {
                    Collections.swap(listString, finalI, finalI - 1);
                    tinyDB.putListString(resume_id + ":skills_details", listString);
                    loadSkillsDetails();
                }
            });

            buttonMoveDown.setOnClickListener(view -> {
                if (finalI < listString.size() - 1) {
                    Collections.swap(listString, finalI, finalI + 1);
                    tinyDB.putListString(resume_id + ":skills_details", listString);
                    loadSkillsDetails();
                }
            });

            layoutMain.addView(itemView);
        }

        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void promptCreateOrEditDetails(int mode, String selectedId) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_skills, null);
        EditText editTextSkillName = dialogView.findViewById(R.id.edittext_skill_name);
        Spinner spinnerSkillLevel = dialogView.findViewById(R.id.spinner_skill_level);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, new String[]{"Beginner", "Intermediate", "Advanced", "Expert"});
        spinnerSkillLevel.setAdapter(adapter);

        if (mode == 1 && !selectedId.isEmpty()) { // Editing existing data
            TinyDB tinyDB = new TinyDB(requireContext());
            ArrayList<String> skillData = tinyDB.getListString(selectedId);
            editTextSkillName.setText(skillData.get(0));

            switch (skillData.get(1)) {
                case "Intermediate":
                    spinnerSkillLevel.setSelection(1);
                    break;
                case "Advanced":
                    spinnerSkillLevel.setSelection(2);
                    break;
                case "Expert":
                    spinnerSkillLevel.setSelection(3);
                    break;
                default:
                    spinnerSkillLevel.setSelection(0);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        dialogView.findViewById(R.id.button_save_details).setOnClickListener(view1 -> {
            String skillName = editTextSkillName.getText().toString();
            String skillLevel = spinnerSkillLevel.getSelectedItem().toString();

            if (skillName.isEmpty() || skillLevel.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> newSkillData = new ArrayList<>();
            newSkillData.add(skillName);
            newSkillData.add(skillLevel);

            TinyDB tinyDB = new TinyDB(requireContext());

            if (mode == 1 && !selectedId.isEmpty()) { // Edit existing skill
                tinyDB.putListString(selectedId, newSkillData);
            } else if (mode == 0) { // Add new skill
                String newId = "skills_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                ArrayList<String> listString = tinyDB.getListString(resume_id + ":skills_details");

                listString.add(newId); // Add the new ID to the list of skills
                tinyDB.putListString(resume_id + ":skills_details", listString);
                tinyDB.putListString(newId, newSkillData); // Save the new skill data

                Toast.makeText(requireContext(), "Skill added successfully.", Toast.LENGTH_SHORT).show();
            }

            alertDialog.dismiss();
            loadSkillsDetails(); // Refresh the skills list after saving
        });

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(view12 -> alertDialog.dismiss());
    }

    @Override
    public void onEditClicked() {
        if (!selectedId.isEmpty()) {
            promptCreateOrEditDetails(1, selectedId);
        }
    }

    @Override
    public void onDeleteClicked() {
        if (!selectedId.isEmpty()) {
            TinyDB tinyDB = new TinyDB(requireContext());
            ArrayList<String> listString = tinyDB.getListString(resume_id + ":skills_details");
            listString.remove(selectedId);
            tinyDB.putListString(resume_id + ":skills_details", listString);
            loadSkillsDetails();
        }
    }
}
