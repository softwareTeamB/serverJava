package serverjava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Installer klasse
 *
 * @author michel
 */
public class Installer {

    //folder naam locatie
    private final String[] FOLDER_NAME = {"config", "temp"};
    private final String[] BESTAND_CHECK = {"config.properties", "saveMarktData.properties"};

    //boolean
    private boolean succes = true;

    /**
     * Constructor die de sub methodes aan roept
     */
    public Installer() {

        //roep de folderCheck op
        folderCheck();
        bestandCheck();

    }

    /**
     * Methoden die na kijkt of de folder bestaat en als die niet bestaat maakt de folder aan
     */
    private void folderCheck() {

        //loop door de array heen
        for (int i = 0; i < FOLDER_NAME.length; i++) {

            //maak het object folder aan
            File folder = new File(FOLDER_NAME[i]);

            //kijk of de folder bestaat
            if (!folder.exists()) {

                try {
                    //maak de folder aan
                    folder.mkdirs();

                    //geef aan de de folder is aangemaakt
                    System.out.println("Folder aangemaakt: " + folder.getName());

                } catch (SecurityException se) {
                    System.err.println("Er is een error bij de installer om folder aan te maken. \n Error is " + se);
                    succes = false;
                }
            } else {
                System.out.println("Folder " + FOLDER_NAME[i] + " bestaat al.");
            }
        }
    }

    /**
     * Methoden die kijkt of de bestanden er zijn
     */
    private void bestandCheck() {

        for (int i = 0; i < BESTAND_CHECK.length; i++) {
            String bestandNaam = BESTAND_CHECK[i];

            //maak de variable bestand naam
            File f = new File("/config/" + bestandNaam);

            //kijk of het bestand bestaat
            if (!f.exists()) {

                //switch
                switch (bestandNaam) {

                    //om een properties bestand aan temaken 
                    case "config.properties":
                        try {
                            configProperties();
                        } catch (IOException ex) {

                            System.err.println(ex);

                            //Stop het systeem omdat het een fatale error is
                            System.exit(0);
                        }
                        break;

                    //om een properties bestand aan temaken 
                    case "saveMarktData.properties":
                        try {
                           marktLijstProperties();
                        } catch (IOException ex) {
                            System.err.println(ex);

                            //Stop het systeem omdat het een fatale error is
                            System.exit(0);
                        }
                        break;
                    default:
                        //omdat het bestand moet bestaan word het gezien als een fatale error
                        System.err.println("Het bestand niet en kan niet door het systeem worden aangemaakt!");

                        //Stop het systeem omdat het een fatale error is
                        System.exit(0);
                        break;
                }
            } else {
                //omdat het bestand moet bestaan word het gezien als een fatale error
                System.out.println("Het bestand "+ bestandNaam +" bestaat.");
            }
        }
    }

    /**
     * Methoden om configProperties aan te maken
     *
     * @throws FileNotFoundException als het bestand niet is gevonden
     * @throws IOException als er een file error is
     */
    private void configProperties() throws FileNotFoundException, IOException {

        //input file
        Properties prop = new Properties();
        OutputStream output;

        output = new FileOutputStream("./config/config.properties");

        // set the properties value
        prop.setProperty("reloadMarktDataSave", "true");

        // save properties in config folder
        prop.store(output, null);
    }

    /**
     * Methoden om een properites bestand aan te maken om de markt geupdate moet worden
     *
     * @throws FileNotFoundException als het bestand niet is gevonden
     * @throws IOException als er een file error is
     */
    private void marktLijstProperties() throws FileNotFoundException, IOException {
        //input file
        Properties prop = new Properties();
        OutputStream output;

        output = new FileOutputStream("./config/saveMarktData.properties");

        // set the properties value
        prop.setProperty("bittrex", "false");
        prop.setProperty("bitstamp", "false");
        prop.setProperty("GDAX", "false");
        prop.setProperty("poloniex", "false");

        // save properties in config folder
        prop.store(output, null);
    }
}
