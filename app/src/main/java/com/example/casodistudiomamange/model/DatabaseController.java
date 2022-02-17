package com.example.casodistudiomamange.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class DatabaseController {

    public DatabaseReference dataref;
    public String codiceGroupOrder;
    public String codiceSingleOrder;

    public DatabaseController() {
        this.dataref = FirebaseDatabase.getInstance().getReference().child("Ordini");
    }

    public void createOrders(String usernameInserito) {
        //sono nel caso in cui devo creare il group order
        dataref.child("Tavolo1").child("flag").getRef().setValue(1); //imposta tavolo a occupato
        dataref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1"); //riferimento a figlio di tavolo1 DA RENDERE GENERICO

        //creo un nuovo group order con attributo codice che ha valore generato a partire dall'username che si suppone univoco
        GroupOrder groupOrder = new GroupOrder(Math.abs(usernameInserito.hashCode()));
        dataref.push().setValue(groupOrder);

        /*creo single order relativo alla persona che ha creato il group order,
        Posso farlo accorgendomi che è stato inserito un figlio di Tavolo1 (cioè group order)
        e creo un figlio di group order creato, cioè un single order
        Mi accorgo dell'aggiunta con Child Evemt Listener
        */

        dataref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1");
        ChildEventListener childEventListener = new ChildEventListener() {
            int i = 0;
            int k=0;

            //Dsnapshot è l'insieme dei figli di Tavolo 1, occorre fermarmi al primo figlio letto che sarebbe proprio il group order aggiunto
            @Override
            public void onChildAdded(@NonNull DataSnapshot Dsnapshot, @Nullable String previousChildName) {
                if (i == 0) {
                    codiceGroupOrder = Dsnapshot.getKey();//ottengo chiave identificativa del group ordeer inserito
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1").child(codiceGroupOrder); //aggiorno riferimento a db
                    String date = Calendar.getInstance().getTime().toString(); //ottengo data corrente per metterla nel DB

                    /*
                    Il codice dell'ordine singolo è numerico ma viene registrato nel DB come stringa, pertanto occorre fare il toString.
                    Il suddetto codice viene generato a partire dalla stringa ottenuta dalla concatenazione di username e data, prendendo l'hash code
                    in valore assoluto
                     */
                    SingleOrder singleOrder = new SingleOrder(Integer.toString(Math.abs((usernameInserito + date).hashCode())), date, usernameInserito); //creo un single order
                    ref.push().setValue(singleOrder); //carico il single order sul db

                    ChildEventListener childEventListener1= new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if(k==0) {
                                codiceSingleOrder = snapshot.getKey();
                                k++;
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
                    ref.addChildEventListener(childEventListener1);
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
        dataref.addChildEventListener(childEventListener);
    }


    public void joinOrder(String usernameInserito){


        //mi unisco al group order
        dataref =FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1");
        //STESSA LOGICA E FUNZIONAMENTO DEL CASO PRECEDENTE, cambiano solo i riferimenti

        ChildEventListener childEventListener= new ChildEventListener() {
            int i=0;
            int j=0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot Dsnapshot, @Nullable String previousChildName) {
                if(i==0) {
                    codiceGroupOrder = Dsnapshot.getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1").child(codiceGroupOrder);
                    String date = Calendar.getInstance().getTime().toString();
                    SingleOrder singleOrder = new SingleOrder(Integer.toString(Math.abs((usernameInserito+date).hashCode())),date, usernameInserito);
                    ref.push().setValue(singleOrder);
                    ValueEventListener getValueListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(j==0){
                                codiceSingleOrder=dataSnapshot.getKey();
                                j++;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    };
                    ref.addValueEventListener(getValueListener );
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
        dataref.addChildEventListener(childEventListener);
    }


    public void addPlate(String nome, long quantita){
        dataref=FirebaseDatabase.getInstance().getReference().child("Ordini").child("Tavolo1").child(codiceGroupOrder).child(codiceSingleOrder);
        PlateInOrder plateToAdd = new PlateInOrder(nome,quantita);
        dataref.push().setValue(plateToAdd);
    }

}
