package us.cpluspl.yonixw.readheartbatmiband2;

import java.util.UUID;

/**
 * Created by YoniWas on 22/02/2017.
 */
public class Consts {

    //this is common for all BTLE devices. see http://stackoverflow.com/questions/18699251/finding-out-android-bluetooth-le-gatt-profiles
    public static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";
    public static final UUID UUID_CHARACTERISTIC_TEST = UUID.fromString(String.format(BASE_UUID, "FF0D"));

}
