package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
                unisciAlTavolo();
            }
        });
    }

    private void unisciAlTavolo(){
        String username_ins=tw_username.getText().toString();

        if(username_ins.length()==0 || username_ins.length()>MAX_LENGTH){
            AlertDialog.Builder invalidUsernameAlert = new AlertDialog.Builder(LoggedUser.this);
            invalidUsernameAlert.setTitle(R.string.username_nonvalido);
            invalidUsernameAlert.setMessage(R.string.username_nonvalido_descr);

            invalidUsernameAlert.setCancelable(true);

            invalidUsernameAlert.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = invalidUsernameAlert.create();
            alert.show();
        }else {
            Intent intent = new Intent(this, QRCodeActivity.class);
            intent.putExtra("UsernameInserito", username_ins + " (Guest)");
            startActivity(intent);
        }
    }
}