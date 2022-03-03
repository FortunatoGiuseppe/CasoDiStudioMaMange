package com.example.casodistudiomamange.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_Plates_Ordered;
import com.example.casodistudiomamange.adapter.Adapter_Profile;
import com.example.casodistudiomamange.model.Plate;
import com.example.casodistudiomamange.model.PlateInOrder;
import com.example.casodistudiomamange.model.Profile;
import com.example.casodistudiomamange.model.SoPlate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupOrderFragment extends Fragment {

    RecyclerView recyclerView;

    List<Profile> profileList;
    Adapter_Profile adapter_profile;
    Adapter_Plates_Ordered adapter_plates_ordered;
    FirebaseFirestore ffdb;
    ArrayList<Plate> platesInOrder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ffdb = FirebaseFirestore.getInstance();
        profileList = new ArrayList<>();
        platesInOrder = new ArrayList<>();
        platesInOrder.add(new Plate("Ciao","EDAMAME","Piatto buono",(long)2,"ANTIPASTI"));
        adapter_plates_ordered = new Adapter_Plates_Ordered(getContext(),platesInOrder);


        adapter_profile = new Adapter_Profile(profileList,getContext(),adapter_plates_ordered);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_order,null);
        getActivity().setTitle("Group Order");

        recyclerView = v.findViewById(R.id.recyclerGroupOrder);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter_profile);

        caricaProfileEdOrdini();

        return v;
    }


    private void caricaProfileEdOrdini(){

        String usernameInserito = ((MaMangeNavigationActivity) getActivity()).username;
        String codiceSingleOrder = ((MaMangeNavigationActivity) getActivity()).codiceSingleOrder;
        String codiceGroupOrder = ((MaMangeNavigationActivity) getActivity()).codiceGroupOrder;
        String codiceTavolo = ((MaMangeNavigationActivity) getActivity()).codiceTavolo;

        ArrayList<SoPlate> soPlate = new ArrayList<SoPlate>();
        soPlate.add(new SoPlate(codiceSingleOrder,"EDAMAME",2,codiceGroupOrder,codiceTavolo));
        // public SoPlate(String codiceSingleOrder, String nomePiatto, long quantita, String codiceGroupOrder, String codiceTavolo) {

        profileList.add(new Profile(usernameInserito,soPlate));


    }
}
