package com.craft.resumebuilder.resume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.craft.resumebuilder.tinydb.TinyDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;

public class HobbiesActivity extends AppCompatActivity {
    Button buttonAddHobbiesDetails;
    LinearLayout layoutMain;
    LinearLayout layoutNoDataFound;
    String resume_id;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.resume_activity_hobbies);


        //Reguler Banner Ads
        RelativeLayout admob_banner = findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admob_banner, adContainer);


        this.resume_id = getIntent().getStringExtra("resume_id");
        Button button = findViewById(R.id.button_add_hobbies_details);
        this.buttonAddHobbiesDetails = button;
        button.setOnClickListener(view -> HobbiesActivity.this.promptCreateOrEditDetails(view, 0, ""));
        this.layoutMain = findViewById(R.id.layout_main);
        this.layoutNoDataFound = findViewById(R.id.layout_no_data_found);
        final TinyDB tinyDB = new TinyDB(this);
        final ArrayList<String> listString = tinyDB.getListString(this.resume_id + ":hobbies_details");
        for (int i = 0; i < listString.size(); i++) {
            View inflate = getLayoutInflater().inflate(R.layout.resume_layout_hobbies, this.layoutMain, false);
            ((TextView) inflate.findViewById(R.id.textview_hobby_name)).setText(tinyDB.getListString(listString.get(i)).get(0));
            int finalI = i;
            inflate.findViewById(R.id.button_edit_details).setOnClickListener(view -> HobbiesActivity.this.promptCreateOrEditDetails(view, 1, listString.get(finalI)));
            final ArrayList<String> arrayList = listString;
            final int i2 = i;
            final TinyDB tinyDB2 = tinyDB;
            final View view = inflate;
            ((Button) inflate.findViewById(R.id.button_delete)).setOnClickListener(view1 -> {
                //ArrayList arrayList = arrayList;
                arrayList.remove(arrayList.get(i2));
                tinyDB2.putListString(HobbiesActivity.this.resume_id + ":hobbies_details", arrayList);
                view1.setVisibility(View.GONE);
                HobbiesActivity.this.finish();
                HobbiesActivity hobbiesActivity = HobbiesActivity.this;
                hobbiesActivity.startActivity(hobbiesActivity.getIntent());
            });
            Button button2 = inflate.findViewById(R.id.button_move_up);
            Button button3 = inflate.findViewById(R.id.button_move_down);
            if (i < 1) {
                button2.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);
            } else if (i > listString.size() - 2) {
                button3.setVisibility(View.GONE);
            }
            int finalI1 = i;
            int finalI2 = i;
            button2.setOnClickListener(view2 -> {
                ArrayList arrayList1 = listString;
                //int i = i;
                Collections.swap(arrayList1, finalI1, finalI2 - 1);
                tinyDB.putListString(HobbiesActivity.this.resume_id + ":hobbies_details", listString);
                HobbiesActivity.this.finish();
                HobbiesActivity hobbiesActivity = HobbiesActivity.this;
                hobbiesActivity.startActivity(hobbiesActivity.getIntent());
            });
            int finalI3 = i;
            int finalI4 = i;
            button3.setOnClickListener(view3 -> {
                ArrayList arrayList2 = listString;
                //int i = i;
                Collections.swap(arrayList2, finalI3, finalI4 + 1);
                tinyDB.putListString(HobbiesActivity.this.resume_id + ":hobbies_details", listString);
                HobbiesActivity.this.finish();
                HobbiesActivity hobbiesActivity = HobbiesActivity.this;
                hobbiesActivity.startActivity(hobbiesActivity.getIntent());
            });
            this.layoutMain.addView(inflate);
        }
        if (listString.isEmpty()) {
            this.layoutNoDataFound.setVisibility(View.VISIBLE);
        }
    }

    public void promptCreateOrEditDetails(View view, int i, String str) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.resume_prompt_hobbies, null);
        final EditText editText = inflate.findViewById(R.id.edittext_hobby_name);
        Button button = inflate.findViewById(R.id.button_save_details);
        Button button2 = inflate.findViewById(R.id.button_cancel);
        if (i == 1) {
            editText.setText(new TinyDB(this).getListString(str).get(0));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        builder.setCancelable(false);
        final AlertDialog create = builder.create();
        create.show();
        final int i2 = i;
        final String str2 = str;
        final AlertDialog alertDialog = create;
        button.setOnClickListener(view1 -> {
            String obj = editText.getText().toString();
            if (!obj.equals("")) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(obj);
                String format = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                TinyDB tinyDB = new TinyDB(HobbiesActivity.this);
                if (i2 == 1) {
                    tinyDB.putListString(str2, arrayList);
                } else {
                    ArrayList<String> listString = tinyDB.getListString(HobbiesActivity.this.resume_id + ":hobbies_details");
                    listString.add("hobbies_details:" + format);
                    tinyDB.putListString(HobbiesActivity.this.resume_id + ":hobbies_details", listString);
                    tinyDB.putListString("hobbies_details:" + format, arrayList);
                }
                Toast.makeText(HobbiesActivity.this, "Hobby details saved successfully.", Toast.LENGTH_SHORT).show();
                HobbiesActivity.this.finish();
                HobbiesActivity hobbiesActivity = HobbiesActivity.this;
                hobbiesActivity.startActivity(hobbiesActivity.getIntent());
                alertDialog.cancel();
                return;
            }
            Toast.makeText(HobbiesActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
        });
        button2.setOnClickListener(view2 -> create.cancel());
    }
}
