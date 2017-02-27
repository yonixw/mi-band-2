package us.cpluspl.yonixw.talkingalarm;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by YoniWas on 05/02/2017.
 */
public class IOHelper {
    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    public static File getStorageDir(Context context) {
        // Get the directory for the app's private directory (remove @ uninstall).
        File file = context.getExternalFilesDir(null);
        if (!file.exists() &&  !file.mkdirs()) {
            Log.e(MainActivity.LOG_TAG, "Directory not created " + file.getAbsolutePath());
        }
        return file;
    }
}
