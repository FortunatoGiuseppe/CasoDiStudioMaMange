package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.model.Plate;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class Adapter_plates extends RecyclerView.Adapter<Adapter_plates.myViewHolder> {

    private Context context;
    private ArrayList<Plate> plateArrayList;
    private int total=0;

    public  Adapter_plates(Context context, ArrayList<Plate> plateArrayList){
        this.context =context;
        this.plateArrayList=plateArrayList;
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
        Plate plate = plateArrayList.get(position);
        String s=plate.getNome();
        holder.textView_plate.setText(plate.getNome());
        holder.textView_plate_description.setText(plate.getDescrizione());
        Picasso.get().load(plate.getImg()).into(holder.imageView_plate);


        if((plate.getFlag() != null) && plate.getFlag().equals("1")){
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

                //Aggiunta del piatto nel DB
                //((MaMangeNavigationActivity) context).dbc.addPlate(platesName.get(position),1);

                //aggiornamento icona aggiunta
                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addPlateBtn.setVisibility(View.GONE);
                holder.tvCount.setText("1");
                total=1;
            }
        });

        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // vedi la quantità del piatto
                total--;
                if(total > 0 ) {
                    //aggiorna quantità nel db
                    holder.tvCount.setText(total +"");
                } else {
                    holder.addMoreLayout.setVisibility(View.GONE);
                    holder.addPlateBtn.setVisibility(View.VISIBLE);
                    //aggiorna quantità nel db
                }
            }
        });

        holder.imageAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total++;
                if(total <= 10 ) {
                    //aggiorna quantità nel db
                    holder.tvCount.setText(total +"");
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.massimoPiatti);
                    total--;
                    builder1.setCancelable(true);
                    AlertDialog alert = builder1.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return  plateArrayList.size();
    }

    public  static  class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView_plate;
        TextView textView_plate;
        TextView textView_plate_description;
        ImageView imageView_plate_flag;
        Button addPlateBtn;
        // view per aggiunta piatti
        ConstraintLayout addMoreLayout;
        ImageView imageMinus;
        ImageView imageAddOne;
        TextView  tvCount;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_plate = itemView.findViewById(R.id.imageView_plate);
            textView_plate = itemView.findViewById(R.id.textView_plate);
            textView_plate_description= itemView.findViewById(R.id.textView_plate_description);
            imageView_plate_flag = itemView.findViewById(R.id.imageViewGlobal);
            addPlateBtn =itemView.findViewById(R.id.aggiungiBtn);

            // aggiunta piatti
            imageMinus = itemView.findViewById(R.id.imageMinus);
            imageAddOne = itemView.findViewById(R.id.imageAddOne);
            tvCount = itemView.findViewById(R.id.tvCount);
            addMoreLayout  = itemView.findViewById(R.id.constraintLayoutPeM);

        }
    }
}
