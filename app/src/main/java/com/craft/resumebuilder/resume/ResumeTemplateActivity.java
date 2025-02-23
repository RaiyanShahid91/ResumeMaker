package com.craft.resumebuilder.resume;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.craft.resumebuilder.tinydb.TinyDB;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;
import app.craft.myresume.databinding.ResumeActivityResumeTemplateBinding;
import co.resume.utils.Constants;

public class ResumeTemplateActivity extends Fragment {
    CardView cardViewTemplate1;
    CardView cardViewTemplate10;
    CardView cardViewTemplate11;
    CardView cardViewTemplate12;
    CardView cardViewTemplate2;
    CardView cardViewTemplate3;
    CardView cardViewTemplate4;
    CardView cardViewTemplate5;
    CardView cardViewTemplate6;
    CardView cardViewTemplate7;
    CardView cardViewTemplate8;
    CardView cardViewTemplate9;
    String resume_id;
    TextView textViewSelectedTemplate;
    TextView textViewTemplate1;
    TextView textViewTemplate10;
    TextView textViewTemplate11;
    TextView textViewTemplate12;
    TextView textViewTemplate2;
    TextView textViewTemplate3;
    TextView textViewTemplate4;
    TextView textViewTemplate5;
    TextView textViewTemplate6;
    TextView textViewTemplate7;
    TextView textViewTemplate8;
    TextView textViewTemplate9;
    ImageView img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12;
    TinyDB tinydb;
    private ResumeActivityResumeTemplateBinding binding; // Declare the binding instance

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ResumeActivityResumeTemplateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        char c = 1;

        this.textViewSelectedTemplate = view.findViewById(R.id.textview_selected_template);
        this.cardViewTemplate1 = view.findViewById(R.id.cardview_template_1);
        this.cardViewTemplate2 = view.findViewById(R.id.cardview_template_2);
        this.cardViewTemplate3 = view.findViewById(R.id.cardview_template_3);
        this.cardViewTemplate4 = view.findViewById(R.id.cardview_template_4);
        this.cardViewTemplate5 = view.findViewById(R.id.cardview_template_5);
        this.cardViewTemplate6 = view.findViewById(R.id.cardview_template_6);
        this.cardViewTemplate7 = view.findViewById(R.id.cardview_template_7);
        this.cardViewTemplate8 = view.findViewById(R.id.cardview_template_8);
        this.cardViewTemplate9 = view.findViewById(R.id.cardview_template_9);
        this.cardViewTemplate10 = view.findViewById(R.id.cardview_template_10);
        this.cardViewTemplate11 = view.findViewById(R.id.cardview_template_11);
        this.cardViewTemplate12 = view.findViewById(R.id.cardview_template_12);
        this.textViewTemplate1 = view.findViewById(R.id.textview_template_1);
        this.textViewTemplate2 = view.findViewById(R.id.textview_template_2);
        this.textViewTemplate3 = view.findViewById(R.id.textview_template_3);
        this.textViewTemplate4 = view.findViewById(R.id.textview_template_4);
        this.textViewTemplate5 = view.findViewById(R.id.textview_template_5);
        this.textViewTemplate6 = view.findViewById(R.id.textview_template_6);
        this.textViewTemplate7 = view.findViewById(R.id.textview_template_7);
        this.textViewTemplate8 = view.findViewById(R.id.textview_template_8);
        this.textViewTemplate9 = view.findViewById(R.id.textview_template_9);
        this.textViewTemplate10 = view.findViewById(R.id.textview_template_10);
        this.textViewTemplate11 = view.findViewById(R.id.textview_template_11);
        this.textViewTemplate12 = view.findViewById(R.id.textview_template_12);
        this.img1 = view.findViewById(R.id.img1);
        this.img2 = view.findViewById(R.id.img2);
        this.img3 = view.findViewById(R.id.img3);
        this.img4 = view.findViewById(R.id.img4);
        this.img5 = view.findViewById(R.id.img5);
        this.img6 = view.findViewById(R.id.img6);
        this.img7 = view.findViewById(R.id.img7);
        this.img8 = view.findViewById(R.id.img8);
        this.img9 = view.findViewById(R.id.img9);
        this.img10 = view.findViewById(R.id.img10);
        this.img11 = view.findViewById(R.id.img11);
        this.img12 = view.findViewById(R.id.img12);
        TinyDB tinyDB = new TinyDB(requireContext());
        this.tinydb = tinyDB;
        this.resume_id = tinyDB.getString(Constants.SELECTED_RESUME_ID);


        String string = String.valueOf(MyApplication.getuser_theme());

