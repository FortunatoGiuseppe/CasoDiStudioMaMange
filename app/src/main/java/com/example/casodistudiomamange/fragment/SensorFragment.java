package com.example.casodistudiomamange.fragment;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.adapter.Adapter_plates;
import com.example.casodistudiomamange.model.Plate;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SensorFragment extends Fragment {

    TextView PlateName;
    ImageView img;
    TextView Descrizione;
    private FirebaseFirestore db;
    private ArrayList<Plate> plates;    //lista che conterrà i nomi delle categorie
    private Adapter_plates adapter_plates;
    String name;
    String descrizione;
    String image;
    private BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BLUETOOTH = 0;

    public SensorFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        plates= new ArrayList<Plate>();
        adapter_plates = new Adapter_plates(getContext(), plates);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_sensor, container, false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent richiesta = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(richiesta, REQUEST_ENABLE_BLUETOOTH);
        }


        PlateName=v.findViewById(R.id.nomePiatto);
        Descrizione=v.findViewById(R.id.descrizione);
        img=v.findViewById(R.id.imagePlate);


        Bundle bundle = getArguments();
        name=bundle.getString("PlateName");
        PlateName.setText(name);
        image = bundle.getString("Img");
        Picasso.get().load(image).into(img);
        descrizione= bundle.getString("Descrizione");
        Descrizione.setText(descrizione);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}