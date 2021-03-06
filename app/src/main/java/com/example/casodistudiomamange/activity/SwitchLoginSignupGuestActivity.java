package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.adapter.ViewPagerFragmentAdapter;
import com.example.casodistudiomamange.connection.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Locale;

/**
 * Activity in cui l'utente decide come entrare nell'app, se come ospite, registarsi o loggarsi a un account esistente
 */
public class SwitchLoginSignupGuestActivity extends AppCompatActivity {

    NetworkChangedListener networkChangedListener = new NetworkChangedListener();
    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    public static int controlValue;
    private final String[] titles = {"Guest", "Login", "Signup"};
    private final String[] titles_it = {"Ospite", "Accedi", "Registrati"};
    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final com.google.mlkit.nl.translate.Translator Translator = Translation.getClient(options);
    Dialog progdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_login_signup_guest);
        FirebaseAuth lAuth = FirebaseAuth.getInstance();
        viewPager2=findViewById(R.id.view_pager);
        tabLayout=findViewById(R.id.tab_layout);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this);
        Log.d("TAG", String.valueOf(controlValue));
        ManageDownload();

        viewPager2.setAdapter(viewPagerFragmentAdapter);
        if(lAuth.getCurrentUser() != null){
            Toast.makeText(this,getResources().getString(R.string.loggedIn),Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoggedUser.class));
            finish();
        }

        if(Locale.getDefault().getLanguage().equals("it")){
            new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles_it[position]))).attach();
        }else{
            new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> tab.setText(titles[position]))).attach();
        }

    }

    @Override
    protected void onStart(){
        //verifica la presenza di connessione internet
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        //verifica la presenza di connessione internet
        unregisterReceiver(networkChangedListener);
        super.onStop();
    }

    /**
     * Metodo che permette di salvare lo stato dell'alert che gestisce il download della lingua
     * @param isChecked indica se ?? stato gi?? scaricato o no
     */
    private void storeDialogStatus(boolean isChecked,int v){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("item", isChecked);
        mEditor.putInt("value", v);
        mEditor.apply();
    }

    /**
     * Metodo che all'avvio dell'app non in lingua italiana fa ritornare lo stato dell'alert mostrato per il download del pacchetto di traduzione
     * @return stato dello shared preferences
     */
    private boolean getDialogStatus(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getBoolean("item", false);
    }

    /**
     * Metodo che all'avvio dell'app non in lingua italiana fa ritornare lo stato dell'alert mostrato per il download del pacchetto di traduzione
     * @return stato dello shared preferences
     */
    private int getDialogStatusTrans(){
        SharedPreferences mSharedPreferences = getSharedPreferences("CheckItem", MODE_PRIVATE);
        return mSharedPreferences.getInt("value", controlValue);
    }

    /**
     * Metodo che verifica la lingua del dispositivo. Se questa non ?? in italiano viene chiesto all'utente
     * di scaricare un pacchetto di traduzione del men?? attraverso un alert.
     * L'utente pu?? decidere di scaricarlo o meno
     */
    private void ManageDownload(){
        if(!Locale.getDefault().getLanguage().equals((new Locale("it").getLanguage()))){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mView = getLayoutInflater().inflate(R.layout.check_menu_language, null);
            CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
            mBuilder.setCancelable(false);
            mBuilder.setTitle("Men?? language choice");
            mBuilder.setMessage("We detect that your system is in English. Do you want to download the english men?? or continue with the italian men???");
            mBuilder.setView(mView);
            mBuilder.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    progressBar();
                    downloadModel();
                    controlValue=0;
                }
            });
            mBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    controlValue=1;
                }
            });

            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //verifica che la chechbox "non mostrare pi??" sia stata premuta
                    if(compoundButton.isChecked()){
                        storeDialogStatus(true,1);

                    }else{
                        storeDialogStatus(false,1);
                    }
                }
            });

            if(getDialogStatus() && getDialogStatusTrans()==1){
                controlValue=1;
                mDialog.hide();
            }else{
                mDialog.show();
            }
        }

    }

    /**
     * Metodo nel quale avviene il download del pacchetto di traduzione
     */
    private void downloadModel(){
        DownloadConditions conditions = new DownloadConditions.Builder()
                .build();
        Translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void v) {
                //se il pacchetto viene scaricato il messaggio del rilevamento lingua non viene pi?? visualizzato
                progdialog.dismiss();
                storeDialogStatus(true,0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    /**
     *  Metodo che permette di creare graficamente una progress bar d'attesa durante il download del pacchetto.
     *  Questa progress bar rimane fin quando il pacchetto non viene scaricato
     */
    private void progressBar(){
        progdialog = new Dialog(this, android.R.style.Theme_Dialog);

        progdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progdialog.setContentView(LayoutInflater.from(this).inflate(R.layout.progress_bar, null));

        progdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progdialog.getWindow().setGravity(Gravity.CENTER);
        progdialog.getWindow().setLayout(900,900);
        progdialog.show();
        progdialog.getWindow().setGravity(Gravity.CENTER);
        progdialog.setCancelable(false);

    }

}