package com.example.casodistudiomamange.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
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
    TextView pass;
    TextView forgetPass;
    Button loginBtn;
    private FirebaseAuth lAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);
        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.pass);
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
        if(pass.getText().toString().trim().length() < 6){
            pass.setError(getText(R.string.seiCaratteriErr));
            pass.requestFocus();
            return;
        }

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

    private void forgetPass(){
        EditText resetEmail = new EditText(getView().getContext());
        AlertDialog.Builder passResetDialog = new AlertDialog.Builder(getView().getContext());
        passResetDialog.setTitle(R.string.passwordReset);
        passResetDialog.setMessage("");
        passResetDialog.setView(resetEmail);

        passResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetEmail.getText().toString();
                lAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(),R.string.emailLinkReset,Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),R.string.emailLinkResetFailed+ e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        passResetDialog.create().show();
    }
}