package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
import global.FileSystem;
import http.Http;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.Mysql;

/**
 * Methoden die de cexMarkt toevoegd
 *
 * @author michel
 */
public class CexIoMarktUpdate extends MainMarktUpdate {

    //private string base url
    private String CEX_IO = "https://cex.io/api";
    private ArrayList arrayList = new ArrayList();
    private final int exchangeID;

    Http http = new Http();
    Mysql mysql = new Mysql();
    FileSystem fileSystem = new FileSystem();

    /**
     * Constructor
     */
    public CexIoMarktUpdate() {

        int tempExchangeId = 0;
        
        try {
            tempExchangeId = mysql.mysqlNummer("select getExchangeNummer('cexIo') AS nummer");
            ConsoleColor.out(tempExchangeId);
        } catch (Exception ex) {
            ConsoleColor.err(ex);
            System.exit(0);
        }

        //zet de final neer
        this.exchangeID = tempExchangeId;
        ConsoleColor.out(exchangeID);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void marktUpdateLijsten() throws Exception {

        //url
        String url = CEX_IO + "/currency_limits";

        String httpReponse = http.getHttpBrowser(url);

        //maak er een jsonObject van
        JSONObject responseObject = new JSONObject(httpReponse);

        ConsoleColor.out(responseObject);

        //vul de array
        JSONArray array = responseObject.getJSONObject("data").getJSONArray("pairs");

        //for loop
        for (int i = 0; i < array.length(); i++) {

            //boolean
            boolean symbol1Boolean = false;
            boolean symbol2Boolean = false;

            //pak het eerst volgende object
            JSONObject object = array.getJSONObject(i);

            //pak het eerste symbool
            String symbol1 = object.getString("symbol1");
            String symbol2 = object.getString("symbol2");

            for (int j = 0; j < arrayList.size(); j++) {

                //kijk naar het [j] element
                String element = arrayList.get(j).toString();
                //boolean

                if (!symbol1Boolean && element.equals(symbol1)) {
                    symbol1Boolean = true;
                }

                if (!symbol2Boolean && element.equals(symbol2)) {
                    symbol2Boolean = true;
                }
            }

            //als symbol1Boolean false is dan staat die niet in de array list
            if (!symbol1Boolean) {
                arrayList.add(symbol1);
            }

            //als symbol1Boolean false is dan staat die niet in de array list
            if (!symbol2Boolean) {
                arrayList.add(symbol2);
            }
        }

        ConsoleColor.warn(arrayList);

        //maak de string
        String urlParameters = "";

        //for loop
        for (int i = 0; i < arrayList.size(); i++) {
            Object get = arrayList.get(i);
            urlParameters += "/" + get;
        }

        //sla string op
        fileSystem.whriteFile("./temp/cexParameters.txt", urlParameters);

        //roep alle markten op
        String urlMarktUpdate = CEX_IO + "/tickers" + urlParameters;

        //vraag de gegevens op
        String httpReponseMarkt = http.getHttpBrowser(urlMarktUpdate);

        //maak er een JSONObject van
        JSONObject httpReonseObject2 = new JSONObject(httpReponseMarkt);

        //maak er een JSONArray van
        JSONArray array2 = httpReonseObject2.getJSONArray("data");

        for (int i = 0; i < array2.length(); i++) {

            //pak het eerst volgende object
            JSONObject object2 = array2.getJSONObject(i);

            //coin marktNaam
            String marktNaam = object2.getString("pair");

            //kijk of de marktnaam bestaat
            boolean marktNaamBestaat = super.marktLijstenmBoolean(marktNaam, exchangeID);

            if (marktNaamBestaat) {

                //stop de for loop
                continue;
            }

            //spilt de pair op
            String[] parts = marktNaam.split(":");
            String part1 = parts[0];
            String part2 = parts[1];

            //marktNaamDB
            int marktNaamDBNummer;

            //kijk of de coin combinatie in de database staat
            boolean optie1 = super.marktDbNaamBoolean(part1 + "-" + part2);

            //if stamanent de marktDBnummer te kijrgen
            //kijk of de marktnaamDB bestaat
            if (optie1) {

                //vraag het idMarktNaam nummer op
                marktNaamDBNummer = mysql.mysqlNummer("SELECT idMarktNaam as nummer FROM marktnaam"
                        + " WHERE marktnaamDb='" + part1 + "-" + part2 + "'");

            } else {

                //kijk of optie 2 true is
                boolean optie2 = super.marktDbNaamBoolean(part2 + "-" + part1);
                if (optie2) {

                    //vraag het idMarktNaam nummer op
                    marktNaamDBNummer = mysql.mysqlNummer("SELECT idMarktNaam as nummer FROM marktnaam"
                            + " WHERE marktnaamDb='" + part2 + "-" + part1 + "'");

                } else {

                    String marktNaamDB = part1 + "-" + part2;

                    String sqlInsert = "INSERT INTO marktnaam (marktnaamDb, baseCoin, marktCurrency) "
                            + "VALUES ('" + marktNaamDB + "', '" + part1 + "', '" + part2 + "')";
                    mysql.mysqlExecute(sqlInsert);

                    marktNaamDBNummer = mysql.mysqlNummer("SELECT idMarktNaam as nummer FROM marktnaam"
                            + " WHERE marktnaamDB = '" + marktNaamDB + "';");
                }
            }

            //insert info marktlijsten
            String insertInto = "INSERT INTO marktlijsten (naamMarkt, idHandelsplaats, idMarktNaam) "
                    + "VALUES ('" + marktNaam + "', '" + exchangeID + "' , '" + marktNaamDBNummer + "')";
            mysql.mysqlExecute(insertInto);

            ConsoleColor.out("Nieuw markt toegevoegd aan cexIo");
        }

    }
}
