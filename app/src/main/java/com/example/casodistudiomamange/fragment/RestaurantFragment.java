package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.adapter.Adapter_category;
import com.example.casodistudiomamange.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<String> categories;    //lista che conterrà i nomi delle categorie
    private Adapter_category adapter_category;
    DatabaseReference dataref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_restaurant,null);
        getActivity().setTitle("Menu");
        recyclerView = v.findViewById(R.id.recycleview);
        adapter_category = new Adapter_category(getContext(), categories);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2 , LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter_category);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataref= FirebaseDatabase.getInstance().getReference().child("Categorie");
        categories = new ArrayList<String>();


        dataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //lettura delle singole categorie dal db (snapshot)
                // dataSnapshot è la porzione di DB letta a partire da dataref
                //snapshot è un singolo figlio di dataSnapshot
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Category cat= snapshot.getValue(Category.class);
                    categories.add(cat.img);
                }
                adapter_category.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }





}
