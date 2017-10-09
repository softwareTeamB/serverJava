package serverjava;

import global.LoadPropFile;
import invullenMarktlijst.BittrexMarktUpdate;
import invullenMarktlijst.insertFuncties;
import java.util.logging.Level;
import java.util.logging.Logger;
import invullenMarktlijst.BitstampMarktUpdate;
import invullenMarktlijst.GDAXMarktUpdate;
import marktGevens.Bittrex;
import marktGevens.Poloniex;
import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;
import marktGevens.SaveController;

/**
 * Main starter
 *
 * @author Jaros
 */
public class ServerJava {

    //private static methoden
    private static SaveController saveController;
    private static int reloadTime;

    //task controller voor saveController
    private static final TimerTask SAVE_CONTROLLER_TASK = new TimerTask() {

        @Override
        public void run() {
            //run saver
            saveController.runSaver();

            //boolean updater
            saveController.loadBoolean();
        }
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //run even de installer
        Installer install = new Installer();

        //laat config prop file
        LoadPropFile loadPropFile = new LoadPropFile();
        Properties prop;
        try {
            prop = loadPropFile.loadPropFile("./config/config.properties");
            reloadTime = Integer.parseInt(prop.getProperty("reloadTijd"));
        } catch (IOException ex) {

            prop = new Properties();

            //Sluit het systeem op procedure
            System.err.println(ex);
            System.err.println("Er een systeem error. Het programma wordt afgesloten");
            System.exit(0);
        }

        //constructor
        saveController = new SaveController(reloadTime);

        //kijk of marktlijsten ingevuld moet worden
        //boolean vulMarktLijsten;
        String configVulMarktLijsten = prop.getProperty("checkMarktLijst");
        //String configVulMarktLijsten = "true";
        if ("true".equals(configVulMarktLijsten)) {
            //hier wordt de methoden opgeroepen die het invullen van de marktLijsten regeld
            try {
                invullenMarktLijst();
            } catch (IOException ex) {

                //Sluit het systeem op procedure
                System.err.println(ex);
                System.err.println("Er een systeem error. Het programma wordt afgesloten");
                System.exit(0);
            } catch (Exception ex) {
                Logger.getLogger(ServerJava.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("invullenMarktLijst wordt niet geladen");
        }

        //run de saveController in een aparte thread
        Thread thread = new Thread(SAVE_CONTROLLER_TASK);
        thread.start();
    }

    /**
     * Methoden die de markt lijsten update
     */
    private static void invullenMarktLijst() throws Exception {
        insertFuncties InstersFuncies = new insertFuncties();

        String markt = "poloniex";//naam van de makrt in de database
        String url = "https://poloniex.com/public?command=returnTicker"; //website voor controle informatie
        String symbool = "_"; //symbool voor de naam van de markt

        //bittrexMarktdata bit = new bittrexMarktdata();
        //bit.bittrexMarktdataControler();
        //methoden die de update op roep voor poloniex
        InstersFuncies.invullenCoinsBittrex();
        InstersFuncies.invullenCoinsPolo(markt, url, symbool);

        //methoden die de update op roep voor bittrex
        BittrexMarktUpdate bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
        bittrexMarktUpdate.marktUpdateLijsten();

        //methoden die de update op roep voor bitstamp
        BitstampMarktUpdate bMU = new BitstampMarktUpdate();
        bMU.marktUpdateLijsten();

        //methoden die de update op roep voor GDAX
        GDAXMarktUpdate gdaxAddMarkt = new GDAXMarktUpdate("GDAX");
        gdaxAddMarkt.marktUpdateLijsten();
    }
}
