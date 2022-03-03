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
import com.example.casodistudiomamange.model.Profile;

import java.util.List;

public class Adapter_Profile extends RecyclerView.Adapter<Adapter_Profile.myViewHolder> {

    private List<Profile> profileList;
    private Context context;
    private Adapter_Plates_Ordered adapter_plates_ordered;
    RecyclerView recyclerView_plates;

    public Adapter_Profile(List<Profile> profileList, Context context,Adapter_Plates_Ordered adapter_plates_ordered) {

        this.profileList = profileList;
        this.context = context;
        this.adapter_plates_ordered = adapter_plates_ordered;
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
                    profile.setExpandable(!profile.isExpandable());
                    notifyItemChanged(getBindingAdapterPosition());

                }
            });
        }
    }
}
