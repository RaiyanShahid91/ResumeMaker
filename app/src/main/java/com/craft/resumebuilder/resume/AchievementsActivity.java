package com.craft.resumebuilder.resume;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

import app.craft.myresume.R;
import co.resume.listener.BottomSheetListener;
import co.resume.utils.BottomSheetOptionsUtil;
import co.resume.utils.Constants;

public class AchievementsActivity extends Fragment implements BottomSheetListener {
    ImageView buttonAddAchievementsDetails;
    LinearLayout layoutMain;
    LinearLayout layoutNoDataFound;
    String resume_id;
    String selectedAchievementId = "";

    private TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_achievements, container, false);

        buttonAddAchievementsDetails = view.findViewById(R.id.button_add_achievement_details);
        buttonAddAchievementsDetails.setOnClickListener(view1 -> promptCreateOrEditDetails(0, ""));

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);
        tinyDB = new TinyDB(requireContext());
        resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);

        // Load Achievements initially
        loadAchievements();

        return view;
    }

    private void loadAchievements() {
        layoutMain.removeAllViews(); // Clear previous views before refreshing

        ArrayList<String> listString = tinyDB.getListString(resume_id + ":achievements_details");

        for (int i = 0; i < listString.size(); i++) {
            View inflate = getLayoutInflater().inflate(R.layout.resume_layout_achievements, layoutMain, false);
            ((TextView) inflate.findViewById(R.id.textview_achievement_name))
                    .setText(tinyDB.getListString(listString.get(i)).get(0));

            int finalI = i;
            inflate.findViewById(R.id.img_option).setOnClickListener(view -> {
                selectedAchievementId = listString.get(finalI);
                BottomSheetOptionsUtil.INSTANCE.showBottomSheetDialog(requireContext(), this);
            });

            ImageView buttonMoveUp = inflate.findViewById(R.id.button_move_up);
            ImageView buttonMoveDown = inflate.findViewById(R.id.button_move_down);

            buttonMoveUp.setVisibility(i == 0 ? View.GONE : View.VISIBLE);
            buttonMoveDown.setVisibility(i == listString.size() - 1 ? View.GONE : View.VISIBLE);

            buttonMoveUp.setOnClickListener(view -> {
                Collections.swap(listString, finalI, finalI - 1);
                tinyDB.putListString(resume_id + ":achievements_details", listString);
                loadAchievements();
            });

            buttonMoveDown.setOnClickListener(view -> {
                Collections.swap(listString, finalI, finalI + 1);
                tinyDB.putListString(resume_id + ":achievements_details", listString);
                loadAchievements();
            });

            layoutMain.addView(inflate);
        }

        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void promptCreateOrEditDetails(int mode, String id) {
        View inflate = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_achievements, null);
        final EditText editText = inflate.findViewById(R.id.edittext_achievement_name);
        Button buttonSave = inflate.findViewById(R.id.button_save_details);
        Button buttonCancel = inflate.findViewById(R.id.button_cancel);

        if (mode == 1) {
            editText.setText(tinyDB.getListString(id).get(0));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(inflate);

        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        buttonSave.setOnClickListener(view -> {
            String inputText = editText.getText().toString();
            if (!inputText.isEmpty()) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(inputText);
                String uniqueId = "achievements_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

                if (mode == 1) {
                    tinyDB.putListString(id, arrayList);
                } else {
                    ArrayList<String> listString = tinyDB.getListString(resume_id + ":achievements_details");
                    listString.add(uniqueId);
                    tinyDB.putListString(resume_id + ":achievements_details", listString);
                    tinyDB.putListString(uniqueId, arrayList);
                }

                Toast.makeText(requireContext(), getString(R.string.achievement_saved_successfully), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadAchievements(); // Refresh UI
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onEditClicked() {
        if (!selectedAchievementId.isEmpty()) {
            promptCreateOrEditDetails(1, selectedAchievementId);
        }
    }

    @Override
    public void onDeleteClicked() {
        if (!selectedAchievementId.isEmpty()) {
            ArrayList<String> listString = tinyDB.getListString(resume_id + ":achievements_details");
            listString.remove(selectedAchievementId);
            tinyDB.putListString(resume_id + ":achievements_details", listString);
            tinyDB.remove(selectedAchievementId);

            Toast.makeText(requireContext(), getString(R.string.achievement_deleted_successfully), Toast.LENGTH_SHORT).show();
            loadAchievements(); // Refresh UI
        }
    }
}