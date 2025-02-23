package com.craft.resumebuilder.resume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import app.craft.myresume.R;
import app.craft.myresume.databinding.ResumeActivityPersonalDetailsBinding;
import co.resume.utils.Constants;

public class PersonalDetailsActivity extends Fragment {

    private ResumeActivityPersonalDetailsBinding binding; // Declare the binding instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResumeActivityPersonalDetailsBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    private void init() {

        final TinyDB tinyDB = new TinyDB(requireContext());
        final String stringExtra = tinyDB.getString(Constants.SELECTED_RESUME_ID);

        binding.edittextPersonName.setText(tinyDB.getString(stringExtra + ":name"));
        binding.edittextPersonEmail.setText(tinyDB.getString(stringExtra + ":email"));
        binding.edittextPersonPhoneNumber.setText(tinyDB.getString(stringExtra + ":phone"));
        binding.edittextPersonAddress.setText(tinyDB.getString(stringExtra + ":address"));

        binding.buttonSaveDetails.setOnClickListener(view -> {
            String obj = binding.edittextPersonName.getText().toString();
            String obj2 = binding.edittextPersonEmail.getText().toString();
            String obj3 = binding.edittextPersonPhoneNumber.getText().toString();
            String obj4 = binding.edittextPersonAddress.getText().toString();
            if (!obj.isEmpty() && !obj2.isEmpty() && !obj3.isEmpty() && !obj4.isEmpty()) {
                tinyDB.putString(stringExtra + ":name", obj);
                tinyDB.putString(stringExtra + ":email", obj2);
                tinyDB.putString(stringExtra + ":phone", obj3);
                tinyDB.putString(stringExtra + ":address", obj4);
                Toast.makeText(requireContext(), getString(R.string.details_saved_successfully), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), getString(R.string.please_enter_the_name), Toast.LENGTH_SHORT).show();
        });
    }
}
