package us.cpluspl.yonixw.readheartbatmiband2;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    Handler handler = new Handler(Looper.getMainLooper());
    BLEMiBand2Helper helper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new BLEMiBand2Helper(MainActivity.this, handler);
    }

    @Override
    protected void onDestroy() {

            DisconnectGatt();
        super.onDestroy();
    }

    Context myContext;
    public void btnRun(View view) {
        // Like network card, connect to all devices in Bluetooth (like PC in Netowrk)
        final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice myBand =  findMiBandBluetooth(myBluetoothAdapter);

        myContext = view.getContext();
        if (myBand != null) {
            ConnectToGatt(myBand);
        }
    }












    boolean setup = false;
    public void getHeartBeat() throws InterruptedException {
        /*
        Steps to read heartbeat:
            - Register Notification (like in touch press)
                - Extra step with description
            - Write predefined bytes to control_point to trigger measurement
            - Listener will get result
        */

        Log.d(TAG, "* Getting gatt servie for heartbeat");
        BluetoothGattService myGatService =
                myGatBand.getService(Consts.UUID_SERVICE_HEARTBEAT);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic for hearbeat callback");

            for (BluetoothGattCharacteristic c: myGatService.getCharacteristics()) {
                Log.d(TAG,"Found: " + c.getUuid().toString());
            }

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Consts.UUID_NOTIFICATION_HEARTRATE);
            if (myGatChar != null) {

                Log.d(TAG, "* Statring listening");

                // second parametes is for starting\stopping the listener.
                boolean status =  myGatBand.setCharacteristicNotification(myGatChar, true);
                Log.d(TAG, "* Set notification status :" + status);

                // Set Descriptor:
                BluetoothGattDescriptor myDescriptor
                    = myGatChar.getDescriptor(Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION);
                if (myDescriptor != null) {
                    Log.d(TAG, "Writing decriptors");
                    myDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    status = myGatBand.writeDescriptor(myDescriptor);
                    Log.d(TAG, "Writing decriptors result: " + status);

                    setup =true;
                }

            }

            // Need to wait before first trigger, maybe something about the descriptor....
            Thread.sleep(5000,0);

            getNewHeartBeat();
        }



    }

    public void getNewHeartBeat() throws InterruptedException {
        if (!setup) {
            Toast.makeText(MainActivity.this, "Please setup first!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "* Getting gatt servie for heartbeat");
        BluetoothGattService myGatService =
                myGatBand.getService(Consts.UUID_SERVICE_HEARTBEAT);
        if (myGatService != null) {
            // Now write to trigger heartbeat:
            Log.d(TAG, "* Getting gatt Characteristic for hearbeat trigger");
            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Consts.UUID_START_HEARTRATE_CONTROL_POINT);
            if (myGatChar != null) {
                Log.d(TAG, "* Writing trigger");
                myGatChar.setValue(Consts.BYTE_NEW_HEART_RATE_SCAN);

                boolean status =  myGatBand.writeCharacteristic(myGatChar);
                Log.d(TAG, "* Writting trigger status :" + status);
            }
        }
    }

    public void btnTest(View view) throws InterruptedException {
        //readDataExampleToTest();
        getTouchNotifications();


    }

    public void btnSetuphearRate(View view) throws InterruptedException {
        getHeartBeat();
    }

    public void btnTestHeartRate(View view) throws InterruptedException {
        getNewHeartBeat();
    }

}

/*
Credit and thanks:

https://github.com/lwis/miband-notifier/
http://allmydroids.blogspot.co.il/2014/12/xiaomi-mi-band-ble-protocol-reverse.html
https://github.com/Freeyourgadget/Gadgetbridge
http://stackoverflow.com/questions/20043388/working-with-ble-android-4-3-how-to-write-characteristics

// Available services\Characteristics:
http://jellygom.com/2016/09/30/Mi-Band-UUID.html
https://github.com/Freeyourgadget/Gadgetbridge/blob/master/app/src/main/java/nodomain/freeyourgadget/gadgetbridge/devices/miband/MiBand2Service.java
https://devzone.nordicsemi.com/question/310/notificationindication-difference/

// Heartbeat info
http://stackoverflow.com/questions/36311874/heart-rate-measuring-using-xiaomi-miband-and-ble
https://github.com/AlexanderHryk/MiFood/
again: http://stackoverflow.com/questions/20043388/working-with-ble-android-4-3-how-to-write-characteristics
https://github.com/dkhmelenko/miband-android/blob/master/miband-sdk/src/main/java/com/khmelenko/lab/miband/model/Protocol.java

http://stackoverflow.com/questions/7378936/how-to-show-toast-message-from-background-thread
*/
