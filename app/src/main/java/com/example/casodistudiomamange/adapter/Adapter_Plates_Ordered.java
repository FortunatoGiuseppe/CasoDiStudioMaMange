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
import com.example.casodistudiomamange.activity.SwitchLoginSignupGuestActivity;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

/** Classe che fornisce un'adattatore alla RecyclerView dei piatti ordinati
 * Proprietà:
 * -SHARED_PREFS (shared preferences per il salvataggio in locale della quantità del piatto)
 * -Context (il contesto)
 * -plateArrayList (lista di tutti i piatti ordinati)
 * -total(lista delle quantità del piatto)
 */
public class Adapter_Plates_Ordered extends RecyclerView.Adapter<Adapter_Plates_Ordered.myViewHolder> {

    public static final String SHARED_PREFS = "sharedPrefs";
    private Context context;
    private ArrayList<SoPlate> plateArrayList;
    private ArrayList<Integer> total= new ArrayList<>();

    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final com.google.mlkit.nl.translate.Translator Translator = Translation.getClient(options);

    public Adapter_Plates_Ordered(Context context, ArrayList<SoPlate> plateArrayList) {
        this.context = context;
        this.plateArrayList = plateArrayList;
    }

    @NonNull
    @Override
    public Adapter_Plates_Ordered.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v_gen = LayoutInflater.from(context).inflate(R.layout.single_plate_ordered,parent,false);
        return new myViewHolder(v_gen);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Plates_Ordered.myViewHolder holder,@SuppressLint("RecyclerView") int position) {
        SoPlate soplate = plateArrayList.get(position);
        //controllo della lingua del dispositivo
        if(Locale.getDefault().getDisplayLanguage().equals("italiano")){
            holder.textView_plate.setText(soplate.getNomePiatto());
        }else{
            if(SwitchLoginSignupGuestActivity.controlValue==1){
                holder.textView_plate.setText(soplate.getNomePiatto());
            }else{
                //traduzione menù
                prepareModelTranslation(soplate.getNomePiatto(),holder.textView_plate);
            }

        }


        total.add(position,0);

        //Appena creo view devo vedere se il totale nello shared preferences è 0 oppure no
        // se non è zero devo rendere gone il tasto aggiungi e devo visualizzare +- con il numero che devo leggere dallo shared
        if(loadDataSharedPref(soplate.getNomePiatto())!=0){

            holder.addMoreLayout.setVisibility(View.VISIBLE);
            //imposto nell'array list alla quantità del piatto selezionato il valore letto dallo shared preferences
            total.set(position, loadDataSharedPref(soplate.getNomePiatto()));
            //imposto alla textView che visualizza la quantità già aggiunta il valore appena letto dallo shared preferences o dal file letto
            holder.tvCount.setText(total.get(position).toString());
        }else{
            if(total.get(position)==0){
                holder.tvCount.setText(String.valueOf(soplate.getQuantita()));
            }else{
                holder.addMoreLayout.setVisibility(View.GONE);
            }
        }

        // decremento quantità del piatto
        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decremento quantità
                total.set(position,total.get(position)-1);
                //salvo la nuova quantità nello shared preferences
                saveDataSharedPref(soplate.getNomePiatto(),total.get(position));
                if(total.get(position) > 0 ) {
                    ((MaMangeNavigationActivity) context).dbc
                            .decrementQuantityPlateOrdered(soplate.getNomePiatto(),((MaMangeNavigationActivity) context)
                                    .codiceSingleOrder,((MaMangeNavigationActivity) context)
                                    .codiceGroupOrder,((MaMangeNavigationActivity) context)
                                    .codiceTavolo,((MaMangeNavigationActivity) context)
                                    .username,total.get(position));
                    holder.tvCount.setText(total.get(position) +"");
                } else {
                    //passo true per indicare che deve essere aggiornato il fragment
                    ((MaMangeNavigationActivity) context).dbc
                            .deletePlateOrdered(soplate.getNomePiatto(),((MaMangeNavigationActivity) context)
                                    .codiceSingleOrder,((MaMangeNavigationActivity) context)
                                    .codiceGroupOrder,((MaMangeNavigationActivity) context)
                                    .codiceTavolo,((MaMangeNavigationActivity) context)
                                    .username, ((MaMangeNavigationActivity) context),true);
                    ((MaMangeNavigationActivity) context).updateQuantityOnBadge();
                    holder.addMoreLayout.setVisibility(View.GONE);
                    holder.itemView.setVisibility(View.GONE);

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.piattoRimosso);
                    builder1.setCancelable(true);
                    AlertDialog alert = builder1.create();
                    alert.show();
                }
            }
        });

        // Incremento quantità del piatto
        holder.imageAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incremento quantità
                total.set(position,total.get(position)+1);
                //salvo la nuova quantità nello shared preferences
                saveDataSharedPref(soplate.getNomePiatto(),total.get(position));
                if(total.get(position) <= 10 ) {
                    ((MaMangeNavigationActivity) context).dbc
                            .incrementQuantityPlateOrdered(soplate.getNomePiatto(),((MaMangeNavigationActivity) context)
                                    .codiceSingleOrder,((MaMangeNavigationActivity) context)
                                    .codiceGroupOrder,((MaMangeNavigationActivity) context)
                                    .codiceTavolo,((MaMangeNavigationActivity) context)
                                    .username, total.get(position));
                    //aggiorno visualizzatore contatore quantità
                    holder.tvCount.setText(total.get(position) +"");
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.massimoPiatti);
                    total.set(position,total.get(position)-1);
                    //salvo la nuova quantità nello shared preferences
                    saveDataSharedPref(soplate.getNomePiatto(),total.get(position));
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

        // view per aggiunta piatti
        ConstraintLayout addMoreLayout;
        ImageView imageMinus;
        ImageView imageAddOne;
        TextView  tvCount;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_plate = itemView.findViewById(R.id.textView_NamePlateGO);

            // aggiunta piatti
            imageMinus = itemView.findViewById(R.id.imageMinus);
            imageAddOne = itemView.findViewById(R.id.imageAddOne);
            tvCount = itemView.findViewById(R.id.tvCountGO);
            addMoreLayout  = itemView.findViewById(R.id.constraintLayoutPeM2);

        }
    }

    //metodo per salvare nello shared preferences la quantità relativa al piatto passato come parametro
    public void saveDataSharedPref(String nomePiatto, int total) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(nomePiatto,total);   //salvataggio nello shared preference del piatto la quantità
        editor.apply();
    }

    //metodo per caricare dallo shared preferences la quantità relativa al piatto passato come parametro
    public int loadDataSharedPref(String nomePiatto) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //0 è il valore passato di default, cioè se nello shared preferences non esiste una quantità precedentemente aggiunta per quel piatto
        return sharedPreferences.getInt(nomePiatto,0);
    }

    /**
     * Metodo che effettua la traduzione del menù
     * @param holder Textview in cui sarà caricata la stringa tradotta
     * @param trans stringa tradotta
     */
    private void prepareModelTranslation(String trans,TextView holder){

                Translator.translate(trans).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        holder.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.setText(trans);
                    }
                });

    }
}
