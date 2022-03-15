package com.example.casodistudiomamange.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.QRCodeActivity;
import com.example.casodistudiomamange.adapter.Adapter_plates;
import com.example.casodistudiomamange.model.Plate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class SensorFragment extends Fragment {

    /*costanti che rappresentano lo stato del bluetooth*/
    static final int REQUEST_ENABLE_BLUETOOTH = 0;

    BluetoothAdapter bluetoothAdapter;

    TextView PlateName;
    ImageView img;
    TextView Descrizione;
    String name;
    String descrizione;
    String image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Controlla che i eprmessi siano stati dai*/
        checkBTPermission();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);

        PlateName = v.findViewById(R.id.nomePiatto);
        Descrizione = v.findViewById(R.id.descrizione);
        img = v.findViewById(R.id.imagePlate);
        Bundle bundle = getArguments();
        name = bundle.getString("PlateName");
        PlateName.setText(name);
        image = bundle.getString("Img");
        Picasso.get().load(image).into(img);
        descrizione = bundle.getString("Descrizione");
        Descrizione.setText(descrizione);

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void attivaBluetooth(){

        if (!bluetoothAdapter.isEnabled()) {
            Intent richiesta = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(richiesta, REQUEST_ENABLE_BLUETOOTH);
        }

    }

    private void checkBTPermission(){

        int permissionCheck = getContext().checkSelfPermission("android.Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck+= getContext().checkSelfPermission("android.Manifest.permission.ACCESS_COARSE_LOCATION");
        if(permissionCheck == 0){
            attivaBluetooth();
        } else if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Importanza dei permessi");
            builder.setMessage("Accettare i permessi per accedere a questa funzionalità, sei sicuro di non voler accettare i permessi?");

            builder.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.setNegativeButton(
                    "Annulla",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1001);
                        }
                    });


            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},1001);
        }
    }



}


