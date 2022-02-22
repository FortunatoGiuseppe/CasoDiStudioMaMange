package com.example.casodistudiomamange.model;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class DatabaseController {
    public FirebaseFirestore df;
    private Table table;

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

                    //imposto tavolo a occupato
                    docRef.update("flag",1);

                    //creo group order
                    Map<String, Object> nuovoGroupOrder = new HashMap<>();
                    nuovoGroupOrder.put("codice", "GO2");       //ricorda di modificare generando in modo casuale il codice
                    nuovoGroupOrder.put("codiceTavolo", codiceTavolo);
                    nuovoGroupOrder.put("stato", true); //STATO TRUE-> G.O. attivo, tutti gli altri avranno stato falso.
                    //aggiungo group order
                    df.collection("GROUP ORDERS").add(nuovoGroupOrder);

                    //creo single order
                    Map<String, Object> nuovoSingleOrder = new HashMap<>();
                    nuovoSingleOrder.put("codice", "SO2");       //ricorda di modificare generando in modo casuale il codice
                    nuovoSingleOrder.put("codiceGroupOrder", "GO2"); //qui va il codice generato in automatico che hai inserito in riga 61
                    //aggiungo single order
                    df.collection("SINGLE ORDERS").add(nuovoSingleOrder);

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

}
