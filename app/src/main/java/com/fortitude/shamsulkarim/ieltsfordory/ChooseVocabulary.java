package com.fortitude.shamsulkarim.ieltsfordory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.fortitude.apps.vocabularybuilder.ChooseLanguageActivity;
import com.fortitude.shamsulkarim.ieltsfordory.forCheckingConnection.ConnectivityHelper;
import mehdi.sakout.fancybuttons.FancyButton;

public class ChooseVocabulary extends AppCompatActivity {


    private SharedPreferences sp;
    private CheckBox ieltsCheckbox, toeflCheckbox, satCheckbox, greCheckbox;
    private boolean isIeltsChecked, isToeflChecked, isSatChecked, isGreChecked;
    private FancyButton conTinue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_vocabulary);

        // This code reports to Crashlytics of connection
        checkInternetConnection();




        sp = this.getSharedPreferences("com.example.shamsulkarim.vocabulary", Context.MODE_PRIVATE);
        if(sp.contains("home")){

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }else{

            initialization();
            conTinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(ChooseVocabulary.this, ChooseLanguageActivity.class));
                    finish();

                }
            });
        }
    }


    private void initialization(){

        ieltsCheckbox = findViewById(R.id.ielts_checkbox);
        toeflCheckbox = findViewById(R.id.toefl_checkbox);
        satCheckbox =   findViewById(R.id.sat_checkbox);
        greCheckbox =   findViewById(R.id.gre_checkbox);
        conTinue = findViewById(R.id.train_finished_unlearn);
        isIeltsChecked = true;
        isToeflChecked = true;
        isSatChecked =   true;
        isGreChecked =   true;









        if(!sp.contains("home")){

            sp.edit().putBoolean("isIELTSActive",true).apply();
            sp.edit().putBoolean("isTOEFLActive",true).apply();
            sp.edit().putBoolean("isSATActive",true).apply();
            sp.edit().putBoolean("isGREActive",true).apply();



        }
        Crashlytics.setBool("isIELTSActive",true);
        Crashlytics.setBool("isTOEFLActive",true);
        Crashlytics.setBool("isSATActive",true);
        Crashlytics.setBool("isGREActive",true);
//        isIeltsChecked = sp.getBoolean("isIELTSActive",true);
//        isToeflChecked = sp.getBoolean("isTOEFLActive", true);
//        isSatChecked =   sp.getBoolean("isSATActive", true);
//        isGreChecked =   sp.getBoolean("isGREActive",true);









        ieltsCheckbox.setChecked(isIeltsChecked);
        toeflCheckbox.setChecked(isToeflChecked);
        satCheckbox.setChecked(isSatChecked);
        greCheckbox.setChecked(isGreChecked);






        ieltsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if(!ieltsCheckbox.isChecked()){

                    if( toeflCheckbox.isChecked() || satCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isIELTSActive", false).apply();
                        Crashlytics.setBool("isIELTSActive",false);
                        Toast.makeText(ChooseVocabulary.this, "Ielts unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(ChooseVocabulary.this, "At least select one", Toast.LENGTH_SHORT).show();
                        ieltsCheckbox.setChecked(true);


                    }


                }else {


                    sp.edit().putBoolean("isIELTSActive", true).apply();
                    Crashlytics.setBool("isIELTSActive",true);
                    Toast.makeText(ChooseVocabulary.this, "IELTS checked",Toast.LENGTH_SHORT).show();

                }


            }
        });


        toeflCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!toeflCheckbox.isChecked()){


                    if( ieltsCheckbox.isChecked() || satCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isTOEFLActive", false).apply();
                        Crashlytics.setBool("isTOEFLActive",false);
                        Toast.makeText(ChooseVocabulary.this, "TOEFL unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(ChooseVocabulary.this, "At least select one", Toast.LENGTH_SHORT).show();
                        toeflCheckbox.setChecked(true);

                    }





                }else {

                    sp.edit().putBoolean("isTOEFLActive", true).apply();
                    Crashlytics.setBool("isTOEFLActive",true);
                    Toast.makeText(ChooseVocabulary.this, "TOEFL checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        satCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!satCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || greCheckbox.isChecked()){


                        sp.edit().putBoolean("isSATActive", false).apply();
                        Crashlytics.setBool("isSATActive",false);
                        Toast.makeText(ChooseVocabulary.this, "SAT unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(ChooseVocabulary.this, "At least select one", Toast.LENGTH_SHORT).show();
                        satCheckbox.setChecked(true);

                    }



                }else {



                    sp.edit().putBoolean("isSATActive", true).apply();
                    Crashlytics.setBool("isSATActive",true);
                    Toast.makeText(ChooseVocabulary.this, "SAT checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

        greCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!greCheckbox.isChecked()){

                    if( ieltsCheckbox.isChecked() || toeflCheckbox.isChecked() || satCheckbox.isChecked()){


                        sp.edit().putBoolean("isGREActive", false).apply();
                        Crashlytics.setBool("isGREActive",false);
                        Toast.makeText(ChooseVocabulary.this, "GRE unchecked", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(ChooseVocabulary.this, "At least select one", Toast.LENGTH_SHORT).show();
                        greCheckbox.setChecked(true);

                    }



                }else {

                    sp.edit().putBoolean("isGREActive", true).apply();
                    Crashlytics.setBool("isGREActive",true);
                    Toast.makeText(ChooseVocabulary.this, "GRE checked",Toast.LENGTH_SHORT).show();


                }


            }
        });

    }






    // This method Checks internet connection and reports to Crashlytics
    private void checkInternetConnection(){
        Boolean connected = ConnectivityHelper.isConnectedToNetwork(this);
        Crashlytics.setBool("Connection Status",connected);
    }


}
