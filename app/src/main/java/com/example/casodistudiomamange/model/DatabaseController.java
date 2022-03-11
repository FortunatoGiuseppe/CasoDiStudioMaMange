package com.example.casodistudiomamange.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.casodistudiomamange.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Single;

public class DatabaseController {
    
    public FirebaseFirestore df;
    private Table table;
    private GroupOrder groupOrder;
    private SingleOrder singleOrder;
    private SoPlate singleOrderPlate;
    private int codiceGroupOrder;
    private int codiceSingleOrder;

    private boolean alreadyExists=false;
    
    public DatabaseController() {
        this.df= FirebaseFirestore.getInstance();
    }

    /*Metodo per la creazione del GroupOrder e SingleOrder*/
    public void createOrdersFirestore(String usernameInserito, String codiceTavolo, metododiCallback mycallBack){
        
        DocumentReference docRef = df.collection("TAVOLI").document(codiceTavolo);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                table= documentSnapshot.toObject(Table.class);

                /*Se il tavolo è libero avvia la creazione del nuovo GroupOrder e SingleOrder*/

                if(table.isTableFree()){    //modificato
                    
                    docRef.update("tableFree",false);//Imposta il tavolo come occupato

                    //Vado ad ordinare in ordine decrescente lo snapshot limitandolo ad 1, quindi prendo l'ultimo elemento presente con il valore più alto.
                    //Incremento l'ultimo elemento e costruisco la stringa per creare il nuovo grouporder
                    df.collection("GROUP ORDERS")
                        .whereEqualTo("codiceTavolo",codiceTavolo)
                        .orderBy("codice", Query.Direction.DESCENDING).limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            
                            if (task.isSuccessful()) {
                                // se il groupOrder è nullo, creo il primo GroupOrder con codice GO0
                                if(task.getResult().isEmpty()){
                                    groupOrder = new GroupOrder();
                                    groupOrder.setCodiceTavolo(codiceTavolo);
                                    groupOrder.setTableFree(false);
                                    groupOrder.setCodice("GO0");

                                    //creo groupOrder per permettere ai SingleOrder di unirsi al tavolo
                                    Map<String, Object> nuovoGroupOrder = new HashMap<>();
                                    nuovoGroupOrder.put("codice", groupOrder.getCodice());
                                    nuovoGroupOrder.put("codiceTavolo", groupOrder.getCodiceTavolo());
                                    nuovoGroupOrder.put("tableFree", groupOrder.isTableFree());
                                    df.collection("GROUP ORDERS").add(nuovoGroupOrder);
                                }
                                else {
                                    //prendo l'ultimo elemento della lista
                                    groupOrder=(task.getResult().toObjects(GroupOrder.class)).get(0);

                                    //seleziono la parte numerica del codiceGroupOrder
                                    codiceGroupOrder=Integer.parseInt(task.getResult()
                                            .toObjects(GroupOrder.class)
                                            .get(0).getCodice().substring(2));

                                    //inceremento il nuovo codiceGroupOrder
                                    codiceGroupOrder+=1;

                                    //ricostruisco la stringa
                                    groupOrder.setCodice("GO"+String.valueOf(codiceGroupOrder));

                                    //creo groupOrder per permettere ai SingleOrder di unirsi al tavolo
                                    Map<String, Object> nuovoGroupOrder = new HashMap<>();
                                    nuovoGroupOrder.put("codice", groupOrder.getCodice());
                                    nuovoGroupOrder.put("codiceTavolo", codiceTavolo);
                                    nuovoGroupOrder.put("tableFree", false);
                                    df.collection("GROUP ORDERS").add(nuovoGroupOrder);
                                }

                                //creo single order supponendo che il primo single order abbia come codice "SO0"
                                singleOrder = new SingleOrder();
                                singleOrder.setCodiceSingleOrder("SO0");
                                singleOrder.setCodiceGroupOrder(groupOrder.getCodice());
                                singleOrder.setCodiceTavolo(codiceTavolo);
                                Map<String, Object> nuovoSingleOrder = new HashMap<>();
                                nuovoSingleOrder.put("codiceSingleOrder", singleOrder.getCodiceSingleOrder());
                                nuovoSingleOrder.put("codiceGroupOrder", singleOrder.getCodiceGroupOrder());
                                nuovoSingleOrder.put("codiceTavolo",singleOrder.getCodiceTavolo());
                                nuovoSingleOrder.put("singleOrderConfirmed",false);
                                df.collection("SINGLE ORDERS").add(nuovoSingleOrder);
                                
                                //Assegno al metodo di CallBack il codice del SingleOrder e GroupOrder
                                mycallBack.onCallback(singleOrder.getCodiceSingleOrder(),groupOrder.getCodice());
                                
                            }
                            
                        }
                    });
                    
                } else {
                    
                    //Il tavolo è occupato, perciò esiste già il group order
                    //Perciò leggo il codiceGroupOrder dal DB e creo il SingleOrder associato
                    
                    //Query per prendere il groupOrder più recente in base al codice del tavolo
                    df.collection("GROUP ORDERS")
                        .whereEqualTo("codiceTavolo",codiceTavolo)
                        .orderBy("codice",Query.Direction.DESCENDING).limit(1)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            
                            if (task2.isSuccessful()) {
                                
                                //prendo risultato della Query
                                groupOrder = task2.getResult().toObjects(GroupOrder.class).get(0);
                                
                                //prendo il singleOrder più recente in base al GroupOrder
                                df.collection("SINGLE ORDERS")
                                    .whereEqualTo("codiceGroupOrder",groupOrder.getCodice())
                                    .orderBy("codiceSingleOrder",Query.Direction.DESCENDING).limit(1)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            
                                            if(task.isSuccessful()){
                                                
                                                singleOrder=(task.getResult().toObjects(SingleOrder.class)).get(0);
                                                codiceSingleOrder=Integer.parseInt(task.getResult().toObjects(SingleOrder.class).get(0).getCodiceSingleOrder().substring(2));
                                                codiceSingleOrder+=1;
                                                singleOrder.setCodiceSingleOrder("SO"+String.valueOf(codiceSingleOrder));
                                                
                                                //creo single order
                                                Map<String, Object> nuovoSingleOrder = new HashMap<>();
                                                nuovoSingleOrder.put("codiceSingleOrder", singleOrder.getCodiceSingleOrder());
                                                nuovoSingleOrder.put("codiceGroupOrder", singleOrder.getCodiceGroupOrder());
                                                nuovoSingleOrder.put("codiceTavolo",codiceTavolo);
                                                nuovoSingleOrder.put("singleOrderConfirmed",false);
                                                df.collection("SINGLE ORDERS").add(nuovoSingleOrder);

                                                //Assegno al metodo di CallBack il codice del SingleOrder e GroupOrder
                                                mycallBack.onCallback(singleOrder.getCodiceSingleOrder(),groupOrder.getCodice());
                                                
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

    /*Metodo che associa il piatto selezionato al codice del singleOrder*/
    public void orderPlate(String plate, String codiceSingleOrder, String codiceGroupOrder, String codiceTavolo,String username,Long quantita){
        
        df.collection("SO-PIATTO")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    
                    Map<String, Object> creaSoPiatto = new HashMap<>();
                    creaSoPiatto.put("codiceSingleOrder", codiceSingleOrder);
                    creaSoPiatto.put("nomePiatto", plate);
                    creaSoPiatto.put("quantita",quantita);
                    creaSoPiatto.put("codiceGroupOrder",codiceGroupOrder);
                    creaSoPiatto.put("codiceTavolo",codiceTavolo);
                    creaSoPiatto.put("username",username);
                    df.collection("SO-PIATTO").add(creaSoPiatto);
                    
                }
                
            }
                
        });
    }

    /*Metodo che aumenta la quantita del piatto aggiunto in base al codice del SingleOrder*/
    public void incrementQuantityPlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo,String username){
       
        df.collection("SO-PIATTO")
            .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
            .whereEqualTo("nomePiatto",plate)
            .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
            .whereEqualTo("codiceTavolo", codiceTavolo)
            .whereEqualTo("username",username)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                
                    if(task.isSuccessful()){

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                            df.collection("SO-PIATTO")
                                .document(documentSnapshot.getId())
                                .update("quantita",1+ singleOrderPlate.getQuantita());
                        }

                    }
                }
        });
    }

    /*Metodo che diminuisce la quantita del piatto in base al codice del SingleOrder*/
    public void decrementQuantityPlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder, String codiceTavolo,String username){

        df.collection("SO-PIATTO")
            .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
            .whereEqualTo("nomePiatto",plate)
            .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
            .whereEqualTo("codiceTavolo", codiceTavolo)
            .whereEqualTo("username",username)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful()){

                        for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            singleOrderPlate = documentSnapshot.toObject(SoPlate.class);
                            df.collection("SO-PIATTO")
                                .document(documentSnapshot.getId())
                                .update("quantita",singleOrderPlate.getQuantita() - 1);
                        }
                    }
                }
        });
    }

    /*Metodo che elimina totalmente il piatto se la quantita è pari a 0*/
    public void deletePlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo,String username){

        df.collection("SO-PIATTO")
            .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
            .whereEqualTo("nomePiatto",plate)
            .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
            .whereEqualTo("codiceTavolo", codiceTavolo)
            .whereEqualTo("username",username)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

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


    //Metodo che permette di capire se piatti di un ordine letti dal file locale sono già presenti nel db
    public boolean checkIfPlateHasAlreadyBeenOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo,String username){

        df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
                .whereEqualTo("nomePiatto",plate)
                .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
                .whereEqualTo("codiceTavolo", codiceTavolo)
                .whereEqualTo("username",username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            alreadyExists= true;    //se ha trovato risultati vuol dire che l'ordine era già stato aggiunto
                        }else{
                            alreadyExists=false;
                        }
                    }
                });
        return alreadyExists;
    }


    public void setSingleOrderConfirmed(String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo){
        df.collection("SINGLE ORDERS")
                .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
                .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
                .whereEqualTo("codiceTavolo", codiceTavolo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                df.collection("SINGLE ORDERS")
                                        .document(documentSnapshot.getId())
                                        .update("singleOrderConfirmed",true);
                            }
                        }
                    }
                });
    }

    public void allSingleOrdersAreConfirmed(String codiceGroupOrder,String codiceTavolo, metododiCallbackAllSingleOrderConfirmed areAllConfirmedCallback) {


        df.collection("SINGLE ORDERS")
                .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
                .whereEqualTo("codiceTavolo", codiceTavolo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<SingleOrder> singleOrdersControlConfirmed = new ArrayList<>();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                SingleOrder singleOrderControl = documentSnapshot.toObject(SingleOrder.class);
                                singleOrdersControlConfirmed.add(singleOrderControl);

                                //se trovo anche solo uno non confermato allora non sono tutti confermati
                                if(!singleOrderControl.isSingleOrderConfirmed()){
                                    Boolean areAllConfirmed=false;
                                    areAllConfirmedCallback.onCallback(areAllConfirmed);
                                }
                            }
                        }
                    }
                });
        areAllConfirmedCallback.onCallback(true);

    }

    public void sendOrdersToTheKitchen() {

    }


    /*Interfaccia che permette di chiamare il metodo di Callback*/
    public interface metododiCallback{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(String codiceSingleOrderCheMiServe,String codiceGroupOrder);
    }

    /*Interfaccia che permette di chiamare il metodo di Callback AllSingleOrderConfirmed*/
    public interface metododiCallbackAllSingleOrderConfirmed{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(boolean areAllSingleOrderConfirmed);
    }

}
