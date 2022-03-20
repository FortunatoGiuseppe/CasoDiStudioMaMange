package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.CaptureAct;
import com.example.casodistudiomamange.model.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Objects;


public class QRCodeActivity extends AppCompatActivity {

    private EditText insertQrCode;
    String usernameInserito;
    private FirebaseFirestore db;
    private static final int REQUEST_ENABLE_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        db = FirebaseFirestore.getInstance();
        Button scanQrCodeBtn = findViewById(R.id.scanQrCodeBtn);
        Button confirmBtn = findViewById(R.id.confirmBtn);
        insertQrCode = findViewById(R.id.insertQrCode);
        TextView benvenuto = findViewById(R.id.textView_benvenuto);

        checkCameraPermission();//controllo se i permessi della fotocamera sono stati dati

        getSupportActionBar().hide();

        Intent intent = getIntent();
        usernameInserito = intent.getStringExtra("UsernameInserito");
        if (usernameInserito==null){
            usernameInserito = getIntent().getStringExtra("UsernameInserito");
        }

        String benv = getResources().getString(R.string.benvenuto);
        benvenuto.setText(benv+" "+usernameInserito);

        scanQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
                scanQrCode();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                existsTableWithCode(insertQrCode.getText().toString());
            }
        });

    }

    /**
     * Medoto che inizializza la scansione QR del tavolo
     *
     */
    private void scanQrCode(){

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt(getText(R.string.scannerizzaQr).toString());
        integrator.initiateScan();
    }

    /**
     * Metodo che controlla dell'esistenza del codice QR
     * scannerizzato dalla fotocamera
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                db.collection("TAVOLI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Table> tables = new ArrayList<>();
                            for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                                tables.add(doc.toObject(Table.class));
                            }
                            for(int i = 0; i < tables.size(); i++){
                                if(result.getContents().equals(tables.get(i).getCodiceTavolo())){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                                    builder.setMessage(result.getContents());
                                    builder.setTitle(getResources().getString(R.string.scannerizzando));
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    Intent intent= new Intent(getApplicationContext(),MaMangeNavigationActivity.class);
                                    intent.putExtra("UsernameInserito",usernameInserito);
                                    intent.putExtra("CodiceTavolo",tables.get(i).getCodiceTavolo());
                                    startActivity(intent);

                                }
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this,getResources().getString(R.string.norisultati), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Metodo che controlla l'esistenza del tavolo associato
     * al codice scannerizzato tramite Fotocamera o inserito manualmente
     *
     * @param QrCode
     */
    private void existsTableWithCode(String QrCode){

        //effettuo una ricerca nella tabella TAVOLI presente su firebase
        db.collection("TAVOLI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isFound=false;  //indica se il tavolo è stato trovato, necessario per far comparire alert tavolo non trovato

                if(task.isSuccessful()){
                    ArrayList<Table> tables = new ArrayList<>();
                    for(QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())){
                        tables.add(doc.toObject(Table.class));
                    }
                    for(int i = 0; i < tables.size(); i++){
                        if (QrCode.equals(tables.get(i).getCodiceTavolo())){
                            //se tavolo viene trovato fai intent caricando prossima activity
                            Toast.makeText(QRCodeActivity.this,R.string.tavoloTrovato , Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(getApplicationContext(),MaMangeNavigationActivity.class);
                            intent.putExtra("UsernameInserito",usernameInserito);
                            intent.putExtra("CodiceTavolo",insertQrCode.getText().toString());
                            startActivity(intent);
                            isFound=true;
                        }
                    }

                    if(!isFound){//se tavolo non è stato trovato, allora avvisa utente
                        AlertDialog.Builder tableNotFoundAlert = new AlertDialog.Builder(QRCodeActivity.this);
                        tableNotFoundAlert.setMessage(R.string.tavoloNonTrovato);
                        tableNotFoundAlert.setCancelable(true);

                        tableNotFoundAlert.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = tableNotFoundAlert.create();
                        alert.show();
                    }
                }
            }
        });
    }

    /**
     * Metodo con il quale si controllano se i permessi della "CAMERA"
     * e altri permessi associati siano stati dati da parte dell'utente.
     *
     * Questi permessi servono all'app per far funzionare
     * la Fotocamera e di conseguenza scasionare il Qrcode.
     */
    public void checkCameraPermission(){

        int permissionCheck = this.checkSelfPermission("android.Manifest.permission.CAMERA");

        //se il permesso è stato dato allora continua esecuzione
        if(permissionCheck == 0){

            //altrimenti mostra il razionale, ovvero il perchè è importante dare i seguenti permessi
        } else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.importanzaDeiPermessi);
            builder.setMessage(getText(R.string.messaggioPermessi)+"\n"+getText(R.string.richiestaPermessi));

            builder.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //l'utente è sicuro di non voler chiedere più i permessi
                        }
                    });

            builder.setNegativeButton(
                    "Annulla",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //l'utente vuole fare richiesta di accettazione dei permessi
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA},REQUEST_ENABLE_PERMISSION);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},REQUEST_ENABLE_PERMISSION);
        }
    }

    /**
     * Metodo che viene invocato per ogni chiamata su requestPermissions
     *
     * E' possibile che l'interazione della richiesta di autorizzazione
     * dei permessi con l'utente venga interrotta
     * Perciò è dovere controllare la sua risposta in modo da far
     * riprendere la corretta esecuzione del'app.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_PERMISSION:
                //Se la richiesta viene cancellata il risultato dell'array sarà vuoto
                //In tal caso è doveroso informare l'utente che non potrà più accedere
                //a questa funzionalità a meno che non l'attivi manualmente nel
                //gestore delle app.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this
                    );
                    builder.setTitle(getText(R.string.importanzaDeiPermessi));
                    builder.setMessage(getText(R.string.messaggioPermessi));
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
        }

    }
}