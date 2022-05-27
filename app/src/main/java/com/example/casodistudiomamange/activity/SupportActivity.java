package com.example.casodistudiomamange.activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.casodistudiomamange.R;

/*
 * Activity che permette all'utente di poter mandare un email
 * agli sviluppatori per un supporto riguardante l'app
 */
public class SupportActivity extends AppCompatActivity {
    EditText etSubject, etMessage;
    TextView etTo;
    View send;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        etTo = findViewById(R.id.et_to);
        etSubject = findViewById(R.id.et_subject);
        etMessage = findViewById(R.id.et_message);
        send = findViewById(R.id.constraintSend);
        imageView = findViewById(R.id.imageView10);


        imageView.setImageAlpha(128);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Viene inserito nell'array ,che servirà
                // nella creazione dell'email, il destinario
                String recipientList = etTo.getText().toString();
                String[] recipients = recipientList.split(",");

                //Viene salvato l'oggetto e il
                // il contenuto dell'email
                String subject = etSubject.getText().toString();
                String message = etMessage.getText().toString();

                //Viene creata l'email con
                //il destinatario, il soggetto,
                // e il messaggio di richiesta
                //da parte dell'utente
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });
    }
}
