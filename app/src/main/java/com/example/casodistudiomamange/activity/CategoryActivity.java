package com.example.casodistudiomamange.activity;

import com.example.casodistudiomamange.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.adapter.Adapter_plates;
import com.example.casodistudiomamange.model.Plate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView_plates;
    private List<String> platesName;    //contenitore nomi dei piatti
    private List<String> platesImg;     //contenitore immagini dei piatti
    private List<String> platesDescription; //contenitore descrizioni dei piatti
    private List<String> plateFlag; //flag delle bevande
    private Adapter_plates adapter_plates;
    DatabaseReference dataref_plates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        Intent intent = getIntent();
        String CategoryKey = intent.getStringExtra("CategoryKey");
        String nameConverted= convertToName(CategoryKey);

        recyclerView_plates = findViewById(R.id.recycleview_plates);
        dataref_plates= FirebaseDatabase.getInstance().getReference().child("Mamange").child(nameConverted);
        platesName= new ArrayList<String>();
        platesImg= new ArrayList<String>();
        platesDescription= new ArrayList<String>();
        plateFlag = new ArrayList<String>();
        adapter_plates = new Adapter_plates(this,platesName,platesImg,platesDescription,plateFlag);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(nameConverted);
        actionBar.setDisplayHomeAsUpEnabled(true);


        dataref_plates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            /*dataSnapshot-> quello che legge dal db a partire dall'ultimo child messo a riga 42
             snapshot-> le informazioni di ciascun child letto a partire da dataSnapshotm, quindi un livello più "dentro"

             occorre leggere gli snapshot fino al penultimo, perché l'ultimo sarebbe img della categoria che
             se viene letta da problemi perché non combacia con cioè che si aspetta, cioè Plate*/
                int i=0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if(i== dataSnapshot.getChildrenCount()-1){
                        break;
                    }

                    Plate plate = snapshot.getValue(Plate.class);
                    platesImg.add(plate.img);
                    platesName.add(plate.nome);
                    platesDescription.add(plate.descrizione);
                    plateFlag.add(plate.flag);

                    i++;
                }
                adapter_plates.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1 , LinearLayoutManager.VERTICAL, false);
        recyclerView_plates.setLayoutManager(gridLayoutManager);
        recyclerView_plates.setHasFixedSize(true);
        recyclerView_plates.setAdapter(adapter_plates);

    }

// metodo che consente di convertire la posizione della categoria cliccata nella corrispondente stringa identificativa nel DB
    private String convertToName(String categoryKey) {
        switch (categoryKey) {
            case "0":{
                return "ANTIPASTI";
            }
            case "1":{
                return "BEVANDE";
            }
            case "2":{
                return "FUTOMAKI";
            }
            case "3":{
                return "GUNKAN";
            }
            case "4":{
                return "HOSOMAKI";
            }
            case "5":{
                return "NIGIRI";
            }
            case "6":{
                return "PASTA";
            }
            case "7":{
                return "RISO";
            }
            case "8":{
                return "SASHIMI";
            }
            case "9":{
                return "URAMAKI";
            }
        }
        return "NO";
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

