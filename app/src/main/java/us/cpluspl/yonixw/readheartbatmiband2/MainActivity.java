package us.cpluspl.yonixw.readheartbatmiband2;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
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

import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "YONI-MI-2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    protected void onDestroy() {
        if (myGatBand != null)
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

    BluetoothDevice findMiBandBluetooth(BluetoothAdapter myBluetoothAdapter) {
        Log.d(TAG, "(*) Initialising Bluetooth connection");

        if(myBluetoothAdapter.isEnabled()) {
            for (BluetoothDevice pairedDevice : myBluetoothAdapter.getBondedDevices()) {
                if (pairedDevice.getName().contains("MI")) {
                    Log.d(TAG, "\tName: " +  pairedDevice.getName());
                    Log.d(TAG, "\tMAC: " + pairedDevice.getAddress());

                    return pairedDevice;
                }
            }
        }

        return  null; // Not found
    }

    private BluetoothGattCallback myGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if(status == BluetoothGatt.GATT_SUCCESS)
            {

            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            switch(newState)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "Gatt state: connected");
                    gatt.discoverServices();

                    break;
                default:
                    Log.d(TAG, "Gatt state: not connected");
                    break;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.d(TAG, "Write successful: " + Arrays.toString(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "Read successful: " + Arrays.toString(characteristic.getValue()));
            super.onCharacteristicRead(gatt, characteristic, status);
        }
    };

    BluetoothGatt myGatBand;
    public void ConnectToGatt(BluetoothDevice myBand) {
        // GATT is Just another specification:
        // https://www.bluetooth.com/specifications/gatt/services
        Log.d(TAG, "(*) Establishing connection to gatt");
        myGatBand = myBand.connectGatt(myContext, true ,myGattCallback );
    }

    public  void  DisconnectGatt()  {
        if(myGatBand != null)
        {
            new Handler(Looper.getMainLooper()).post(new Runnable()
            {
                @Override public void run()
                {
                    myGatBand.disconnect();
                    myGatBand.close();
                    myGatBand = null;
                }
            });
        }
    }

    public void readDataExampleToTest() {
        Log.d(TAG, "* Getting gatt servie for mi band 2");
        BluetoothGattService myGatService =
                myGatBand.getService(Consts.UUID_SERVICE_GENERIC);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic for device name");

            for (BluetoothGattCharacteristic c: myGatService.getCharacteristics()) {
                Log.d(TAG,"Found: " + c.getUuid().toString());
            }

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Consts.UUID_CHARACTERISTIC_DEVICE_NAME);
            if (myGatChar != null) {
                Log.d(TAG, "* Reading data");

                /*
                byte[] value = new byte[10];
                value[0] = (byte) (2 & 0xFF);
                myGatChar.setValue(value);
                boolean status = myGatBand.writeCharacteristic(myGatChar);*/

                boolean status =  myGatBand.readCharacteristic(myGatChar);
                Log.d(TAG, "* Read status :" + status);
            }
        }
    }

    public void btnTest(View view) {
        readDataExampleToTest();
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

*/
