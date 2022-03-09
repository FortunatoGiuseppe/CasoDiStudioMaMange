package com.example.casodistudiomamange.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.casodistudiomamange.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.model.SoPlate;

import java.util.List;

public class Adapter_Profile_Ordered_GroupOrder extends RecyclerView.Adapter<Adapter_Profile_Ordered_GroupOrder
        .Adapter_Profile_Ordered_GroupOrderViewHolder> {

    private List<SoPlate> soPlateList;

    public Adapter_Profile_Ordered_GroupOrder(List<SoPlate> soPlateList) {
        this.soPlateList = soPlateList;
    }

    @NonNull
    @Override
    public Adapter_Profile_Ordered_GroupOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_plate_ordered_grouporder, parent, false);
        return new Adapter_Profile_Ordered_GroupOrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Profile_Ordered_GroupOrderViewHolder holder, int position) {
        holder.plateName.setText(soPlateList.get(position).getNomePiatto());
        holder.tvCount.setText(Integer.toString((int)soPlateList.get(position).getQuantita()));
    }

    @Override
    public int getItemCount() {
        return soPlateList.size();
    }

    public class Adapter_Profile_Ordered_GroupOrderViewHolder extends RecyclerView.ViewHolder{
        private TextView plateName;
        private TextView tvCount;
        public Adapter_Profile_Ordered_GroupOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            plateName = itemView.findViewById(R.id.textView_NamePlateGO);
            tvCount = itemView.findViewById(R.id.tvCountGO);
        }
    }
}
