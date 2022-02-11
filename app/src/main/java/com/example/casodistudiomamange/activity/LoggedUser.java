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

import com.example.casodistudiomamange.R;

public class LoggedUser extends AppCompatActivity {

    private static final int MAX_LENGTH = 10;
    private Button uniscitiTavolo;
    private EditText tw_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);

        uniscitiTavolo = findViewById(R.id.uniscitiGroupOrder2);
        tw_username=findViewById(R.id.username2);



        uniscitiTavolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chiama();
            }
        });
    }

    private void chiama(){
        String username_ins=tw_username.getText().toString();

        if(username_ins.length()==0 || username_ins.length()>MAX_LENGTH){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoggedUser.this);
            builder1.setTitle(R.string.username_nonvalido);
            builder1.setMessage(R.string.username_nonvalido_descr);

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
        }else {
            Intent intent = new Intent(this, QRCodeActivity.class);
            intent.putExtra("UsernameInserito", tw_username.getText().toString());
            startActivity(intent);
        }
    }
}