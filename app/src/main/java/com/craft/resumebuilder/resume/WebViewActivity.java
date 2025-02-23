package com.craft.resumebuilder.resume;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.craft.resumebuilder.tinydb.TinyDB;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;

public class WebViewActivity extends AppCompatActivity {
    static final boolean $assertionsDisabled = false;
    Button buttonDownloadResume;
    ImageButton buttonDownloadResumeTop;
    Button buttonPublishResume;
    ImageButton buttonPublishResumeTop;
    String fileTitle;
    StringBuilder htmlCode;
    String mLine = "";
    boolean printBtnPressed = false;
    PrintJob printJob;
    WebView printWeb;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    String resume_id;
    String[] stringColorCodes = {"AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black", "BlanchedAlmond", "Blue", "BlueViolet", "Brown", "BurlyWood", "CadetBlue", "Chartreuse", "Chocolate", "Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenrod", "DarkGray", "DarkGreen", "DarkKhaki", "DarkMagenta", "DarkOliveGreen", "DarkOrange", "DarkOrchid", "DarkRed", "DarkSalmon", "DarkSeaGreen", "DarkSlateBlue", "DarkSlateGray", "DarkTurquoise", "DarkViolet", "DeepPink", "DeepSkyBlue", "DimGray", "DodgerBlue", "FireBrick", "FloralWhite", "ForestGreen", "Fuchsia", "Gainsboro", "GhostWhite", "Gold", "Goldenrod", "Gray", "Green", "GreenYellow", "HoneyDew", "HotPink", "IndianRed", "Indigo", "Ivory", "Khaki", "Lavender", "LavenderBlush", "LawnGreen", "LemonChiffon", "LightBlue", "LightCoral", "LightCyan", "LightGoldenrodYellow", "LightGray", "LightGreen", "LightPink", "LightSalmon", "LightSalmon", "LightSeaGreen", "LightSkyBlue", "LightSlateGray", "LightSteelBlue", "LightYellow", "Lime", "LimeGreen", "Linen", "Magenta", "Maroon", "MediumAquamarine", "MediumBlue", "MediumOrchid", "MediumPurple", "MediumSeaGreen", "MediumSlateBlue", "MediumSlateBlue", "MediumSpringGreen", "MediumTurquoise", "MediumVioletRed", "MidnightBlue", "MintCream", "MistyRose", "Moccasin", "NavajoWhite", "Navy", "OldLace", "Olive", "OliveDrab", "Orange", "OrangeRed", "Orchid", "PaleGoldenrod", "PaleGreen", "PaleTurquoise", "PaleVioletRed", "PapayaWhip", "PeachPuff", "Peru", "Pink", "Plum", "PowderBlue", "Purple", "RebeccaPurple", "Red", "RosyBrown", "RoyalBlue", "SaddleBrown", "Salmon", "SandyBrown", "SeaGreen", "SeaShell", "Sienna", "Silver", "SkyBlue", "SlateBlue", "SlateGray", "Snow", "SpringGreen", "SteelBlue", "Tan", "Teal", "Thistle", "Tomato", "Turquoise", "Violet", "Wheat", "White", "WhiteSmoke", "Yellow", "YellowGreen"};
    String[] stringFontNames = {"Arial", "Times", "Courier", "Consolas", "Cursive", "Abril Fatface", "Arvo", "Bad Script", "Bitter", "Comfortaa", "Courier Prime", "Dancing Script", "Darker Grotesque", "Dosis", "Fuzzy Bubbles", "Gentium Basic", "Inconsolata", "Indie Flower", "Joan", "Josefin Sans", "Libre Baskerville", "Libre Caslon Text", "Lobster", "Lora", "Montserrat", "Mulish", "Nunito", "Oooh Baby", "Open Sans", "Overpass", "Pacifico", "Playfair Display", "Poiret One", "Poppins", "Prompt", "Questrial", "Quicksand", "Raleway", "Roboto", "Rubik", "Shadows Into Light", "Stoke", "Ubuntu", "Varela Round"};
    TextView textViewResumeTitle;
    TinyDB tinydb;
    FloatingActionButton downloadbtn;

