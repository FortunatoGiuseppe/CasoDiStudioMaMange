package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.fragment.CongratulationFragment;
import com.example.casodistudiomamange.model.Question;
import com.google.protobuf.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionNo;
    private TextView tvTimer;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2,rb3;
    private Button btnNext;
    private ImageView img;

    int width=360;
    int height=360;
    Bitmap bmp;
    int totalQuestions;
    int qCounter=0;
    int score =0;
    int DOMANDE=3;

    View viewCostraint;
    ColorStateList dfRbColor;
    CountDownTimer countDownTimer;
    boolean answered;

    private Question currentQuestion;

    private List<Question> questionsList;

    private List<Question> Questions= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        questionsList=new ArrayList<>();

        tvQuestion=findViewById(R.id.textQuestion);
        tvScore=findViewById(R.id.textScore);
        tvQuestionNo=findViewById(R.id.textQuestionNo);
        tvTimer=findViewById(R.id.textTimer);


        viewCostraint = findViewById(R.id.imageCostraint);
        img = findViewById(R.id.img1);
        radioGroup=findViewById(R.id.radioGroup);
        rb1=findViewById(R.id.rb1);
        rb2=findViewById(R.id.rb2);
        rb3=findViewById(R.id.rb3);
        btnNext=findViewById(R.id.btnNext);

        dfRbColor=rb1.getTextColors();

        addQuestions();
        getQuizRandom();
        totalQuestions=5;
        showNextQuestion();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked()){
                        checkAnswer();
                        countDownTimer.cancel();
                    }else{
                        Toast.makeText(QuestionActivity.this, R.string.selezionaOpzione, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showNextQuestion();
                }
            }
        });
    }

    private void checkAnswer() {

        answered=true;
        RadioButton rbSelected= findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNo = radioGroup.indexOfChild(rbSelected) + 1;
        if(answerNo == currentQuestion.getCorrectAnsNo()){
            score++;
            tvScore.setText("Score:"+score);
        }
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        switch(currentQuestion.getCorrectAnsNo()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                break;
        }

        if(qCounter<totalQuestions){
            btnNext.setText(R.string.prossimaDomanda);
        }else{
            btnNext.setText(R.string.fine);
        }

    }

    private void showNextQuestion() {
        radioGroup.clearCheck();
        rb1.setTextColor(dfRbColor);
        rb2.setTextColor(dfRbColor);
        rb3.setTextColor(dfRbColor);


        if(qCounter<DOMANDE){
            timer();
            currentQuestion=Questions.get(qCounter);
            img.setImageResource(currentQuestion.getImage());
            if(currentQuestion.getImage()==0){
                viewCostraint.setVisibility(View.GONE);
            }
            else{
                viewCostraint.setVisibility(View.VISIBLE);
                bmp= BitmapFactory.decodeResource(getResources(),currentQuestion.getImage());//image is your image
                bmp= Bitmap.createScaledBitmap(bmp, width,height, true);
                img.setImageBitmap(bmp);
            }
            tvQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            qCounter++;
            btnNext.setText(R.string.invia);
            tvQuestionNo.setText("Question:"+" "+qCounter+"/"+totalQuestions);
            answered=false;
        }else{

            Intent intent = new Intent(QuestionActivity.this, RecognizeActivity.class);
            intent.putExtra("score", score);
            //intent.putExtra("max",Questions.size());
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

    private void addQuestions() {

        questionsList.add(new Question(R.string.question1,R.string.question1option1,R.string.question1option2,R.string.question1option3,2,0));
        questionsList.add(new Question(R.string.question2,R.string.question2option1,R.string.question2option2,R.string.question2option3,1, 0));
        questionsList.add(new Question(R.string.question3,R.string.question3option1,R.string.question3option2,R.string.question3option3,2, 0));
        questionsList.add(new Question(R.string.question4,R.string.question4option1,R.string.question4option2,R.string.question4option3,1,0));
        questionsList.add(new Question(R.string.question5,R.string.question5option1,R.string.question5option2,R.string.question5option3,1, 0));
        questionsList.add(new Question(R.string.question6,R.string.question6option1,R.string.question6option2,R.string.question6option3,1, 0));
        questionsList.add(new Question(R.string.question7,R.string.question7option1,R.string.question7option2,R.string.question7option3,3, 0));
        questionsList.add(new Question(R.string.question8,R.string.question8option1,R.string.question8option2,R.string.question8option3,1, 0));
        questionsList.add(new Question(R.string.question9,R.string.question9option1,R.string.question9option2,R.string.question9option3,1, 0));
        questionsList.add(new Question(R.string.question10,R.string.question10option1,R.string.question10option2,R.string.question10option3,2, 0));
        questionsList.add(new Question(R.string.question11,R.string.question11option1,R.string.question11option2,R.string.question11option3,1, 0));


        questionsList.add(new Question(R.string.question12,R.string.question12option1,R.string.question12option2,R.string.question12option3,1, R.drawable.alga));
        questionsList.add(new Question(R.string.question13,R.string.question13option1,R.string.question13option2,R.string.question13option3,3, R.drawable.temaki));
        questionsList.add(new Question(R.string.question13,R.string.question14option1,R.string.question14option2,R.string.question14option3,2, R.drawable.sashimi));
        questionsList.add(new Question(R.string.question13,R.string.question15option1,R.string.question15option2,R.string.question15option3,1, R.drawable.ghoan));
        questionsList.add(new Question(R.string.question16,R.string.question16option1,R.string.question16option2,R.string.question16option3,2, R.drawable.gamberi));
        questionsList.add(new Question(R.string.question13,R.string.question17option1,R.string.question17option2,R.string.question17option3,3, R.drawable.nigiri));
        questionsList.add(new Question(R.string.question13,R.string.question18option1,R.string.question18option2,R.string.question18option3,1, R.drawable.roll));

    }



    private void getQuizRandom(){

        List<Question> trueList=new ArrayList<Question>();

        trueList.addAll(questionsList);
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