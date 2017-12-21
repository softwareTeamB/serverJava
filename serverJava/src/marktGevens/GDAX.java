package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
import http.Http;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.Mysql;

/**
 * Klassen om de markt data van GDAX te kijken
 *
 * @author michel
 */
public class GDAX extends MainMarktGevens {

    //maak het object aan
    Http http = new Http();
    Mysql mysql = new Mysql();

    //JSONArray
    JSONArray arrayMarkt;
    JSONObject markKey;

    //exchange nummer
    private final int ID_EXCHANGE;
    private final String EXCHANGE_NAAM = "GDAX";
    private final String BASIS_URL = "https://api.gdax.com";

    public GDAX(boolean saveData) throws Exception {
        //vul de final met de exchange id nummer
        this.ID_EXCHANGE = super.getExchangeNummer(EXCHANGE_NAAM);

        //vraag alle markt namen op met het exchange nummer
        JSONObject responsUpdate = super.fixKeysMarktLijst(EXCHANGE_NAAM);
        this.arrayMarkt = responsUpdate.getJSONArray("array");
        this.markKey = responsUpdate.getJSONObject("object");

    }

    /**
     * Methoden om alle data te krijgen
     *
     * @param saveData sla de data op
     */
    @Override
    public void getMarktData(boolean saveData) {

        System.out.println(arrayMarkt);

        for (int i = 0; i < arrayMarkt.length(); i++) {

            //krijg de marktnaam uit de array
            String marktNaam = arrayMarkt.getString(i);

            //String url 
            String url = BASIS_URL + "/products/" + marktNaam + "/ticker";

            //get String
            String responseString;
            try {
                responseString = http.getHTTP(url);
            } catch (Exception ex) {

                //console error bericht
                ConsoleColor.err("Er is een probleem op de data op te vragen van de server van: " + url + "\n error bericht is: " + ex);

                //return stament
                return;
            }
            System.out.println(responseString);

            //maak er een object van
            JSONObject response = new JSONObject(responseString);

            //vul de variable met het object
            double last = response.getDouble("price");
            double bid = response.getDouble("bid");
            double ask = response.getDouble("ask");

            double low;
            double high;

            //vaak volume en volumeBTC double aan
            double volume;
            double volumeBTC;

            //als er een error op treed bij het toevoegen of updaten van de data
            try {
                //super.marktDataUpdate(high, low, volume, volumeBTC, bid, ask, last, idExchange, markKey.getInt(marktNaam), saveData);
            } catch (Exception ex) {
                System.err.println("Error bij bitstamp in de package marktGegevens. " + ex);
            }

        }

        /*  "price": "333.99",
  "size": "0.193",
  "bid": "333.98",
  "ask": "333.99",
  "volume": "5957.11914015",*/
    }

    //wacht 1 seconde
    //       TimeUnit.SECONDS.sleep(1);   
}
