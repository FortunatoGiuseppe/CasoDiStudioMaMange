package com.example.casodistudiomamange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.casodistudiomamange.R;
import androidx.appcompat.app.ActionBar;

import com.example.casodistudiomamange.adapter.Adapter_category;
import com.example.casodistudiomamange.model.Category;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> categories;    //lista che conterrà i nomi delle categorie
    private Adapter_category adapter_category;
    DatabaseReference dataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        recyclerView = findViewById(R.id.recycleview);  //riferimento alla lista che conterrà le categorie lette
        //Ottiene riferimento al db per le categorie
        dataref= FirebaseDatabase.getInstance().getReference().child("Categorie");
        categories = new ArrayList<String>();
        adapter_category = new Adapter_category(this, categories);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Metti nome ristorante");
        actionBar.setDisplayHomeAsUpEnabled(true);


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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2 , LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter_category);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
