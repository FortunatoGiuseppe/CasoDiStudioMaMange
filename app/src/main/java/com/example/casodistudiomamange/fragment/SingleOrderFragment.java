package com.example.casodistudiomamange.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.ConfirmActivity;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.model.DatabaseController;
import com.example.casodistudiomamange.model.FileOrderManager;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SingleOrderFragment extends Fragment {

    private static final String FILE_NAME = "lastOrder.txt";
    private RecyclerView recyclerView_plates;
    private Adapter_Plates_Ordered adapter_plates;
    private TextView username_tv;
    private FirebaseFirestore db;
    private ArrayList<SoPlate> soPlate;
    private boolean wantsLastOrder=false;   //variabile che serve a determinare se l'utente vuole vedere il single order caricato dal file oppure quello fatto al momento
    private boolean isSingleOrderEmpty=true; //variabile che serve a capire se il isingle order che si vuole confermare è vuoto o pieno

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        soPlate = new ArrayList<>();
        adapter_plates = new Adapter_Plates_Ordered(getContext(), soPlate);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_single_order, null);
        getActivity().setTitle("Single Order");

        username_tv = v.findViewById(R.id.usernameTextView);
        String ordinazione = getResources().getString(R.string.ordinazione);
        String usernameInserito = ((MaMangeNavigationActivity) getActivity()).username;
        username_tv.setText(ordinazione + " " + usernameInserito);

        recyclerView_plates = v.findViewById(R.id.recyclerViewSingleOrderPlates);
        recyclerView_plates.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView_plates.setLayoutManager(gridLayoutManager);
        recyclerView_plates.setAdapter(adapter_plates);


        //Vedo se sono arrivato qui da una chiamata dal tasto inserisci ultimo ordine
        if (getArguments().getString("chiamante").equals("lastOrder") ){
            //se è vero allora voglio caricare l'ultimo ordine
            wantsLastOrder=true;
        }

        caricaOrdinazione();

        Button conferma= v.findViewById(R.id.confirm);
        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder richiestaSicuro = new AlertDialog.Builder(getActivity());
                richiestaSicuro.setTitle(getResources().getString(R.string.attenzione));
                richiestaSicuro.setMessage(getResources().getString(R.string.msgAttenzione));
                richiestaSicuro.setPositiveButton((getResources().getString(R.string.Confirm)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //crea file contenente i piatti ordinati (salvataggio ultimo ordine)
                        //IL FILE CONTIENE NOME PIATTO E QUANTITÀ
                        FileOrderManager fileOrderManager = new FileOrderManager();
                        fileOrderManager.savePlatesLastOrder(soPlate, getContext(), FILE_NAME);

                        String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
                        String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
                        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;

                        if(isSingleOrderEmpty) {
                            Toast.makeText(getContext(), getResources().getString(R.string.nessunPiatto), Toast.LENGTH_SHORT).show();
                        }else {

                            /*confermo l'ordinazione*/
                            ((MaMangeNavigationActivity) getActivity()).dbc.setSingleOrderConfirmed(codiceSingleOrder, codiceGroupOrder, codiceTavolo, new DatabaseController.metododiCallbackAllSingleOrderConfirmed() {
                                @Override
                                public void onCallback(boolean areAllSingleOrderConfirmed) {
                                    if (areAllSingleOrderConfirmed) { //SE TUTTI I SINGLE ORDER SONO COMFERMATI MANDO L'ORDINE TOTALE ALLA CUCINA
                                        ((MaMangeNavigationActivity) getActivity()).dbc.sendOrderToTheKitchen(codiceSingleOrder, codiceGroupOrder, codiceTavolo, ((MaMangeNavigationActivity) getContext()));
                                    }
                                }
                            });

                            /*Avviso l'utente che l'ordinazione è stata confermata*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getResources().getString(R.string.ordineSalvato));
                            builder.setMessage(" ");
                            AlertDialog dialogConfermato = builder.create();
                            dialogConfermato.show();

                            //pulisco shared delle quantità
                            clearSharedPreferencesQuantities();

                            /*Avvio l'activity di scelta tra quiz e condivisione dell'ordine*/
                            Intent intent = new Intent(getActivity(), ConfirmActivity.class);
                            intent.putExtra("UsernameInserito",usernameInserito);
                            startActivity(intent);
                        }
                    }
                });
                AlertDialog dialog = richiestaSicuro.create();
                dialog.show();
            }
        });


        return v;

    }

    /*Metodo che permette di caricare la singola ordinazione dell'utente*/
    private void caricaOrdinazione() {

        //se l'utente vuole caricare l'ultimo ordine fatto
        if(wantsLastOrder){

            //carico l'array globale plates con i nomi dei piatti letti dal file
            //NOTA: NON VIENE LETTA LA QUANTITÀ PERCHÈ IN OGGETTI DI PLATES NON È POSSIBILE INSERIRLA
            //occorrerebbe stampare la lista degli soplate piuttosto che la lista di plates, modifica che impatterebbe anche su singlePlates corrente e non letto dal file
            FileOrderManager fileOrderManager= new FileOrderManager();
            fileOrderManager.loadPlateLastOrder((MaMangeNavigationActivity) getActivity(), FILE_NAME,soPlate);
            isSingleOrderEmpty=false;   //se trovo piatti lo imposto a falso, che indica che contiene piatti

            //devo stampare nelle view ciò che leggo dal file
            adapter_plates.notifyDataSetChanged();

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getResources().getString(R.string.ordineCaricato));
            builder.setMessage(" ");
            AlertDialog dialog = builder.create();
            dialog.show();

        }else{

            String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
            String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
            String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;
            String username = ((MaMangeNavigationActivity) getActivity()).username;


            db.collection("SO-PIATTO")
                    .whereEqualTo("codiceSingleOrder",codiceSingleOrder)
                    .whereEqualTo("codiceGroupOrder",codiceGroupOrder)
                    .whereEqualTo("codiceTavolo",codiceTavolo)
                    .whereEqualTo("username",username)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            soPlate.add(documentSnapshot.toObject(SoPlate.class));
                            adapter_plates.notifyDataSetChanged();
                            isSingleOrderEmpty=false; //se trovo piatti lo imposto a falso, che indica che contiene piatti
                            //non posso mettere questa istruzione fuori perché me la farebbe anche se trova 0 piatti,
                            // perché risultato vuoto è comunque un task con successo
                        }

                    }
                }
            });
        }
    }

    private void clearSharedPreferencesQuantities() {
        SharedPreferences sharedPreferences =  (getContext()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
