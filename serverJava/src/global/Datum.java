package global;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse die de datum terug geeft
 *
 * @author michel
 */
public class Datum {

    /**
     * Methoden die de datum en tijd return
     * @return datum en tijd
     */
    public static String datum() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
