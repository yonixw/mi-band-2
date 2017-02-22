package us.cpluspl.yonixw.readheartbatmiband2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "YONI-MI-2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void btnRun(View view) {
        logAllBluetoothDevices();
    }

    void logAllBluetoothDevices() {
        Log.d(TAG, "Initialising Bluetooth connection");

        if(BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice pairedDevice : pairedDevices) {
                if (pairedDevice.getName().contains("MI")) {
                    Log.d(TAG, "Name: " +  pairedDevice.getName());
                    Log.d(TAG, "MAC: " + pairedDevice.getAddress());
                }
            }
        }
    }

}
