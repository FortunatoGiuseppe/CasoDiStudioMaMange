package com.example.casodistudiomamange.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.casodistudiomamange.activity.QRCodeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    public FirebaseFirestore df;
    private Table table;
    private SoPlate singleOrderPlate;
    private GroupOrder groupOrder;
    private int codiceGroupOrder;
    private SingleOrder singleOrder;
    private int codiceSingleOrder;


    public DatabaseController() {
        this.df= FirebaseFirestore.getInstance();
    }

    //false-> tavolo occupato  true-> tavolo libero
    public void createOrdersFirestore(String usernameInserito, String codiceTavolo, metododiCallback mycallBack){
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

                    df.collection("GROUP ORDERS").whereEqualTo("codiceTavolo",codiceTavolo).orderBy("codice", Query.Direction.DESCENDING).limit(1)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                nuovoSingleOrder.put("codiceSingleOrder", "SO0");       //ricorda di modificare generando in modo casuale il codice
                                nuovoSingleOrder.put("codiceGroupOrder", groupOrder.getCodice()); //qui va il codice generato in automatico che hai inserito in riga 61
                                //aggiungo single order
                                df.collection("SINGLE ORDERS").add(nuovoSingleOrder);
                                singleOrder = new SingleOrder();
                                singleOrder.setCodiceSingleOrder("SO0");
                                singleOrder.setCodiceSingleOrder(groupOrder.getCodice());

                                mycallBack.onCallback(singleOrder.getCodiceSingleOrder());
                            }
                        }
                    });
                }else{
                    //Allora il tavolo è occupato, perciò esiste già il group order (che devo leggere) e devo solo
                    // creare il single order che si deve unire al group order già presente

                    //Prendo il document corrispondente al group order con stato true, cioè quello attivo
                   // System.out.println(codiceTavolo);
                    System.out.println(codiceTavolo);
                    df.collection("GROUP ORDERS").whereEqualTo("codiceTavolo",codiceTavolo).orderBy("codice",Query.Direction.DESCENDING).limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if (task2.isSuccessful()) {


                                   groupOrder = task2.getResult().toObjects(GroupOrder.class).get(0);
                                    df.collection("SINGLE ORDERS").whereEqualTo("codiceGroupOrder",groupOrder.getCodice()).orderBy("codiceSingleOrder",Query.Direction.DESCENDING).limit(1)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                singleOrder=(task.getResult().toObjects(SingleOrder.class)).get(0);
                                                Log.d("TAG",singleOrder.getCodiceSingleOrder());
                                                Log.d("TAG",singleOrder.getCodiceGroupOrder());
                                                codiceSingleOrder=Integer.parseInt(task.getResult().toObjects(SingleOrder.class).get(0).getCodiceSingleOrder().substring(2));
                                                codiceSingleOrder+=1;
                                                //costruisco la stringa
                                                singleOrder.setCodiceSingleOrder("SO"+String.valueOf(codiceSingleOrder));
                                                //creo single order
                                                Map<String, Object> nuovoSingleOrder = new HashMap<>();
                                                nuovoSingleOrder.put("codiceSingleOrder", singleOrder.getCodiceSingleOrder());       //ricorda di modificare generando in modo casuale il codice
                                                nuovoSingleOrder.put("codiceGroupOrder", singleOrder.getCodiceGroupOrder()); //qui va il codice generato in automatico che hai inserito in riga 61
                                                //aggiungo single order
                                                df.collection("SINGLE ORDERS").add(nuovoSingleOrder);
                                                mycallBack.onCallback(singleOrder.getCodiceSingleOrder());
                                            }

                                        }
                                    });

                                }
                            }
                        });
                }
            }
        });
    }

    /*Metodo che crea il piatto associato al codice sel singleOrder */
    public void createSoPlateFirestore(String plate){
       df.collection("SO-PIATTO")
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                                Map<String, Object> creaSoPiatto = new HashMap<>();
                            creaSoPiatto.put("codiceSingleOrder", "SO4");
                            creaSoPiatto.put("nomePiatto", plate);
                            creaSoPiatto.put("quantita",1);
                                df.collection("SO-PIATTO").add(creaSoPiatto);
                        }
                    }
                });
    }

    /*Metodo che aumenta la quantita del piatto*/
    public void addPlateFirestore(String plate){
       df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder","SO4")
                .whereEqualTo("nomePiatto",plate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                        df.collection("SO-PIATTO").document(documentSnapshot.getId()).update("quantita",1+ singleOrderPlate.getQuantita());
                    }

                }
            }
        });
    }

    /*Metodo che diminuisce la quantita del piatto*/
    public void removePlateFirestore(String plate){
        df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder","SO4")
                .whereEqualTo("nomePiatto",plate)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                        df.collection("SO-PIATTO").document(documentSnapshot.getId()).update("quantita",singleOrderPlate.getQuantita() - 1);
                    }

                }
            }
        });
    }

    /*Metodo che elimina totalmente il piatto se la quantita è pari a 0*/
    public void deletePlateFirestore(String plate){
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

public interface metododiCallback{
        void onCallback(String codiceSingleOrderCheMiServe);
}
}
