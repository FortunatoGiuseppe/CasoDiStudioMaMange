package com.example.casodistudiomamange.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Classe con la quale si effettua il login nell'app
 */
public class LoginFragment extends Fragment {
    TextView email;
    EditText pass;
    TextView forgetPass;
    Button loginBtn;
    private FirebaseAuth lAuth;
    boolean passwordVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.pass);

        pass.setOnTouchListener((view, motionEvent) -> {
            final int right =2;
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
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

        forgetPass = root.findViewById(R.id.forget_pass);
        loginBtn = root.findViewById(R.id.loginBtn);
        lAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPass();
            }
        });

        return root;
    }

    /**
     * Metodo con il quale si effettua il login
     *
     * Si controllano prima che le stringhe inserite in input rispettano
     * gli standard convenzionali (Ad esempio una password da almeno 8 cifre)
     *
     */
    private void login(){
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
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            email.setError(getText(R.string.emailValidaErr));
            email.requestFocus();
            return;
        }
        if(pass.getText().toString().trim().length() < 8){
            pass.setError(getText(R.string.ottoCaratteriErr));
            pass.requestFocus();
            return;
        }
        //metodo di firebase usato per effettuare login
        lAuth.signInWithEmailAndPassword(email.getText().toString().trim(),pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),R.string.loggedIn,Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), SwitchLoginSignupGuestActivity.class));

                } else {
                    Toast.makeText(getActivity(),R.string.loggedInFailed,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metodo per fare richiesta di password dimenticata
     */
    private void forgetPass(){
        EditText resetEmail = new EditText(getView().getContext());
        AlertDialog.Builder passResetDialog = new AlertDialog.Builder(getView().getContext());
        passResetDialog.setTitle(R.string.passwordReset);
        passResetDialog.setMessage(R.string.emailReset);
        passResetDialog.setView(resetEmail);

        passResetDialog.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailReset = resetEmail.getText().toString();
                //controllo che l'email inserita nell'Alert non sia vuota
                if(!emailReset.equals("")){
                    lAuth.sendPasswordResetEmail(emailReset).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getActivity(),R.string.emailLinkReset,Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),R.string.emailLinkResetFailed,Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(),R.string.emailValidaErr,Toast.LENGTH_SHORT).show();
                }


            }
        });

        passResetDialog.setNegativeButton(getString(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        passResetDialog.create().show();
    }
}