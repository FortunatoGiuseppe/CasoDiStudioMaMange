package com.example.casodistudiomamange.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
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

/** Fragment che si occupa della visulizzazione dell'ordine personale dell'utente **/
public class SingleOrderFragment extends Fragment {

    private static final String FILE_NAME = "lastOrder.txt";
    private RecyclerView recyclerView_plates;
    private Adapter_Plates_Ordered adapter_plates;
    private TextView username_tv;
    private FirebaseFirestore db;
    private ArrayList<SoPlate> soPlate;
    private boolean wantsLastOrder=false;   //variabile che serve a determinare se l'utente vuole vedere il single order caricato dal file oppure quello fatto al momento
    private boolean isSingleOrderEmpty=true; //variabile che serve a capire se il isingle order che si vuole confermare è vuoto o pieno
    private boolean canShowSuggerim;  //variabile che serve a capire se il suggerimento di caricare l'ultimo ordine può essere mostrato o meno

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

        /** imposto titolo top bar**/
        ((MaMangeNavigationActivity)getContext()).topBarTitle.setText(R.string.SingleOrder);


        /** Prossime righe utili solo per cambiare colore e dimensione a codice SO**/
        username_tv = v.findViewById(R.id.usernameTextView);
        String ordinazione = getResources().getString(R.string.ordinazione);
        String usernameInserito = ((MaMangeNavigationActivity) getActivity()).username;
        String intestazioneDaStampare=ordinazione + " " + usernameInserito;
        Spannable spannableIntestazione = new SpannableString(intestazioneDaStampare);
        spannableIntestazione.setSpan(new ForegroundColorSpan(Color.parseColor("#8C7C7C")), intestazioneDaStampare.length()-3, intestazioneDaStampare.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableIntestazione.setSpan(new AbsoluteSizeSpan(40),intestazioneDaStampare.length()-3, intestazioneDaStampare.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        username_tv.setText(spannableIntestazione, TextView.BufferType.SPANNABLE);


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

        /*
        Se tasto inserisci ultimo ordine è abilitato posso mostrare il suggerimento perché l'utente non ha ancora usufruito della funzionalità
        altrimenti non può farlo.
         */
        canShowSuggerim = ((MaMangeNavigationActivity) getContext()).lastOrderItem.isEnabled();

        caricaOrdinazione(v);

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
                            //Se single order è vuoto avviso l'utente che non può confermare
                            Toast.makeText(getContext(), getResources().getString(R.string.nessunPiatto), Toast.LENGTH_SHORT).show();
                        }else {

                            /*confermo l'ordinazione*/
                            ((MaMangeNavigationActivity) getActivity()).dbc.setSingleOrderConfirmed(codiceSingleOrder, codiceGroupOrder, codiceTavolo, new DatabaseController.metododiCallbackAllSingleOrderConfirmed() {
                                @Override
                                public void onCallback(boolean areAllSingleOrderConfirmed) {
                                    if (areAllSingleOrderConfirmed) { //SE TUTTI I SINGLE ORDER SONO COMFERMATI MANDO L'ORDINE TOTALE ALLA CUCINA
                                        ((MaMangeNavigationActivity) getActivity()).dbc.sendOrderToTheKitchen(codiceGroupOrder, codiceTavolo);
                                    }
                                }
                            });

                            //pulisco shared delle quantità
                            ((MaMangeNavigationActivity)getContext()).clearSharedPreferencesQuantities();

                            // Avvio l'activity di scelta tra quiz e condivisione dell'ordine
                            Intent intent = new Intent(getActivity(), ConfirmActivity.class);
                            intent.putExtra("UsernameInserito",usernameInserito);
                            startActivity(intent);
                        }
                    }
                });
                richiestaSicuro.setNegativeButton((getResources().getString(R.string.annulla)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //non fare niente
                    }
                });
                AlertDialog dialog = richiestaSicuro.create();
                dialog.show();
            }
        });
        return v;
}

    /**
     * Metodo che permette di caricare la singola ordinazione dell'utente, cioè la lista dei piatti ordinati e loro quantità
     * @param v è la view nella quale è compresa la textView contenente il suggerimento di caricare ultimo ordine
     * */
    private void caricaOrdinazione(View v) {

        /*Carico tutte i piatti ordinati già presenti*/
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
                        isSingleOrderEmpty=false; //se trovo piatti lo imposto a falso, che indica che contiene piatti
                    }
                    ((MaMangeNavigationActivity) getActivity()).showBadge(soPlate.size());

                    /* Se l'utente vuole caricare l'ultimo ordine e non aveva aggiunto piatti allora può farlo, quindi carico piatti salvati */
                    if(wantsLastOrder && isSingleOrderEmpty){

                        //carico l'array globale plates con i nomi dei piatti letti dal file
                        FileOrderManager fileOrderManager= new FileOrderManager();
                        fileOrderManager.loadPlateLastOrder((MaMangeNavigationActivity) getActivity(), FILE_NAME,soPlate);

                        //Se ho letto dei piatti allora devo stamparli, altrimenti avviso che non ci sono piatti
                        if(soPlate.size()>0){
                            //Se ho caricato l'ultimo ordine salvato allora devo aggiungere nello shared preferences i piatti letti
                            for (int i = 0; i < soPlate.size(); i++) {
                                ((MaMangeNavigationActivity) getContext()).saveDataSharedPreferences(soPlate.get(i).getNomePiatto(), (int) soPlate.get(i).getQuantita());
                            }

                            ((MaMangeNavigationActivity) getActivity()).showBadge(soPlate.size()); //aggiorno quantità del badge
                            isSingleOrderEmpty=false;   //ho aggiunto piatti quindi single order non è più vuoto

                            AlertDialog.Builder builderCaricato = new AlertDialog.Builder(getActivity());
                            builderCaricato.setTitle(getResources().getString(R.string.ordineCaricato));
                            builderCaricato.setMessage(" ");
                            AlertDialog dialogCaricato = builderCaricato.create();
                            dialogCaricato.show();
                        }else{
                            AlertDialog.Builder builderNonCaricato = new AlertDialog.Builder(getActivity());
                            builderNonCaricato.setTitle(getResources().getString(R.string.ordineNonCaricato));
                            builderNonCaricato.setMessage(" ");
                            AlertDialog dialogNonCaricato = builderNonCaricato.create();
                            dialogNonCaricato.show();
                        }



                    }else if (!isSingleOrderEmpty && wantsLastOrder){
                            //se vuole caricare ordine ma ha già aggiunto piatti allora devo comunicare che non può farlo
                            Toast.makeText(getContext(), getResources().getString(R.string.lastOrderCaricamento), Toast.LENGTH_SHORT).show();
                            // lo rimetto a enable perché se toglie i piatti già aggiunti allora può caricare
                            ((MaMangeNavigationActivity) getContext()).lastOrderItem.setEnabled(true);
                            ((MaMangeNavigationActivity) getContext()).lastOrderItem.setIcon(R.drawable.ic_ordine_ultimo);
                            wantsLastOrder=false;
                        }
                    else if (canShowSuggerim && isSingleOrderEmpty){

                        //imposto suggerimento per caricare ordine vecchio
                        ImageSpan is = new ImageSpan(((MaMangeNavigationActivity) getContext()), R.drawable.ic_menu);
                        SpannableString spannableText= new SpannableString(getResources().getString(R.string.ispirazioneLastOrder));
                        int startIndex= getResources().getString(R.string.ispirazioneLastOrder).indexOf("_");
                        spannableText.setSpan(is, startIndex, startIndex+1, 0);
                        TextView suggerimento= v.findViewById(R.id.ispirazioneTW);
                        suggerimento.setText(spannableText);
                        suggerimento.setVisibility(View.VISIBLE);
                    }
                    adapter_plates.notifyDataSetChanged();
                }
            }
        });
    }
}
