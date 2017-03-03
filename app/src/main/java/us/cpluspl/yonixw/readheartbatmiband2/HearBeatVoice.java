package us.cpluspl.yonixw.readheartbatmiband2;

/**
 * Created by YoniWas on 03/03/2017.
 */
public class HearBeatVoice {
    static String cantRead = "invalid";
    static String andWord = "ve";
    static String nullSound = "empty";
    static String tensPostfix = "esre";

    static String intro = "intro"; // My hear beat is:
    static String units = "units"; // beat per minute.

    // ones - "o1" to "o9"
    // tens - "t1" to "t9"
    // hundreds - "h1" to "h9" // (but hear beat is never more than 200 ...)

    static String str(Integer i) {
        return  Integer.toString(i);
    }

    // For numbers < 100
    static void addLessThan100(SoundHelper sh, int Hearbeat) {
        int t = (Hearbeat % 100)/10;    // Tens
        int o = Hearbeat % 10;          // Ones

        if (t == 0 && o ==0) {
            sh.addWAVSound(nullSound);
        }
        else if (o == 0) {
            // only tens:
            sh.addWAVSound("t" + str(t));
        }
        // From now on we assume ther are ones.
        else if (Hearbeat >= 20 ) {
            sh.addWAVSound("t" + str(t)); // shloshim

            sh.addWAVSound(andWord);      // ve
            sh.addWAVSound("o" + str(o)); // Hamesh

        }
        else if (Hearbeat > 10 ) {
            sh.addWAVSound("o" + str(o));
            sh.addWAVSound(tensPostfix);
        }
        else  { // Only ones... (t == 0)
            sh.addWAVSound("o" + str(o));
        }
    }

    public static void readHeartbeat(SoundHelper sh, int Hearbeat) {
        int h = Hearbeat/100; // Hundreds
        int h11 = (Hearbeat % 100) ; // Number without hundreds

        if (Hearbeat < 0 ) return;




        if (Hearbeat == 0 ) {
            sh.addWAVSound(cantRead); // Mi band couldnt read heartbeat.
        }
        else {
            sh.addWAVSound(intro);

            if (h > 0 && h11 >= 20 ) {
                sh.addWAVSound("h" + str(h)); // Mea
            }
            else if (h > 0 && h11 < 20) {
                sh.addWAVSound("h" + str(h)); // Mea
                sh.addWAVSound(andWord);      // ve
            }
            // Else less than 100.

            addLessThan100(sh,Hearbeat);

            sh.addWAVSound(units);
        }


    }
}
