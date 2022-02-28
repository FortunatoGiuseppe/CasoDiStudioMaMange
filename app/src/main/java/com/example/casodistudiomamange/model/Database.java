package com.example.casodistudiomamange.model;

import android.os.Bundle;


import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Database {

    /*ArrayList che andremo ad avvalorare con tutti i GroupOrders presenti nel DB*/
    private ArrayList<GroupOrder> groupOrders;
    /*ArrayList che andremo ad avvalorare con tutti i SingleOrders presenti nel DB*/
    private ArrayList<SingleOrder> singleOrders;
    /*Creo il riferimento del Database*/
    private FirebaseFirestore ff;


    public Database() {
        this.ff = FirebaseFirestore.getInstance();
        this.groupOrders = new ArrayList<GroupOrder>();
    }

    public Database(ArrayList<GroupOrder> groupOrders, ArrayList<SingleOrder> singleOrders) {
        this.groupOrders = new ArrayList<GroupOrder>();
        this.singleOrders = singleOrders;
        this.ff = FirebaseFirestore.getInstance();
    }

    /*Metodo che permette di caricare tutti i GroupOrders*/
    public void readGroupOrders(){
        ff.collection("GROUP ORDERS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        groupOrders.add(documentSnapshot.toObject(GroupOrder.class));
                    }
                }
            }
        });
    }

    /*Il nostro intento è prendere il codice del GroupOrder e mandarlo nell'Activity "x"
      in modo tale da eseguire le query in maniera + "soft";
      Il ritorno dell'oggetto chiaramente non funziona per via del metodo void
      all'interno del db.collection
     */

    public ArrayList<GroupOrder> getGroupOrders() {
        return groupOrders;
    }

    public void setGroupOrders(ArrayList<GroupOrder> groupOrders) {
        this.groupOrders = groupOrders;
    }
}
