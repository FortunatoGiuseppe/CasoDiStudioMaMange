package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.model.Plate;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class SingleOrderFragment extends Fragment {

    private RecyclerView recyclerView_plates;
    private Adapter_Plates_Ordered adapter_plates;
    private ArrayList<Plate> plates;
    private TextView username;
    private FirebaseFirestore db;


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

        caricaOrdinazione();

        return v;

    }


    private void caricaOrdinazione() {



        String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
        String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;
        String username = ((MaMangeNavigationActivity) getActivity()).username;
        ArrayList<SoPlate> soPlate = new ArrayList<SoPlate>();

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
