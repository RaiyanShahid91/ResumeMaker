package com.craft.resumebuilder.resume;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import app.craft.myresume.R;
import app.craft.myresume.ads.AdsCommon;

import com.craft.resumebuilder.tinydb.TinyDB;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button buttonSaveSettings;
    LinearLayout layoutColorOne;
    LinearLayout layoutColorTwo;
    String resume_id;
    Spinner spinnerColorOne;
    Spinner spinnerColorTwo;
    Spinner spinnerFont;
    String[] stringColorCodes = {"#F0F8FF", "#FAEBD7", "#00FFFF", "#7FFFD4", "#F0FFFF", "#F5F5DC", "#FFE4C4", "#000000", "#FFEBCD", "#0000FF", "#8A2BE2", "#A52A2A", "#DEB887", "#5F9EA0", "#7FFF00", "#D2691E", "#FF7F50", "#6495ED", "#FFF8DC", "#DC143C", "#00FFFF", "#00008B", "#008B8B", "#B8860B", "#A9A9A9", "#006400", "#BDB76B", "#8B008B", "#556B2F", "#FF8C00", "#9932CC", "#8B0000", "#E9967A", "#8FBC8B", "#483D8B", "#2F4F4F", "#00CED1", "#9400D3", "#FF1493", "#00BFFF", "#696969", "#1E90FF", "#B22222", "#FFFAF0", "#228B22", "#FF00FF", "#DCDCDC", "#F8F8FF", "#FFD700", "#DAA520", "#808080", "#008000", "#ADFF2F", "#F0FFF0", "#FF69B4", "#CD5C5C", "#4B0082", "#FFFFF0", "#F0E68C", "#E6E6FA", "#FFF0F5", "#7CFC00", "#FFFACD", "#ADD8E6", "#F08080", "#E0FFFF", "#FAFAD2", "#D3D3D3", "#90EE90", "#FFB6C1", "#FFA07A", "#FFA07A", "#20B2AA", "#87CEFA", "#778899", "#B0C4DE", "#FFFFE0", "#00FF00", "#32CD32", "#FAF0E6", "#FF00FF", "#800000", "#66CDAA", "#0000CD", "#BA55D3", "#9370DB", "#3CB371", "#7B68EE", "#7B68EE", "#00FA9A", "#48D1CC", "#C71585", "#191970", "#F5FFFA", "#FFE4E1", "#FFE4B5", "#FFDEAD", "#000080", "#FDF5E6", "#808000", "#6B8E23", "#FFA500", "#FF4500", "#DA70D6", "#EEE8AA", "#98FB98", "#AFEEEE", "#DB7093", "#FFEFD5", "#FFDAB9", "#CD853F", "#FFC0CB", "#DDA0DD", "#B0E0E6", "#800080", "#663399", "#FF0000", "#BC8F8F", "#4169E1", "#8B4513", "#FA8072", "#F4A460", "#2E8B57", "#FFF5EE", "#A0522D", "#C0C0C0", "#87CEEB", "#6A5ACD", "#708090", "#FFFAFA", "#00FF7F", "#4682B4", "#D2B48C", "#008080", "#D8BFD8", "#FF6347", "#40E0D0", "#EE82EE", "#F5DEB3", "#FFFFFF", "#F5F5F5", "#FFFF00", "#9ACD32"};
    String[] stringColorNames = {"AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black", "BlanchedAlmond", "Blue", "BlueViolet", "Brown", "BurlyWood", "CadetBlue", "Chartreuse", "Chocolate", "Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenrod", "DarkGray", "DarkGreen", "DarkKhaki", "DarkMagenta", "DarkOliveGreen", "DarkOrange", "DarkOrchid", "DarkRed", "DarkSalmon", "DarkSeaGreen", "DarkSlateBlue", "DarkSlateGray", "DarkTurquoise", "DarkViolet", "DeepPink", "DeepSkyBlue", "DimGray", "DodgerBlue", "FireBrick", "FloralWhite", "ForestGreen", "Fuchsia", "Gainsboro", "GhostWhite", "Gold", "Goldenrod", "Gray", "Green", "GreenYellow", "HoneyDew", "HotPink", "IndianRed", "Indigo", "Ivory", "Khaki", "Lavender", "LavenderBlush", "LawnGreen", "LemonChiffon", "LightBlue", "LightCoral", "LightCyan", "LightGoldenrodYellow", "LightGray", "LightGreen", "LightPink", "LightSalmon", "LightSalmon", "LightSeaGreen", "LightSkyBlue", "LightSlateGray", "LightSteelBlue", "LightYellow", "Lime", "LimeGreen", "Linen", "Magenta", "Maroon", "MediumAquamarine", "MediumBlue", "MediumOrchid", "MediumPurple", "MediumSeaGreen", "MediumSlateBlue", "MediumSlateBlue", "MediumSpringGreen", "MediumTurquoise", "MediumVioletRed", "MidnightBlue", "MintCream", "MistyRose", "Moccasin", "NavajoWhite", "Navy", "OldLace", "Olive", "OliveDrab", "Orange", "OrangeRed", "Orchid", "PaleGoldenrod", "PaleGreen", "PaleTurquoise", "PaleVioletRed", "PapayaWhip", "PeachPuff", "Peru", "Pink", "Plum", "PowderBlue", "Purple", "RebeccaPurple", "Red", "RosyBrown", "RoyalBlue", "SaddleBrown", "Salmon", "SandyBrown", "SeaGreen", "SeaShell", "Sienna", "Silver", "SkyBlue", "SlateBlue", "SlateGray", "Snow", "SpringGreen", "SteelBlue", "Tan", "Teal", "Thistle", "Tomato", "Turquoise", "Violet", "Wheat", "White", "WhiteSmoke", "Yellow", "YellowGreen"};
    String[] stringFontNames = {"Arial", "Times", "Courier", "Consolas", "Cursive", "Abril Fatface", "Arvo", "Bad Script", "Bitter", "Comfortaa", "Courier Prime", "Dancing Script", "Darker Grotesque", "Dosis", "Fuzzy Bubbles", "Gentium Basic", "Inconsolata", "Indie Flower", "Joan", "Josefin Sans", "Libre Baskerville", "Libre Caslon Text", "Lobster", "Lora", "Montserrat", "Mulish", "Nunito", "Oooh Baby", "Open Sans", "Overpass", "Pacifico", "Playfair Display", "Poiret One", "Poppins", "Prompt", "Questrial", "Quicksand", "Raleway", "Roboto", "Rubik", "Shadows Into Light", "Stoke", "Ubuntu", "Varela Round"};

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public SettingsActivity() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.resume_activity_settings);


        //Reguler Banner Ads
        RelativeLayout admob_banner = (RelativeLayout) findViewById(R.id.Admob_Banner_Frame);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        AdsCommon.RegulerBanner(this, admob_banner, adContainer);


        this.resume_id = getIntent().getStringExtra("resume_id");
        Spinner spinner = (Spinner) findViewById(R.id.spinner_color_one);
        this.spinnerColorOne = spinner;
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, this.stringColorNames);
        arrayAdapter.setDropDownViewResource(17367049);
        this.spinnerColorOne.setAdapter(arrayAdapter);
        this.spinnerColorOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SettingsActivity.this.layoutColorOne.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(SettingsActivity.this.stringColorCodes[i])));
            }
        });
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner_color_two);
        this.spinnerColorTwo = spinner2;
        spinner2.setOnItemSelectedListener(this);
        arrayAdapter.setDropDownViewResource(17367049);
        this.spinnerColorTwo.setAdapter(arrayAdapter);
        this.spinnerColorTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SettingsActivity.this.layoutColorTwo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(SettingsActivity.this.stringColorCodes[i])));
            }
        });
        Spinner spinner3 = (Spinner) findViewById(R.id.spinner_font_family);
        this.spinnerFont = spinner3;
        spinner3.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.spinner_item, this.stringFontNames);
        arrayAdapter2.setDropDownViewResource(17367049);
        this.spinnerFont.setAdapter(arrayAdapter2);
        final TinyDB tinyDB = new TinyDB(this);
        int i = tinyDB.getInt(this.resume_id + ":color_one");
        this.spinnerColorOne.setSelection(i);
        int i2 = tinyDB.getInt(this.resume_id + ":color_two");
        this.spinnerColorTwo.setSelection(i2);
        this.spinnerFont.setSelection(tinyDB.getInt(this.resume_id + ":font_family"));
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_color_one);
        this.layoutColorOne = linearLayout;
        linearLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(this.stringColorCodes[i])));
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.layout_color_two);
        this.layoutColorTwo = linearLayout2;
        linearLayout2.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(this.stringColorCodes[i2])));
        Button button = (Button) findViewById(R.id.button_save_settings);
        this.buttonSaveSettings = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tinyDB.putInt(SettingsActivity.this.resume_id + ":color_one", SettingsActivity.this.spinnerColorOne.getSelectedItemPosition());
                tinyDB.putInt(SettingsActivity.this.resume_id + ":color_two", SettingsActivity.this.spinnerColorTwo.getSelectedItemPosition());
                tinyDB.putInt(SettingsActivity.this.resume_id + ":font_family", SettingsActivity.this.spinnerFont.getSelectedItemPosition());
                Toast.makeText(SettingsActivity.this, "Settings has been saved successfully.", 0).show();
            }
        });
    }
}
