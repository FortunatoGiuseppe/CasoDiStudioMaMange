package com.example.casodistudiomamange.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casodistudiomamange.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.model.Plate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Plates_Ordered extends RecyclerView.Adapter<Adapter_Plates_Ordered.myViewHolder> {

    public static final String SHARED_PREFS = "sharedPrefs";
    private Context context;
    private ArrayList<Plate> plateArrayList;
    private ArrayList<Integer> total= new ArrayList<>();

    public Adapter_Plates_Ordered(Context context, ArrayList<Plate> plateArrayList) {
        this.context = context;
        this.plateArrayList = plateArrayList;
    }

    @NonNull
    @Override
    public Adapter_Plates_Ordered.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_plate_ordered,parent,false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Plates_Ordered.myViewHolder holder,@SuppressLint("RecyclerView") int position) {
        Plate plate = plateArrayList.get(position);
        holder.textView_plate.setText(plate.getNome());
        Picasso.get().load(plate.getImg()).into(holder.imageView_plate);

        total.add(position,0);

        //Appena creo view devo vedere se il totale nello shared preferences è 0 oppure no
        // se non è zero devo rendere gone il tasto aggiungi e devo visualizzare +- con il numero che devo leggere dallo shared
        if(loadData(plate.getNome())!=0){
            holder.addMoreLayout.setVisibility(View.VISIBLE);

            //imposto nell'array list alla quantità del piatto selezionato il valore letto dallo shared preferences
            total.set(position,loadData(plate.getNome()));
            //imposto alla textView che visualizza la quantità già aggiunta il valore appena letto dallo shared preferences
            holder.tvCount.setText(total.get(position).toString());
        }else{
            holder.addMoreLayout.setVisibility(View.GONE);

        }

        //visualizzazione icona mondo
        if((plate.getFlag() != null) && plate.getFlag()==1){
            holder.imageView_plate_flag.setImageResource(R.drawable.ic_baseline_public_24);
            holder.imageView_plate_flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //creazione nuovo fragment per visualizzazione dati
                }
            });
        }


        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decremento quantità
                total.set(position,total.get(position)-1);
                //salvo la nuova quantità nello shared preferences
                saveData(plate.getNome(),total.get(position));
                if(total.get(position) > 0 ) {
                    ((MaMangeNavigationActivity) context).dbc.decrementQuantityPlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo);
                    holder.tvCount.setText(total.get(position) +"");
                } else {
                    ((MaMangeNavigationActivity) context).dbc.deletePlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo);
                    holder.addMoreLayout.setVisibility(View.GONE);

                    //aggiorna quantità nel db
                }
            }
        });

        holder.imageAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incremento quantità
                total.set(position,total.get(position)+1);
                //salvo la nuova quantità nello shared preferences
                saveData(plate.getNome(),total.get(position));
                if(total.get(position) <= 10 ) {
                    ((MaMangeNavigationActivity) context).dbc.incrementQuantityPlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo);
                    //aggiorno visualizzatore contatore quantità
                    holder.tvCount.setText(total.get(position) +"");
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.massimoPiatti);
                    total.set(position,total.get(position)-1);
                    //salvo la nuova quantità nello shared preferences
                    saveData(plate.getNome(),total.get(position));
                    builder1.setCancelable(true);
                    AlertDialog alert = builder1.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return plateArrayList.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView textView_plate;
        ImageView imageView_plate;

        ImageView imageView_plate_flag;
        // view per aggiunta piatti
        ConstraintLayout addMoreLayout;
        ImageView imageMinus;
        ImageView imageAddOne;
        TextView  tvCount;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_plate = itemView.findViewById(R.id.imageView_plate3);
            textView_plate = itemView.findViewById(R.id.textView_NamePlate);

            imageView_plate_flag = itemView.findViewById(R.id.imageViewGlobal3);


            // aggiunta piatti
            imageMinus = itemView.findViewById(R.id.imageMinus);
            imageAddOne = itemView.findViewById(R.id.imageAddOne);
            tvCount = itemView.findViewById(R.id.tvCount);
            addMoreLayout  = itemView.findViewById(R.id.constraintLayoutPeM2);

        }
    }

    //metodo per salvare nello shared preferences la quantità relativa al piatto passato come parametro
    public void saveData(String nomePiatto,int total) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(nomePiatto,total);   //salvataggio nello shared preference del piatto la quantità
        editor.apply();
    }

    //metodo per caricare dallo shared preferences la quantità relativa al piatto passato come parametro
    public int loadData(String nomePiatto) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //0 è il valore passato di default, cioè se nello shared preferences non esiste una quantità precedentemente aggiunta per quel piatto
        return sharedPreferences.getInt(nomePiatto,0);
    }
}
