package com.example.casodistudiomamange.fragment;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Message;
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
    Button connettitiBtn;
    TextView temperaturaConserazione, torbidita, statoConnessione, umidita, info;

    /*variabili della bevanda selezionata*/
    TextView drinkName;
    ImageView drinkImg;
    TextView drinkDescrizione;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        /*Controlla che i permessi siano stati dai*/
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



        connettitiBtn = v.findViewById(R.id.connect);
        listaDispositiviBluetooth = v.findViewById(R.id.listaDispositiviBluetooth);
        temperaturaConserazione = v.findViewById(R.id.temperatura);
        statoConnessione = v.findViewById(R.id.statoConnessione);
        torbidita = v.findViewById(R.id.torbidita);
        umidita = v.findViewById(R.id.umidita);

        connettitiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaDispositiviBluetooth.setVisibility(View.VISIBLE);
                //controllo se i permessi bluetooth sono stati dati
                checkBTPermission();

                //prendo dal bluetooth adapter la lista dei dispositivi paireati
                //così da poter selezionare il server a cui connettermi
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                String[] strings=new String[pairedDevices.size()];
                bluetoothDevice=new BluetoothDevice[pairedDevices.size()];
                int index=0;

                if (pairedDevices.size() > 0) {
                    //per ogni device paireato lo aggiungo nella lista dei bluetooth device
                    //ed aggiungo il nome del dispositivo per visualizzarlo all'inerno della listView
                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothDevice[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    //creo un adapter per inserire i dispositivi all'intenro dell'array di stringhe
                    //nella listView di dispositivi bluetooth inizialmente vuota
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,strings);
                    listaDispositiviBluetooth.setAdapter(arrayAdapter);
                }

                //se viene cliccato un nome di un dispositivo paireato
                //istanzio una classe client e provo a connettermi
                //al server selezionato
                listaDispositiviBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Message message=Message.obtain();
                        message.what= STATO_IN_ASCOLTO;
                        handler.sendMessage(message);

                        Client client = new Client(bluetoothDevice[i], handler,SensorFragment.this);
                        client.start();

                        listaDispositiviBluetooth.setVisibility(View.GONE);
                        connettitiBtn.setVisibility(View.GONE);
                        temperaturaConserazione.setVisibility(View.VISIBLE);
                        torbidita.setVisibility(View.VISIBLE);
                        umidita.setVisibility(View.VISIBLE);

                    }
                });

            }
        });

        return v;
    }

    /**
     * Metodo che controlla se il bluetooth è già attivo
     * Altrimenti controlla se i permessi sono stati dati
     * e fa richiesta per l'attivazione
     */
    private void attivaBluetooth(){

        if (!bluetoothAdapter.isEnabled()) {
            Intent richiesta = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(richiesta, REQUEST_ENABLE_BLUETOOTH);
        }

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
                    torbidita.setText(R.string.torbidita+stringaSplittata[0]);
                    temperaturaConserazione.setText(R.string.temperatura+stringaSplittata[1]);
                    umidita.setText("Umidita' di conservazione: "+stringaSplittata[2]);

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
    public void checkBTPermission(){

        int permissionCheck = getContext().checkSelfPermission("android.Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck+= getContext().checkSelfPermission("android.Manifest.permission.ACCESS_COARSE_LOCATION");

        //se il permesso è stato dato allora attiva il bluetooth
        if(permissionCheck == 0){
            attivaBluetooth();

            //altrimenti mostra il razionale, ovvero il perchè è importante dare i seguenti permessi
        } else if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.importanzaDeiPermessi);
            builder.setMessage(getText(R.string.messaggioPermessi)+"\n"+getText(R.string.richiestaPermessi));

            builder.setPositiveButton(
                    "Si",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //l'utente è sicuro di non voler chieder epiù i permessi
                        }
                    });

            builder.setNegativeButton(
                    "Annulla",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //l'utente vuole fare richiesta di accettazione dei permessi
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_ENABLE_PERMISSION);
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_ENABLE_PERMISSION);
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
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    attivaBluetooth();

                }  else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.importanzaDeiPermessi);
                    builder.setMessage(R.string.messaggioPermessi);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return;
        }

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


