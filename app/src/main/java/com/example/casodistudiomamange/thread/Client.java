package com.example.casodistudiomamange.thread;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

public class Client extends Thread{

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private Context context;
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");


}
