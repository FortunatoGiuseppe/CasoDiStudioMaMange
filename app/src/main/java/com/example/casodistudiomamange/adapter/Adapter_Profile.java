package com.example.casodistudiomamange.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
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
import java.util.Objects;

public class Adapter_Profile extends RecyclerView.Adapter<Adapter_Profile.myViewHolder> {

    private List<Profile> profileList;
    private Context context;
    private Adapter_Plates_Ordered adapter_plates_ordered;
    RecyclerView recyclerView_plates;
    FirebaseFirestore ffdb;
    ArrayList<Plate> plates;

    public Adapter_Profile(List<Profile> profileList, Context context,Adapter_Plates_Ordered adapter_plates_ordered) {

        this.profileList = profileList;
        this.context = context;
        this.adapter_plates_ordered = adapter_plates_ordered;
        ffdb = FirebaseFirestore.getInstance();
        plates = new ArrayList<>();
    }



    @NonNull
    @Override
    public Adapter_Profile.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_profile,parent,false);
        recyclerView_plates = view.findViewById(R.id.recycleViewPlateGroupOrder);
        recyclerView_plates.setHasFixedSize(true);
        LinearLayoutManager expandableManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView_plates.setLayoutManager(expandableManager);
        recyclerView_plates.setAdapter(adapter_plates_ordered);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Profile.myViewHolder holder, int position) {

        Profile profile = profileList.get(position);
        holder.nomeProfilo.setText(profile.getNomeProfilo());

        holder.recyclerViewPlate.setAdapter(adapter_plates_ordered);

        boolean isExpandible = profileList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandible ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView nomeProfilo;
        CardView cardView;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;
        RecyclerView recyclerViewPlate;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProfilo = itemView.findViewById(R.id.profile);
            cardView = itemView.findViewById(R.id.cardViewProfile);
            linearLayout = itemView.findViewById(R.id.linearProfile);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            recyclerViewPlate = itemView.findViewById(R.id.recycleViewPlateGroupOrder);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Profile profile = profileList.get(getBindingAdapterPosition());
                    caricaPiatti(profile);  //passo profilo corrispondente all'username su cui ho cliccato nella lista
                    profile.setExpandable(!profile.isExpandable());
                    notifyItemChanged(getBindingAdapterPosition());
                    //fin qui ok
                }
            });
        }

        private void caricaPiatti(Profile profile){
            ArrayList<SoPlate> soPlate = new ArrayList<>();
            //vedo quali sono i piatti aggiunti all'ordine da quell'utente
            ffdb.collection("SO-PIATTO")
                    .whereEqualTo("username", profile.getNomeProfilo())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            soPlate.add(documentSnapshot.toObject(SoPlate.class));  //aggiungo i piatti alla lista FUNZIONA
                        }
                        for(int i =0; i<soPlate.size();i++){
                            //vedo quali sono i piatti con nome uguale a quello del piatto aggiunto all'ordine
                            ffdb.collection("PIATTI")
                                    .whereEqualTo("nome",soPlate.get(i).getNomePiatto())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                    if (task2.isSuccessful()) {
                                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task2.getResult())) {
                                            plates.add(doc.toObject(Plate.class)); //aggiungo i piatti alla lista  FUNZIONA
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
    }
}
