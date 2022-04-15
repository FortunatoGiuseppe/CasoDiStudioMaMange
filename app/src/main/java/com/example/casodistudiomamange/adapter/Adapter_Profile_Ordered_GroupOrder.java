package com.example.casodistudiomamange.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.casodistudiomamange.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;
import java.util.Locale;

public class Adapter_Profile_Ordered_GroupOrder extends RecyclerView.Adapter<Adapter_Profile_Ordered_GroupOrder
        .Adapter_Profile_Ordered_GroupOrderViewHolder> {

    private List<SoPlate> soPlateList;
    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final com.google.mlkit.nl.translate.Translator Translator = Translation.getClient(options);

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
        //controllo della lingua del dispositivo
        if(!Locale.getDefault().getLanguage().equals((new Locale("it").getLanguage()))){
            holder.plateName.setText(soPlateList.get(position).getNomePiatto());
        }else{
            //traduzione menù
            prepareModelTranslation(soPlateList.get(position).getNomePiatto(),holder.plateName);
        }
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