        if (!string.isEmpty()) {
            string.hashCode();
            switch (string.hashCode()) {
                case 49:
                    if (string.equals("1")) {
                        c = 0;
                        break;
                    }
                case 50:
                    break;
                case 51:
                    if (string.equals("3")) {
                        c = 2;
                        break;
                    }
                case 52:
                    if (string.equals("4")) {
                        c = 3;
                        break;
                    }
                case 53:
                    if (string.equals("5")) {
                        c = 4;
                        break;
                    }
                case 54:
                    if (string.equals("6")) {
                        c = 5;
                        break;
                    }
                case 55:
                    if (string.equals("7")) {
                        c = 6;
                        break;
                    }
                case 56:
                    if (string.equals("8")) {
                        c = 7;
                        break;
                    }
                case 57:
                    if (string.equals("9")) {
                        c = 8;
                        break;
                    }
                case 1567:
                    if (string.equals("10")) {
                        c = 9;
                        break;
                    }
                case 1568:
                    if (string.equals("11")) {
                        c = 10;
                        break;
                    }
                case 1569:
                    if (string.equals("12")) {
                        c = 11;
                        break;
                    }
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    txt1();
                    break;
                case 1:
                    txt2();
                    break;
                case 2:
                    txt3();
                    break;
                case 3:
                    txt4();
                    break;
                case 4:
                    txt5();
                    break;
                case 5:
                    txt6();
                    break;
                case 6:
                    txt7();
                    break;
                case 7:
                    txt8();
                    break;
                case 8:
                    txt9();
                    break;
                case 9:
                    txt10();
                    break;
                case 10:
                    txt11();
                    break;
                case 11:
                    txt12();
                    break;
                //Toast.makeText(this, getResources().getString(R.string.string_pro_resume_template), 0).show();
                //startActivity(new Intent(this, SubscriptionActivity.class));
                //break;
            }
        } else {
            this.textViewTemplate1.setTextColor(getResources().getColor(R.color.dodgerblue));
            this.textViewSelectedTemplate.setText("Template 1");
        }
        this.cardViewTemplate1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("1");
            }
        });
        this.cardViewTemplate2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("2");
            }
        });
        this.cardViewTemplate3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("3");
            }
        });
        this.cardViewTemplate4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("4");
            }
        });
        this.cardViewTemplate5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("5");
            }
        });
        this.cardViewTemplate6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("6");
            }
        });
        this.cardViewTemplate7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("7");
            }
        });
        this.cardViewTemplate8.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("8");
            }
        });
        this.cardViewTemplate9.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("9");
            }
        });
        this.cardViewTemplate10.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("10");
            }
        });
        this.cardViewTemplate11.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("11");
            }
        });
        this.cardViewTemplate12.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ResumeTemplateActivity.this.changeTemplate("12");
            }
        });

        return view;
    }


    public void changeTemplate(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 49:
                if (str.equals("1")) {
                    c = 0;
                    break;
                }
                break;
            case 50:
                if (str.equals("2")) {
                    c = 1;
                    break;
                }
                break;
            case 51:
                if (str.equals("3")) {
                    c = 2;
                    break;
                }
                break;
            case 52:
                if (str.equals("4")) {
                    c = 3;
                    break;
                }
                break;
            case 53:
                if (str.equals("5")) {
                    c = 4;
                    break;
                }
                break;
            case 54:
                if (str.equals("6")) {
                    c = 5;
                    break;
                }
                break;
            case 55:
                if (str.equals("7")) {
                    c = 6;
                    break;
                }
                break;
            case 56:
                if (str.equals("8")) {
                    c = 7;
                    break;
                }
                break;
            case 57:
                if (str.equals("9")) {
                    c = 8;
                    break;
                }
                break;
            case 1567:
                if (str.equals("10")) {
                    c = 9;
                    break;
                }
                break;
            case 1568:
                if (str.equals("11")) {
                    c = 10;
                    break;
                }
                break;
            case 1569:
                if (str.equals("12")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                txt1();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(1);
                return;
            case 1:
                txt2();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(2);
                return;
            case 2:
                txt3();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(3);
                return;
            case 3:
                txt4();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(4);
                return;
            case 4:
                txt5();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(5);
                return;
            case 5:
                txt6();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(6);
                return;
            case 6:
                txt7();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(7);
                return;
            case 7:
                txt8();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(8);
                return;
            case 8:
                txt9();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(9);
                return;
            case 9:
                txt10();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(10);
                return;
            case 10:
                txt11();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(11);
                return;
            case 11:
                txt12();
                Toast.makeText(requireContext(), getString(R.string.template_selected), Toast.LENGTH_SHORT).show();
                MyApplication.setuser_theme(12);
                return;

            default:
                return;
        }
    }

    private void txt1() {
        this.textViewSelectedTemplate.setText("EliteEdge");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));
        this.img1.setVisibility(View.VISIBLE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt2() {
        this.textViewSelectedTemplate.setText("CareerCanvas");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));
        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.VISIBLE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt3() {
        this.textViewSelectedTemplate.setText("ProVista");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));
        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.VISIBLE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt4() {
        this.textViewSelectedTemplate.setText("FuturaFlow");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.VISIBLE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt5() {
        this.textViewSelectedTemplate.setText("NeoClassic");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.VISIBLE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt6() {
        this.textViewSelectedTemplate.setText("InnoGraph");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.VISIBLE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt7() {
        this.textViewSelectedTemplate.setText("BoldImpact");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.VISIBLE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt8() {
        this.textViewSelectedTemplate.setText("MinimalistPro");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.VISIBLE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt9() {
        this.textViewSelectedTemplate.setText("Visionary");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.VISIBLE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt10() {
        this.textViewSelectedTemplate.setText("ModernEdge");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.VISIBLE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt11() {
        this.textViewSelectedTemplate.setText("CorporateElite");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.dodgerblue));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.black));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.VISIBLE);
        this.img12.setVisibility(View.GONE);
    }

    private void txt12() {
        this.textViewSelectedTemplate.setText("DynamicFlow");

        this.textViewTemplate1.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate2.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate3.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate4.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate5.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate6.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate7.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate8.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate9.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate10.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate11.setTextColor(getResources().getColor(R.color.black));
        this.textViewTemplate12.setTextColor(getResources().getColor(R.color.dodgerblue));

        this.img1.setVisibility(View.GONE);
        this.img2.setVisibility(View.GONE);
        this.img3.setVisibility(View.GONE);
        this.img4.setVisibility(View.GONE);
        this.img5.setVisibility(View.GONE);
        this.img6.setVisibility(View.GONE);
        this.img7.setVisibility(View.GONE);
        this.img8.setVisibility(View.GONE);
        this.img9.setVisibility(View.GONE);
        this.img10.setVisibility(View.GONE);
        this.img11.setVisibility(View.GONE);
        this.img12.setVisibility(View.VISIBLE);
    }
}