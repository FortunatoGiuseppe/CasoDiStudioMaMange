package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.casodistudiomamange.R;

import com.example.casodistudiomamange.model.GroupOrder;
import com.example.casodistudiomamange.model.SingleOrder;
import com.example.casodistudiomamange.model.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Calendar;




public class QRCodeActivity extends AppCompatActivity {

    private Button scanQrCodeBtn;
    private Button confirmBtn;
    private TextView benvenuto;
    private EditText insertQrCode;
    private String Code1 = "MST001";
    private Button logout;
    DatabaseReference dataref_guest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        scanQrCodeBtn = findViewById(R.id.scanQrCodeBtn);
        confirmBtn = findViewById(R.id.confirmBtn);
        insertQrCode = findViewById(R.id.insertQrCode);
        logout = findViewById(R.id.logout);
        benvenuto=findViewById(R.id.textView_benvenuto);

        Intent intent = getIntent();
        String usernameInserito = intent.getStringExtra("UsernameInserito");

        benvenuto.setText("Benvenuto "+usernameInserito);

        scanQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQrCode();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlCode(insertQrCode.getText().toString());
                dataref_guest = FirebaseDatabase.getInstance().getReference().child("Ordini");

                /*Partendo da ordini vedi tutti i tavoli presenti, quando trovi tavolo con codice inserito (MST001) (da modificare nell'if)
                 allora vedi se è libero, cioè se flag=0.
                 Se lo è allora l'utente corrente è il primo e imposta il flag =1 e si crea il group order
                 Se non lo è allora il group order esiste già e perciò gli altri si devono solo aggiungere
                */

                dataref_guest.addListenerForSingleValueEvent(new ValueEventListener() {
                    String codiceTav1;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Table table = dataSnapshot.getValue(Table.class);
                            if(table.getCodicetavolo().equals("MST001")) { // da modificare con codiceTavoloInserito invece di MST001
                                if (table.getFlag() == 0) {
                                    //sono nel caso in cui devo creare il group order
                                    dataref_guest.child("Tavolo1").child("flag").getRef().setValue(1); //imposta tavolo a occupato
                                    dataref_guest=FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1"); //riferimento a figlio di tavolo1 DA RENDERE GENERICO

                                    //creo un nuovo group order con attributo codice che ha valore generato a partire dall'username che si suppone univoco
                                    GroupOrder groupOrder=new GroupOrder(Math.abs(usernameInserito.hashCode()));
                                    dataref_guest.push().setValue(groupOrder);

                                    /*creo single order relativo alla persona che ha creato il group order,
                                    Posso farlo accorgendomi che è stato inserito un figlio di Tavolo1 (cioè group order)
                                    e creo un figlio di group order creato, cioè un single order
                                    Mi accorgo dell'aggiunta con Child Evemt Listener
                                    */
                                    dataref_guest=FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1");
                                    ChildEventListener childEventListener= new ChildEventListener() {
                                        int i=0;
                                        //Dsnapshot è l'insieme dei figli di Tavolo 1, occorre fermarmi al primo figlio letto che sarebbe proprio il group order aggiunto
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot Dsnapshot, @Nullable String previousChildName) {
                                            if(i==0) {
                                                codiceTav1 = Dsnapshot.getKey();//ottengo chiave identificativa del group ordeer inserito
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1").child(codiceTav1); //aggiorno riferimento a db
                                                String date = Calendar.getInstance().getTime().toString(); //ottengo data corrente per metterla nel DB

                                                /*
                                                Il codice dell'ordine singolo è numerico ma viene registrato nel DB come stringa, pertanto occorre fare il toString.
                                                Il suddetto codice viene generato a partire dalla stringa ottenuta dalla concatenazione di username e data, prendendo l'hash code
                                                in valore assoluto
                                                 */
                                                SingleOrder singleOrder = new SingleOrder(Integer.toString(Math.abs((usernameInserito+date).hashCode())),date, usernameInserito); //creo un single order
                                                ref.push().setValue(singleOrder); //carico il single order sul db
                                                i++;
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };
                                    dataref_guest.addChildEventListener(childEventListener);

                                }else{
                                    //mi unisco al group order
                                    dataref_guest=FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1");
                                    //STESSA LOGICA E FUNZIONAMENTO DEL CASO PRECEDENTE, cambiano solo i riferimenti

                                    ChildEventListener childEventListener= new ChildEventListener() {
                                        int i=0;
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot Dsnapshot, @Nullable String previousChildName) {
                                            if(i==0) {
                                                codiceTav1 = Dsnapshot.getKey();
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1").child(codiceTav1);
                                                String date = Calendar.getInstance().getTime().toString();
                                                SingleOrder singleOrder = new SingleOrder(Integer.toString(Math.abs((usernameInserito+date).hashCode())),date, usernameInserito);
                                                ref.push().setValue(singleOrder);
                                                i++;
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };
                                    dataref_guest.addChildEventListener(childEventListener);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }
/*
    private long generateNumber() {

    }*/


    private void scanQrCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                if(result.getContents().equals(Code1)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(result.getContents());
                    builder.setTitle("Scanning result");
                    builder.setPositiveButton("finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(QRCodeActivity.this, RestaurantActivity.class));
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            } else {
                Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void controlCode(String QrCode){
        if (QrCode.contains(Code1)){
            Toast.makeText(this,R.string.tavoloTrovato , Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MaMangeNavigationActivity.class));
        } else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(QRCodeActivity.this);
            builder1.setMessage(R.string.tavoloNonTrovato);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder1.create();
            alert.show();
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this,SwitchLoginSignupGuestActivity.class));
        finish();
    }
}