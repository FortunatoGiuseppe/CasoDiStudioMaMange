package com.example.casodistudiomamange.activity;

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

import com.example.casodistudiomamange.model.Restaurant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedOutputStream;

public class QRCodeActivity extends AppCompatActivity {

    private Button scanQrCodeBtn;
    private Button confirmBtn;
    private EditText insertQrCode;
    private String Code1 = "MST001";
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        scanQrCodeBtn = findViewById(R.id.scanQrCodeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
        insertQrCode = findViewById(R.id.insertQrCode);
        logout = findViewById(R.id.logout);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning result");
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanQrCode();
                    }
                }).setPositiveButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void controlCode(String QrCode){
        if (QrCode.contains(Code1)){
            Toast.makeText(this, "okay", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), RestaurantActivity.class));
        } else{
            Toast.makeText(this, "no", Toast.LENGTH_LONG).show();
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SwitchLoginSignupGuestActivity.class));
        finish();
    }
}