    public WebView webView;

    static void lambda$onCreate$0(InitializationStatus initializationStatus) {
    }

    public WebViewActivity() {
    }

    @SuppressLint("WrongThread")
    @Override
    public void onCreate(Bundle bundle) {
        String str;
        String str2;
        BufferedReader bufferedReader;
        Throwable th;
        super.onCreate(bundle);
        setContentView(R.layout.resume_activity_web_view);


        int i = 0;
        MobileAds.initialize(this, WebViewActivity$$ExternalSyntheticLambda2.INSTANCE);
        this.resume_id = getIntent().getStringExtra("resume_id");
        TinyDB tinyDB = new TinyDB(this);
        this.tinydb = tinyDB;
        this.fileTitle = tinyDB.getString(this.resume_id + ":name");
        this.fileTitle += "'s Resume";

        downloadbtn = findViewById(R.id.downloadResume);

//        this.buttonDownloadResumeTop.setOnClickListener(new WebViewActivity$$ExternalSyntheticLambda1(this));
        downloadbtn.setOnClickListener(new WebViewActivity$$ExternalSyntheticLambda1(this));

//        button.setOnClickListener(new WebViewActivity$$ExternalSyntheticLambda0(this));
        this.progressBar = findViewById(R.id.progress);
        ProgressDialog progressDialog2 = new ProgressDialog(this);
        this.progressDialog = progressDialog2;
        progressDialog2.setMessage("Loading...");
        WebView webView2 = findViewById(R.id.webview);
        this.webView = webView2;
        webView2.setWebViewClient(new WebViewClient());
        this.webView.getSettings().setDomStorageEnabled(true);
        String string = this.tinydb.getString(this.resume_id + ":resume_template");
        String str3 = "";

        String string123 = String.valueOf(MyApplication.getuser_theme());

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(!string.equals(str3) ? "templates/template_" + string + "/index.html" : "templates/template_" + string123 + "/index.html"), StandardCharsets.UTF_8));
            this.mLine = (String) bufferedReader.lines().collect(Collectors.joining());
            bufferedReader.close();
        } catch (IOException unused) {
        } catch (Throwable th2) {
            th2.addSuppressed(th2);
        }
        StringBuilder sb = new StringBuilder("<p class='sample_resume_name'>" + this.tinydb.getString(this.resume_id + ":name") + "</p>");
        this.htmlCode = sb;
        sb.append("<p class='sample_resume_email'>").append(this.tinydb.getString(this.resume_id + ":email")).append("</p>");
        this.htmlCode.append("<p class='sample_resume_phone'>").append(this.tinydb.getString(this.resume_id + ":phone")).append("</p>");
        this.htmlCode.append("<p class='sample_resume_address'>").append(this.tinydb.getString(this.resume_id + ":address").replaceAll("\\n", "<br>")).append("</p>");
        this.htmlCode.append("<p class='sample_objective'>").append(this.tinydb.getString(this.resume_id + ":objective").replaceAll("\\n", "<br>")).append("</p>");
        ArrayList<String> listString = this.tinydb.getListString(this.resume_id + ":educational_details");
        this.htmlCode.append("<div class = 'sample_educational_details'>");
        for (int i2 = 0; i2 < listString.size(); i2++) {
            ArrayList<String> listString2 = this.tinydb.getListString(listString.get(i2));
            this.htmlCode.append("<div class = 'element_educational_details'>");
            this.htmlCode.append("<p class = 'template_education_course'>" + listString2.get(0) + "</p>");
            this.htmlCode.append("<p class = 'template_education_university'>" + listString2.get(1) + "</p>");
            this.htmlCode.append("<p class = 'template_education_grade'>Grade: <b>" + listString2.get(2) + "</b></p>");
            this.htmlCode.append("<p class = 'template_education_duration'>" + listString2.get(3) + " - " + listString2.get(4) + "</p>");
            this.htmlCode.append("</div>");
        }
        this.htmlCode.append("</div>");
        ArrayList<String> listString3 = this.tinydb.getListString(this.resume_id + ":work_experience");
        this.htmlCode.append("<div class = 'sample_work_experience'>");
        for (int i3 = 0; i3 < listString3.size(); i3++) {
            ArrayList<String> listString4 = this.tinydb.getListString(listString3.get(i3));
            this.htmlCode.append("<div class = 'element_work_experience'>");
            this.htmlCode.append("<p class = 'template_experience_job'>" + listString4.get(0) + "</p>");
            this.htmlCode.append("<p class = 'template_experience_company'>" + listString4.get(1) + "</p>");
            this.htmlCode.append("<p class = 'template_experience_duration'>" + listString4.get(2) + " - " + listString4.get(3) + "</p>");
            this.htmlCode.append("<p class = 'template_experience_description'>" + listString4.get(4).replaceAll("\\n", "<br>") + "</p>");
            this.htmlCode.append("</div>");
        }
        this.htmlCode.append("</div>");
        ArrayList<String> listString5 = this.tinydb.getListString(this.resume_id + ":projects_details");
        this.htmlCode.append("<div class = 'sample_projects_details'>");
        int i4 = 0;
        while (i4 < listString5.size()) {
            ArrayList<String> listString6 = this.tinydb.getListString(listString5.get(i4));
            this.htmlCode.append("<div class = 'element_projects_details'>");
            this.htmlCode.append("<p class = 'template_project_name'>" + listString6.get(i) + "</p>");
            this.htmlCode.append("<p class = 'template_project_description'>" + listString6.get(1).replaceAll("\\n", "<br>") + "</p>");
            this.htmlCode.append("<p class = 'template_project_duration'>" + listString6.get(2) + " - " + listString6.get(3) + "</p>");
            this.htmlCode.append("<p class = 'template_project_link'>" + listString6.get(4) + "</p>");
            this.htmlCode.append("</div>");
            i4++;
            i = 0;
        }
        this.htmlCode.append("</div>");
        ArrayList<String> listString7 = this.tinydb.getListString(this.resume_id + ":achievements_details");
        this.htmlCode.append("<div class = 'sample_achievements_details'>");
        for (int i5 = 0; i5 < listString7.size(); i5++) {
            ArrayList<String> listString8 = this.tinydb.getListString(listString7.get(i5));
            this.htmlCode.append("<div class = 'element_achievements_details'>");
            this.htmlCode.append("<p class = 'template_achievement_name'>" + listString8.get(0) + "</p>");
            this.htmlCode.append("</div>");
        }
        this.htmlCode.append("</div>");
        ArrayList<String> listString9 = this.tinydb.getListString(this.resume_id + ":skills_details");
        this.htmlCode.append("<div class = 'sample_skills_details'>");
        int i6 = 0;
        while (i6 < listString9.size()) {
            ArrayList<String> listString10 = this.tinydb.getListString(listString9.get(i6));
            this.htmlCode.append("<div class = 'element_skills_details'>");
            this.htmlCode.append("<p class = 'template_skill_name'>" + listString10.get(0) + "</p>");
            this.htmlCode.append("<p class = 'template_skill_level'>" + listString10.get(1) + "</p>");
            this.htmlCode.append("</div>");
            i6++;
            str3 = str3;
        }
        String str4 = str3;
        this.htmlCode.append("</div>");
        ArrayList<String> listString11 = this.tinydb.getListString(this.resume_id + ":interests_details");
        this.htmlCode.append("<div class = 'sample_interests_details'>");
        int i7 = 0;
        while (i7 < listString11.size()) {
            ArrayList<String> listString12 = this.tinydb.getListString(listString11.get(i7));
            this.htmlCode.append("<div class = 'element_interests_details'>");
            this.htmlCode.append("<p class = 'template_interest_name'>" + listString12.get(0) + "</p>");
            this.htmlCode.append("</div>");
            i7++;
            listString11 = listString11;
        }
        ArrayList<String> arrayList = listString11;
        this.htmlCode.append("</div>");
        ArrayList<String> listString13 = this.tinydb.getListString(this.resume_id + ":hobbies_details");
        this.htmlCode.append("<div class = 'sample_hobbies_details'>");
        int i8 = 0;
        while (i8 < listString13.size()) {
            ArrayList<String> listString14 = this.tinydb.getListString(listString13.get(i8));
            this.htmlCode.append("<div class = 'element_hobbies_details'>");
            this.htmlCode.append("<p class = 'template_hobby_name'>" + listString14.get(0) + "</p>");
            this.htmlCode.append("</div>");
            i8++;
            listString13 = listString13;
        }
        ArrayList<String> arrayList2 = listString13;
        this.htmlCode.append("</div>");
        ArrayList<String> listString15 = this.tinydb.getListString(this.resume_id + ":languages_details");
        this.htmlCode.append("<div class = 'sample_languages_details'>");
        int i9 = 0;
        while (i9 < listString15.size()) {
            ArrayList<String> listString16 = this.tinydb.getListString(listString15.get(i9));
            this.htmlCode.append("<div class = 'element_languages_details'>");
            this.htmlCode.append("<p class = 'template_language_name'>" + listString16.get(0) + "</p>");
            this.htmlCode.append("</div>");
            i9++;
            listString15 = listString15;
        }
        ArrayList<String> arrayList3 = listString15;
        this.htmlCode.append("</div>");
        this.htmlCode.append("<p class='sample_declaration'>").append(this.tinydb.getString(this.resume_id + ":declaration").replaceAll("\\n", "<br>")).append("</p>");
        this.htmlCode.append("<p class='sample_declaration_place'>Place: <b>").append(this.tinydb.getString(this.resume_id + ":declaration_place").replaceAll("\\n", "<br>")).append("</b></p>");
        this.htmlCode.append("<p class='sample_declaration_date'>Date: <b>").append(this.tinydb.getString(this.resume_id + ":declaration_date").replaceAll("\\n", "<br>")).append("</b></p>");
        if (listString3.isEmpty()) {
            this.htmlCode.append("<style>.div_work_experience{display:none;}</style>");
        }
        if (listString.isEmpty()) {
            this.htmlCode.append("<style>.div_educational_details{display:none;}</style>");
        }
        if (listString5.isEmpty()) {
            this.htmlCode.append("<style>.div_projects_details{display:none;}</style>");
        }
        if (listString7.isEmpty()) {
            this.htmlCode.append("<style>.div_achievements_details{display:none;}</style>");
        }
        if (listString9.isEmpty()) {
            this.htmlCode.append("<style>.div_skills_details{display:none;}</style>");
        }
        if (arrayList.isEmpty()) {
            this.htmlCode.append("<style>.div_interests_details{display:none;}</style>");
        }
        if (arrayList2.isEmpty()) {
            this.htmlCode.append("<style>.div_hobbies_details{display:none;}</style>");
        }
        if (arrayList3.isEmpty()) {
            this.htmlCode.append("<style>.div_languages_details{display:none;}</style>");
        }
        String str5 = str4;
        if (this.tinydb.getString(this.resume_id + ":profile_photo").equals(str5)) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BitmapFactory.decodeResource(getResources(), R.drawable.icon_resume_profile_photo).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            str = Base64.encodeToString(byteArrayOutputStream.toByteArray(), 0);
            byte[] decode = Base64.decode(str, 0);
            BitmapFactory.decodeByteArray(decode, 0, decode.length);
        } else {
            str = this.tinydb.getString(this.resume_id + ":profile_photo");
        }
        this.htmlCode.append("<div class='sample_resume_image'><img class='profile-img' src = 'data:image/png;base64,").append(str).append("' /></div>");
