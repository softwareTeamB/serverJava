package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
import http.Http;
import java.sql.SQLException;
import mysql.Mysql;

/**
 * Methoden om alle gdax markten op te slaan
 *
 * @author michel
 */
public class GDAXMarktUpdate extends MainMarktUpdate {

    //exchange nummer
    private final int exchangeNummer;

    //maak objecten aan
    Http http = new Http();
    Mysql mysql = new Mysql();

    /**
     * De constructor
     *
     * @param exchangeNaam naam handelpslaats
     * @throws java.lang.Exception error exception
     */
    public GDAXMarktUpdate(String exchangeNaam) throws Exception {

        //vraag het exchageNummer op
        String sqlFunctions = "SELECT getExchangeNummer('" + exchangeNaam + "') AS nummer;";

        //vul de bittrex nummer
        this.exchangeNummer
                = mysql.mysqlExchangeNummer(sqlFunctions);
    }

    /**
     * Markt lijsten update
     *
     * @throws java.sql.SQLException als er een error op treed in SQLException
     * @throws Exception als er een error optreed
     */
    @Override
    public void marktUpdateLijsten() throws SQLException, Exception {

        //http get url string
        String reponseString = http.getHTTP(super.getUrl_GDAX() + "/products");

        //zet de reponse in een JSONArray
        JSONArray array = new JSONArray(reponseString);

        //loop door de reponse heen
        for (int i = 0; i < array.length(); i++) {

            //zet er een jsonobject van
            JSONObject object = array.getJSONObject(i);

            //kijk of de coins bekend is in de lijst
            String naamMarkt = object.getString("id");

            //count string
            String countSql = "SELECT COUNT(*) AS total FROM marktlijsten WHERE naamMarkt='" + naamMarkt + "'"
                    + " AND idHandelsplaats=" + exchangeNummer + "";
            int count = mysql.mysqlCount(countSql);

            //kijk of de markt aan in de lijst staat
            if (count == 0) {

                //roep addMartLijsten die kijkt of er al een db versie is van de markt
                addMarktLijsten(object);
            }

        }
    }

    /**
     * Methoden die de markt in de marktLijsten
     *
     * @param object het object met de data er in
     * @return of het succesvol is toegevoegd de markt
     */
    private boolean addMarktLijsten(JSONObject object) throws SQLException, Exception {

        //boolean of de switch succesvol is gegaan
        boolean switchSuccesVol = false;

        //Base currentie
        String baseCoin = object.getString("base_currency");
        String secCoin = object.getString("quote_currency");
        String naamMarkt = object.getString("id");
        String naamMarktDB = null;

        //kijken hoe de naamMarktDB genoemt moet worden
        switch (naamMarkt) {
            case "BTC-EUR":
            case "BTC-GBP":
            case "ETH-EUR":
            case "BTC-USD":
                //maak de string van hoe de coin in de database heet
                naamMarktDB = baseCoin + "-" + secCoin;

                //zet de switchSuccesVol op true
                switchSuccesVol = true;
                break;
            case "ETH-BTC":
            case "ETH-USD":
            case "LTC-BTC":
            case "LTC-USD":
                //maak de string van hoe de coin in de database heet
                naamMarktDB = secCoin + "-" + baseCoin;

                //zet de switchSuccesVol op true
                switchSuccesVol = true;
                break;
            default:
                ConsoleColor.error("Er is een nieuwer markt bij GDAX! Nieuwe marktnaam heet " + naamMarkt);
                break;
        }

        //return dat de swtich niet succesvol is gelukt
        if (!switchSuccesVol || naamMarktDB == null) {
            ConsoleColor.err("De switch is niet succesvol uitgevoerd!");
            return false;
        }

        //kijk of de dbnaam in de lijst staat
        String sqlCount = "SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb='" + naamMarktDB + "';";
        int count = mysql.mysqlCount(sqlCount);

        //markt nummer
        int marktDBNummer;

        //kijk of de markt toegeovegd moet worden door de methoden addMarktNaam
        if (count == 0) {

            //roep de methoden op
            marktDBNummer = addMarktNaam(naamMarktDB, baseCoin, secCoin);
        } else {

            //vraag het markt naam op
            String sqlSelect = "SELECT idMarktNaam AS nummer FROM marktnaam WHERE marktnaamDb='" + naamMarktDB + "'";
            marktDBNummer = mysql.mysqlNummer(sqlSelect);
        }

        //insert stament
        String insertMarktLijsten = "INSERT INTO marktLijsten (idHandelsplaats, idMarktNaam, naamMarkt) "
                + "VALUES(" + exchangeNummer + ", " + marktDBNummer + ", '" + object.getString("id") + "')";
        mysql.mysqlExecute(insertMarktLijsten);

        return true;
    }

    /**
     * De methoden die de marktnaam db in de database zet
     *
     * @param marktNaamDB hoe de markt in de dabase staat
     * @param baseCoin de basis coin
     * @param marktCoin de markt coin
     */
    private int addMarktNaam(String marktNaamDB, String baseCoin, String marktCoin) throws SQLException, Exception {

        //insert stament
        String sqlInsert = "INSERT INTO marktnaam (marktnaamDb, baseCoin, marktCurrency) VALUES ('" + marktNaamDB + "','" + baseCoin + "', '" + marktCoin + "')";
        mysql.mysqlExecute(sqlInsert);
        
        
        //vraag het nummer op
        int nummer = mysql.mysqlNummer("SELECT idMarktNaam as nummer FROM marktnaam"
                + " WHERE marktnaamDB = '" + marktNaamDB + "';");

        return nummer;

    }
}
