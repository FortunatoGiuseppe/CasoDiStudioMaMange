package com.example.casodistudiomamange.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
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

        questionsList.add(new Question("In quale nazione il salmone è maggiormente pescato?","Finlandia","Norvegia","Abruzzo",3,0));
        questionsList.add(new Question("Il sushi è stato inventato in?","Cina","Giappone","Thailandia",3, 0));
        questionsList.add(new Question("Come si mangia il sushi?","Forchetta","Bacchette","Cucchiaio",3, 0));
        questionsList.add(new Question("A cosa serve lo zenzero?","Pulire il palato","Aggiungere sapore","D'abbellimento",3,R.drawable.dice));
        questionsList.add(new Question("Cosa è il sashimi?","Pesce senza riso","Pesce con riso","Riso senza pesce",3, R.drawable.dice));
        questionsList.add(new Question("Da quale pesce proviene il 'fugu'?","Pesce palla","Pesce cane","Pesce spada",3, R.drawable.dice));

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
}