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
import com.example.casodistudiomamange.model.SoPlate;

import java.util.List;

public class Adapter_Profile extends RecyclerView.Adapter<Adapter_Profile.myViewHolder> {

    private List<Profile> profileList;
    private List<SoPlate> soPlates;

    public Adapter_Profile(List<Profile> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public Adapter_Profile.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_profile,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Profile.myViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.nomeProfilo.setText(profile.getNomeProfilo());
        boolean isExpandible = profileList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandible ? View.VISIBLE : View.GONE);

        Adapter_Profile_Ordered_GroupOrder adapter = new Adapter_Profile_Ordered_GroupOrder(soPlates);
        holder.recyclerViewPlate.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewPlate.setHasFixedSize(true);
        holder.recyclerViewPlate.setAdapter(adapter);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile.setExpandable(!profile.isExpandable());
                soPlates = profile.getSoPlates();
                notifyItemChanged(holder.getBindingAdapterPosition());
            }
        });

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

        }
    }
}
