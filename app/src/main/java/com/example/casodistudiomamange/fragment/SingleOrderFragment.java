package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_plates;

public class SingleOrderFragment extends Fragment {


    private RecyclerView recyclerView_plates;
    private Adapter_plates adapter_plates;
    private TextView username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_single_order,null);
        getActivity().setTitle("Single Order");

        username = v.findViewById(R.id.usernameTextView);

        String ordinazione = getResources().getString(R.string.ordinazione);
        String usernameInserito = ((MaMangeNavigationActivity)getActivity()).username;
        username.setText(ordinazione + " "+ usernameInserito);
        recyclerView_plates = v.findViewById(R.id.recycleview_plates);

        return v;
    }
}
