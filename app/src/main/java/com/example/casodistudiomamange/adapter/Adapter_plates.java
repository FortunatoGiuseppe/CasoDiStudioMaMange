package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.activity.QRCodeActivity;
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

        holder.addPlateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage(R.string.piattoAggiunto);
                builder1.setCancelable(true);
                AlertDialog alert = builder1.create();
                alert.show();


                // Chiudi automaticamente dopo un secondo e mezzo
                final Handler handler  = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (alert.isShowing()) {
                            alert.dismiss();
                        }
                    }
                };
                handler.postDelayed(runnable, 1500);
            }
        });
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
        Button addPlateBtn;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_plate = itemView.findViewById(R.id.imageView_plate);
            textView_plate = itemView.findViewById(R.id.textView_plate);
            textView_plate_description= itemView.findViewById(R.id.textView_plate_description);
            imageView_plate_flag = itemView.findViewById(R.id.imageViewGlobal);
            addPlateBtn =itemView.findViewById(R.id.aggiungiBtn);
        }
    }
}
