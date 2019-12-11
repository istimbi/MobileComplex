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
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    Queue<Integer> values = new LinkedList<>();
     byte[]startStream = {0x77, 0x66, 0x55, (byte)0xAA, 0x42, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x02, 0x21 };

    byte[]stopStream = {0x77, 0x66, 0x55, (byte)0xAA, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x1E };

    byte[] BIASNEgativeSignalDerivationCS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x0e, (byte) 0xFF, 0x00, 0x00, 0x03, 0x2B };
    byte[] BIASNEgativeSignalDerivationCS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x0e, (byte) 0xFF, 0x00, 0x00, 0x03, 0x2C };
    byte[] BIASPositiveSignalDerivationCS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x0D, (byte) 0xFF, 0x00, 0x00, 0x03, 0x2B };
    byte[] BIASPositiveSignalDerivationCS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x0D, (byte) 0xFF, 0x00, 0x00, 0x03, 0x2B };
    byte[] EnableInternalReferenceCS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x03, (byte) 0xEC, 0x00, 0x00, 0x03, 0x0D };
    byte[] EnableInternalReferenceCS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x03, (byte) 0xEC, 0x00, 0x00, 0x03, 0x0E };
    byte[] LeadOffCurentCS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x04, 0x02, 0x00, 0x00, 0x02, 0x24 };
    byte[] LeadOffCurentCS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x04, 0x02, 0x00, 0x00, 0x02, 0x25 };
    byte[] SetChannel1CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x05, 0x68, 0x00, 0x00, 0x02, (byte) 0x8B };
    byte[] SetChannel1CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x05, 0x68, 0x00, 0x00, 0x02, (byte) 0x8C };
    byte[] SetChannel2CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x06, 0x68, 0x00, 0x00, 0x02, (byte) 0x8C };
    byte[] SetChannel2CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x06, 0x68, 0x00, 0x00, 0x02, (byte) 0x8D };
    byte[] SetChannel3CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x07, 0x68, 0x00, 0x00, 0x02, (byte) 0x8D };
    byte[] SetChannel3CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x07, 0x68, 0x00, 0x00, 0x02, (byte) 0x8E };
    byte[] SetChannel4CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x08, 0x68, 0x00, 0x00, 0x02, (byte) 0x8E };
    byte[] SetChannel4CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x08, 0x68, 0x00, 0x00, 0x02, (byte) 0x8F };
    byte[] SetChannel5CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x09, 0x68, 0x00, 0x00, 0x02, (byte) 0x8F };
    byte[] SetChannel5CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x09, 0x68, 0x00, 0x00, 0x02, (byte) 0x90 };
    byte[] SetChannel6CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x0A, 0x68, 0x00, 0x00, 0x02, (byte) 0x90 };
    byte[] SetChannel6CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x0A, 0x68, 0x00, 0x00, 0x02, (byte) 0x91 };
    byte[] SetChannel7CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x0B, 0x68, 0x00, 0x00, 0x02, (byte) 0x91 };
    byte[] SetChannel7CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x0B, 0x68, 0x00, 0x00, 0x02, (byte) 0x92 };
    byte[] SetChannel8CS1 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x01, 0x0C, 0x68, 0x00, 0x00, 0x02, (byte) 0x92 };
    byte[] SetChannel8CS2 = {0x77, 0x66, 0x55, (byte) 0xAA, 0x41, 0x00, 0x00, 0x02, 0x0C, 0x68, 0x00, 0x00, 0x02, (byte) 0x93 };

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;


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
    Viewport viewport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = findViewById(R.id.count);
        status = findViewById(R.id.status);
        connect = findViewById(R.id.connect);
        start_Stream = findViewById(R.id.start_stream);
        stop_Stream = findViewById(R.id.stop_stream);


        /*  GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
         viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(99999999);
        viewport.setScrollable(true);
        viewport.setScalable(true);


        Thread chart = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (values != null && !values.isEmpty()){
                        //addEntry(values.element());
                        series.appendData(new DataPoint(lastX++, Math.abs(values.element())), true, 500);
                    viewport.scrollToEnd();
                        values.remove();
                    }
                }
            }
        });
        chart.start();
*/

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MobileK");
        /*String dirName = "MobileK";
        File myDir = new File("sdcard", dirName);*/

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Get permission", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }

        int code = this.getPackageManager().checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                this.getPackageName());

        if (code == PackageManager.PERMISSION_GRANTED) {
            if(!file.exists())
                file.mkdirs();
            targetFile = new File(file, "EEG.txt");//getFilesDir()
            if (!targetFile.exists()) {
                try {
                    targetFile.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
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
               } catch (IOException | InterruptedException e) {
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

                               if (FILE_SIZE * 2 > 1024) {

                                       count.setText(FILE_SIZE*2/1024 + "Mb");
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

    private void init() throws InterruptedException, IOException {
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
                            try {
                                socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                socket.connect();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this,"Unable to connect to Mobile Complex. Make sure that you power it up>",Toast.LENGTH_LONG).show();

                            }
                            if (socket.isConnected()){

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    status.setBackground(this.getResources().getDrawable(R.drawable.round_connected));
                                    status.setText("Connected");
                                }
                            }
                            outputStream = socket.getOutputStream();
                            inStream = socket.getInputStream();

                            //run();
                            write(BIASNEgativeSignalDerivationCS1);
                            Thread.sleep(100);
                            write(EnableInternalReferenceCS1);
                            Thread.sleep(100);
                            write(LeadOffCurentCS1);
                            Thread.sleep(100);
                            write(SetChannel1CS1);
                            Thread.sleep(100);
                            write(SetChannel2CS1);
                            Thread.sleep(100);
                            write(SetChannel3CS1);
                            Thread.sleep(100);
                            write(SetChannel4CS1);
                            Thread.sleep(100);
                            write(SetChannel5CS1);
                            Thread.sleep(100);
                            write(SetChannel6CS1);
                            Thread.sleep(100);
                            write(SetChannel7CS1);
                            Thread.sleep(100);
                            write(SetChannel8CS1);

                            Thread.sleep(100);
                            write(BIASNEgativeSignalDerivationCS2);
                            Thread.sleep(100);
                            write(EnableInternalReferenceCS2);
                            Thread.sleep(100);
                            write(LeadOffCurentCS2);
                            Thread.sleep(100);
                            write(SetChannel1CS2);
                            Thread.sleep(100);
                            write(SetChannel2CS2);
                            Thread.sleep(100);
                            write(SetChannel3CS2);
                            Thread.sleep(100);
                            write(SetChannel4CS2);
                            Thread.sleep(100);
                            write(SetChannel5CS2);
                            Thread.sleep(100);
                            write(SetChannel6CS2);
                            Thread.sleep(100);
                            write(SetChannel7CS2);
                            Thread.sleep(100);
                            write(SetChannel8CS2);

                        }
                    }
                    //BluetoothDevice device = (BluetoothDevice) devices[1];



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


    /*public void run() throws IOException, InterruptedException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;

            Thread.sleep(10000);
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
        String str = new String(buffer, "UTF-8");
                count.setText(bytes);

    }*/


    /*public void run() throws IOException, InterruptedException {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                final int BUFFER_SIZE = 1024;
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytes = 0;
                while (a<50000){
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        str = new String(buffer, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            count.setText(""+str);
                        }
                    });
                    bytes = 0;
                    buffer = new byte[BUFFER_SIZE];
                    a++;
                }
            }
        });
        thread.start();



    }*/
    public void run() {

        try {
            outStream = new FileOutputStream(targetFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                /*try {
                    String startString = "TimestampAndroid   TimeFromStartDevice  Channel1  Channel2  Channel3  ...  Channel16\r\n";
                    outStream.write(startString.getBytes(), 0, startString.length());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                while (!stopStreamBool){
                    try {

                        //Log.e("BufferAvaliable", String.valueOf(inStream.available()));
                        if ( buffer == null && inStream.available() > BUFFER_SIZE){
                            buffer = new byte[BUFFER_SIZE];
                            inStream.read(buffer, 0, BUFFER_SIZE );


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                        if (buffer!=null ){
                           // Thread writeToFile = new Thread(new Runnable() {
                             //   @Override
                               // public void run() {

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
                                        //if (j == 0){
                                            //addEntry(sampleValue);
                                            //values.add(sampleValue);
                                        //}
                                        sample += sampleValue + " ";
                                    }
                                    sample+= "\r\n";



                                }
                            }
                                    try {
                                        outStream.write(sample.getBytes(), 0, sample.length());//BUFFER_SIZE);

                                        sample = "";
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //outStream.write(buffer, 0, BUFFER_SIZE);
                            FILE_SIZE++;
                                    buffer=null;

                              //  }
                            //});
                           // writeToFile.start();
                        }


                }

            }
        });
        thread.start();



    }
       /*
          for (int i = 0; i<BUFFER_SIZE-65; i++){
                                if (buffer[i] == 170 && buffer[i+1] == 85 && buffer[i+2] == 102 && buffer[i+3] == 119 && buffer[i+4] == 163 && buffer[i+65] == 170){
                                    byte[] array= {buffer[i+9], buffer[i+10], buffer[i+11], buffer[i+12]};
                                    int timestamp = ByteBuffer.wrap(array).getInt();
                                    sample += timestamp+" ";
                                    for (int j = 0; j< 16; j++){
                                        byte[] sampleArray = { buffer[i+12+j*3], buffer[i+13+j*3], buffer[i+14+j*3]};
                                        int sampleValue =  Bit24ToInt32(sampleArray);
                                        sample += sampleValue + " ";
                                    }
                                    sample+="\r\n";


                                }
                            }
                        outStream.write(sample.getBytes(), 0, BUFFER_SIZE);
     */


       private void addEntry(int value) {
           // here, we choose to display max 10 points on the viewport and we scroll to end
           //

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

    /*private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d("0", "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.write(startStream);
        mConnectedThread.start();
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d("0", "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;




            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d("0", "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e("0", "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d("0", "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("0", "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
*/

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
