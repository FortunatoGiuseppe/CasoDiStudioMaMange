package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.casodistudiomamange.R;

public class LoggedUser extends AppCompatActivity {

    private Button uniscitiTavolo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_user);

        uniscitiTavolo = findViewById(R.id.uniscitiGroupOrder2);

        uniscitiTavolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chiama();
            }
        });
    }

    private void chiama(){
        startActivity(new Intent(this,QRCodeActivity.class));
    }
}