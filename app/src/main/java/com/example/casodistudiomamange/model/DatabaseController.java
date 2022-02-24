package com.example.casodistudiomamange.model;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    public FirebaseFirestore df;
    private Table table;
    private SoPlate singleOrderPlate;
    private GroupOrder groupOrder;
    private int codiceGroup;
    private int codiceGroupOrder;
    private String codiceGO;


    public DatabaseController() {
        this.df= FirebaseFirestore.getInstance();
    }

    //false-> tavolo occupato  true-> tavolo libero
    public void createOrdersFirestore(String usernameInserito, String codiceTavolo){
        DocumentReference docRef = df.collection("TAVOLI").document(codiceTavolo);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                table= documentSnapshot.toObject(Table.class);
                if(table.getFlag()==0){
                    //Allora il tavolo è libero, perciò devo impostare il tavolo a occupato sul db, creare group order
                    // e creare single order relativo al group order esistente

                    //imposto tavolo a occupatoa
                    docRef.update("flag",1);

                    //Vado ad ordinare in ordine decrescente lo snapshot limitandolo ad 1, quindi prendo l'ultimo elemento presente con il valore più alto. Incremento l'ultimo elemento e costruisco la stringa per creaare il nuovo grouporder

                    df.collection("GROUP ORDERS").orderBy("codice", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {


                                groupOrder=(task.getResult().toObjects(GroupOrder.class)).get(0);
                                //prendo l'ultimo elemento della lista
                                codiceGroupOrder=Integer.parseInt(task.getResult().toObjects(GroupOrder.class).get(0).getCodice().substring(2));
                                codiceGroupOrder+=1;
                                //costruisco la stringa
                                groupOrder.setCodice("GO"+String.valueOf(codiceGroupOrder));



                                //creo group order
                                Map<String, Object> nuovoGroupOrder = new HashMap<>();
                                nuovoGroupOrder.put("codice", groupOrder.getCodice());       //ricorda di modificare generando in modo casuale il codice
                                nuovoGroupOrder.put("codiceTavolo", codiceTavolo);
                                nuovoGroupOrder.put("stato", true); //STATO TRUE-> G.O. attivo, tutti gli altri avranno stato falso.
                                //aggiungo group order
                                df.collection("GROUP ORDERS").add(nuovoGroupOrder);

                                //creo single order
                                Map<String, Object> nuovoSingleOrder = new HashMap<>();
                                nuovoSingleOrder.put("codice", "SO0");       //ricorda di modificare generando in modo casuale il codice
                                nuovoSingleOrder.put("codiceGroupOrder", groupOrder.getCodice()); //qui va il codice generato in automatico che hai inserito in riga 61
                                //aggiungo single order
                                df.collection("SINGLE ORDERS").add(nuovoSingleOrder);
                            }
                        }
                    });










                }else{
                    //Allora il tavolo è occupato, perciò esiste già il group order (che devo leggere) e devo solo
                    // creare il single order che si deve unire al group order già presente

                    //Prendo il document corrispondente al group order con stato true, cioè quello attivo
                    df.collection("GROUP ORDERS")
                            .whereEqualTo("stato", true)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //ho trovato il group order, adesso devo creare il sigle order che ha come chiave esterna al group order
                                            //il codice del group order stesso.

                                            Map<String, Object> nuovoSingleOrder = new HashMap<>();
                                            nuovoSingleOrder.put("codice", "SO4"); //ricorda di modificare generando in modo casuale il codice
                                            nuovoSingleOrder.put("codiceGroupOrder", document.get("codice").toString()); //qui metto chiave esterna
                                            //aggiungo single order
                                            df.collection("SINGLE ORDERS").add(nuovoSingleOrder);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    /*Metodo che crea il piatto associato al codice sel singleOrder */
    public void createSoPlateFirestore(String plate,long quantita){
       df.collection("SO-PIATTO")
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                                Map<String, Object> creaSoPiatto = new HashMap<>();
                            creaSoPiatto.put("codiceSingleOrder", "SO4");
                            creaSoPiatto.put("nomePiatto", plate);
                            creaSoPiatto.put("quantita",quantita);
                                df.collection("SO-PIATTO").add(creaSoPiatto);
                        }
                    }
                });
    }

    /*Metodo che aumenta la quantita del piatto*/
    public void addPlateFirestore(String plate,long quantita){
       df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder","SO4")
                .whereEqualTo("nomePiatto",plate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                        df.collection("SO-PIATTO").document(documentSnapshot.getId()).update("quantita",quantita+ singleOrderPlate.getQuantita());
                    }

                }
            }
        });
    }

    /*Metodo che diminuisce la quantita del piatto*/
    public void removePlateFirestore(String plate,long quantita){
        df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder","SO4")
                .whereEqualTo("nomePiatto",plate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                        df.collection("SO-PIATTO").document(documentSnapshot.getId()).update("quantita",singleOrderPlate.getQuantita() - quantita);
                    }

                }
            }
        });
    }

    /*Metodo che elimina totalmente il piatto se la quantita è pari a 0*/
    public void deletePlateFirestore(String plate,long quantita){
        df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder","SO4")
                .whereEqualTo("nomePiatto",plate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){

                        df.collection("SO-PIATTO").document(documentSnapshot.getId()).delete();
                    }

                }
            }
        });
    }


}
