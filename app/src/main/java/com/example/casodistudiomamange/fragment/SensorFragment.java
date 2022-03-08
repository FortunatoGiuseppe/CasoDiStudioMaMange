package com.example.casodistudiomamange.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SensorFragment extends Fragment {

    TextView PlateName;
    ImageView img;
    TextView Descrizione;
    String name;
    String descrizione;
    String image;
    int REQUEST_ENABLE_BLUETOOTH = 0;
    int BLUETOOTH_SCAN = -1;
    BluetoothAdapter bluetoothAdapter;


    public SensorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //lista che conterrà i nomi delle categorie
        ArrayList<Plate> plates = new ArrayList<>();
        Adapter_plates adapter_plates = new Adapter_plates(getContext(), plates);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            Intent richiesta = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(richiesta, REQUEST_ENABLE_BLUETOOTH);
        }

        boolean salvoBOEJA = bluetoothAdapter.startDiscovery();



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

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    int permissionCheck = getContext().checkSelfPermission("Manifest.permission.BLUETOOTH_CONNECT");
                    if (permissionCheck != 0) {
                        getActivity().requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1001);
                    }
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d("TAG",deviceName+"->"+deviceHardwareAddress);
            }
        }
    };



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}