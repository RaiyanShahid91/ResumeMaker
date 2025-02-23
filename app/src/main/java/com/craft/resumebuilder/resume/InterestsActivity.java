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

public class InterestsActivity extends Fragment implements BottomSheetListener {
    ImageView buttonAddInterestsDetails;
    LinearLayout layoutMain;
    LinearLayout layoutNoDataFound;
    String resume_id;
    private String selectedInterestId = "";
    ImageView moreOption;
    TinyDB tinyDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_activity_interests, container, false);
        tinyDB = new TinyDB(requireContext());

        this.resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);
        buttonAddInterestsDetails = view.findViewById(R.id.button_add_interest_details);
        buttonAddInterestsDetails.setOnClickListener(view1 -> promptCreateOrEditDetails(view, 0, ""));

        layoutMain = view.findViewById(R.id.layout_main);
        layoutNoDataFound = view.findViewById(R.id.layout_no_data_found);

        loadInterestDetails();

        return view;
    }

    private void loadInterestDetails() {
        ArrayList<String> listString = tinyDB.getListString(this.resume_id + ":interests_details");

        layoutMain.removeAllViews(); // Remove all existing views before updating

        for (int i = 0; i < listString.size(); i++) {
            View inflate = getLayoutInflater().inflate(R.layout.resume_layout_interests, layoutMain, false);
            TextView interestName = inflate.findViewById(R.id.textview_interest_name);
            interestName.setText(tinyDB.getListString(listString.get(i)).get(0));

            // Edit button logic
            int finalI = i;
            moreOption = inflate.findViewById(R.id.img_option);
            moreOption.setOnClickListener(view -> {
                selectedInterestId = listString.get(finalI);
                BottomSheetOptionsUtil.INSTANCE.showBottomSheetDialog(requireContext(), this);
            });

            // Move Up button logic
            ImageView buttonMoveUp = inflate.findViewById(R.id.button_move_up);
            ImageView buttonMoveDown = inflate.findViewById(R.id.button_move_down);

            // Show/Hide Move Up and Move Down based on the list size
            if (listString.size() > 1) {
                if (i < 1) {
                    buttonMoveUp.setVisibility(View.GONE);
                    buttonMoveDown.setVisibility(View.VISIBLE);
                } else if (i >= listString.size() - 1) {
                    buttonMoveUp.setVisibility(View.VISIBLE);
                    buttonMoveDown.setVisibility(View.GONE);
                } else {
                    buttonMoveUp.setVisibility(View.VISIBLE);
                    buttonMoveDown.setVisibility(View.VISIBLE);
                }
            } else {
                // If there is only one item, hide both buttons
                buttonMoveUp.setVisibility(View.GONE);
                buttonMoveDown.setVisibility(View.GONE);
            }

            buttonMoveUp.setOnClickListener(view -> {
                if (finalI > 0) {
                    Collections.swap(listString, finalI, finalI - 1);
                    updateLayout(listString);
                }
            });

            buttonMoveDown.setOnClickListener(view -> {
                if (finalI < listString.size() - 1) {
                    Collections.swap(listString, finalI, finalI + 1);
                    updateLayout(listString);
                }
            });

            layoutMain.addView(inflate);
        }

        // Show "No Data Found" if the list is empty
        layoutNoDataFound.setVisibility(listString.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateLayout(ArrayList<String> listString) {
        TinyDB tinyDB = new TinyDB(requireContext());
        tinyDB.putListString(resume_id + ":interests_details", listString);

        // Now update the layout with the new order of items
        loadInterestDetails(); // Dynamically update the UI
    }

    public void promptCreateOrEditDetails(View view, int mode, String interestId) {
        View inflate = LayoutInflater.from(requireContext()).inflate(R.layout.resume_prompt_interests, null);
        final EditText editText = inflate.findViewById(R.id.edittext_interest_name);
        Button buttonSaveDetails = inflate.findViewById(R.id.button_save_details);
        Button buttonCancel = inflate.findViewById(R.id.button_cancel);

        if (mode == 1) {
            // Editing existing interest
            editText.setText(new TinyDB(requireContext()).getListString(interestId).get(0));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(inflate);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();

        buttonSaveDetails.setOnClickListener(view1 -> {
            String interestName = editText.getText().toString();
            if (!interestName.isEmpty()) {
                ArrayList<String> interestDetails = new ArrayList<>();
                interestDetails.add(interestName);
                String interestKey = mode == 1 ? interestId : "interests_details:" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

                // Save new or edited interest details
                TinyDB tinyDB = new TinyDB(requireContext());
                tinyDB.putListString(interestKey, interestDetails);

                if (mode == 0) {
                    ArrayList<String> listString = tinyDB.getListString(InterestsActivity.this.resume_id + ":interests_details");
                    listString.add(interestKey);
                    tinyDB.putListString(InterestsActivity.this.resume_id + ":interests_details", listString);
                }

                Toast.makeText(requireContext(), getString(R.string.interest_details_saved_successfully), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loadInterestDetails(); // Update UI after save
            } else {
                Toast.makeText(requireContext(), getString(R.string.please_fill_the_mandatory_details), Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(view1 -> alertDialog.dismiss());
    }

    @Override
    public void onEditClicked() {
        if (!selectedInterestId.isEmpty()) {
            promptCreateOrEditDetails(null, 1, selectedInterestId);
        }
    }

    @Override
    public void onDeleteClicked() {
        if (!selectedInterestId.isEmpty()) {
            TinyDB tinyDB = new TinyDB(requireContext());
            ArrayList<String> listString = tinyDB.getListString(resume_id + ":interests_details");
            listString.remove(selectedInterestId);
            tinyDB.putListString(resume_id + ":interests_details", listString);
            loadInterestDetails(); // Update UI after deletion
            Toast.makeText(requireContext(), getString(R.string.interest_deleted_successfully), Toast.LENGTH_SHORT).show();
        }
    }
}
