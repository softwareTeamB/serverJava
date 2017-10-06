package serverjava;

import invullenMarktlijst.BittrexMarktUpdate;
import invullenMarktlijst.bittrexMarktdata;

import invullenMarktlijst.insertFuncties;
import java.util.logging.Level;
import java.util.logging.Logger;
import invullenMarktlijst.BitstampMarktUpdate;
import invullenMarktlijst.GDAXMarktUpdate;
import marktGevens.Bittrex;
import marktGevens.Poloniex;
import marktGevens.SaveController;

/**
 * Main starter
 *
 * @author Jaros
 */
public class ServerJava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //hier wordt de methoden opgeroepen die het invullen van de marktLijsten regeld
        try {
            //invullenMarktLijst();
        } catch (Exception ex) {
            Logger.getLogger(ServerJava.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Poloniex poloniex = new Poloniex("poloniex", false);
            //roep saveController
            //SaveController saveController = new SaveController();

            //Bittrex bittrex = new Bittrex("bittrex", false);
            //bittrex.getMarktData();
            //SaveController saveController = new SaveController();
            //saveController.runSaver();
        } catch (Exception ex) {
            Logger.getLogger(ServerJava.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //dataMarktUpdater();
        //run de installer
        Installer install = new Installer();

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
