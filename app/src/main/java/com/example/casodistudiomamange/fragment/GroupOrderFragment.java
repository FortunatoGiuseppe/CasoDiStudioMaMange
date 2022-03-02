package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.ProfileActivity;
import com.example.casodistudiomamange.adapter.Adapter_Profile;
import com.example.casodistudiomamange.model.Profile;

import java.util.ArrayList;
import java.util.List;

public class GroupOrderFragment extends Fragment {

    RecyclerView recyclerView;
    List<Profile> profileList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_order,null);
        getActivity().setTitle("Group Order");

        recyclerView = v.findViewById(R.id.recyclerGroupOrder);
        Adapter_Profile adapter_profile = new Adapter_Profile(profileList);
        recyclerView.setAdapter(adapter_profile);
        recyclerView.setHasFixedSize(true);
        caricaProfileEdOrdini();


        return v;
    }


    private void caricaProfileEdOrdini(){

        profileList = new ArrayList<Profile>();
        profileList.add(new Profile("nomeUtente","listaOrdinazione"));

    }
}
