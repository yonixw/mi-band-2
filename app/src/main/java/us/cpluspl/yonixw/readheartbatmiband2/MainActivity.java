package us.cpluspl.yonixw.readheartbatmiband2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "YONI-MI-2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void btnRun(View view) {
        // Like network card, connect to all devices in Bluetooth (like PC in Netowrk)
        final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        findMiBandBluetooth(myBluetoothAdapter);
    }

    BluetoothDevice findMiBandBluetooth(BluetoothAdapter myBluetoothAdapter) {
        Log.d(TAG, "Initialising Bluetooth connection");

        if(myBluetoothAdapter.isEnabled()) {
            for (BluetoothDevice pairedDevice : myBluetoothAdapter.getBondedDevices()) {
                if (pairedDevice.getName().contains("MI")) {
                    Log.d(TAG, "Name: " +  pairedDevice.getName());
                    Log.d(TAG, "MAC: " + pairedDevice.getAddress());

                    return pairedDevice;
                }
            }
        }

        return  null; // Not found
    }

}

/*
Credit and thanks:

https://github.com/lwis/miband-notifier/

*/
