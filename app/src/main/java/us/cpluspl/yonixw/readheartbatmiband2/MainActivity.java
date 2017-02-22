package us.cpluspl.yonixw.readheartbatmiband2;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "YONI-MI-2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    };

    public void ConnectToGatt(BluetoothDevice myBand) {
        // GATT is Just another specification:
        // https://www.bluetooth.com/specifications/gatt/services
        Log.d(TAG, "(*) Establishing connection to gatt");
        myBand.connectGatt(myContext, true ,myGattCallback );
    }



}

/*
Credit and thanks:

https://github.com/lwis/miband-notifier/

*/
