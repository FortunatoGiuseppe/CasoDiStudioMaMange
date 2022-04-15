package com.example.casodistudiomamange.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.fragment.SingleOrderFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


    /**
     * Metodo per la creazione del GroupOrder e SingleOrder
     * @param codiceTavolo
     * @param mycallBack
     */
    public void createOrdersFirestore(String codiceTavolo, metododiCallback mycallBack){
        
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
                                    .whereEqualTo("codiceTavolo", codiceTavolo)
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


    /**
     * Metodo che scrive nel DB il piatto selezionato, legato agli identificatori passati come parametro
     * @param plate nome del piatto
     * @param codiceSingleOrder codice dell'ordine singolo dell'utente corrente
     * @param codiceGroupOrder codice dell'ordine del gruppo
     * @param codiceTavolo codice del tavolo
     * @param username username dell'utente corrente
     * @param quantita numerosità del piatto ordinato
     */
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


    /**
     * Metodo che aumenta la quantita del piatto aggiunto
     * @param plate
     * @param codiceSingleOrder
     * @param codiceGroupOrder
     * @param codiceTavolo
     * @param username
     * @param quantita
     */
    public void incrementQuantityPlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo,String username, long quantita){
       
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
                                .update("quantita",quantita);
                        }

                    }
                }
        });
    }


    /**
     * Metodo che diminuisce la quantita del piatto
     * @param plate
     * @param codiceSingleOrder
     * @param codiceGroupOrder
     * @param codiceTavolo
     * @param username
     * @param quantita
     */
    public void decrementQuantityPlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder, String codiceTavolo,String username,long quantita){

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
                                .update("quantita",quantita);
                        }
                    }
                }
        });
    }


    /**
     * Metodo che elimina totalmente il piatto dal db
     * @param plate nome del piatto da cancellare
     * @param codiceSingleOrder
     * @param codiceGroupOrder
     * @param codiceTavolo
     * @param username
     * @param context necessario per riavviare il fragment (quando richiesto)
     * @param refreshFragment true-> è necessario riavviare il fragment o meno, per aggiornare l'elenco in single order fragment (se chiamato da Adapter_plates_ordered)
     *                        false-> non è necessario, accade quando questo metodo viene chiamato da Adapter_plates
     *
     */
    public void deletePlateOrdered(String plate, String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo,String username, MaMangeNavigationActivity context,boolean refreshFragment){

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

                        if(refreshFragment){
                            //Riavvio fragment per avere lista aggiornata
                            Fragment fragment = new SingleOrderFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("chiamante", "riavvioAdapter"); //specifica a singleOrderFragment che deve caricare l'ordine corrente
                            fragment.setArguments(bundle);
                            ((MaMangeNavigationActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }

                    }
                }
        });
    }

    /*Metodo che permette di:
        1. conferma il singleOrder
        2.
        3. Invia l'ordinazione di gruppo alla cucina
     */

    /**
     * Metodo che permette:
     * 1) la conferma del singleOrder (imposta confermato sul DB)
     * 2) permette di capire se tutti hanno confermato, attraverso i callback comunica l'esito
     * @param codiceSingleOrder
     * @param codiceGroupOrder
     * @param codiceTavolo
     * @param callback è true se tutti i single order sono stati confermati, quindi posso impostare il tavolo a libero e inviare l'ordine alla cucina, è false altrimenti
     */
    public void setSingleOrderConfirmed(String codiceSingleOrder,String codiceGroupOrder,String codiceTavolo, metododiCallbackAllSingleOrderConfirmed callback){
        /* Vado a confermare il singleOrder*/
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

                        /* Prendo tutti i singleOrder di questo tavolo con questo codiceGrouOrder*/
                        df.collection("SINGLE ORDERS")
                                .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
                                .whereEqualTo("codiceTavolo", codiceTavolo)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //flag per capire se è entrato nell'if
                                            boolean isInIf=false;
                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                SingleOrder singleOrderControl = documentSnapshot.toObject(SingleOrder.class);

                                                /*Per ogni singleOrder se non è stato confermato:
                                                    1.Passa al metodo di callback il valore falso che indica che mancano ancora degli ordini da confermare
                                                    2. Setto il flag a true che mi indica che sono entrato nell'if
                                                    3. Esco dal ciclo per ridurre il costo computazionale
                                                 */
                                                if (!singleOrderControl.isSingleOrderConfirmed()) {
                                                    callback.onCallback(false);
                                                    isInIf = true;
                                                    break;
                                                }
                                            }
                                            /* Se non è entrato nell'if allora vuol dire che sono stati tutti confermati:
                                                1: Passa al metodo di callback il valore true che indica che tutti gli ordini sono stati confermati
                                                2: Libera il tavolo
                                                3: Invia l'ordinazione complessiva alla cucina
                                             */
                                            if(!isInIf){
                                                callback.onCallback(true);
                                                setTableFreeOnDB(codiceTavolo);

                                            }
                                        }
                                    }
                                });
                    }
                });
    }


    /**
     * Metodo che imposta il tavolo a libero
     * @param codiceTavolo
     */
    private void setTableFreeOnDB(String codiceTavolo) {
        df.collection("TAVOLI").whereEqualTo("codiceTavolo", codiceTavolo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                df.collection("TAVOLI").
                                        document(documentSnapshot.getId())
                                        .update("tableFree", true);
                            }
                        }

                    }
                });
    }


    /**
     * Metodo che invia l'ordinazione di gruppo alla cucina, cioè scrive il file contenente tutti i piatti ordinati con username e quantità) nel DB
     * @param codiceGroupOrder
     * @param codiceTavolo
     */
    public void sendOrderToTheKitchen(String codiceGroupOrder, String codiceTavolo){
        /*
        1. Seleziono tutti gli So-Piatto che hanno stesso codice groupOrder e stesso codice tavolo
        2. Salvo in una lista di So-piatto il risultato della query
        3. Chiamo il metodo  che mi genera in un file la lista precedente
        4. Carico sul DB il file generato
         */
        ArrayList<SoPlate> listaSoPiatto = new ArrayList<>();
        df.collection("SO-PIATTO")
                .whereEqualTo("codiceTavolo", codiceTavolo)
                .whereEqualTo("codiceGroupOrder", codiceGroupOrder)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot qds : task.getResult()){
                                listaSoPiatto.add(qds.toObject(SoPlate.class));
                            }

                            FileOrderManager fom = new FileOrderManager();
                            String FILE_NAME = codiceTavolo +"_"+ codiceGroupOrder+".txt" ;//Creo il nome del file
                            String orderToSend= fom.saveGroupOrderForKitchen(listaSoPiatto);    //Ottengo la stringa corrispondente alla lista dei piatti ordinati da tutto il gruppo
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Ordini");
                            storageReference.child(FILE_NAME).putBytes(orderToSend.getBytes()); //scrivo su db storage
                        }
                    }
                });
    }

    public void deleteAllDataOfUser(String codiceTavolo, String codiceGroupOrder, String codiceSingleOrder, String username){
                df.collection("SO-PIATTO")
                .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
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
                                df.collection("SINGLE ORDERS").document(documentSnapshot.getId()).delete();
                            }

                        }
                    }
                });

    }

    /**Interfaccia che permette di chiamare il metodo di Callback**/
    public interface metododiCallback{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(String codiceSingleOrderCheMiServe,String codiceGroupOrder);
    }

    /**Interfaccia che permette di chiamare il metodo di Callback AllSingleOrderConfirmed**/
    public interface metododiCallbackAllSingleOrderConfirmed{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(boolean areAllSingleOrderConfirmed);
    }

}
