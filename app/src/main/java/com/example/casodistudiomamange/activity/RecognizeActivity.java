package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.Question;
import com.example.casodistudiomamange.model.RecognizeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RecognizeActivity extends AppCompatActivity {

    private static final int DOMANDE = 2;
    private TextView tvTimer, tvScore, NoQuestion, Question;
    private EditText op1;
    private List<RecognizeModel> questionList;
    int totalQuestion = 5;
    int qCounter = 3;
    int score;
    int i=0;
    int width=380;
    int height=380;
    View viewCostraint;
    ColorStateList dfRbColor;
    CountDownTimer countDownTimer;
    Bitmap bmp;
    boolean answered;
    private RecognizeModel currentQuestion;
    private Button btnNext;
    private ImageView img1;
    private List<RecognizeModel> Questions= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        Question = findViewById(R.id.question);
        viewCostraint = findViewById(R.id.imageCostraint);
        img1 = findViewById(R.id.img1);
        op1 = findViewById(R.id.insertValue2);
        tvTimer = findViewById(R.id.textTimer);
        tvScore = findViewById(R.id.textScore);
        NoQuestion = findViewById(R.id.textQuestionNo);
        questionList = new ArrayList<>();
        dfRbColor = op1.getTextColors();
        btnNext = findViewById(R.id.btnNext);
        Question = findViewById(R.id.question);

        op1.setTextColor(Color.BLACK);
        dfRbColor = op1.getTextColors();

        Intent intent = getIntent();
        score = intent.getIntExtra("score",0);

        addQuestion();
        getQuizRandom();
        showNextQuestion();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answered == false){
                    if(op1.getText().length()>0 ){
                        checkAnswer();
                        countDownTimer.cancel();
                    } else{
                        Toast.makeText(RecognizeActivity.this, "Please insert value", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;
        String stringa =op1.getText().toString().toLowerCase(Locale.ROOT);
        if(stringa.equals(currentQuestion.getCorrectEN())|| stringa.equals(currentQuestion.getCorrectIT())){
            op1.setTextColor(Color.GREEN);
            score++;
            tvScore.setText("Score: " +score);
        }
        if(!(stringa.equals(currentQuestion.getCorrectEN())|| stringa.equals(currentQuestion.getCorrectIT()))){
            op1.setTextColor(Color.RED);
        }
        if(qCounter<6){
            btnNext.setText(R.string.prossimaDomanda);
        }else{
            btnNext.setText(R.string.fine);
        }
    }

    private void showNextQuestion() {

        op1.setText("");
        op1.setTextColor(dfRbColor);
        Question.setText(R.string.questionR);
        tvScore.setText("Score: " +score);

        if(qCounter < totalQuestion){
            timer();

            currentQuestion = Questions.get(i);

            img1.setImageResource(currentQuestion.getImg1());
            viewCostraint.setVisibility(View.VISIBLE);
            bmp= BitmapFactory.decodeResource(getResources(),currentQuestion.getImg1());//image is your image
            bmp= Bitmap.createScaledBitmap(bmp, width,height, true);
            img1.setImageBitmap(bmp);

            i++;
            qCounter++;
            NoQuestion.setText("Question: "+ qCounter+" / "+ totalQuestion);
            answered = false;
            btnNext.setText("Submit");
        }
        else{
            String usernameInserito = getIntent().getStringExtra("UsernameInserito");
            Intent intent = new Intent(RecognizeActivity.this, CongratulationActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("UsernameInserito",usernameInserito);
            startActivity(intent);
        }
    }

    private void timer() {
        countDownTimer=new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long l) {
                tvTimer.setText("00:"+ l/1000);
            }

            @Override
            public void onFinish() {
                showNextQuestion();
            }
        }.start();
    }

    private void addQuestion() {
        questionList.add(new RecognizeModel("tonno","tuna", R.drawable.tonno));
        questionList.add(new RecognizeModel("ravioli","ravioli",R.drawable.ravioli));
        questionList.add(new RecognizeModel("salmone","salmon",R.drawable.salmonee));
        questionList.add(new RecognizeModel("ramen","ramen",R.drawable.ramen));
        questionList.add(new RecognizeModel("edamame","edamame", R.drawable.edamame));
        questionList.add(new RecognizeModel("involtini di primavera","spring rolls",R.drawable.involtini));
        questionList.add(new RecognizeModel("poke","poke",R.drawable.poke));
    }

    private void getQuizRandom(){

        List<RecognizeModel> trueList = new ArrayList<>();

        trueList.addAll(questionList);
        final int min = 0;

        for(int i=0;i<DOMANDE;i++){
            final int max = trueList.size()-1;
            final int random = new Random().nextInt((max - min) + 1) + min;
            Questions.add(trueList.get(random));
            trueList.remove(random);

        }
    }

    @Override
    public void onBackPressed() {


    }
}