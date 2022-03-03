package com.example.casodistudiomamange.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.model.Profile;

import java.util.List;

public class Adapter_Profile extends RecyclerView.Adapter<Adapter_Profile.myViewHolder> {

    private List<Profile> profileList;

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
        holder.testoProva.setText(profile.getTestoProva());

        boolean isExpandible = profileList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandible ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        private TextView nomeProfilo, testoProva;
        CardView cardView;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProfilo = itemView.findViewById(R.id.profile);
            testoProva = itemView.findViewById(R.id.testoProva);
            cardView = itemView.findViewById(R.id.cardViewProfile);
            linearLayout = itemView.findViewById(R.id.linearProfile);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Profile profile = profileList.get(getAdapterPosition());
                    profile.setExpandable(!profile.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });
        }
    }
}
