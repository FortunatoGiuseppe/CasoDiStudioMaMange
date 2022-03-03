package com.example.casodistudiomamange.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.adapter.Adapter_plates;
import com.example.casodistudiomamange.model.Plate;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView_plates;
    private ArrayList<Plate> plates;    //lista che conterrà i nomi delle categorie
    private Adapter_plates adapter_plates;
    private FirebaseFirestore db;
    String CategoryKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        CategoryKey = bundle.getString("CategoryKey");

        db = FirebaseFirestore.getInstance();
        plates= new ArrayList<>();
        adapter_plates = new Adapter_plates(getContext(), plates);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category,null);
        getActivity().setTitle("Categorie");


        recyclerView_plates = v.findViewById(R.id.recycleview_plates);
        recyclerView_plates.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1 , LinearLayoutManager.VERTICAL, false);
        recyclerView_plates.setLayoutManager(gridLayoutManager);

        recyclerView_plates.setAdapter(adapter_plates);

        caricaPiatti();

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void caricaPiatti() {

        db.collection("PIATTI")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestone error", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                if(dc.getDocument().get("categoria")!=null && dc.getDocument().get("categoria").equals(CategoryKey)) {
                                    plates.add(dc.getDocument().toObject(Plate.class));
                                }
                            }
                            adapter_plates.notifyDataSetChanged();
                        }
                    }
                });
    }
}