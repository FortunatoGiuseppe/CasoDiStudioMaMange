package com.example.casodistudiomamange.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.activity.QRCodeActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.model.DatabaseController;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SingleOrderFragment extends Fragment {

    private static final String FILE_NAME = "lastOrder.txt";
    private RecyclerView recyclerView_plates;
    private Adapter_Plates_Ordered adapter_plates;
    private TextView username_tv;
    private FirebaseFirestore db;
    private ArrayList<SoPlate> soPlate;
    private boolean wantsLastOrder=false;   //variabile che serve a determinare se l'utente vuole vedere il single order caricato dal file oppure quello fatto al momento

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(R.string.ordineSalvato));
                        builder.setMessage(" ");
                        AlertDialog dialogConfermato = builder.create();
                        dialogConfermato.show();


                        Fragment fragment= new ConfirmFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, fragment);
                        fragmentTransaction.commit();


                        //crea file contenente i piatti ordinati (salvataggio ultimo ordine)
                        //IL FILE CONTIENE NOME PIATTO E QUANTITÀ
                        save(v,soPlate);

                        //imposta l'attributo singleOrderConfirmed a true
                        String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
                        String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
                        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;

                        ((MaMangeNavigationActivity) getActivity()).dbc.setSingleOrderConfirmed(codiceSingleOrder,codiceGroupOrder,codiceTavolo);

                        //svuoto lo shared preferences
                        clearSharedPreferences();

                        //se tutti gli ordini singoli sono stati confermati allora manda ordine e carica gioco
                        //altrimenti carica pagina di attesa

                        ((MaMangeNavigationActivity) getActivity()).dbc.allSingleOrdersAreConfirmed(codiceGroupOrder, codiceTavolo, new DatabaseController.metododiCallbackAllSingleOrderConfirmed() {
                            @Override
                            public void onCallback(boolean areAllSingleOrderConfirmed) {
                                if(areAllSingleOrderConfirmed){
                                    //invio ordine
                                    ((MaMangeNavigationActivity) getActivity()).dbc.sendOrdersToTheKitchen();
                                    //avviso l'utente
                                    AlertDialog.Builder ordineInviatoCucina = new AlertDialog.Builder(getContext());
                                    ordineInviatoCucina.setTitle(getResources().getString(R.string.inviatoCucina));
                                    ordineInviatoCucina.setMessage(getResources().getString(R.string.inviatoCucinaMsg));
                                    ordineInviatoCucina.setPositiveButton(getResources().getString(R.string.chiudi), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    AlertDialog dialog = ordineInviatoCucina.create();
                                    dialog.show();
                                }else{


                                }
                            }
                        });
                    }
                });
                richiestaSicuro.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = richiestaSicuro.create();
                dialog.show();


            }
        });

        return v;

    }

    private void caricaOrdinazione() {

        //se l'utente vuole caricare l'ultimo ordine fatto
        if(wantsLastOrder){

            //carico l'array globale plates con i nomi dei piatti letti dal file
            //NOTA: NON VIENE LETTA LA QUANTITÀ PERCHÈ IN OGGETTI DI PLATES NON È POSSIBILE INSERIRLA
            //occorrerebbe stampare la lista degli soplate piuttosto che la lista di plates, modifica che impatterebbe anche su singlePlates corrente e non letto dal file
            load();

            //Aggiungi piatti ordinati nel DB


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
                        }

                    }
                }
            });

        }

    }

    //Metodo per salvare i piatti dell'ultimo ordine effettuato
    public void save(View v, ArrayList<SoPlate> soPlateParam) {

        String text="Nessun Piatto Aggiunto";   //Stringa di default se non ci sono piatti
        for(int i=0;i<soPlateParam.size();i++){
            if(i==0){
                text="";    //se ci sono piatti allora pulisco la stringa perchè dovrà contenere la lista dei piatti
            }
            text = text+soPlateParam.get(i).getNomePiatto()+","+soPlateParam.get(i).getQuantita()+"\n";
        }

        FileOutputStream fos = null;

        try {
            fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_PRIVATE);
            fos.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Metodo per caricare i piatti dell'ultimo ordine effettuato, li aggiunge al DB e alla lista dalla quale l'adapter prende i dati per stamparli
    public void load() {

        String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
        String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;
        String username = ((MaMangeNavigationActivity) getActivity()).username;

        FileInputStream fis = null;

        try {
            fis = getContext().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            while ((text = br.readLine()) != null) {
                text=text+("/");    //aggiungo lo slash per identificare la fine della riga
                SoPlate plateOrdered= new SoPlate();
                plateOrdered.setNomePiatto(text.substring(0, text.indexOf(",")));   //seleziono nomepiatto e lo metto nell'oggetto
                plateOrdered.setQuantita(Long.parseLong(text.substring(text.indexOf(",")+1, text.indexOf("/")))); //seleziono quantità

                soPlate.add(plateOrdered);   //aggiungo il piatto appena letto alla lista dei piatti da stampare

                //aggiungi piatto ordinato al db
                //se il piatto non esiste già nell'ordine dell'utente lo aggiungo
                if(!((MaMangeNavigationActivity) getActivity()).dbc.checkIfPlateHasAlreadyBeenOrdered(plateOrdered.getNomePiatto(), codiceSingleOrder, codiceGroupOrder, codiceTavolo, username)){
                    ((MaMangeNavigationActivity) getActivity()).dbc.orderPlate(plateOrdered.getNomePiatto(), codiceSingleOrder, codiceGroupOrder, codiceTavolo, username,plateOrdered.getQuantita());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void clearSharedPreferences() {
        SharedPreferences sharedPreferences =  (getContext()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
