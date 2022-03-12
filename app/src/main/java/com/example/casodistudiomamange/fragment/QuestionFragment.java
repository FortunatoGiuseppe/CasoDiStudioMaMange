package com.example.casodistudiomamange.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuestionFragment extends Fragment {

    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionNo;
    private TextView tvTimer;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2,rb3;
    private Button btnNext;

    int totalQuestions;
    int qCounter=0;
    int score =0;
    int DOMANDE=3;

    ColorStateList dfRbColor;
    CountDownTimer countDownTimer;
    boolean answered;

    private Question currentQuestion;

    private List<Question> questionsList;

    private List<Question> Questions= new ArrayList<>();



    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_question, container, false);
        questionsList=new ArrayList<>();

        tvQuestion=v.findViewById(R.id.textQuestion);
        tvScore=v.findViewById(R.id.textScore);
        tvQuestionNo=v.findViewById(R.id.textQuestionNo);
        tvTimer=v.findViewById(R.id.textTimer);


        radioGroup=v.findViewById(R.id.radioGroup);
        rb1=v.findViewById(R.id.rb1);
        rb2=v.findViewById(R.id.rb2);
        rb3=v.findViewById(R.id.rb3);
        btnNext=v.findViewById(R.id.btnNext);

        dfRbColor=rb1.getTextColors();

        addQuestions();
        getQuizRandom();
        totalQuestions=Questions.size();
        showNextQuestion();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answered==false){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked()){
                        checkAnswer(v);
                        countDownTimer.cancel();
                    }else{
                        Toast.makeText(getActivity(), "Selezionare un'opzione", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    showNextQuestion();
                }
            }
        });

        return v;


    }


    private void checkAnswer(View v) {

        answered=true;
        RadioButton rbSelected= v.findViewById(radioGroup.getCheckedRadioButtonId());
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
            btnNext.setText("Prossima domanda");
        }else{
            btnNext.setText("Fine");
        }


    }

    private void showNextQuestion() {
        radioGroup.clearCheck();
        rb1.setTextColor(dfRbColor);
        rb2.setTextColor(dfRbColor);
        rb3.setTextColor(dfRbColor);
        if(qCounter<totalQuestions){
            timer();
            currentQuestion=Questions.get(qCounter);
            tvQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());

            qCounter++;
            btnNext.setText("Invia");
            tvQuestionNo.setText("Domanda: "+qCounter+"/"+totalQuestions);
            answered=false;
        }else{

                Bundle bundle = new Bundle();
                bundle.putString("score", String.valueOf(score));
                bundle.putString("max", String.valueOf(Questions.size()));



                Fragment fragment= new CongratulationFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();


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

        questionsList.add(new Question("In quale nazione il salmone è maggiormente pescato?","Finlandia","Norvegia","Abruzzo",3));
        questionsList.add(new Question("Il sushi è stato inventato in?","Cina","Giappone","Thailandia",3));
        questionsList.add(new Question("Come si mangia il sushi?","Forchetta","Bacchette","Cucchiaio",3));
        questionsList.add(new Question("A cosa serve lo zenzero?","Pulire il palato","Aggiungere sapore","D'abbellimento",3));
        questionsList.add(new Question("Cosa è il sashimi?","Pesce senza riso","Pesce con riso","Riso senza pesce",3));
        questionsList.add(new Question("Da quale pesce proviene il 'fugu'?","Pesce palla","Pesce cane","Pesce spada",3));

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