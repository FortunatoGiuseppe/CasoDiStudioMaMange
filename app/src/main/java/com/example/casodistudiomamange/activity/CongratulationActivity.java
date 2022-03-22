package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;

import java.util.Random;

public class CongratulationActivity extends AppCompatActivity {

    private TextView congratulationsTv,codeTv,homeTv;
    private View congcostr, whatsappConstr;
    private ImageView image;
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
        //int max = intent.getIntExtra("max",0);

        congratulationsTv=findViewById(R.id.congratulationsTv);
        homeTv=findViewById(R.id.textHome);
        image = findViewById(R.id.imageWhatsapp);
        image.setImageResource(R.drawable.whatsapp);

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

        whatsappConstr= findViewById(R.id.ConstrWhatsapp);
        whatsappConstr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                whatsappIntent.setType("text/plain");
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Ciao! Guarda il mio punteggio al quiz dell'app MaMangè: "+ score +"/5");
                try {
                    startActivity(whatsappIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CongratulationActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(score==max){
            congratulationsTv.setText(R.string.vittoriaQuiz);
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