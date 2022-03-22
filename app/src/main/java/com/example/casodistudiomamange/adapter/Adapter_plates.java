package com.example.casodistudiomamange.adapter;

import static com.google.firebase.firestore.core.UserData.Source.Set;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casodistudiomamange.fragment.SensorFragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.model.FileOrderManager;
import com.example.casodistudiomamange.model.Plate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Adapter_plates extends RecyclerView.Adapter<Adapter_plates.myViewHolder> {

    public static final String SHARED_PREFS = "sharedPrefs";
    private Context context;
    private ArrayList<Plate> plateArrayList;
    private ArrayList<Integer> total= new ArrayList<>();//lista delle quantità, in ogni posizione c'è la quantità di un piatto (o bevanda)
    private static final String FILE_NAME = "lastOrder.txt";
    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final Translator Translator = Translation.getClient(options);
    RemoteModelManager modelManager = RemoteModelManager.getInstance();

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

        if(Locale.getDefault().getDisplayLanguage().equals("italiano")){
            holder.textView_plate.setText(plate.getNome());
            holder.textView_plate_description.setText(plate.getDescrizione());

        }else{
            prepareModel(plate.getNome(),holder);
            prepareModelDescription(plate.getDescrizione(),holder);
        }


        Picasso.get().load(plate.getImg()).into(holder.imageView_plate);


        //invece di aggiungere quantità a 0 a priori dovrei leggere quantità per i piatti che sono stati caricati da file locale
        Map<String,Long> mapPlateQuantity = new HashMap<>();    //  creo mappa che contiene nome piatto e quantità relativa ordinata nell'ordine salvato
        FileOrderManager fileOrderManager=new FileOrderManager();



        //Se non ho caricato l'ultimo ordine salvato (cioè il tasto per caricarlo è ancora abilitato)
        if(((MaMangeNavigationActivity)context).lastOrderItem.isEnabled()){
            //Non ho caricato ultimo rodne quindi lavoro sullo shared preferences e vedo se piatto sta nello shared preferences dell'ordine corrente
            if(((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome())!=0){
                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addPlateBtn.setVisibility(View.GONE);

                //imposto nell'array list alla quantità del piatto selezionato il valore letto dallo shared preferences
                if(total.size()>=position){ //controllo per vedere se la quantità per il piatto esisteva già o meno (per evitare null pointer), se esisteva allora position<size
                    total.add(position, ((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome()));
                }else{
                    total.set(position, ((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome()));
                }

                //imposto alla textView che visualizza la quantità già aggiunta il valore appena letto dallo shared preferences
                holder.tvCount.setText(total.get(position).toString());
            }else{
                //se non stava nello shared allora utente non aveva selezionato quel piatto, quindi imposto quantità a 0
                total.add(position, 0);
                holder.addMoreLayout.setVisibility(View.GONE);
                holder.addPlateBtn.setVisibility(View.VISIBLE);
            }
        }else {//se ho caricato ultimo ordine
            fileOrderManager.loadQuantitiesFromFile((MaMangeNavigationActivity) context,FILE_NAME,mapPlateQuantity); //carico la mappa
            //Se il piatto che si sta caricando nel menu è presente nella mappa (ultimo ordine) (caricamento mappa riga 65 questo file)
            //NOTA: il controllo è fatto "al contrario" (cioè a partire dalla mappa) per sfruttare metodo della mappa efficiente
            if (mapPlateQuantity.containsKey(plate.getNome())) {

                //Se la quantità che sta nella mappa (cioè letto dal file ultimo ordine) non coincide con quello dello shared, vuol dire che l'utente l'ha modificato, quindi "vince" quello
                //che sta nello shared, quindi la quantitàdello shared deve essere visualizzata nel menu
                if(mapPlateQuantity.get(plate.getNome())!=((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome())){
                    if(total.size()>=position){//controllo per vedere se la quantità per il piatto esisteva già o meno (per evitare null pointer), se esisteva allora position<size
                        total.add(position, ((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome()));
                    }else{
                        total.set(position, ((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome()));
                    }
                }else {
                    //se le quantità sono uguali allora copio quelle prese dal file
                    //se sta allora nel menu deve essere mostrata la quantità che ho letto
                    if(total.size()>=position){//controllo per vedere se la quantità per il piatto esisteva già o meno (per evitare null pointer), se esisteva allora position<size
                        total.add(position, Math.toIntExact(mapPlateQuantity.get(plate.getNome())));
                    }else{
                        total.set(position, Math.toIntExact(mapPlateQuantity.get(plate.getNome())));
                    }
                }

                //imposto stampa quantità del piatto
                holder.tvCount.setText(total.get(position).toString());
                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addMoreLayout.setEnabled(true);
                holder.addPlateBtn.setVisibility(View.GONE);
            } else {
                //Se non sta nel file dell'ultimo ordine è probabile che l'utente abbia aggiunto il piatto dopo aver caricato l'ordine, quindi lo trovo nello shared
                //Se nello shared preferences c'è il piatto corrente (cioè se ha quantità diversa da 0)
                if(((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome())!=0){
                    total.add(position, ((MaMangeNavigationActivity) context).getQuantityForParameterPlateSharedPreferences(plate.getNome())); //aggiungo la quantità al totale

                    //imposto stampa quantità del piatto
                    holder.tvCount.setText(total.get(position).toString());
                    holder.addMoreLayout.setVisibility(View.VISIBLE);
                    holder.addMoreLayout.setEnabled(true);
                    holder.addPlateBtn.setVisibility(View.GONE);

                }else{
                    //se non sta allora devo mettere 0, cioè il piatto non è mai stato selezionato dall'utente (nè in ordine vecchio nè in quello dopo aver caricato ordine vecchio)
                    if(total.size()>=position){
                        total.add(position, 0);
                    }else{
                        total.set(position, 0);
                    }

                    holder.addMoreLayout.setVisibility(View.GONE);
                    holder.addPlateBtn.setVisibility(View.VISIBLE);
                    holder.addPlateBtn.setEnabled(true);
                }
            }
        }

        //visualizzazione icona mondo
        if((plate.getFlag() != null) && plate.getFlag()==1){
            holder.imageView_plate_flag.setImageResource(R.drawable.ic_baseline_public_24);
            holder.imageView_plate_flag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Fragment fragment=new SensorFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("PlateName", plate.getNome());
                    bundle.putString("Img", plate.getImg());
                    bundle.putString("Descrizione", plate.getDescrizione());

                    fragment.setArguments(bundle);
                    FragmentManager manager = ((AppCompatActivity)
                            context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });
        }


        holder.addPlateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Aggiunta del piatto nel DB

                ((MaMangeNavigationActivity) context).dbc.orderPlate(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo,((MaMangeNavigationActivity) context).username, (long)1);

                //aggiornamento icona aggiunta
                holder.addMoreLayout.setVisibility(View.VISIBLE);
                holder.addPlateBtn.setVisibility(View.GONE);

                //siccome ho potuto cliccare sul tasto aggiungi vuol dire che prima la quantità era 0 perciò ora è sicuramente 1
                holder.tvCount.setText(String.valueOf(total.get(position)+1));
                total.set(position, total.get(position)+1);
                //salvo la quantità nello shared preferences
                ((MaMangeNavigationActivity) context).saveDataSharedPreferences(plate.getNome(),total.get(position));
            }
        });

        holder.imageMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decremento quantità
                total.set(position,total.get(position)-1);
                //salvo la nuova quantità nello shared preferences
                ((MaMangeNavigationActivity) context).saveDataSharedPreferences(plate.getNome(),total.get(position));
                if(total.get(position) > 0 ) {
                    ((MaMangeNavigationActivity) context).dbc.decrementQuantityPlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo,((MaMangeNavigationActivity) context).username);
                    holder.tvCount.setText(total.get(position) +"");
                } else {
                    ((MaMangeNavigationActivity) context).dbc.deletePlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo,((MaMangeNavigationActivity) context).username);
                    holder.addMoreLayout.setVisibility(View.GONE);
                    holder.addPlateBtn.setVisibility(View.VISIBLE);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.piattoRimosso);
                    builder1.setCancelable(true);
                    AlertDialog alert = builder1.create();
                    alert.show();
                }
            }
        });

        holder.imageAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incremento quantità
                total.set(position,total.get(position)+1);
                //salvo la nuova quantità nello shared preferences
                ((MaMangeNavigationActivity) context).saveDataSharedPreferences(plate.getNome(),total.get(position));
                if(total.get(position) <= 10 ) {
                    ((MaMangeNavigationActivity) context).dbc.incrementQuantityPlateOrdered(plate.getNome(),((MaMangeNavigationActivity) context).codiceSingleOrder,((MaMangeNavigationActivity) context).codiceGroupOrder,((MaMangeNavigationActivity) context).codiceTavolo,((MaMangeNavigationActivity) context).username);
                    //aggiorno visualizzatore contatore quantità
                    holder.tvCount.setText(total.get(position) +"");
                }else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage(R.string.massimoPiatti);
                    total.set(position,total.get(position)-1);
                    //salvo la nuova quantità nello shared preferences
                    ((MaMangeNavigationActivity) context).saveDataSharedPreferences(plate.getNome(),total.get(position));
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
            tvCount = itemView.findViewById(R.id.tvCountGO);
            addMoreLayout  = itemView.findViewById(R.id.constraintLayoutPeM2);

        }
    }

    //metodo per salvare nello shared preferences la quantità relativa al piatto passato come parametro
    public void saveDataSharedPreferences(String nomePiatto, int total) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(nomePiatto,total);   //salvataggio nello shared preference del piatto la quantità
        editor.apply();
    }

    //metodo per caricare dallo shared preferences la quantità relativa al piatto passato come parametro
    public int getQuantityForParameterPlateSharedPreferences(String nomePiatto) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        //0 è il valore passato di default, cioè se nello shared preferences non esiste una quantità precedentemente aggiunta per quel piatto
        return sharedPreferences.getInt(nomePiatto,0);
    }

    private void prepareModel(String trans,@NonNull myViewHolder holder){



        Translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Translator.translate(trans).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        holder.textView_plate.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void prepareModelDescription(String trans,@NonNull myViewHolder holder){


        Translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Translator.translate(trans).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        holder.textView_plate_description.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }




}
