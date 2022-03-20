package com.example.casodistudiomamange.thread;

import static com.example.casodistudiomamange.fragment.SensorFragment.MY_UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.example.casodistudiomamange.fragment.SensorFragment;

import java.io.IOException;


public class Client extends Thread {
    static final int STATO_CONNESSO = 3;
    static final int STATO_CONNESSIONE_FALLITO = 4;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private Handler handler;
    private Receive recive;
    private SensorFragment sensorFragment;

    public Client(BluetoothDevice device1, Handler handler, SensorFragment sensorFragment) {
        device = device1;
        this.handler = handler;
        this.sensorFragment = sensorFragment;

        try {
            sensorFragment.checkBTPermission();
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            sensorFragment.checkBTPermission();
            socket.connect();
            Message message=Message.obtain();
            message.what= STATO_CONNESSO;
            handler.sendMessage(message);
            recive =new Receive(socket, handler);
            recive.start();

        } catch (IOException e) {
            e.printStackTrace();
            Message message=Message.obtain();
            message.what= STATO_CONNESSIONE_FALLITO;
            handler.sendMessage(message);
        }
    }

}
