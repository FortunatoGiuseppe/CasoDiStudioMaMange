package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth lAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        Button logout = findViewById(R.id.logout);
        Button changePsw = findViewById(R.id.changePsw);
        lAuth = FirebaseAuth.getInstance();
        TextView name = findViewById(R.id.nomeUtente);
        TextView email = findViewById(R.id.showEmailUtente);

        Intent intent = getIntent();
        String usernameInserito = intent.getStringExtra("username");
        String emailInserito = getEmail();
        name.setText(usernameInserito);
        email.setText(emailInserito);

        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void ChangePassword(){
        EditText resetEmail = new EditText(ProfileActivity.this);
        AlertDialog.Builder passResetDialog = new AlertDialog.Builder(ProfileActivity.this);
        passResetDialog.setTitle("Change Password?");
        passResetDialog.setMessage("Enter your email to recived reset link");
        passResetDialog.setView(resetEmail);

        passResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail = resetEmail.getText().toString();
                lAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ProfileActivity.this,"Reset Link Sent To Your Email",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,"Error! Reset Link is Not Sent"+ e.getMessage(),Toast.LENGTH_SHORT).show();
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

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SwitchLoginSignupGuestActivity.class));
        finish();
    }

    private String getEmail(){
        if(lAuth.getCurrentUser()!=null){
            return lAuth.getCurrentUser().getEmail();
        }
        return null;
    }
}