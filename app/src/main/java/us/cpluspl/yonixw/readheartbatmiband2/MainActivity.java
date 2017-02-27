package us.cpluspl.yonixw.readheartbatmiband2;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

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
        if (helper != null)
            helper.DisconnectGatt();
        super.onDestroy();
    }

    // Like network card, connect to all devices in Bluetooth (like PC in Netowrk)
    final BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public void btnRun(View view) {
        helper.findBluetoothDevice(myBluetoothAdapter, "MI");
        helper.ConnectToGatt();
    }


    public void setupHeartBeat() throws InterruptedException {
        /*
        Steps to read heartbeat:
            - Register Notification (like in touch press)
                - Extra step with description
            - Write predefined bytes to control_point to trigger measurement
            - Listener will get result
        */

        if (helper != null)
            helper.getNotificationsWithDescriptor(
                Consts.UUID_SERVICE_HEARTBEAT,
                Consts.UUID_NOTIFICATION_HEARTRATE,
                Consts.UUID_DESCRIPTOR_UPDATE_NOTIFICATION
            );

        // Need to wait before first trigger, maybe something about the descriptor....
        Thread.sleep(5000,0);
    }

    public void getNewHeartBeat() throws InterruptedException {
        if (helper == null || !helper.isConnected()) {
            Toast.makeText(MainActivity.this, "Please setup first!", Toast.LENGTH_SHORT).show();
            return;
        }

            helper.writeData(
                    Consts.UUID_SERVICE_HEARTBEAT,
                    Consts.UUID_START_HEARTRATE_CONTROL_POINT,
                    Consts.BYTE_NEW_HEART_RATE_SCAN
                    );
    }

    public void getTouchNotifications() {
        helper.getNotifications(
                Consts.UUID_SERVICE_MIBAND_SERVICE,
                Consts.UUID_BUTTON_TOUCH);
    }

    public void btnTest(View view) throws InterruptedException {
        getTouchNotifications();
    }

    public void btnSetuphearRate(View view) throws InterruptedException {
        setupHeartBeat();
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
