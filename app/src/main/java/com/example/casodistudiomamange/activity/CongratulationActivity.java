package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.casodistudiomamange.R;

import java.util.Random;

public class CongratulationActivity extends AppCompatActivity {

    private TextView congratulationsTv,codeTv;
    private View congcostr;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        //Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        String score, max;

        if (extras != null) {
            score = extras.getString("score");
            max= extras.getString("max");
            // and get whatever type user account id is
        }

        congratulationsTv=findViewById(R.id.congratulationsTv);
        congcostr=findViewById(R.id.CongCostr);
        if(score.equals(max)){
            congratulationsTv.setText("Congratulazioni, hai vinto un codice sconto!");
            codeTv=findViewById(R.id.CodeTv);
            codeTv.setText(getRandomString(7));
        }else{
            congratulationsTv.setText("Ritenta la prossima volta");
        }
    }

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}