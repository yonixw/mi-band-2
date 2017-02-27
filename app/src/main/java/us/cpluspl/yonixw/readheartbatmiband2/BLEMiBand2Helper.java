package us.cpluspl.yonixw.readheartbatmiband2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Handler;

/**
 * Created by YoniWas on 27/02/2017.
 */
public class BLEMiBand2Helper {
    public static final String TAG = "YONI-MI-2";

    private Context myContext = null;
    private android.os.Handler myHandler = null;

    private BluetoothDevice activeDevice = null; // The mi band
    private boolean isConnectedToGatt = false; // the gatt connection
    private BluetoothGatt myGatBand = null;


    private  BLEMiBand2Helper() {}

    public BLEMiBand2Helper(Context context, android.os.Handler handler) {
        myContext = context;
        myHandler = handler;
    }

    public  boolean isConnected() {
        return  isConnectedToGatt;
    }

    /* =========  Handling Initializing  ============== */

    void findBluetoothDevice(BluetoothAdapter myBluetoothAdapter,
                                        String filter) {
        Log.d(TAG, "(*) Initialising Bluetooth connection for device: " + filter);

        if(myBluetoothAdapter.isEnabled()) {
            for (BluetoothDevice pairedDevice : myBluetoothAdapter.getBondedDevices()) {
                if (pairedDevice.getName().contains(filter /*Like MI*/)) {
                    Log.d(TAG, "\tDevice Name: " +  pairedDevice.getName());
                    Log.d(TAG, "\tDevice MAC: " + pairedDevice.getAddress());

                    activeDevice =  pairedDevice;
                    break;
                }
            }
        }

        Log.d(TAG, "\tDidnt find any device!");
    }

    public void ConnectToGatt() {
        if (activeDevice != null) {
            // GATT is Just another specification:
            // https://www.bluetooth.com/specifications/gatt/services
            Log.d(TAG, "(*) Establishing connection to gatt");
            myGatBand = activeDevice.connectGatt(myContext, true ,myGattCallback );
        }
        else  {
            Log.d(TAG, "(*) Cant Establish connection to gatt, device is null");
        }
    }

    public  void  DisconnectGatt()  {
        if(myGatBand != null && isConnectedToGatt)
        {
            myHandler.post(new Runnable()
            {
                @Override public void run()
                {
                    myGatBand.disconnect();
                    myGatBand.close();
                    myGatBand = null;
                    isConnectedToGatt = false;
                }
            });
        }
    }

    private BluetoothGattCallback myGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            if(status == BluetoothGatt.GATT_SUCCESS)
            {

            }
            Log.d(TAG, "Service discovered with status " + status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            switch(newState)
            {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "Gatt state: connected");
                    gatt.discoverServices();
                    isConnectedToGatt = true;
                    break;
                default:
                    Log.d(TAG, "Gatt state: not connected");
                    raiseonDisconnect();
                    isConnectedToGatt = false;
                    break;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.d(TAG, "Write successful: " + Arrays.toString(characteristic.getValue()));
            raiseonWrite(gatt,characteristic,status);
            super.onCharacteristicWrite(gatt,characteristic,status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "Read successful: " + Arrays.toString(characteristic.getValue()));
            raiseonRead(gatt,characteristic,status);
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, " - Notifiaction UUID: " +  characteristic.getUuid().toString());
            Log.d(TAG, " - Notifiaction value: " +  Arrays.toString(characteristic.getValue()));
            raiseonNotification(gatt, characteristic);
            super.onCharacteristicChanged(gatt, characteristic);
        }


    };

    public interface BLEAction {
        void onDisconnect();
        void onRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        void onWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
        void onNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }

    /* =========  Handling Events  ============== */

    private ArrayList<BLEAction> listeners = new ArrayList<BLEAction>();

    public void addListener(BLEAction toAdd) {
        listeners.add(toAdd);
    }

    public void removeListener(BLEAction toDel) {
        listeners.remove(toDel);
    }

    public void raiseonNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onNotification( gatt,characteristic);
    }

    public void raiseonRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onRead( gatt,characteristic,status);
    }

    public void raiseonWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onRead( gatt,characteristic,status);
    }

    public void raiseonDisconnect() {
        // Notify everybody that may be interested.
        for (BLEAction listener : listeners)
            listener.onDisconnect();
    }

    /* =========  Handling Data  ============== */

    public void readData(UUID service, UUID Characteristics) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant read from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service /*Consts.UUID_SERVICE_GENERIC*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics /*Consts.UUID_CHARACTERISTIC_DEVICE_NAME*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Reading data");

                boolean status =  myGatBand.readCharacteristic(myGatChar);
                Log.d(TAG, "* Read status :" + status);
            }
        }
    }

    public void writeData(UUID service, UUID Characteristics,byte[] data) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant read from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service /*Consts.UUID_SERVICE_HEARTBEAT*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics /*Consts.UUID_START_HEARTRATE_CONTROL_POINT*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Writing trigger");
                myGatChar.setValue(data /*Consts.BYTE_NEW_HEART_RATE_SCAN*/);

                boolean status =  myGatBand.writeCharacteristic(myGatChar);
                Log.d(TAG, "* Writting trigger status :" + status);
            }
        }
    }

    public void getNotifications(UUID service, UUID Characteristics) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant get notifications from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service/*Consts.UUID_SERVICE_MIBAND_SERVICE*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics/*Consts.UUID_BUTTON_TOUCH*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Statring listening");

                // second parametes is for starting\stopping the listener.
                boolean status =  myGatBand.setCharacteristicNotification(myGatChar, true);
                Log.d(TAG, "* Set notification status :" + status);
            }
        }
    }

    /**
     * Get notification but also set descriptor to Enable notification. You need to wait couple of
     *      seconds before you could use it (at least in the mi band 2)
     * @param service
     * @param Characteristics
     */
    public void getNotificationsWithDescriptor(UUID service, UUID Characteristics, UUID Descriptor) {
        if (!isConnectedToGatt || myGatBand == null) {
            Log.d(TAG, "Cant get notifications from BLE, not initialized.");
            return;
        }

        Log.d(TAG, "* Getting gatt service, UUID:" + service.toString());
        BluetoothGattService myGatService =
                myGatBand.getService(service/*Consts.UUID_SERVICE_MIBAND_SERVICE*/);
        if (myGatService != null) {
            Log.d(TAG, "* Getting gatt Characteristic. UUID: " + Characteristics.toString());

            BluetoothGattCharacteristic myGatChar
                    = myGatService.getCharacteristic(Characteristics/*Consts.UUID_BUTTON_TOUCH*/);
            if (myGatChar != null) {
                Log.d(TAG, "* Statring listening");

                // second parametes is for starting\stopping the listener.
                boolean status = myGatBand.setCharacteristicNotification(myGatChar, true);
                Log.d(TAG, "* Set notification status :" + status);

                BluetoothGattDescriptor myDescriptor
                        = myGatChar.getDescriptor(Descriptor/*Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION*/);
                if (myDescriptor != null) {
                    Log.d(TAG, "Writing decriptor: " + Descriptor.toString());
                    myDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    status = myGatBand.writeDescriptor(myDescriptor);
                    Log.d(TAG, "Writing decriptors result: " + status);
                }
            }
        }
    }

}





