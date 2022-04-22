package com.example.casodistudiomamange.fragment;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casodistudiomamange.R;
import com.example.casodistudiomamange.activity.MaMangeNavigationActivity;
import com.example.casodistudiomamange.adapter.Adapter_plates;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.example.casodistudiomamange.thread.Client;
import com.squareup.picasso.Picasso;

import java.security.Permission;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;

public class SensorFragment extends Fragment {

    /*costanti che rappresentano lo stato del bluetooth*/
    static final int REQUEST_ENABLE_BLUETOOTH = 0;
    static final int REQUEST_ENABLE_PERMISSION = 1;
    static final int STATO_IN_ASCOLTO= 2;
    static final int STATO_CONNESSO =3;
    static final int STATO_CONNESSIONE_FALLITO =4;
    static final int STATO_MESSAGGIO_RICEVUTO =5;
    public static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ITALIAN)
                    .setTargetLanguage(TranslateLanguage.ENGLISH)
                    .build();
    final com.google.mlkit.nl.translate.Translator Translator = Translation.getClient(options);
    RemoteModelManager modelManager = RemoteModelManager.getInstance();

    /*variabili del bluetooth*/
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] bluetoothDevice;
    ListView listaDispositiviBluetooth;
    Button connettitiBtn, associaBtn, indietro;
    TextView temperaturaConserazione, torbidita, statoConnessione, umidita, cosaFareTw;

    /*variabili della bevanda selezionata*/
    TextView drinkName;
    ImageView drinkImg;
    TextView drinkDescrizione;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTPermission();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sensor, container, false);

        drinkName = v.findViewById(R.id.nomePiatto);
        drinkDescrizione = v.findViewById(R.id.descrizione);
        drinkImg = v.findViewById(R.id.imagePlate);

        Bundle bundle = getArguments();
        drinkName.setText(bundle.getString("PlateName"));
        Picasso.get().load(bundle.getString("Img")).into(drinkImg);
        if(Locale.getDefault().getDisplayLanguage().equals("italiano")){
            drinkDescrizione.setText(bundle.getString("Descrizione"));
        }else{
            prepareModelDescription(bundle.getString("Descrizione"));
        }

        cosaFareTw = v.findViewById(R.id.cosaFareBluetoothTW);
        associaBtn = v.findViewById(R.id.pair);
        connettitiBtn = v.findViewById(R.id.connect);
        indietro = v.findViewById(R.id.indietro);

        temperaturaConserazione = v.findViewById(R.id.temperatura);
        statoConnessione = v.findViewById(R.id.statoConnessione);
        torbidita = v.findViewById(R.id.torbidita);
        umidita = v.findViewById(R.id.umidita);

        connettitiBtn.setVisibility(View.GONE);
        associaBtn.setVisibility(View.GONE);

        return v;
    }

    private void inizializzaBt(){
        //bluetooth connect
        boolean b =checkBTPermission();
        if(b){

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            String[] strings=new String[pairedDevices.size()];
            bluetoothDevice=new BluetoothDevice[pairedDevices.size()];
            listaDispositiviBluetooth = new ListView(getContext());

            int index=0;

            if (pairedDevices.size() > 0) {
                //per ogni device paireato lo aggiungo nella lista dei bluetooth device
                //ed aggiungo il nome del dispositivo per visualizzarlo all'inerno della listView
                for (BluetoothDevice device : pairedDevices) {
                    bluetoothDevice[index] = device;
                    //bluetooth connect
                    strings[index] = device.getName();
                    index++;
                }
            }

            if(isCantinaPair(bluetoothDevice)){
                associaBtn.setVisibility(View.GONE);
                connettitiBtn.setVisibility(View.VISIBLE);
                cosaFareTw.setText(R.string.adessoConnettiti);
            }

            associaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));

                    associaBtn.setVisibility(View.GONE);
                    connettitiBtn.setVisibility(View.VISIBLE);
                    cosaFareTw.setText(R.string.adessoConnettiti);
                }
            });

            indietro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new RestaurantFragment();
                    ((MaMangeNavigationActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            });

            connettitiBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //listaDispositiviBluetooth.setVisibility(View.VISIBLE);
                    //controllo se i permessi bluetooth sono stati dati

                    //bluetooth connect
                    checkBTPermission();
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    String[] strings=new String[pairedDevices.size()];
                    bluetoothDevice=new BluetoothDevice[pairedDevices.size()];
                    listaDispositiviBluetooth = new ListView(getContext());
                    int index=0;

                    if (pairedDevices.size() > 0) {
                        //per ogni device paireato lo aggiungo nella lista dei bluetooth device
                        //ed aggiungo il nome del dispositivo per visualizzarlo all'inerno della listView
                        for (BluetoothDevice device : pairedDevices) {
                            bluetoothDevice[index] = device;
                            //bluetooth connect
                            strings[index] = device.getName();
                            index++;
                        }
                    }

                    //prendo dal bluetooth adapter la lista dei dispositivi paireati
                    //così da poter selezionare il server a cui connettermi
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, strings);
                    listaDispositiviBluetooth.setAdapter(arrayAdapter);
                    listaDispositiviBluetooth.setBackgroundResource(R.drawable.dialog_bg);

                    Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(listaDispositiviBluetooth);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.setCancelable(false);

                    //se viene cliccato un nome di un dispositivo paireato
                    //istanzio una classe client e provo a connettermi
                    //al server selezionato
                    listaDispositiviBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            dialog.dismiss();
                            Message message=Message.obtain();
                            message.what= STATO_IN_ASCOLTO;
                            handler.sendMessage(message);
                            System.out.println("Salvo è scemo3");
                            Client client = new Client(bluetoothDevice[i], handler,SensorFragment.this);
                            client.start();

                            listaDispositiviBluetooth.setVisibility(View.GONE);
                            connettitiBtn.setVisibility(View.GONE);
                            cosaFareTw.setVisibility(View.GONE);

                        }
                    });
                }
            });
        } else {
            connettitiBtn.setVisibility(View.GONE);
            associaBtn.setVisibility(View.GONE);
        }
    }

    /**
     * Metodo che controlla se il bluetooth è già attivo
     * Altrimenti controlla se i permessi sono stati dati
     * e fa richiesta per l'attivazione
     */
    private boolean attivaBluetooth(){

        if (!bluetoothAdapter.isEnabled()) {
            Intent richiesta = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(richiesta, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }
        return true;
    }

    /**
     * Metodo con il quale controllo se il dispositivo è già associato
     * al dispositivo Cantina
     * @param dispositivi
     * @return
     */
    public boolean isCantinaPair(BluetoothDevice[] dispositivi){

        for(int i = 0; i < dispositivi.length; i++){
            //bluetooth connect
            checkBTPermission();
            if(dispositivi[i].getName().contains("Cantina")){
                return  true;
            }
        }
        return false;
    }

    /**
     * Metodo che consente di elaborare ed inviare messaggi associati ad un Thread
     * Fa ausilio dell'interfaccia HandlerCallback usato per la gestione dei
     * messaggi
     */
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message messaggio) {

            switch (messaggio.what)
            {
                //stati di connessione del Bluetooth
                case STATO_IN_ASCOLTO:
                    statoConnessione.setText(R.string.inAscolto);
                    break;
                case STATO_CONNESSO:
                    statoConnessione.setText(R.string.connesso);
                    break;
                case STATO_CONNESSIONE_FALLITO:
                    statoConnessione.setText(R.string.connessioneFallita);
                    temperaturaConserazione.setVisibility(View.GONE);
                    torbidita.setVisibility(View.GONE);
                    umidita.setVisibility(View.GONE);
                    indietro.setVisibility(View.VISIBLE);
                    indietro.setHint(R.string.retry);
                    cosaFareTw.setText(R.string.associazioneFallita);


                    break;
                //se il messaggio è ricevuto
                case STATO_MESSAGGIO_RICEVUTO:
                    //creo un buffer per la lettura del messaggio
                    byte[] bufferDiLettura= (byte[]) messaggio.obj;
                    //e lo minserisco all'intenro di una stringa
                    String messaggioTemporaneo=new String(bufferDiLettura,0,messaggio.arg1);

                    //Si è pensata la seguente assunzione:
                    //Il messaggio da parte del server viene inviato in un unica stringa
                    //dove al suo interno sono presenti dei punti esclamativi
                    //utilizzati per dividere la stringa in 3 stringhe

                    //Suddivido il messaggio attraverso il metodo splitted
                    String[] stringaSplittata = messaggioTemporaneo.split("!");
                    temperaturaConserazione.setVisibility(View.VISIBLE);
                    torbidita.setVisibility(View.VISIBLE);
                    umidita.setVisibility(View.VISIBLE);

                    torbidita.setText(getText(R.string.torbidita)+" "+stringaSplittata[0]);
                    temperaturaConserazione.setText(getText(R.string.temperatura)+" "+stringaSplittata[1]);
                    umidita.setText(getText(R.string.umidita)+" "+stringaSplittata[2]);

                    break;

            }
            return true;
        }
    });

    /**
     * Metodo con il quale si controllano se i permessi, "ACCESS_FINE_LOCATION"
     * e "ACCESS_COARSE_LOCTION", da parte dell'utente sono stati dati.
     *
     * Questi permessi servono all'app di accedere ad una posizione
     * precisa ed approssimativa.
     */
    public boolean checkBTPermission(){


        if(getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            attivaBluetooth();
            /*if(!bool){
                inizializzaBt();
            }*/

            return true;

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ENABLE_PERMISSION);
            return false;
        }

    }

    /**
     * Metodo che viene invocato per ogni chiamata su requestPermissions
     *
     * E' possibile che l'interazione della richiesta di autorizzazione
     * dei permessi con l'utente venga interrotta
     * Perciò è dovere controllare la sua risposta in modo da far
     * riprendere la corretta esecuzione del'app.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_PERMISSION:
                //Se la richiesta viene cancellata il risultato dell'array sarà vuoto
                //In tal caso è doveroso informare l'utente che non potrà più accedere
                //a questa funzionalità a meno che non l'attivi manualmente nel
                //gestore delle app.

                if(hasAllPermissionsGranted(grantResults)){
                    attivaBluetooth();
                    /*if(!bool){
                        inizializzaBt();
                    }*/

                }else if(shouldShowRequestPermissionRationale(permissions[0])){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(true);
                    builder.setTitle(R.string.importanzaDeiPermessi);
                    builder.setMessage(R.string.messaggioPermessi);
                    builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(permissions,REQUEST_ENABLE_PERMISSION);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getContext(), "Ok3", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    ad.setCancelable(false);
                    ad.setTitle("Razionale");
                    ad.setMessage("Messaggio razionale");
                    ad.create();
                    ad.show();
                }

                break;
        }

    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }



    private void prepareModelDescription(String trans){



        Translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Translator.translate(trans).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        drinkDescrizione.setText(s);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}


