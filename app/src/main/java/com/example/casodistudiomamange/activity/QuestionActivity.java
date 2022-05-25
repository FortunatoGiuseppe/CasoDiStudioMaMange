package com.example.casodistudiomamange.activity;

/**
 * activity in cui viene gestita la prima parte del quiz
 */

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.Question;
import com.example.casodistudiomamange.model.RecognizeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private TextView tvQuestionNo;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2,rb3;
    private Button btnNext;
    private ImageView img,score1,score2,score3;

    private int currentProgress =0;
    private ProgressBar progressBar;

    int width=330;
    int height=330;
    Bitmap bmp;
    private int totalQuestions = 5;
    int qCounter=0;
    int score =0, Ftime;
    int wrongAnswer=0;
    int DOMANDE=3;

    ProgressBar mProgressBar, mProgressBar1;

    private TextView textViewShowTime;
    private CountDownTimer countDownTimer;
    private static final long START_TIME = 21000;
    private long timeLeft = START_TIME;
    //private long totalTimeCountInMilliseconds;

    View viewCostraint;
    ColorStateList dfRbColor;
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
        tvQuestionNo=findViewById(R.id.textQuestionNo);

        progressBar = findViewById(R.id.progress);
        progressBar.setProgress(currentProgress);

        textViewShowTime = (TextView)
                findViewById(R.id.textView_timerview_time);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar1.setVisibility(View.VISIBLE);

        viewCostraint = findViewById(R.id.imageCostraint);
        img = findViewById(R.id.img1);
        score1 = findViewById(R.id.imageScore1);
        score2 = findViewById(R.id.imageScore2);
        score3 = findViewById(R.id.imageScore3);

        radioGroup=findViewById(R.id.radioGroup);
        rb1=findViewById(R.id.rb1);
        rb2=findViewById(R.id.rb2);
        rb3=findViewById(R.id.rb3);
        btnNext=findViewById(R.id.btnNext);

        dfRbColor=rb1.getTextColors();

        if(score==1){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.GONE);
            score3.setVisibility(score3.GONE);
        }
        if(score==2){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.GONE);
        }
        else if(score==3){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.VISIBLE);
        }

        if((savedInstanceState==null) || (savedInstanceState.size() == 0)){
            addQuestions();
            getQuizRandom();
            showNextQuestion();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answered){
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked()){
                        //controllo delle risposte e arresto del timer nel momento in cui l'uutente clicca una delle opzioni di risposta
                        checkAnswer();
                        Ftime = (int) timeLeft;
                        countDownTimer.cancel();
                        currentProgress += 20;
                        progressBar.setProgress(currentProgress);
                        progressBar.setMax(100);
                        timeLeft = START_TIME;
                    }else{
                        Toast.makeText(QuestionActivity.this, R.string.selezionaOpzione, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(savedInstanceState!=null){
                        savedInstanceState.clear();
                        addQuestions();
                        getQuizRandom();
                    }
                    showNextQuestion();
                }
            }
        });
    }

    /**
     * metodo per verificare la correttezza di una risposta
     */
    private void checkAnswer() {

        answered=true;
        RadioButton rbSelected= findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNo = radioGroup.indexOfChild(rbSelected) + 1;
        //verifica la correttezza della risposta
        if(answerNo == currentQuestion.getCorrectAnsNo()){
            score++;
            if(score==1){
                score1.setVisibility(score1.VISIBLE);
            }
            if(score==2){
                score2.setVisibility(score2.VISIBLE);
            }
            if(score==3){
                score3.setVisibility(score3.VISIBLE);
            }
        }else{
            wrongAnswer++;
        }
        //se la risposta è sbagliata cambia colore in rosso, altrimenti in verde
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
        //Verifica se si è arrivati all'ultima domanda
        if(qCounter<totalQuestions){
            btnNext.setText(R.string.prossimaDomanda);
        }else{
            btnNext.setText(R.string.fine);
        }

    }

    /**
     * metodo per mostrare la domanda seguente
     */
    private void showNextQuestion() {
        radioGroup.clearCheck();
        rb1.setTextColor(dfRbColor);
        rb2.setTextColor(dfRbColor);
        rb3.setTextColor(dfRbColor);
        //reimposta le scritte in nero

        //controlla se l'utente ha sbagliato più di 2 domande
        if(wrongAnswer>=2){
            Intent intent = new Intent(QuestionActivity.this, CongratulationActivity.class);
            startActivity(intent);
        }else{
            if(qCounter<DOMANDE){
                //settaggio della prossima domanda
                setTimer();

                startTimer();

                updateCountDownText();

                currentQuestion=Questions.get(qCounter);
                img.setImageResource(currentQuestion.getImage());
                viewCostraint.setVisibility(View.VISIBLE);
                bmp= BitmapFactory.decodeResource(getResources(),currentQuestion.getImage());//image is your image
                bmp= Bitmap.createScaledBitmap(bmp, width,height, true);
                img.setImageBitmap(bmp);

                tvQuestion.setText(currentQuestion.getQuestion());
                rb1.setText(currentQuestion.getOption1());
                rb2.setText(currentQuestion.getOption2());
                rb3.setText(currentQuestion.getOption3());

                qCounter++;
                btnNext.setText(R.string.invia);
                tvQuestionNo.setText(R.string.question);
                tvQuestionNo.append(qCounter+"/"+totalQuestions);
                answered=false;
            }else{
                String usernameInserito = getIntent().getStringExtra("UsernameInserito");

                Intent intent = new Intent(QuestionActivity.this, QuizActivity.class);
                intent.putExtra("score", score);
                //intent.putExtra("UsernameInserito",usernameInserito);
                intent.putExtra("wrongAnswer",wrongAnswer);
                startActivity(intent);
            }
        }


    }

    /*private void timer() {
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
                //se nessuna opzione è stata premuta allo scadere del tempo allora la risposta è sbagliata
                if(!rb1.isChecked() && !rb2.isChecked() && !rb3.isChecked()){
                    wrongAnswer++;
                }
                showNextQuestion();
            }
        }.start();
    }*/


    /**
     * metodo per la creazione del timer
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeft, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                mProgressBar1.setProgress((int) (timeLeft));
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //se nessuna opzione è stata premuta allo scadere del tempo allora la risposta è sbagliata
                if(!rb1.isChecked() && !rb2.isChecked() && !rb3.isChecked()){
                    wrongAnswer++;
                }
                else{
                    checkAnswer();
                }
                timeLeft = 21000;
                currentProgress += 20;
                progressBar.setProgress(currentProgress);
                progressBar.setMax(100);
                showNextQuestion();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewShowTime.setText(timeLeftFormatted);
    }

    private void updateFinishedTimerText(){
        int minutes = (int) (Ftime / 1000) / 60;
        int seconds = (int) (Ftime / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewShowTime.setText(timeLeftFormatted);
    }


    /**
     * metodo per settare il tempo ad ogni domanda
     */
    private void setTimer(){
        int time = 21;
        int totalTimeCount =  time * 1000;
        mProgressBar1.setMax( totalTimeCount);
    }


    /**
     * metodo per inizializzare le domande che appariranno nel quiz
     */
    private void addQuestions() {

        questionsList.add(new Question(R.string.question1,R.string.question1option1,R.string.question1option2,R.string.question1option3,2,R.drawable.bandiere));
        questionsList.add(new Question(R.string.question2,R.string.question2option1,R.string.question2option2,R.string.question2option3,1, R.drawable.bandiere));
        questionsList.add(new Question(R.string.question3,R.string.question3option1,R.string.question3option2,R.string.question3option3,2, R.drawable.bacchette));
        questionsList.add(new Question(R.string.question4,R.string.question4option1,R.string.question4option2,R.string.question4option3,1,R.drawable.zenzero));
        questionsList.add(new Question(R.string.question5,R.string.question5option1,R.string.question5option2,R.string.question5option3,1, R.drawable.sashimi));
        questionsList.add(new Question(R.string.question6,R.string.question6option1,R.string.question6option2,R.string.question6option3,1, R.drawable.sushi));
        questionsList.add(new Question(R.string.question7,R.string.question7option1,R.string.question7option2,R.string.question7option3,3, R.drawable.sushi));
        questionsList.add(new Question(R.string.question8,R.string.question8option1,R.string.question8option2,R.string.question8option3,1, R.drawable.sake));
        questionsList.add(new Question(R.string.question9,R.string.question9option1,R.string.question9option2,R.string.question9option3,1, R.drawable.bacchette));
        questionsList.add(new Question(R.string.question10,R.string.question10option1,R.string.question10option2,R.string.question10option3,2, R.drawable.preparazione));
        questionsList.add(new Question(R.string.question11,R.string.question11option1,R.string.question11option2,R.string.question11option3,1, R.drawable.sushi));
        questionsList.add(new Question(R.string.question12,R.string.question12option1,R.string.question12option2,R.string.question12option3,1, R.drawable.alga));
        questionsList.add(new Question(R.string.question13,R.string.question13option1,R.string.question13option2,R.string.question13option3,3, R.drawable.temaki));
        questionsList.add(new Question(R.string.question13,R.string.question14option1,R.string.question14option2,R.string.question14option3,2, R.drawable.sashimi));
        questionsList.add(new Question(R.string.question13,R.string.question15option1,R.string.question15option2,R.string.question15option3,1, R.drawable.ghoan));
        questionsList.add(new Question(R.string.question16,R.string.question16option1,R.string.question16option2,R.string.question16option3,2, R.drawable.gamberi));
        questionsList.add(new Question(R.string.question13,R.string.question17option1,R.string.question17option2,R.string.question17option3,3, R.drawable.nigiri));
        questionsList.add(new Question(R.string.question13,R.string.question18option1,R.string.question18option2,R.string.question18option3,1, R.drawable.roll));

    }


    /**
     * metodo per effettuare la generazione di domande del quiz in modo randomico
     * e senza ripetizioni
     */
    private void getQuizRandom(){

        List<Question> trueList=new ArrayList<Question>();

        trueList.addAll(questionsList);
        //copio tutta la lista delle domande
        final int min = 0;

        for(int i=0;i<DOMANDE;i++){
            final int max = trueList.size()-1;
            final int random = new Random().nextInt((max - min) + 1) + min;
            Questions.add(trueList.get(random));
            trueList.remove(random);
            //vado a togliere randomicamente 5 domande dalla lista copiata così che non possano essere mostrate domande duplicate
            //ad ogni iterata diminuisco la dimensione della lista contenente le domande per la generazione randomica dell'indice
        }
    }

    @Override
    public void onBackPressed() {
        //disabilitazione tasto "Indietro"
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putBoolean("Answered",answered);
        savedInstanceState.putInt("score",score);
        savedInstanceState.putInt("wrongAnswer",wrongAnswer);
        savedInstanceState.putInt("qCounter",qCounter);
        savedInstanceState.putInt("question",currentQuestion.getQuestion());
        savedInstanceState.putInt("op1",currentQuestion.getOption1());
        savedInstanceState.putInt("op2",currentQuestion.getOption2());
        savedInstanceState.putInt("op3",currentQuestion.getOption3());
        savedInstanceState.putInt("currentAnsNo",currentQuestion.getCorrectAnsNo());
        savedInstanceState.putInt("image",currentQuestion.getImage());
        //savedInstanceState.putString("CorrectIT",currentQuestion.getCorrectIT());
        //savedInstanceState.putString("CorrectEn",currentQuestion.getCorrectEN());
        //savedInstanceState.putInt("image",currentQuestion.getImg1());
        savedInstanceState.putInt("progress",currentProgress);
        progressBar.setProgress(currentProgress);
        progressBar.setMax(100);

        if (answered == false){
            savedInstanceState.putLong("millisLeft", timeLeft);

            countDownTimer.cancel();
        }
        else {
            if(rb1.isChecked()){
                savedInstanceState.putInt("scelta",1);
            }
            else if(rb2.isChecked()){
                savedInstanceState.putInt("scelta",2);
            }
            else if(rb3.isChecked()){
                savedInstanceState.putInt("scelta",3);
            }
            savedInstanceState.putInt("FTime",Ftime);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        answered = savedInstanceState.getBoolean("Answered");
        score = savedInstanceState.getInt("score");
        wrongAnswer = savedInstanceState.getInt("wrongAnswer");
        qCounter = savedInstanceState.getInt("qCounter");
        int question = savedInstanceState.getInt("question");
        int op1 = savedInstanceState.getInt("op1");
        int op2 = savedInstanceState.getInt("op2");
        int op3 = savedInstanceState.getInt("op3");
        int correctAns = savedInstanceState.getInt("currentAnsNo");
        int image = savedInstanceState.getInt("image");
        currentProgress = savedInstanceState.getInt("progress");
        addQuestions();
        getQuizRandom();

        currentQuestion = new Question(question, op1, op2, op3, correctAns, image);

        img.setImageResource(currentQuestion.getImage());
        viewCostraint.setVisibility(View.VISIBLE);
        bmp= BitmapFactory.decodeResource(getResources(),currentQuestion.getImage());//image is your image
        bmp= Bitmap.createScaledBitmap(bmp, width,height, true);
        img.setImageBitmap(bmp);

        tvQuestion.setText(currentQuestion.getQuestion());
        tvQuestionNo.setText(R.string.question);
        tvQuestionNo.append(qCounter+"/"+totalQuestions);

        if(score==1){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.GONE);
            score3.setVisibility(score3.GONE);
        }
        if(score==2){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.GONE);
        }
        else if(score==3){
            score1.setVisibility(score1.VISIBLE);
            score2.setVisibility(score2.VISIBLE);
            score3.setVisibility(score3.VISIBLE);
        }

        rb1.setText(op1);
        rb2.setText(op2);
        rb3.setText(op3);

        if(answered == false){
            timeLeft = savedInstanceState.getLong("millisLeft");

            updateCountDownText();
            setTimer();
            startTimer();

            if(qCounter<5){
                btnNext.setText(R.string.prossimaDomanda);
            }else{
                btnNext.setText(R.string.fine);
            }
        }
        else{
            Ftime = savedInstanceState.getInt("FTime");

            setTimer();
            //startTimer();
            updateFinishedTimerText();
            mProgressBar1.setProgress(Ftime);

            int scelta = savedInstanceState.getInt("scelta");
            if(scelta==1){
                rb1.setChecked(true);
                rb1.setTextColor(Color.GREEN);
            }
            else if(scelta == 2){
                rb2.setChecked(true);
                rb2.setTextColor(Color.GREEN);
            }
            else if(scelta == 3){
                rb3.setChecked(true);
                rb3.setTextColor(Color.GREEN);
            }

            if(qCounter<5){
                btnNext.setText(R.string.prossimaDomanda);
            }else{
                btnNext.setText(R.string.fine);
            }
        }
    }
}