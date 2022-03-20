package com.example.casodistudiomamange.thread;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

class Receive extends Thread
{
    static final int STATO_CONNESSIONE_FALLITO =4;
    static final int STATO_MESSAGGIO_RICEVUTO =5;
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private Handler handler;
    // private final OutputStream outputStream;

    public Receive(BluetoothSocket socket, Handler handler)
    {
        bluetoothSocket=socket;
        InputStream tempIn=null;
        this.handler = handler;


        try {
            tempIn=bluetoothSocket.getInputStream();
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
                bytes=inputStream.read(buffer);
                handler.obtainMessage(STATO_MESSAGGIO_RICEVUTO,bytes,-1,buffer).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what= STATO_CONNESSIONE_FALLITO;
                handler.sendMessage(message);
                break;
            }
        }
    }

}