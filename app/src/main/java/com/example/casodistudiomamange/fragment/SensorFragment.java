package com.example.casodistudiomamange.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.casodistudiomamange.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class SensorFragment extends Fragment {

    /*costanti che rappresentano lo stato del bluetooth*/
    static final int REQUEST_ENABLE_BLUETOOTH = 0;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;
    static final int STATE_CONNECTION_END = 6;
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] bluetoothDevice;
    Receive receive;

    TextView PlateName;
    ImageView img;
    TextView Descrizione;
    ListView listaDispositiviBluetooth;
    String name;
    String descrizione;
    String image;
    Button connect;

    TextView temperatura,pressione,statoConnessione;

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
        connect = v.findViewById(R.id.connect);
        listaDispositiviBluetooth = v.findViewById(R.id.listaDispositiviBluetooth);
        Bundle bundle = getArguments();
        name = bundle.getString("PlateName");
        PlateName.setText(name);
        image = bundle.getString("Img");
        Picasso.get().load(image).into(img);
        descrizione = bundle.getString("Descrizione");
        Descrizione.setText(descrizione);

        temperatura = v.findViewById(R.id.temperatura);
        statoConnessione = v.findViewById(R.id.statoConnessione);
        pressione = v.findViewById(R.id.pressione);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBTPermission();
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                String[] strings=new String[pairedDevices.size()];
                bluetoothDevice=new BluetoothDevice[pairedDevices.size()];
                int index=0;

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.

                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothDevice[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,strings);
                    listaDispositiviBluetooth.setAdapter(arrayAdapter);
                }

                listaDispositiviBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ClientClass clientClass = new ClientClass(bluetoothDevice[i]);
                        clientClass.start();

                    }
                });


            }
        });

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

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {

                case STATE_CONNECTED:
                    statoConnessione.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    statoConnessione.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String messaggioTemporaneo=new String(readBuff,0,msg.arg1);


                    String[] splitted = messaggioTemporaneo.split(",");
                    for (int i=0; i<splitted.length; i++){
                        System.out.println(splitted[i]);
                    }
                    pressione.setText("Pressione di conservazione: "+splitted[0]);
                    temperatura.setText("Temperatura di conservazione: "+splitted[1]);

                    break;

            }
            return true;
        }
    });

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
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1001:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    attivaBluetooth();

                }  else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Importanza dei permessi");
                    builder.setMessage("Accettare i permessi per accedere a questa funzionalità");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
        }

    }


    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                checkBTPermission();
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                checkBTPermission();
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);
                receive =new Receive(socket);
                receive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class Receive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
       // private final OutputStream outputStream;

        public Receive(BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            //OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
              //  tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
           // outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}


