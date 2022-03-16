package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.example.casodistudiomamange.model.RecognizeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecognizeActivity extends AppCompatActivity {

    private TextView tvTimer, tvScore, NoQuestion, Question;
    private EditText op1;
    private List<RecognizeModel> questionList;
    int totalQuestion = 5;
    int qCounter = 3;
    int score;
    int i=0;
    View viewCostraint;
    ColorStateList dfRbColor;
    CountDownTimer countDownTimer;
    boolean answered;
    private RecognizeModel currentQuestion;
    private Button btnNext;
    private ImageView img1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

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
        if(op1.getText().toString().toLowerCase(Locale.ROOT).equals(currentQuestion.getCorrectAns())){
            op1.setTextColor(Color.GREEN);
            score++;
            tvScore.setText("Score: " +score);
        }
        if(!op1.getText().toString().toLowerCase(Locale.ROOT).equals(currentQuestion.getCorrectAns())){
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
        tvScore.setText("Score: " +score);

        if(qCounter < totalQuestion){
            timer();

            currentQuestion = questionList.get(i);
            img1.setImageResource(currentQuestion.getImg1());
            if(currentQuestion.getImg1()==0){
                viewCostraint.setVisibility(View.GONE);
            }
            else{
                viewCostraint.setVisibility(View.VISIBLE);
            }
            i++;
            qCounter++;
            NoQuestion.setText("Question: "+ qCounter+" / "+ totalQuestion);
            answered = false;
            btnNext.setText("Submit");
        }
        else{
            Intent intent = new Intent(RecognizeActivity.this, CongratulationActivity.class);
            intent.putExtra("score", score);
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
        questionList.add(new RecognizeModel("tonno", R.drawable.dice));
        questionList.add(new RecognizeModel("spigola",R.drawable.dice));
        questionList.add(new RecognizeModel("salmone",R.drawable.dice));
    }
}