package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.casodistudiomamange.R;
import java.util.Random;

public class CongratulationActivity extends AppCompatActivity {

    private TextView congratulationsTv,codeTv,homeTv;
    private View congcostr;
    private int max = 5;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
    private View homeconstr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        Intent intent = getIntent();
        int score = intent.getIntExtra("score",0);
        //int max = intent.getIntExtra("max",0);

        congratulationsTv=findViewById(R.id.congratulationsTv);
        homeTv=findViewById(R.id.textHome);
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
        if(score==max){
            congratulationsTv.setText(R.string.congratulazioni);
            congcostr=findViewById(R.id.CongCostr);
            congcostr.setVisibility(congcostr.VISIBLE);
            codeTv=findViewById(R.id.CodeTv);
            codeTv.setText(getRandomString(7));

        }else{
            congcostr=findViewById(R.id.CongCostr);
            congratulationsTv.setText(R.string.ritenta);
            congcostr.setVisibility(congcostr.GONE);
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

    @Override
    public void onBackPressed() {


    }

}