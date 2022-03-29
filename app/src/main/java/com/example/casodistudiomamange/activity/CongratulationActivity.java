package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import java.util.Random;

/**
 * Activity di comunicazione risultato del quiz
 */
public class CongratulationActivity extends AppCompatActivity {

    private final int lengthCodeTv = 7;
    private TextView congratulationsTv,codeTv,homeTv, textRating;
    private View congcostr;
    private RatingBar rating;
    private int max = 5;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    private View homeconstr;
    NetworkChangedListener networkChangedListener = new NetworkChangedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);

        codeTv=findViewById(R.id.CodeTv);
        codeTv.setText(getRandomString(lengthCodeTv));
        codeTv.setVisibility(codeTv.GONE);

        textRating = findViewById(R.id.textRating);
        if(score>=4){
            textRating.setText(R.string.ratingW);
        }
        else{
            textRating.setText(R.string.ratingL);
        }

        congratulationsTv=findViewById(R.id.congratulationsTv);
        homeTv=findViewById(R.id.textHome);
        rating = findViewById(R.id.ratingBar);

        homeconstr=findViewById(R.id.HomeCostr);
        homeconstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameInserito = getIntent().getStringExtra("UsernameInserito");
                Intent intent = new Intent(CongratulationActivity.this, QRCodeActivity.class);
                intent.putExtra("UsernameInserito",usernameInserito);
                startActivity(intent);
            }
        });

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                final int rating = (int) v;
                String message = null;

                if(score==max || score==max-1){
                    congratulationsTv.setText(R.string.vittoriaQuiz);
                    congcostr=findViewById(R.id.CongCostr);
                    congcostr.setVisibility(congcostr.VISIBLE);
                    codeTv.setVisibility(codeTv.VISIBLE);

                }else{
                    congcostr=findViewById(R.id.CongCostr);
                    congratulationsTv.setText(R.string.ritenta);
                    congcostr.setVisibility(congcostr.GONE);
                }

                switch(rating){

                    case 1:
                        message = "Pessimo";
                        isIndicator();
                        break;

                    case 2:
                        message = "Mediocre";
                        isIndicator();
                        break;

                    case 3:
                        message = "Sufficiente";
                        isIndicator();
                        break;

                    case 4:
                        message = "Ottimo";
                        isIndicator();
                        break;

                    case 5:
                        message = "Perfetto";
                        isIndicator();
                        break;

                }
                Toast.makeText(CongratulationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Metodo che permette di generare una stringa di lunghezza definita
     * @param sizeOfRandomString lunghezza della stringa
     * @return stringa generata 
     */
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
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

    private void isIndicator(){
        rating.setIsIndicator(true);
    }

}