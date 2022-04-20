package com.example.casodistudiomamange.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.SwitchLoginSignupGuestActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Pattern;

/**
 * Classe con il quale si permette ad un nuovo utente di effettuare la registrazione al servizio con la propira E-mail
 */
public class SignUpFragment extends Fragment {

    /**
     * Variabili usate per effettuare la registrazione dell'utente
     */
    TextView email;
    EditText pass;
    EditText passconf;
    Button signup;

    /**
     * Costante stringa che indica i caratteri accettati come password
     */
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?^)(/£])(?=\\S+$).{4,}$";
    boolean passwordVisible;
    boolean passConfVisible;

    /**
     * Variabile per l'istanza dell'utente su firebase
     */
    private FirebaseAuth rAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);

        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.pass);
        passconf = root.findViewById(R.id.passconf);
        signup = root.findViewById(R.id.registerBtn);
        rAuth = FirebaseAuth.getInstance();

        /**
         * Metodo con il quale rendo visibile ed invisibile il campo password
         */
        pass.setOnTouchListener((view, motionEvent) -> {
            final int right =2;
            if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                if(motionEvent.getRawX()>=pass.getRight()-pass.getCompoundDrawables()[right].getBounds().width()){
                    int selection = pass.getSelectionEnd();
                    if(passwordVisible){
                        pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                        pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible=false;
                    }else{
                        pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                        pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible=true;
                    }
                    pass.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        /**
         * Metodo con il quale rendo visibile ed invisibile il campo password di conferma
         */
        passconf.setOnTouchListener((view, motionEvent) -> {
            final int right =2;
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                if(motionEvent.getRawX()>=passconf.getRight()-passconf.getCompoundDrawables()[right].getBounds().width()){
                    int selection = passconf.getSelectionEnd();
                    if(passConfVisible){
                        passconf.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);
                        passconf.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passConfVisible=false;
                    }else{
                        passconf.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);
                        passconf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passConfVisible=true;
                    }
                    passconf.setSelection(selection);
                    return true;
                }
            }
            return false;
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        return root;
    }

    /**
     * Metodo con il quale si effettuano dei controlli nella fase di registrazione dell'utente
     */
    private void register(){

        /**
         * Controllo che i campi email e password non siano vuoti
         */
        if(email.getText().toString().trim().isEmpty()){
            email.setError(getText(R.string.emailRichiestaErr));
            email.requestFocus();
            return;
        }

        if(pass.getText().toString().trim().isEmpty()){
            pass.setError(getText(R.string.passwordRichiestaErr));
            pass.requestFocus();
            return;
        }

        /**
         * Controllo che il campo email inserito abbia un provider valido
         */
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            email.setError(getText(R.string.emailValidaErr));
            email.requestFocus();
            return;
        }
        /**
         * Controllo che la password inserita rispetti gli standard di sicurezza
         */
        if(pass.getText().toString().trim().length() < 8){
            pass.setError(getText(R.string.ottoCaratteriErr));
            pass.requestFocus();
            return;
        }else{
            if(Pattern.matches(PASSWORD_PATTERN, pass.getText().toString().trim())){
                pass.setTextColor(Color.GREEN);
            } else {
                pass.setTextColor(Color.RED);
                pass.setError(getText(R.string.regolePassword));
                pass.requestFocus();
                return;
            }
        }

        /**
         * Controllo che la password coincida con il campo di conferma password
         */
        if(!passconf.getText().toString().trim().contains(pass.getText().toString().trim())){
            passconf.setError(getText(R.string.stessaPassErr));
            passconf.requestFocus();
            return;
        }

        /**
         * Metodo con il quale si inserisce un nuovo utente sul database di Firebase nella sezione Authentication
         */
        rAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),getText(R.string.registerAcepted),Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), SwitchLoginSignupGuestActivity.class));
                        } else {
                            Toast.makeText(getActivity(),getText(R.string.registerInFailed),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}