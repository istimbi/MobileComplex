package com.example.monilek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private OutputStream outputStream;
    private InputStream inStream;

    byte[]startStream = {0x77, 0x66, 0x55, (byte)0xAA, 0x42, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x02, 0x21 };

    byte[]stopStream = {0x77, 0x66, 0x55, (byte)0xAA, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x1E };
    String sample="";
    TextView count;
    String str = null;
    int a = 0;
    File targetFile;
    Boolean stopStreamBool = false;
    OutputStream outStream = null;
    Button connect,start_Stream,stop_Stream, status;
    int FILE_SIZE=0;
    BluetoothSocket socket;
    final int BUFFER_SIZE = 1024;
    byte[] buffer = null;
    long counts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = findViewById(R.id.count);
        status = findViewById(R.id.status);
        connect = findViewById(R.id.connect);
        start_Stream = findViewById(R.id.start_stream);
        stop_Stream = findViewById(R.id.stop_stream);

        targetFile = new File(getFilesDir(), "EEG.txt");
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        //Request Permission
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Get permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        start_Stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FILE_SIZE = 0;
                stopStreamBool = false;
                try {

                    write(startStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                run();


                Thread thread = new Thread(new Runnable(){


                    @Override
                    public void run() {

                        while(!stopStreamBool) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (FILE_SIZE*2 > 1024) {

                                        count.setText(FILE_SIZE/1000 + "Mb");
                                    } else {
                                        count.setText(FILE_SIZE*2 + "Kb");
                                    }


                                }
                            });

                        }
                    }
                });
                thread.start();

            }
        });
        stop_Stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopStreamBool = true;
                try {
                    write(stopStream);
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });


    }

    private void init() throws IOException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();
                    for (Object dev: devices
                    ) {
                        BluetoothDevice device = (BluetoothDevice) dev;
                        if (device.getName().equals("MobileK")  ){
                            ParcelUuid[] uuids = device.getUuids();
                            socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                            socket.connect();
                            if (socket.isConnected()){

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    status.setBackground(this.getResources().getDrawable(R.drawable.round_connected));
                                    status.setText("Connected");
                                }
                            }
                            outputStream = socket.getOutputStream();
                            inStream = socket.getInputStream();

                        }
                    }
                }
                else{

                    Log.e("error", "No appropriate paired devices.");
                    Toast.makeText(getApplicationContext(),"No bonded device MobileK found", Toast.LENGTH_SHORT).show();
                }
            } else {

                Log.e("error", "Bluetooth is disabled.");
                blueAdapter.enable();
                if (blueAdapter.isEnabled()){
                    init();
                }
            }
        }
    }
    public void write(byte[] s) throws IOException {
        outputStream.write(s);
    }




    public void run() {

        try {
            outStream = new FileOutputStream(targetFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                while (!stopStreamBool){
                    try {
                        if ( buffer ==null && inStream.available() > BUFFER_SIZE){
                            buffer = new byte[BUFFER_SIZE];
                            inStream.read(buffer, 0, BUFFER_SIZE );


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    if (buffer!=null ){


                        for (int i = 0; i<BUFFER_SIZE-65; i++){
                            if (buffer[i] == -86 && buffer[i+1] == 85 && buffer[i+2] == 102 && buffer[i+3] == 119 && buffer[i+4] == -93 && buffer[i+65] == -86){
                                byte[] array= {buffer[i+9], buffer[i+10], buffer[i+11], buffer[i+12]};
                                int timeFromDevice = ByteBuffer.wrap(array).getInt();
                                long timestamp1 = System.currentTimeMillis();

                                Timestamp timestamp = new Timestamp(timestamp1);
                                timestamp.getNanos();
                                sample += timestamp+" "+timeFromDevice+" ";
                                for (int j = 0; j< 16; j++){
                                    byte[] sampleArray = { buffer[i+13+j*3], buffer[i+14+j*3], buffer[i+15+j*3]};
                                    int sampleValue =  Bit24ToInt32(sampleArray);
                                    sample += sampleValue + " ";
                                }
                                sample+= "\r\n";



                            }
                        }
                        try {
                            outStream.write(sample.getBytes(), 0, sample.length());

                            sample = "";
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buffer=null;

                    }


                }

            }
        });
        thread.start();



    }


    private static int Bit24ToInt32(byte[] byteArray)
    {

        int result = (
                ((0xFF & byteArray[0]) << 16) |
                        ((0xFF & byteArray[1]) << 8) |
                        (0xFF & byteArray[2])
        );
        if ((result & 0x00800000) > 0)
        {
            result = (int)((long)result | (long)0xFF000000);
        }
        else
        {
            result = (int)((long)result & (long)0x00FFFFFF);
        }
        return result;
    }



    @Override
    protected void onDestroy() {
        try {
            write(stopStream);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}