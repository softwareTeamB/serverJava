/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import mysql.Mysql;

/**
 *
 * @author Jaros
 */
public class Poloniex extends MainMarktGevens {

    Mysql mysql = new Mysql();
    Http http = new Http();

    //naam exchange
    private final String NAAM_EXCHANGE;
    private final int idExchange;
    private final String BASIS_URL = "https://poloniex.com/public?command=returnTicker";
    private boolean saveData;

    //jsonarray
    JSONArray arrayMarkt;
    JSONObject markKey;

    public Poloniex(String exchangeNaam, boolean saveData) throws Exception {

        //maak een JSONArray aan
        this.arrayMarkt = new JSONArray();

        //maak key lijst
        this.markKey = new JSONObject();

        //naam exchange
        this.NAAM_EXCHANGE = exchangeNaam;

        //id exchange
        String functionSql = "select getExchangeNummer('" + NAAM_EXCHANGE + "') AS nummer;";
        this.idExchange = mysql.mysqlExchangeNummer(functionSql);

        //vul die variable die de data opslaat
        this.saveData = saveData;

        //roep de methoden op die fixKeysMarktlijst
        JSONObject responsUpdate = super.fixKeysMarktLijst(exchangeNaam);
        this.arrayMarkt = responsUpdate.getJSONArray("array");
        this.markKey = responsUpdate.getJSONObject("object");

        //print de markt key uit
        System.out.println(markKey);
    }


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
            double high = 0;//response.getDouble("");//invullen!!!!!!!!
            double last = response.getDouble("last");
            double bid = response.getDouble("highestBid");
            double low = response.getDouble("low");
            double ask = response.getDouble("lowestAsk");

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
