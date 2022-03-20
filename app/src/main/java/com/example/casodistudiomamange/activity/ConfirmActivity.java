package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import com.example.casodistudiomamange.model.FileOrderManager;

public class ConfirmActivity extends AppCompatActivity {

    ImageView quiz,share;
    View quizCostraint, shareCostraint, homeconstr,homeTv;
    private static final String FILE_NAME = "lastOrder.txt";
    NetworkChangedListener networkChangedListener = new NetworkChangedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        String usernameInserito = getIntent().getStringExtra("UsernameInserito");
        quizCostraint=findViewById(R.id.QuizConstraint);
        quiz=findViewById(R.id.QuizImg);

        quizCostraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmActivity.this, QuestionActivity.class);
                intent.putExtra("UsernameInserito",usernameInserito);
                startActivity(intent);
            }
        });

        shareCostraint =findViewById(R.id.Shareconstraint);
        share = findViewById(R.id.ShareImg);
        shareCostraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //devo chiedere in che app vuole condividere (whatsapp), scegliere persona, nella chat caricare
                //come messaggio l'ordine che ha fatto
                FileOrderManager fileOrderManager= new FileOrderManager();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, fileOrderManager.loadPlatesOrderedFromFileForMessage(FILE_NAME,ConfirmActivity.this));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        homeconstr=findViewById(R.id.HomeCostr);
        homeTv=findViewById(R.id.textHome);
        homeconstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmActivity.this, QRCodeActivity.class);
                intent.putExtra("UsernameInserito",usernameInserito);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {


    }


    @Override
    protected void onStart(){
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangedListener);
        super.onStop();
    }
}