//        if (this.tinydb.getString(this.resume_id + ":signature").equals(str5)) {
//            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
//            BitmapFactory.decodeResource(getResources(), R.drawable.icon_sample_signature).compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2);
//            str2 = Base64.encodeToString(byteArrayOutputStream2.toByteArray(), 0);
//            byte[] decode2 = Base64.decode(str2, 0);
//            BitmapFactory.decodeByteArray(decode2, 0, decode2.length);
//        } else {
//            str2 = this.tinydb.getString(this.resume_id + ":signature");
//        }
//        this.htmlCode.append("<div class='sample_resume_signature'><img class='signature-img' src = 'data:image/png;base64,").append(str2).append("' /></div>");
        this.htmlCode.append(this.mLine);
        int i10 = this.tinydb.getInt(this.resume_id + ":font_family");
        this.htmlCode.append("<style>@import url('https://fonts.googleapis.com/css2?family=" + this.stringFontNames[i10] + "&display=swap');\nbutton, html, select, input{font-family:'" + this.stringFontNames[i10] + "';}</style>");
        this.htmlCode.append("<style>:root{--color-one: " + this.stringColorCodes[this.tinydb.getInt(this.resume_id + ":color_one")] + ";--color-two: " + this.stringColorCodes[this.tinydb.getInt(this.resume_id + ":color_two")] + ";}</style>");
        this.webView.loadData(this.htmlCode.toString(), "text/html", (String) null);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.setWebViewClient(new webClient());
        this.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                WebViewActivity.this.progressBar.setVisibility(View.VISIBLE);
                WebViewActivity.this.progressBar.setProgress(i);
                WebViewActivity.this.progressDialog.show();
                if (i == 100) {
                    WebViewActivity.this.progressBar.setVisibility(View.GONE);
                    WebViewActivity.this.progressDialog.dismiss();
                }
                super.onProgressChanged(webView, i);
            }

            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //Toast.makeText(WebViewActivity.this, "Line " + consoleMessage.lineNumber() + " : " + consoleMessage.message(), Toast.LENGTH_SHORT).show();
                return super.onConsoleMessage(consoleMessage);
            }
        });
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("MyUserPrefs", 0);
        if (sharedPreferences.getString("first_time", "1").equals("1")) {
            promptWebViewInformation();
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("first_time", "0");
            edit.commit();
            return;
        }
        return;
    }


    public void printFunction(View view) {
        WebView webView2 = this.printWeb;
        if (webView2 != null) {
            PrintTheWebPage(webView2);
        } else {
            Toast.makeText(this, getString(R.string.web_page_not_fully_loaded), Toast.LENGTH_SHORT).show();
        }
    }

    private void PrintTheWebPage(WebView webView2) {
        this.printBtnPressed = true;
        String str = this.fileTitle;
        this.printJob = ((PrintManager) getSystemService(Context.PRINT_SERVICE)).print(str, webView2.createPrintDocumentAdapter(str), new PrintAttributes.Builder().build());
    }

    @Override
    public void onResume() {
        super.onResume();
        PrintJob printJob2 = this.printJob;
        if (printJob2 != null && this.printBtnPressed) {
            if (printJob2.isCompleted()) {
                Toast.makeText(this, getString(R.string.resume_saved_successfully), Toast.LENGTH_SHORT).show();
            } else if (!this.printJob.isStarted() && !this.printJob.isBlocked() && !this.printJob.isCancelled()) {
                if (this.printJob.isFailed()) {
                    Toast.makeText(this, getString(R.string.failed_to_save_resume), Toast.LENGTH_SHORT).show();
                } else {
                    this.printJob.isQueued();
                }
            }
            this.printBtnPressed = false;
        }
    }

    public void publishResume(View view) {
        //Toast.makeText(this, getResources().getString(R.string.string_pro_publish_resume), 0).show();
        //startActivity(new Intent(this, SubscriptionActivity.class));
    }

    public class webClient extends WebViewClient {
        public webClient() {
        }

        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            WebViewActivity webViewActivity = WebViewActivity.this;
            webViewActivity.printWeb = webViewActivity.webView;
        }
    }

    public void promptWebViewInformation() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.prompt_webview_information, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        builder.setCancelable(true);
        final AlertDialog create = builder.create();
        create.show();
        ((Button) inflate.findViewById(R.id.prompt_button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create.cancel();
            }
        });
    }
}
