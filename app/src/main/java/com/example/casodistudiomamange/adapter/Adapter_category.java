package com.example.casodistudiomamange.adapter;

import com.example.casodistudiomamange.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.fragment.CategoryFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter_category extends RecyclerView.Adapter<Adapter_category.myViewHolder> {

    private Context context;
    private List<String> categories;

    public  Adapter_category(Context context, List<String> categories){
        this.context =context;
        this.categories = categories;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_item,parent,false);
        return  new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(categories.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("CategoryKey",Integer.toString(position));
                fragment.setArguments(bundle);

                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container, fragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public  static  class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }
}
