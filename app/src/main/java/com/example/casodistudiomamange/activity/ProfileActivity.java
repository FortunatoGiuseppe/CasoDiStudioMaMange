package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.connection.NetworkChangedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

/*
 * Activity nel quale viene mostrata l'email dell'utente
 * e permette a quest'ultimo di poter modificare la sua password
 * e di fare logout
 */
public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth lAuth;
    NetworkChangedListener networkChangedListener = new NetworkChangedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View logout = findViewById(R.id.back);
        View changePsw = findViewById(R.id.change);
        lAuth = FirebaseAuth.getInstance();
        TextView email = findViewById(R.id.showEmailUtente);
        ImageView img = findViewById(R.id.imageView12);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String emailInserito = getEmail(); //metodo per ottenere l'email dell'utente loggato
        email.setText(emailInserito);

        changePsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePassword(); //metodo usato per modificare la password dell'utente loggato
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(); //metodo usato per permettere all'utente loggato di fare logout
            }
        });
    }


    /*
     * Metodo usato per modificare la password dell'utente loggato
     */
    private void ChangePassword(){
        EditText resetEmail = new EditText(ProfileActivity.this);
        AlertDialog.Builder passResetDialog = new AlertDialog.Builder(ProfileActivity.this);
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
                            Toast.makeText(ProfileActivity.this,R.string.emailLinkReset,Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this,R.string.emailLinkResetFailed,Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this,R.string.emailValidaErr,Toast.LENGTH_SHORT).show();
                }


            }
        });

        passResetDialog.setNegativeButton(getText(R.string.annulla), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        passResetDialog.create().show();
    }


    /*
     * Metodo usato per permettere all'utente loggato di fare logout
     */
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,SwitchLoginSignupGuestActivity.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /*
     * Metodo per ottenere l'email dell'utente loggato
     */
    private String getEmail(){
        if(lAuth.getCurrentUser()!=null){
            return lAuth.getCurrentUser().getEmail();
        }
        return null;
    }

    @Override
    protected void onStart(){
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangedListener);
        super.onStop();

    }

}