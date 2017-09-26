package global;

import java.util.Date;

/**
 * Methoden om de tijd aan te geven
 *
 * @author michel
 */
public class Tijd {

    /**
     * Die de timestamp opvraagd
     *
     * @return
     */
    private long timeStamp() {

        //krijg de time stamp
        Date date = new Date();
        long time = date.getTime();

        return time;
    }

    /**
     * methoden die de timestamp geeft
     *
     * @return timestamp in string
     */
    public int getTimeStamp() {
        return (int) timeStamp() / 1000;
    }

}
