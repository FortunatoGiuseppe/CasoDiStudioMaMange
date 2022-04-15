package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.fragment.CategoryFragment;
import com.example.casodistudiomamange.model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Locale;


public class Adapter_category extends RecyclerView.Adapter<Adapter_category.myViewHolder> {

    private Context context;
    private ArrayList<Category>  categoryArrayList;


    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final Translator Translator = Translation.getClient(options);


    public  Adapter_category(Context context, ArrayList<Category> categories){
        this.context =context;
        this.categoryArrayList = categories;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_item_category,parent,false);

        return  new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category= categoryArrayList.get(position);
        //controllo della lingua del dispositivo
        if(!Locale.getDefault().getLanguage().equals((new Locale("it").getLanguage()))){
            holder.categoryTv.setText(category.getNome());
        }else{

                //traduzione menù se la lingua non è italiano
                prepareModelName(category.getNome(), new metododiCallbackTransaltion() {
                    @Override
                    public void onCallback(String stringaTradotta) {
                        holder.categoryTv.setText(stringaTradotta);
                    }
                });


        }

        Picasso.get().load(category.getImg()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("CategoryKey", category.getNome());
                fragment.setArguments(bundle);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    public  static  class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView categoryTv;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTv=itemView.findViewById(R.id.categoryTv);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }

    /**
     * Metodo che effettua la traduzione del menù
     * @param stringaTradotta metodo di callback per gestire asincronismo
     * @param trans stringa tradotta
     */
    private void prepareModelName(String trans,metododiCallbackTransaltion stringaTradotta){

        Translator.translate(trans).addOnSuccessListener(new OnSuccessListener<String>() {

            public void onSuccess(String s) {

                stringaTradotta.onCallback(s);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                stringaTradotta.onCallback(trans);
            }
        });

    }


    /*Interfaccia che permette di chiamare il metodo di Callback delle stringhe tradotte*/
    public interface metododiCallbackTransaltion{
        //metodo che permette di utilizzare ile stringhe tradotte
        void onCallback(String stringaTradotta);
    }
}
