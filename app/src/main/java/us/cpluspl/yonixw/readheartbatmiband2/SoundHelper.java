package us.cpluspl.yonixw.readheartbatmiband2;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by YoniWas on 05/02/2017.
 */
public class SoundHelper {

    Context myContext;
    ArrayList<MediaPlayer> loadedSounds = new ArrayList<MediaPlayer>() ;

    public SoundHelper(Context context) {
        myContext =context;
    }

    private MediaPlayer getMediaPlayer( String localFileName) {
        Uri filePath = Uri.fromFile(new File (IOHelper.getStorageDir(myContext), localFileName));
        MediaPlayer mp =  MediaPlayer.create(
                myContext,
                filePath
        );
        if (mp == null) {
            String logMessage = "Can't load file '" + localFileName
                    + "'\nIn place: '" + filePath.toString() + "';";
            Log.d(MainActivity.LOG_TAG, logMessage);
            Toast.makeText(myContext, "Cant load file:" + localFileName, Toast.LENGTH_LONG).show();
        }

        return mp;
    }

    private void addSound(String shortName) {
        loadedSounds.add(getMediaPlayer(shortName));
    }

    /**
     * Add sound to the media player. the file must have wav extention even if the format is mp3
     * @param shortName the name of the file (ex. /../../music.wav => music)
     */
    public void addWAVSound(String shortName) {
        addSound(shortName + ".wav");
    }

    public void releaseAllSounds() {
        for (MediaPlayer m : loadedSounds) {
            m.release();
        }
        loadedSounds.clear();
    }

    public void playAllAsync() {
        Thread r = new Thread() {


            @Override
            public void  run() {
                int counter = 0;
                for (MediaPlayer m : loadedSounds) {
                    Log.d(MainActivity.LOG_TAG, "Playing sound no. " + counter);
                    m.start();
                    while (m.isPlaying()) {
                        try {
                            Thread.sleep(100,0); // lower cpu if we wait and not just busy wait
                        } catch (InterruptedException e) { e.printStackTrace();}
                    }

                    counter++;
                }

                raisePlayBackFinishEvent();
            }
         };


        r.start();
    }

    // Event when all activites done.
    public interface PlaybackFinishListener {
        public void onPlaybackFinish() ;
    }

    private PlaybackFinishListener myPlaybackFinishListener = null;
    public void setPlaybackFinishListener(PlaybackFinishListener listener) {
        myPlaybackFinishListener = listener;
    }

    private void raisePlayBackFinishEvent() {
        Log.d(MainActivity.LOG_TAG, "Playback Finished!");
        if(myPlaybackFinishListener !=null) {
            myPlaybackFinishListener.onPlaybackFinish();
        }
    }
}
