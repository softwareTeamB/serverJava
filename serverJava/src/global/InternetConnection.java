package global;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author michel
 */
public class InternetConnection {

    /**
     * Methoden om de internet verbinding te testen
     *
     * @param urlNaam naam van de url
     * @return boolean true stament false
     */
    public static boolean internetConnectionTest(String urlNaam) {

        try {
            //maak de url aan
            URL url = new URL(urlNaam);

            //open de internet connectie
            URLConnection conn = url.openConnection();

            //connect
            conn.connect();

            //bericht dat het is gelukt
            ConsoleColor.out("Er kan internet verbinding worden gemaakt met de server van de url: " + urlNaam);

            //return stament
            return true;

        } catch (MalformedURLException ex) {
            ConsoleColor.err("Er kan geen internet connectie gemaakt worden met de url: " + urlNaam);

            //return false
            return false;
        } catch (IOException ex) {
            ConsoleColor.err("Er kan geen internet connectie gemaakt worden met de url: " + urlNaam);

            //return false
            return false;
        }

    }
}
