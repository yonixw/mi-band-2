package us.cpluspl.yonixw.readheartbatmiband2;

import java.util.UUID;

/**
 * Created by YoniWas on 22/02/2017.
 */
public class Consts {

    //this is common for all BTLE devices. see http://stackoverflow.com/questions/18699251/finding-out-android-bluetooth-le-gatt-profiles
    public static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

    public static final UUID UUID_SERVICE_GENERIC = UUID.fromString(String.format(BASE_UUID, "1800"));
    public static final UUID UUID_SERVICE_MIBAND_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE0"));
    public static final UUID UUID_SERVICE_MIBAND2_SERVICE = UUID.fromString(String.format(BASE_UUID, "FEE1"));

    public static final UUID UUID_CHARACTERISTIC_DEVICE_NAME = UUID.fromString(String.format(BASE_UUID, "2A00"));

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        //http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
