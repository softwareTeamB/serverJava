package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql.Mysql;

/**
 *
 * @author michel
 */
public class Bitstamp extends MainMarktGevens {

    Mysql mysql = new Mysql();
    Http http = new Http();

    //naam exchange
    private final String NAAM_EXCHANGE;
    private final int idExchange;
    private final String BASIS_URL = "	https://www.bitstamp.net/api/v2";

    //jsonarray
    JSONArray arrayMarkt;
    JSONObject markKey;

    /**
     * Constructor
     *
     * @param exchangeNaam naam van de exchange
     * @throws java.sql.SQLException sql errpr
     */
    public Bitstamp(String exchangeNaam) throws SQLException, Exception {

        //maak een JSONArray aan
        this.arrayMarkt = new JSONArray();

        //maak key lijst
        this.markKey = new JSONObject();

        //naam exchange
        this.NAAM_EXCHANGE = exchangeNaam;

        //id exchange
        String functionSql = "select getExchangeNummer('" + NAAM_EXCHANGE + "') AS nummer;";
        this.idExchange = mysql.mysqlExchangeNummer(functionSql);

        //roep de methoden op die fixKeysMarktlijst
        JSONObject responsUpdate = super.fixKeysMarktLijst(exchangeNaam);
        this.arrayMarkt = responsUpdate.getJSONArray("array");
        this.markKey = responsUpdate.getJSONObject("object");

        //print de markt key uit
        System.out.println(markKey);

    }

    /**
     * Vraag de markt data op
     */
    @Override
    public void getMarktData(boolean saveData) {

        System.out.println(arrayMarkt);

        //loop door de array heen
        for (int i = 0; i < arrayMarkt.length(); i++) {

            //krijg de marktnaam uit de array
            String marktNaam = arrayMarkt.getString(i);

            //get String
            String responseString = http.getHTTP(BASIS_URL + "/ticker/" + marktNaam);

            //maak er een object van
            JSONObject response = new JSONObject(responseString);

            //vul de variable met het object
            double high = response.getDouble("high");
            double last = response.getDouble("last");
            double bid = response.getDouble("bid");
            double low = response.getDouble("low");
            double ask = response.getDouble("ask");

            //vaak volume en volumeBTC double aan
            double volume;
            double volumeBTC;

            if ("btcusd".equals(marktNaam)) {
                //dit moet zo gebeuren omdat we van btc naar dollar om zetten en dat is dan het volume
                volume = response.getDouble("volume") * bid;
                volumeBTC = response.getDouble("volume");
            } else {
                volume = response.getDouble("volume");
                volumeBTC = response.getDouble("volume") * bid;

            }

            //als er een error op treed bij het toevoegen of updaten van de data
            try {
                super.marktDataUpdate(high, low, volume, volumeBTC, bid, ask, last, idExchange, markKey.getInt(marktNaam), saveData);
            } catch (Exception ex) {
                System.err.println("Error bij bitstamp in de package marktGegevens. " + ex);
            }
        }

    }
}
