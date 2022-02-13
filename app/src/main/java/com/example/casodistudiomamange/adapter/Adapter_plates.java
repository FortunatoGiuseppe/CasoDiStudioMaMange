package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_plates extends RecyclerView.Adapter<Adapter_plates.myViewHolder> {

    private Context context;
    private List<String> platesName;
    private List<String> platesImg;
    private List<String> platesDescription;
    private List<String> plateFlag;

    public  Adapter_plates(Context context, List<String> platesName,List<String> platesImg, List<String> platesDescription, List<String> plateFlag){
        this.context =context;
        this.platesName = platesName;
        this.platesImg = platesImg;
        this.platesDescription=platesDescription;
        this.plateFlag = plateFlag;
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

        if((plateFlag.get(position) != null) && plateFlag.get(position).equals("1")){
            holder.imageView_plate_flag.setImageResource(R.drawable.ic_baseline_public_24);
            holder.imageView_plate_flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creazione nuovo fragment per visualizzazione dati
                }
            });
        }

       /*FARE SELEZIONE DEL PIATTO !!! */

    }

    @Override
    public int getItemCount() {
        return  platesName.size();
    }

    public  static  class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView_plate;
        TextView textView_plate;
        TextView textView_plate_description;
        ImageView imageView_plate_flag;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_plate = itemView.findViewById(R.id.imageView_plate);
            textView_plate = itemView.findViewById(R.id.textView_plate);
            textView_plate_description= itemView.findViewById(R.id.textView_plate_description);
            imageView_plate_flag = itemView.findViewById(R.id.imageViewGlobal);
        }
    }
}
