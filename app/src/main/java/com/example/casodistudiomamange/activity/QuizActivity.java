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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.RecognizeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private static final int DOMANDE = 2;
    private TextView tvScore, NoQuestion, Question;
    private EditText op1;
    private List<RecognizeModel> questionList;
    int totalQuestion = 5;
    int qCounter = 3;
    int score;
    int wrongAnswer;
    int i=0;
    int width=350;
    int height=350;
    View viewCostraint;
    ColorStateList dfRbColor;

    private int currentProgress =60;
    private ProgressBar progressBar;

    ProgressBar mProgressBar, mProgressBar1;

    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;

    Bitmap bmp;
    boolean answered;
    private RecognizeModel currentQuestion;
    private Button btnNext;
    private ImageView img1,score1,score2,score3,score4,score5;
    private List<RecognizeModel> Questions= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);

        Question = findViewById(R.id.question);
        viewCostraint = findViewById(R.id.imageCostraint);
        img1 = findViewById(R.id.img1);
        op1 = findViewById(R.id.insertValue2);

        score1 =  findViewById(R.id.imageScore1);
        score2 = findViewById(R.id.imageScore2);
        score3 = findViewById(R.id.imageScore3);
        score4 = findViewById(R.id.imageScore4);
        score5 = findViewById(R.id.imageScore5);

        progressBar = findViewById(R.id.progress);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);

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
        wrongAnswer= intent.getIntExtra("wrongAnswer",wrongAnswer);


        if(score==2){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.GONE);
            score4.setVisibility(score4.GONE);
            score5.setVisibility(score5.GONE);
        }
        else if(score==3){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.VISIBLE);
            score4.setVisibility(score4.GONE);
            score5.setVisibility(score5.GONE);
        }

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
                        currentProgress += 20;
                        progressBar.setProgress(currentProgress);
                        progressBar.setMax(100);
                    } else{
                        Toast.makeText(QuizActivity.this, R.string.inserireValore, Toast.LENGTH_SHORT).show();
                    }
                } else{
                    showNextQuestion();
                    op1.setEnabled(true);
                }
            }
        });
    }

    private void checkAnswer() {
        answered = true;
        String stringa =op1.getText().toString().toLowerCase(Locale.ROOT);
        if(stringa.equals(currentQuestion.getCorrectEN())|| stringa.equals(currentQuestion.getCorrectIT())){
            op1.setTextColor(Color.GREEN);
            op1.setEnabled(false);
            score++;
            tvScore.setText("Score: " +score);

            if(score==3){
                score3.setVisibility(score3.VISIBLE);
            }
            if(score==4){
                score4.setVisibility(score4.VISIBLE);
            }
            if(score==5){
                score5.setVisibility(score5.VISIBLE);
            }
        }else{
            wrongAnswer++;
        }
        if(!(stringa.equals(currentQuestion.getCorrectEN())|| stringa.equals(currentQuestion.getCorrectIT()))){
            op1.setTextColor(Color.RED);
            op1.setEnabled(false);
        }
        if(qCounter<5){
            btnNext.setText(R.string.prossimaDomanda);
        }else{
            btnNext.setText(R.string.fine);
        }
    }

    private void showNextQuestion() {

        op1.setText("");
        op1.setTextColor(dfRbColor);
        Question.setText(R.string.questionR);

        if(wrongAnswer>=2){
            String usernameInserito = getIntent().getStringExtra("UsernameInserito");
            Intent intent = new Intent(QuizActivity.this, CongratulationActivity.class);
            intent.putExtra("UsernameInserito",usernameInserito);
            startActivity(intent);
        }else{
            if(qCounter < totalQuestion){

                setTimer();
                mProgressBar.setVisibility(View.INVISIBLE);

                timer();
                mProgressBar1.setVisibility(View.VISIBLE);

                currentQuestion = Questions.get(i);

                img1.setImageResource(currentQuestion.getImg1());
                viewCostraint.setVisibility(View.VISIBLE);
                bmp= BitmapFactory.decodeResource(getResources(),currentQuestion.getImg1());//image is your image
                bmp= Bitmap.createScaledBitmap(bmp, width,height, true);
                img1.setImageBitmap(bmp);

                i++;
                qCounter++;
                NoQuestion.setText(R.string.question);
                NoQuestion.append(qCounter+"/"+totalQuestion);
                answered = false;
                btnNext.setText(R.string.Invia);
            }
            else{
                String usernameInserito = getIntent().getStringExtra("UsernameInserito");
                Intent intent = new Intent(QuizActivity.this, CongratulationActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("UsernameInserito",usernameInserito);
                startActivity(intent);
            }

        }


    }

    private void timer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 1) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                mProgressBar1.setProgress((int) (leftTimeInMilliseconds));

                textViewShowTime.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));
            }
            @Override
            public void onFinish() {

                if(!(op1.getText().length()>0)){
                    wrongAnswer++;
                }
                showNextQuestion();
            }
        }.start();
    }

    private void setTimer(){
        int time = 21;
        totalTimeCountInMilliseconds =  time * 1000;
        mProgressBar1.setMax( time * 1000);
    }

    private void addQuestion() {
        questionList.add(new RecognizeModel("tonno","tuna", R.drawable.tonno));
        questionList.add(new RecognizeModel("ravioli","ravioli",R.drawable.ravioli));
        questionList.add(new RecognizeModel("salmone","salmon",R.drawable.salmonee));
        questionList.add(new RecognizeModel("ramen","ramen",R.drawable.ramen));
        questionList.add(new RecognizeModel("edamame","edamame", R.drawable.edamame));
        questionList.add(new RecognizeModel("involtini primavera","spring rolls",R.drawable.involtini));
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