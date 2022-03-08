package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.adapter.Adapter_Profile;
import com.example.casodistudiomamange.model.Plate;
import com.example.casodistudiomamange.model.Profile;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupOrderFragment extends Fragment {

    RecyclerView recyclerView;
    List<Profile> profileList;
    Adapter_Profile adapter_profile;
    FirebaseFirestore db;
    ArrayList<ArrayList<SoPlate>>listadiLista;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        listadiLista = new ArrayList<>();
        adapter_profile = new Adapter_Profile(profileList);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_order,null);
        getActivity().setTitle("Group Order");

        recyclerView = v.findViewById(R.id.recyclerGroupOrder);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter_profile);

        leggiUsername();


        return v;
    }

    private void leggiUsername(){
        String groupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;


        ArrayList<SoPlate> listaUtentiDelGroupOrder = new ArrayList<>(); //lista che conterrà i document di soplate letti dal db
        //Query 1: dobbiamo selezionare tutti gli utenti di quel group order
        //Query 2: Per ogni utente dobbiamo selezionare tutti gli so-plate associati a lui che ha ordinato


        db.collection("SO-PIATTO")
                .whereEqualTo("codiceGroupOrder",groupOrder)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (listaUtentiDelGroupOrder.size() == 0) { //se è vuota aggiungi quello appena letto
                            listaUtentiDelGroupOrder.add(documentSnapshot.toObject(SoPlate.class));
                            Profile profile = new Profile();
                            profile.setNomeProfilo(documentSnapshot.toObject(SoPlate.class).getUsername());
                            profileList.add(profile);
                            leggiOrdinazioni(profileList);

                            adapter_profile.notifyDataSetChanged();
                        } else {
                            //variabile che serve a capire se esiste già nella lista un username
                            boolean trovato = false;
                            //per tutti gli elementi della lista vedi se esiste un document di soplate che ha come username
                            // quello del document che si ha appena letto
                            for (int j = 0; j < listaUtentiDelGroupOrder.size(); j++) {
                                if (listaUtentiDelGroupOrder.get(j).getUsername().equals(documentSnapshot.toObject(SoPlate.class).getUsername())) {
                                    trovato = true;
                                }
                            }
                            //se non esiste un suddetto documento allora quello appena letto va aggiunto alla lista
                            if (!trovato) {
                                listaUtentiDelGroupOrder.add(documentSnapshot.toObject(SoPlate.class));
                                Profile profile = new Profile();
                                profile.setNomeProfilo(documentSnapshot.toObject(SoPlate.class).getUsername());
                                profileList.add(profile);
                                leggiOrdinazioni(profileList);
                                adapter_profile.notifyDataSetChanged();
                            }
                        }
                    }
                }

            }
        });

    }

    private void leggiOrdinazioni(List<Profile> profileList){
        ArrayList<SoPlate>listaDiAppoggio = new ArrayList<>();
        for(int i=0; i<profileList.size();i++){
            db.collection("SO-PIATTO")
                    .whereEqualTo("username",profileList.get(i).getNomeProfilo())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                                    listaDiAppoggio.add(queryDocumentSnapshot.toObject(SoPlate.class));
                                }
                                listadiLista.add(listaDiAppoggio);
                                associaTutto(profileList, listadiLista);
                            }

                        }
                    });
        }
    }

    private void associaTutto(List<Profile> profileList, ArrayList<ArrayList<SoPlate>>listadiLista){
        for(int i=0; i<profileList.size(); i++){
            profileList.get(i).setSoPlates(listadiLista.get(i));
            adapter_profile.notifyDataSetChanged();
        }
    }

    /*
    private void caricaProfili(){
        String groupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;


        ArrayList<SoPlate> listaUtentiDelGroupOrder = new ArrayList<SoPlate>();
        //Query 1: dobbiamo selezionare tutti gli utenti di quel group order
        //Query 2: Per ogni utente dobbiamo selezionare tutti gli so-plate associati a lui che ha ordinato


        ffdb.collection("SO-PIATTO").whereEqualTo("codiceGroupOrder",groupOrder)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        listaUtentiDelGroupOrder.add(documentSnapshot.toObject(SoPlate.class));

                    }
                    for (int i = 0; i < listaUtentiDelGroupOrder.size(); i++){
                        Profile profile = new Profile();
                        profile.setNomeProfilo(listaUtentiDelGroupOrder.get(i).getUsername());
                        profileList.add(profile);
                        caricaPiatti(profile);
                        adapter_profile.notifyDataSetChanged();
                    }

                }

            }
        });
    }

    private void caricaPiatti(Profile profile){
        ArrayList<SoPlate> soPlate = new ArrayList<SoPlate>();
        ffdb.collection("SO-PIATTO")
                .whereEqualTo("username", profile.getNomeProfilo())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        soPlate.add(documentSnapshot.toObject(SoPlate.class));
                    }
                    for(int i =0; i<soPlate.size();i++){
                        ffdb.collection("PIATTI").whereEqualTo("nome",soPlate.get(i).getNomePiatto()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                if (task2.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task2.getResult()) {
                                        plates.add(doc.toObject(Plate.class));
                                        adapter_plates_ordered.notifyDataSetChanged();

                                    }
                                }
                            }
                        });
                    }

                }
            }
        });
    }
*/
}
