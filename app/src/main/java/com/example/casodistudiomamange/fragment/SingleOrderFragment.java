package com.example.casodistudiomamange.fragment;

import android.annotation.SuppressLint;
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
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.activity.QRCodeActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.model.Plate;
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
    private ArrayList<Plate> plates;
    private TextView username;
    private FirebaseFirestore db;
    private ArrayList<SoPlate> soPlate = new ArrayList<SoPlate>();




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        plates = new ArrayList<Plate>();
        adapter_plates = new Adapter_Plates_Ordered(getContext(), plates);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_single_order, null);
        getActivity().setTitle("Single Order");

        username = v.findViewById(R.id.usernameTextView);
        String ordinazione = getResources().getString(R.string.ordinazione);
        String usernameInserito = ((MaMangeNavigationActivity) getActivity()).username;
        username.setText(ordinazione + " " + usernameInserito);

        recyclerView_plates = v.findViewById(R.id.recyclerViewSingleOrderPlates);
        recyclerView_plates.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView_plates.setLayoutManager(gridLayoutManager);
        recyclerView_plates.setAdapter(adapter_plates);


        if (getArguments().getString("chiamante").equals("lastOrder") ){
            load(v);
        }

        caricaOrdinazione();

        Button conferma= v.findViewById(R.id.confirm);
        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //crea file contenente i piatti ordinati (salvataggio ultimo ordine)
                //IL FILE CONTIENE NOME PIATTO E QUANTITÀ
                save(v,soPlate);

            }
        });


     /*   Button load= v.findViewById((R.id.load));
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        return v;

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

    //Metodo per caricare i piatti dell'ultimo ordine effettuato
    public void load(View v) {
        FileInputStream fis = null;

        try {
            fis = getContext().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            //stampa alert con piatti salvati
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(sb.toString());
            AlertDialog dialog = builder.create();
            dialog.show();

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



    private void caricaOrdinazione() {

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
                    }
                    for(int i =0; i<soPlate.size();i++){
                        db.collection("PIATTI").whereEqualTo("nome",soPlate.get(i).getNomePiatto()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if (task2.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task2.getResult()) {
                                        plates.add(doc.toObject(Plate.class));
                                        adapter_plates.notifyDataSetChanged();

                                    }
                                }
                            }
                        });
                    }

                }
            }
        });

    }
}
