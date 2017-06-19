package com.example.ledtoggletemperature;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    final Handler handler = new Handler();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("ChristinesRPi")) { //Note, you will need to change this to match the name of your device{
                    mmDevice = device;
                    break;
                }
            }
        }

        final Button onbutton = (Button) findViewById(R.id.onbutton);
        final Button offbutton = (Button) findViewById(R.id.offbutton);
        final Button tempbutton = (Button) findViewById(R.id.tempbutton);
        onbutton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click
                (new Thread(new WorkerThread("On"))).start();
            }
        });
        offbutton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click
                (new Thread(new WorkerThread("Off"))).start();
            }
        });
        offbutton.setEnabled(false);
        tempbutton.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click
                (new Thread(new WorkerThread("Temp"))).start();
            }
        });

    }

    public void sendBtMsg(String msg){
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()){
                mmSocket.connect();
            }
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final class WorkerThread implements Runnable {

        private String btMsg;

        public WorkerThread(String msg) {
            btMsg = msg;
        }

        public void run() {
            byte delimiter = 33;
            int readBufferPosition = 0;
            final Button onbutton = (Button) findViewById(R.id.onbutton);
            final Button offbutton = (Button) findViewById(R.id.offbutton);
            final TextView temperatureOutput = (TextView) findViewById(R.id.temperatureOutput);

            sendBtMsg(btMsg);

            while(!Thread.currentThread().isInterrupted()) {

                int bytesAvailable;
                boolean workDone = false;

                try {
                    final InputStream mmInputStream;
                    mmInputStream = mmSocket.getInputStream();
                    bytesAvailable = mmInputStream.available();
                    if(bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];
                        byte[] readBuffer = new byte[1024];
                        mmInputStream.read(packetBytes);

                        for(int i=0;i<bytesAvailable;i++) {
                            byte b = packetBytes[i];
                            if(b == delimiter) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;

                                //The variable data now contains our full command
                                handler.post(new Runnable() {
                                    public void run() {
                                        if(data.equals("Light on")){
                                            onbutton.setEnabled(false);
                                            offbutton.setEnabled(true);
                                        }
                                        else if(data.equals("Light off")){
                                            onbutton.setEnabled(true);
                                            offbutton.setEnabled(false);
                                        }
                                        else{
                                            temperatureOutput.setText(data);
                                        }
                                    }
                                });

                                workDone = true;
                                break;


                            }
                            else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }

                        if (workDone){
                            mmSocket.close();
                            break;
                        }

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }


}


