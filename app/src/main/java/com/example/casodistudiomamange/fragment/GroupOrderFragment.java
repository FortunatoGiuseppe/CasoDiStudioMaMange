package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_Profile;
import com.example.casodistudiomamange.model.Profile;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        listadiLista = new ArrayList<ArrayList<SoPlate>>();
        adapter_profile = new Adapter_Profile(profileList);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_order,null);
        getActivity().setTitle("Group Order");

        swipeRefreshLayout = v.findViewById(R.id.SwipeRefreshLayout);

        recyclerView = v.findViewById(R.id.recyclerGroupOrder);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter_profile);

        leggiUsername(new metododiCallbackListaProfili() {
            @Override
            public void onCallback(List<Profile> profili) {
                leggiOrdinazioni(profili, new metododiCallbackListadiListe() {
                    @Override
                    public void onCallback(ArrayList<ArrayList<SoPlate>> listadiListeCallBack) {
                        for(int i= 0; i<profili.size(); i++){
                            listadiListeCallBack.size();
                            profili.get(i).setSoPlates(listadiListeCallBack.get(i));
                            adapter_profile.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadFragment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    /*metodo che ricarica questa fragment*/
    private void reloadFragment(){
        Fragment fragment=new GroupOrderFragment();
        FragmentManager manager = this.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
    /*Metodo che serve a leggere ogni username del GroupOrder corrente ed ad inserirlo in una lista di profili*/
    private void leggiUsername(metododiCallbackListaProfili profilicallback){

        String groupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;
        ArrayList<SoPlate> listaUtentiDelGroupOrder = new ArrayList<>(); //lista che conterrà i document di soplate letti dal db
        //Query 1: dobbiamo selezionare tutti gli utenti di quel group order
        //Query 2: Per ogni utente dobbiamo selezionare tutti gli so-plate associati a lui che ha ordinato

        db.collection("SO-PIATTO")
                .whereEqualTo("codiceTavolo", codiceTavolo)
                .whereEqualTo("codiceGroupOrder",groupOrder).orderBy("username", Query.Direction.ASCENDING)
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
                            profilicallback.onCallback(profileList);
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
                                profilicallback.onCallback(profileList);
                                adapter_profile.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    /*Metodo che serve a leggere ogni SoPlate del GroupOrder corrente ed ad inserire
    * ogni lista di SoPlate di un utente in una lista di SoPlate generica di tutti gli utenti*/
    private void leggiOrdinazioni(List<Profile> profileList, metododiCallbackListadiListe listadiListeCallBack){
        String groupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;
        ArrayList<SoPlate>listaTuttiSO = new ArrayList<>();

        db.collection("SO-PIATTO")
            .whereEqualTo("codiceGroupOrder",groupOrder)
            .whereEqualTo("codiceTavolo",codiceTavolo)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            listaTuttiSO.add(queryDocumentSnapshot.toObject(SoPlate.class));
                        }

                        for(int j = 0; j< profileList.size(); j++){
                            ArrayList<SoPlate> listaordiniutente = new ArrayList<>();
                            for(int i= 0; i< listaTuttiSO.size(); i++){
                                if(listaTuttiSO.get(i).getUsername().equals(profileList.get(j).getNomeProfilo())){
                                    listaordiniutente.add(listaTuttiSO.get(i));
                                }
                            }
                            listadiLista.add(listaordiniutente);
                        }
                        listadiListeCallBack.onCallback(listadiLista);
                    }
                }
            });
        }
    }

    /*Interfaccia che permette di chiamare il metodo di Callback*/
    interface metododiCallbackListaProfili{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(List<Profile> profili);
    }

    /*Interfaccia che permette di chiamare il metodo di Callback*/
    interface metododiCallbackListadiListe{
        //metodo che permette di utilizzare il codiceSingleOrder e codiceGroupOrder letto dal db
        void onCallback(ArrayList<ArrayList<SoPlate>> listadiListeCallBack);
    }


