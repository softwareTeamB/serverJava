package marktGevens;

import global.ConsoleColor;
import global.LoadPropFile;
import java.io.Console;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michel
 */
public class SaveController {

    //booleans
    private boolean loadBittrex, loadPoloniex, loadGDAX, loadBitstamp, loadCexIo;
    private boolean saveBittrex, savePoloniex, saveGDAX, saveBitstamp, saveCexIo;
    private final int RELOAD_TIME;

    //classe namen
    Bittrex bittrex;
    Bitstamp bitstamp;
    CexIo cexIo;

    //properties
    Properties prop;

    private TimerTask task1 = new TimerTask() {

        @Override
        public void run() {

            //reload de boolean
            loadBoolean();
            saveBoolean();

            //roep de methoden op
            loadBittrex();
            loadBitstamp();
            loadCexIo();
        }
    };

    /**
     * Construcotr
     *
     * @param reloadTime om te hoeveel tijd de methodes reload moeten worden
     */
    public SaveController(int reloadTime) {

        //vul reloadTime
        this.RELOAD_TIME = reloadTime;

        //probeer alle klasse aan te maken
        try {
            this.bittrex = new Bittrex("bittrex");
            // this.bitstamp = new Bitstamp("bitstamp");
            this.cexIo = new CexIo();

            //maak de config properties klasse aan en reload het bestand
            LoadPropFile loadPropFile = new LoadPropFile();
            Properties prop = loadPropFile.loadPropFile("loadExchange");
            this.prop = prop;
        } catch (Exception ex) {

            //laat de error zijn
            ConsoleColor.err(ex);

            ConsoleColor.err("Er is een error in de constructor van de saveController. De error is " + ex
                    + " . \n Het systeem wordt veilig afgesloten.");

            //sluit af omdat het belangrijke systeem probleem is
            System.exit(0);
        }
    }

    /**
     * Methoden die alle andere saveController aan gaat sturen
     */
    public void runSaver() {

        //great timer
        Timer timer = new Timer();

        //timer schema
        timer.schedule(task1, new Date(), RELOAD_TIME);
    }

    /**
     * laat boolean
     */
    public void loadBoolean() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("./config/loadExchange.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            String bittrexProp = prop.getProperty("bittrex");

            //swithc er door heen
            switch (bittrexProp) {
                case "false":
                    this.loadBittrex = false;
                    break;
                case "true":
                    this.loadBittrex = true;
                    break;
                default:
                    ConsoleColor.err("Bij bittrexProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String bitstamProp = prop.getProperty("bitstamp");

            //swithc er door heen
            switch (bitstamProp) {
                case "false":
                    this.loadBitstamp = false;
                    break;
                case "true":
                    this.loadBitstamp = true;
                    break;
                default:
                    ConsoleColor.err("Bij bitstamProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String cexIoProp = prop.getProperty("cexIo");

            //switch er door heen
            switch (cexIoProp) {
                case "false":
                    this.loadCexIo = false;
                    break;
                case "true":
                    this.loadCexIo = true;
                    break;
                default:
                    ConsoleColor.err("Bij cexIoProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String GDAXProp = prop.getProperty("gdax");
            
            if(GDAXProp == null){
                ConsoleColor.warn("GDAX prop is leeg");
            } else {
            //swithc er door heen
            switch (GDAXProp) {
                case "false":
                    this.loadGDAX = false;
                    break;
                case "true":
                    this.loadGDAX = true;
                    break;
                default:
                    ConsoleColor.err("Bij GDAXProp is niet true of false");
                    break;
            }}

        } catch (IOException ex) {
            System.err.println(ex);
        } finally {

            //close de input
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    ConsoleColor.err(e);
                }
            }
        }

    }

    /**
     * save boolean
     */
    private void saveBoolean() {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("./config/saveMarktData.properties");

            // load a properties file
            prop.load(input);
            
            ConsoleColor.out(prop);

            // get the property value and print it out
            String bittrexProp = prop.getProperty("bittrex");

            //swithc er door heen
            switch (bittrexProp) {
                case "false":
                    this.saveBittrex = false;
                    break;
                case "true":
                    this.saveBittrex = true;
                    break;
                default:
                    ConsoleColor.err("Bij bittrexProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String bitstamProp = prop.getProperty("bitstamp");

            //swithc er door heen
            switch (bitstamProp) {
                case "false":
                    this.saveBitstamp = false;
                    break;
                case "true":
                    this.saveBitstamp = true;
                    break;
                default:
                    System.err.println("Bij bitstamProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String cexIoProp = prop.getProperty("cexIo");

            //switch er door heen
            switch (cexIoProp) {
                case "false":
                    this.saveCexIo = false;
                    break;
                case "true":
                    this.saveCexIo = true;
                    break;
                default:
                    ConsoleColor.err("Bij cexIoProp is niet true of false");
                    break;
            }

            // get the property value and print it out
            String GDAXProp = prop.getProperty("GDAX");

            //swithc er door heen
            switch (GDAXProp) {
                case "false":
                    this.saveGDAX = false;
                    break;
                case "true":
                    this.saveGDAX = true;
                    break;
                default:
                    System.err.println("Bij GDAXProp is niet true of false");
                    break;
            }

        } catch (IOException ex) {
            ConsoleColor.err(ex);
        } finally {

            //close de input
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    ConsoleColor.err(e);
                }
            }
        }

    }

    /**
     * Methoden die steeds opnieuw geladen word
     */
    private void loadBittrex() {

        //kijk of bittrex geladen moet worden
        if (loadBittrex) {

            try {
                //reload alle methoden om de minut
                bittrex.getMarktData(saveBittrex);
            } catch (Exception ex) {
                ConsoleColor.err(ex);
            }
        } else {
            ConsoleColor.warn("Bittrex getData wordt niet geladen.");
        }
    }
    
    /**
     * Load cexIo
     */
    private void loadCexIo(){
        
        
        //kijk of bittrex geladen moet worden
        if(loadCexIo){
            cexIo.getMarktData(saveBittrex);
        } else {
        ConsoleColor.warn("cexIo wordt niet geladen.");
        }
    }

    /**
     * Methoden die bitstamp laat
     */
    private void loadBitstamp() {

        //kijk of bitstamp geladen moet worden
        if (loadBitstamp) {

            try {
                //reload alle methoden om de minut
                bitstamp.getMarktData(saveBitstamp);
            } catch (Exception ex) {
                ConsoleColor.err(ex);
            }
        } else {
            ConsoleColor.warn("Bitstamp getData wordt niet geladen.");
        }

    }

}
