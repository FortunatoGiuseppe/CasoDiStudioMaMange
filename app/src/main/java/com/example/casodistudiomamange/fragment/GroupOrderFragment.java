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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class GroupOrderFragment extends Fragment {

    RecyclerView recyclerView;
    List<Profile> profileList;
    Adapter_Profile adapter_profile;
    Adapter_Plates_Ordered adapter_plates_ordered;
    FirebaseFirestore ffdb;
    ArrayList<Plate> plates;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ffdb = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        plates = new ArrayList<>();
        adapter_plates_ordered = new Adapter_Plates_Ordered(getContext(),plates);

        adapter_profile = new Adapter_Profile(profileList,getContext(),adapter_plates_ordered);
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


        caricaProfili();

        return v;
    }

    private void caricaProfili(){
        String groupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;


        ArrayList<SoPlate> listaUtentiDelGroupOrder = new ArrayList<SoPlate>();
    private void caricaProfileEdOrdini(){

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
                        adapter_profile.notifyDataSetChanged();
                    }

                }

            }
        });

    }

    /*private void caricaOrdinazione(String username,String codiceGroupOrder, String codiceSingleOrder, String codiceTavolo) {


        ArrayList<SoPlate> soPlate = new ArrayList<SoPlate>();
        ArrayList<SoPlate> soPlate = new ArrayList<>();
        soPlate.add(new SoPlate(codiceSingleOrder,"EDAMAME",2,codiceGroupOrder,codiceTavolo));
        // public SoPlate(String codiceSingleOrder, String nomePiatto, long quantita, String codiceGroupOrder, String codiceTavolo) {

        ffdb.collection("SO-PIATTO")
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

    }*/

    }
}
