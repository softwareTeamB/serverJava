package serverjava;

import Gemiddelde.gemiddeldeMarktupdatehistory;
import Web.Index;
import Web.WebSocket;
import com.sun.net.httpserver.HttpServer;
import global.ConsoleColor;
import invullenMarktlijst.BittrexMarktUpdate;
import invullenMarktlijst.insertFuncties;
import invullenMarktlijst.BitstampMarktUpdate;
import invullenMarktlijst.Driver;
import invullenMarktlijst.GDAXMarktUpdate;
import java.io.Console;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import marktGevens.SaveController;
import mysql.Mysql;

/**
 * Main starter
 *
 * @author michel
 */
public class ServerJava {

    //private static methoden
    private static SaveController saveController;
    private static int reloadTime;

    //static methodens
    public static Web.WebSocket webSocket;

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
        InstallerV2 iV2 = new InstallerV2();
        iV2.main();

        //run de mydql check
        mysqlExchangeCheck();

        //maak de classe aan die de websocket regeld
        try {
            //roep de webSocket aan
            webSocket = new WebSocket();
        } catch (IOException | SQLException ex) {
            ConsoleColor.err(ex);

            //fatale error sluit het systeem af
            System.exit(0);
        }

        //get Routers worden hier geladen
        Index webIndexRouter = new Index();
        try {
            webIndexRouter.index();
        } catch (IOException ex) {
            ConsoleColor.err(ex);

            //fatale error sluit het systeem af
            System.exit(0);
        }

        //fatale error sluit het systeem af
        //gemiddeldeMarktupdatehistory avg = new gemiddeldeMarktupdatehistory();
        //invullenMarktLijst
        //Driver driver = new Driver();
        //driver.driver();
        //roep de saveController op
        SaveController sC = new SaveController(60000);
        sC.runSaver();

        try {
            //invullenMarktLijst();
        } catch (Exception ex) {
            ConsoleColor.err(ex);
        }

        /*
            //laat config prop file
P            LoadPropFile loadPropFile = new LoadPropFile();
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
            }*/
 /*
            
            
            avg.control(1, 12304, "bid", "2017-09-09", "2017-10-12");
            
            //constructor
            saveController = new SaveController(600000);
            
            //kijk of marktlijsten ingevuld moet worden
            //boolean vulMarktLijsten;
            //String configVulMarktLijsten = prop.getProperty("checkMarktLijst");
            String configVulMarktLijsten = "true";
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
            gemiddeldeMarktupdatehistory markt = new gemiddeldeMarktupdatehistory();
            
            //run de saveController in een aparte thread
            Thread thread = new Thread(SAVE_CONTROLLER_TASK);
            thread.start();*/
    }

    /**
     * methoden die na kijkt op handelsplaats goed werkt
     */
    private static void mysqlExchangeCheck() {

        //mysql object
        Mysql mysql = new Mysql();

        //string array voor exchangeLijst
        String[] exchangeNaamArray = {"poloniex", "bittrex"};
        String[] verbindingsTeken = {"_", "-"};

        //loop door de exchangeNaam heen
        for (int i = 0; i < exchangeNaamArray.length; i++) {

            //exchange naam
            String exchangeNaam = exchangeNaamArray[i];

            //count string
            String countSql = "SELECT COUNT(*) AS total FROM handelsplaats "
                    + "WHERE handelsplaatsNaam ='" + exchangeNaam + "' "
                    + "AND verbindingsTeken='" + verbindingsTeken[i] + "'";

            //vraag kijk of de markt er in staat
            int count;
            try {
                //count stament
                count = mysql.mysqlCount(countSql);
            } catch (Exception ex) {
                ConsoleColor.err(ex);

                //fatale error sluit het systeem af
                System.exit(0);
                continue;
            }

            //stament als count 0 is anders naar de else stament
            if (count == 0) {

                //voeg de exchange toe en de verbindings teken
                String sqlInsert = "INSERT INTO handelsplaats (handelsplaatsNaam, verbindingsTeken) "
                        + "VALUES('" + exchangeNaam + "', '" + verbindingsTeken[i] + "')";

                //voer het stament uit
                try {
                    mysql.mysqlExecute(sqlInsert);
                } catch (SQLException ex) {
                    ConsoleColor.err(ex);

                    //fatale error sluit het systeem af
                    System.exit(0);
                }

            } else {

                //kijk of de exchangeNaam bekend is
                String countSql2 = "SELECT COUNT(*) AS total FROM handelsplaats "
                        + "WHERE handelsplaatsNaam ='" + exchangeNaam + "'";

                //vraag kijk of de markt er in staat
                int count2;
                try {
                    //count stament
                    count2 = mysql.mysqlCount(countSql2);
                } catch (Exception ex) {
                    ConsoleColor.err(ex);

                    //fatale error sluit het systeem af
                    System.exit(0);
                    continue;
                }

                //als het 1 is doe niks als het 0 is run het update stament
                if (count2 == 0) {

                    //update sql stament voor verbindigsTeken
                    String updateSql = "UPDATE handelsplaats SET verbindingsTeken='" + verbindingsTeken[i] + "'";

                    //voer het stament uit
                    try {
                        mysql.mysqlExecute(updateSql);
                    } catch (SQLException ex) {
                        ConsoleColor.err(ex);

                        //fatale error sluit het systeem af
                        System.exit(0);
                    }

                }

            }

            ConsoleColor.out("MysqlExchangeCheck is doorlopen.");
        }
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
        //InstersFuncies.invullenCoinsBittrex();
        InstersFuncies.invullenCoinsPolo(markt, url, symbool);

        //methoden die de update op roep voor bittrex
        BittrexMarktUpdate bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
        bittrexMarktUpdate.marktUpdateLijsten();

        //methoden die de update op roep voor bitstamp
        //BitstampMarktUpdate bMU = new BitstampMarktUpdate();
        //bMU.marktUpdateLijsten();
        //methoden die de update op roep voor GDAX
        //GDAXMarktUpdate gdaxAddMarkt = new GDAXMarktUpdate("GDAX");
        //gdaxAddMarkt.marktUpdateLijsten();
    }
}
