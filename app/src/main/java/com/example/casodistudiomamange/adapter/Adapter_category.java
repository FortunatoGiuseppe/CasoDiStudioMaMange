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
import com.example.casodistudiomamange.model.Category;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class Adapter_category extends RecyclerView.Adapter<Adapter_category.myViewHolder> {

    private Context context;
    private ArrayList<Category>  categoryArrayList;

    public  Adapter_category(Context context, ArrayList<Category> categories){
        this.context =context;
        this.categoryArrayList = categories;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_item,parent,false);
        return  new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category= categoryArrayList.get(position);
        Picasso.get().load(category.getImg()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryFragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putString("CategoryKey", category.getNome());
                String s=category.getNome();
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

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
        }
    }
}
