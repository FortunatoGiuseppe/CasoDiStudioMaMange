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

        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);
        int max = intent.getIntExtra("max",0);

        congratulationsTv=findViewById(R.id.congratulationsTv);
        congcostr=findViewById(R.id.CongCostr);
        if(score==max){
            congratulationsTv.setText("Congratulazioni, hai vinto un codice sconto!");
            congcostr.setSystemUiVisibility(congcostr.VISIBLE);
            codeTv=findViewById(R.id.CodeTv);
            codeTv.setText(getRandomString(7));

        }else{
            congratulationsTv.setText("Ritenta la prossima volta");
            congcostr.setSystemUiVisibility(congcostr.GONE);
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