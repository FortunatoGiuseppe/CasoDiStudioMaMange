package com.example.casodistudiomamange.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

/**
 * Classe con la quale ottengo il soket del dispositivo dalla Classe Client
 * Controllo dunque che i permessi mi siano stati dati
 * Controllo che il Client si sia connesso al Server
 * Ed invio la stringa ottentuta dal server al client per visualizzarla
 * nella view del SensorFragment
 */
class Recive extends Thread
{
    /*costanti che rappresentano lo stato del bluetooth*/
    static final int STATO_CONNESSIONE_FALLITO =4;
    static final int STATO_MESSAGGIO_RICEVUTO =5;

    /*variabili del bluetooth*/
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;

    private Handler handler;

    public Recive(BluetoothSocket socket, Handler handler)
    {
        bluetoothSocket=socket;
        InputStream tempIn=null;
        this.handler = handler;


        try {
            tempIn=bluetoothSocket.getInputStream();//provo a prendere dal soket lo Stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream=tempIn;
    }

    public void run()
    {
        byte[] buffer=new byte[1024];
        int bytes;

        while (true)
        {
            try {
                bytes=inputStream.read(buffer);//leggo la srtinga ottenuta
                handler.obtainMessage(STATO_MESSAGGIO_RICEVUTO,bytes,-1,buffer).sendToTarget();//setto emssaggio dell'handler in caso di lettura
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what= STATO_CONNESSIONE_FALLITO;
                handler.sendMessage(message);//setto il messaggio dell'handler come connessione fallita in caso è fallita la lettura dal server
                break;
            }
        }
    }

}