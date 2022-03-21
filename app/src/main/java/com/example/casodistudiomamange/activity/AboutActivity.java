package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.casodistudiomamange.R;

public class AboutActivity extends AppCompatActivity {

    TextView info, developers;
    Button send, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().hide();

        info = findViewById(R.id.Info);
        info.setText(R.string.info);

        developers = findViewById(R.id.textInfoG);
        developers.setText(R.string.developers);

        send = findViewById(R.id.sendEmail);
        send.setText(R.string.sendEmail);

        back = findViewById(R.id.back);
        back.setText(R.string.back);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AboutActivity.this, SupportActivity.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}