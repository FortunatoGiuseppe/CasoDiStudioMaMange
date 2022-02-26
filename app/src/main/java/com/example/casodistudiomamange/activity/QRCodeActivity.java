package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.CaptureAct;
import com.example.casodistudiomamange.model.SingleOrder;
import com.example.casodistudiomamange.model.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


public class QRCodeActivity extends AppCompatActivity {

    private Button scanQrCodeBtn;
    private Button confirmBtn;
    private TextView benvenuto;
    private EditText insertQrCode;
    private Button logout;
    String usernameInserito;
    private FirebaseFirestore db;
    private Table table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        db = FirebaseFirestore.getInstance();
        scanQrCodeBtn = findViewById(R.id.scanQrCodeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
        insertQrCode = findViewById(R.id.insertQrCode);
        logout = findViewById(R.id.logout);
        benvenuto=findViewById(R.id.textView_benvenuto);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        usernameInserito = intent.getStringExtra("UsernameInserito");

        String benv = getResources().getString(R.string.benvenuto);
        benvenuto.setText(benv+" "+usernameInserito);

        scanQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQrCode();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlCode(insertQrCode.getText().toString());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void scanQrCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                db.collection("TAVOLI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<Table> tables = new ArrayList<Table>();
                            for(QueryDocumentSnapshot doc : task.getResult()){
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


    private void controlCode(String QrCode){

        db.collection("TAVOLI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Table> tables = new ArrayList<Table>();
                    for(QueryDocumentSnapshot doc : task.getResult()){
                        tables.add(doc.toObject(Table.class));
                    }
                    for(int i = 0; i < tables.size(); i++){
                        if (QrCode.equals(tables.get(i).getCodiceTavolo())){
                            Toast.makeText(QRCodeActivity.this,R.string.tavoloTrovato , Toast.LENGTH_LONG).show();
                            Intent intent= new Intent(getApplicationContext(),MaMangeNavigationActivity.class);
                            intent.putExtra("UsernameInserito",usernameInserito);
                            intent.putExtra("CodiceTavolo",insertQrCode.getText().toString());
                            startActivity(intent);
                        } else{
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(QRCodeActivity.this);
                            builder1.setMessage(R.string.tavoloNonTrovato);
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert = builder1.create();
                            alert.show();
                        }
                    }
                }
            }
        });
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SwitchLoginSignupGuestActivity.class));
        finish();
    }
}