package com.example.casodistudiomamange.thread;

import static com.example.casodistudiomamange.fragment.SensorFragment.MY_UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import com.example.casodistudiomamange.fragment.SensorFragment;
import java.io.IOException;

/**
 * Classe con la quale ottengo il soket del dispositivo ottenuto dalla View del SensorFragment
 * Controllo dunque che i permessi mi siano stati dati, connetto il client con il server
 * Ed istanzio la classe recive per ricevere i dati dal server mediante il MY_UUID
 */
public class Client extends Thread {

    /*costanti che rappresentano lo stato del bluetooth*/
    static final int STATO_CONNESSO = 3;
    static final int STATO_CONNESSIONE_FALLITO = 4;

    /*variabili del bluetooth*/
    private BluetoothDevice device;
    private BluetoothSocket socket;

    private Handler handler;
    private Recive recive;
    private SensorFragment sensorFragment;

    public Client(BluetoothDevice device1, Handler handler, SensorFragment sensorFragment) {
        device = device1;
        this.handler = handler;
        this.sensorFragment = sensorFragment;

        try {
            sensorFragment.checkBTPermission();//controllo che i permessi sono stati dati
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);//ottengo il socket del device chiamando questo metodo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            sensorFragment.checkBTPermission();//controllo che i permessi sono stati dati
            socket.connect();//mi connetto al socket ottenuto precedentemente
            Message message=Message.obtain();
            message.what= STATO_CONNESSO;
            handler.sendMessage(message);//imposto l'handler come connesso
            recive =new Recive(socket, handler); // istanzio un oggetto della classe recive per ricevere i dati dal server
            recive.start();

        } catch (IOException e) {
            e.printStackTrace();
            Message message=Message.obtain();
            message.what= STATO_CONNESSIONE_FALLITO;
            handler.sendMessage(message);//imposto l'handler come connessione fallita
        }
    }

}
