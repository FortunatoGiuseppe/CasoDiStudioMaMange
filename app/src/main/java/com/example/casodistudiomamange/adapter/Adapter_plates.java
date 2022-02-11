package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_plates extends RecyclerView.Adapter<Adapter_plates.myViewHolder> {

    private Context context;
    private List<String> platesName;
    private List<String> platesImg;
    private List<String> platesDescription;

    public  Adapter_plates(Context context, List<String> platesName,List<String> platesImg, List<String> platesDescription){
        this.context =context;
        this.platesName = platesName;
        this.platesImg = platesImg;
        this.platesDescription=platesDescription;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_plate,parent,false);
        return  new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        /* attribuisco i valori letti alle textview corrispondenti*/
        holder.textView_plate.setText(platesName.get(position));
        holder.textView_plate_description.setText(platesDescription.get(position));
        Picasso.get().load(platesImg.get(position)).into(holder.imageView_plate);

        /*FARE SELEZIONE DEL PIATTO !!! */

    }

    @Override
    public int getItemCount() {
        return platesName.size();
    }

    public  static  class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView_plate;
        TextView textView_plate;
        TextView textView_plate_description;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_plate = itemView.findViewById(R.id.imageView_plate);
            textView_plate = itemView.findViewById(R.id.textView_plate);
            textView_plate_description= itemView.findViewById(R.id.textView_plate_description);
        }
    }
}
