package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import com.example.casodistudiomamange.model.FileOrderManager;

/**
 * Activity che compare una volta che l'utente ha confermato il proprio ordine singolo.
 * Contiene i tasti che richiamano alle funzionalità di invio ordine tramite messaggio o per fare il quiz
 */
public class ConfirmActivity extends AppCompatActivity {

    ImageView quiz,share;
    View quizCostraint, shareCostraint;
    private static final String FILE_NAME = "lastOrder.txt";
    NetworkChangedListener networkChangedListener = new NetworkChangedListener();
    int countClicksOnBackButton=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        String usernameInserito = getIntent().getStringExtra("UsernameInserito");
        quizCostraint=findViewById(R.id.QuizConstraint);
        quiz=findViewById(R.id.QuizImg);
        //Mostro l'icona del quiz
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

                // Mostro la lista di app attraverso le quali è possibile condividere
                FileOrderManager fileOrderManager= new FileOrderManager();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                // con loadPlatesOrderedFromFileForMessage scrivo il corpo del messaggio con la lista dei piatti ordinati
                sendIntent.putExtra(Intent.EXTRA_TEXT, fileOrderManager.loadPlatesOrderedFromFileForMessage(FILE_NAME,ConfirmActivity.this));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        countClicksOnBackButton++;
        if(countClicksOnBackButton > 1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this
            );
            builder.setTitle(getText(R.string.uscire));
            builder.setPositiveButton(getText(R.string.si), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity(); //elimina tutto lo stack delle activity attive
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    countClicksOnBackButton = 0;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }



    @Override
    protected void onStart(){
        //verifica della presenza della connessione internet
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        //verifica della presenza della connessione internet
        unregisterReceiver(networkChangedListener);
        super.onStop();
    }
}