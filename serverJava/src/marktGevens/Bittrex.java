/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import invullenMarktlijst.BittrexMarktUpdate;
import mysql.Mysql;

/**
 *
 * @author michel
 */
public class Bittrex extends MainMarktGevens {

    Mysql mysql = new Mysql();
    Http http = new Http();

    //naam exchange
    private final String NAAM_EXCHANGE;
    private final int idExchange;
    private final String BASIS_URL = "https://bittrex.com/api/v1.1";
    private boolean saveData;

    //jsonarray
    JSONArray arrayMarkt;
    JSONObject markKey;

    /**
     * Roep de constructor aan
     *
     * @param exchangeNaam
     * @param saveData
     * @throws java.lang.Exception error exceptie
     */
    public Bittrex(String exchangeNaam, boolean saveData) throws Exception {

        //naam van de exchnage
        this.NAAM_EXCHANGE = exchangeNaam;

        //id exchange
        String functionSql = "select getExchangeNummer('" + NAAM_EXCHANGE + "') AS nummer;";
        this.idExchange = mysql.mysqlExchangeNummer(functionSql);

        //vul die variable die de data opslaat
        this.saveData = saveData;

        System.out.println("Bittrex constructor in marktGegevens geladen.");
    }

    /**
     * Maak een markt updater
     */
    @Override
    public void getMarktData() {

        //maak een url
        String httpUrl = BASIS_URL + "/public/getmarketsummaries";

        //vraag de data op aan de servers
        String stringHttpReponse = http.getHTTP(httpUrl);

        //JSONObject
        JSONObject reponseObject = new JSONObject(stringHttpReponse);

        //kijk of alles gelukt is
        if (!reponseObject.getBoolean("success")) {
            System.err.println("Er is een error om de bittrex data te ontvangen."
                    + " Dit is de melding:" + reponseObject.getString("message"));

            //stop de methoden
            return;
        }

        //maak een array aan met alle data
        JSONArray array = reponseObject.getJSONArray("result");

        //start de for loop
        for (int i = 0; i < array.length(); i++) {

            //vul het jsonobject met het eerst volgende object
            JSONObject object = array.getJSONObject(i);

            String idMarktNaamSql = "SELECT idMarktNaam AS nummer FROM marktnaam "
                    + "WHERE marktnaamDb = '" + object.getString("MarketName") + "'";

            int idMarktNaam = -1;

            //vraag de idMarktNaam op
            try {
                idMarktNaam = mysql.mysqlExchangeNummer(idMarktNaamSql);
            } catch (Exception ex) {
                System.err.println(ex);

                //roep de methoden op markten de updaten
                try {
                    //roep de methoden op die alle markten van bittrex bij moet houden
                    BittrexMarktUpdate bmp = new BittrexMarktUpdate("bittrex");
                    bmp.marktUpdateLijsten();
                } catch (Exception ex1) {

                    //print de error
                    System.err.println(ex1);

                    //sluit de applicatie omdat er geen oplossing meer is
                    System.exit(0);
                }

            }

            //als het idMarktNaam -1 blijft roep dan de try catch op
            if (idMarktNaam == -1) {
                try {
                    //roep de methoden op die alle markten van bittrex bij moet houden
                    BittrexMarktUpdate bmp = new BittrexMarktUpdate("bittrex");
                    bmp.marktUpdateLijsten();
                } catch (Exception ex1) {

                    //print de error
                    System.err.println(ex1);

                    //sluit de applicatie omdat er geen oplossing meer is
                    System.exit(0);
                }
            }

            //vraag de markt data op
            double high = object.getDouble("High");
            double low = object.getDouble("Low");
            double volume = object.getDouble("Volume");
            double bid = object.getDouble("Bid");
            double ask = object.getDouble("Ask");
            double last = object.getDouble("Last");
            double volumeBTC = volume * last;

            //roep de methoden op die de data verwerkt
            try {
                super.marktDataUpdate(high, low, volume, volumeBTC, bid, ask, last, idMarktNaam, idExchange, saveData);
            } catch (Exception ex) {

                //problemen met het database
                System.err.println(ex);
            }
        }

    }

    /**
     * Methoden om boolean te updaten
     *
     * @param saveData boolean of de markt data opgeslagen moet worden
     */
    @Override
    public void setterSaveData(boolean saveData) {
        this.saveData = saveData;
    }
}
