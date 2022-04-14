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

public class SignUpFragment extends Fragment {
    TextView email;
    EditText pass;
    EditText passconf;
    Button signup;
    private FirebaseAuth rAuth;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    boolean passwordVisible;
    boolean passConfVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up, container, false);

        email = root.findViewById(R.id.email);
        pass = root.findViewById(R.id.pass);
        passconf = root.findViewById(R.id.passconf);
        signup = root.findViewById(R.id.registerBtn);
        rAuth = FirebaseAuth.getInstance();


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

    private void register(){

        if(email.getText().toString().trim().isEmpty()){
            email.setError("email is required");
            email.requestFocus();
            return;
        }
        if(pass.getText().toString().trim().isEmpty()){
            pass.setError("password is required");
            pass.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }
        if(pass.getText().toString().trim().length() < 8){
            pass.setError("Enter at least six character");
            pass.requestFocus();
            return;
        }else{
            if(Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?^)(/£])(?=\\S+$).{4,}$", pass.getText().toString().trim())){
                pass.setTextColor(Color.GREEN);
            } else {
                pass.setTextColor(Color.RED);
                pass.setError("Password Rule:\n" +
                        "At least one capital letter\n" +
                        "At least one number\n" +
                        "At least one symbol");
                pass.requestFocus();
                return;
            }
        }

        if(!passconf.getText().toString().trim().contains(pass.getText().toString().trim())){
            passconf.setError("Enter the same password");
            passconf.requestFocus();
            return;
        }

        rAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"User has been registered successfully!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), SwitchLoginSignupGuestActivity.class));
                        } else {
                            Toast.makeText(getActivity(),"Failed to register",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}