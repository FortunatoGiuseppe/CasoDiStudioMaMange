package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.casodistudiomamange.model.GroupOrder;
import com.example.casodistudiomamange.model.SingleOrder;
import com.example.casodistudiomamange.model.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;




public class QRCodeActivity extends AppCompatActivity {

    private Button scanQrCodeBtn;
    private Button confirmBtn;
    private TextView benvenuto;
    private EditText insertQrCode;
    private String Code1 = "MST001";
    private Button logout;
    String usernameInserito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        scanQrCodeBtn = findViewById(R.id.scanQrCodeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
        insertQrCode = findViewById(R.id.insertQrCode);
        logout = findViewById(R.id.logout);
        benvenuto=findViewById(R.id.textView_benvenuto);


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
                if(result.getContents().equals(Code1)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(result.getContents());
                    builder.setTitle(getResources().getString(R.string.scannerizzando));
                    builder.setPositiveButton(getResources().getString(R.string.termina), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(QRCodeActivity.this, MaMangeNavigationActivity.class));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            } else {
                Toast.makeText(this,getResources().getString(R.string.norisultati), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void controlCode(String QrCode){
        if (QrCode.contains(Code1)){
            Toast.makeText(this,R.string.tavoloTrovato , Toast.LENGTH_LONG).show();


            Intent intent= new Intent(getApplicationContext(),MaMangeNavigationActivity.class);
            intent.putExtra("UsernameInserito",usernameInserito);
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

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SwitchLoginSignupGuestActivity.class));
        finish();
    }
}