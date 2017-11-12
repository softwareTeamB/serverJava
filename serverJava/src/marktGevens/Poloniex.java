package marktGevens;

import JSON.JSONObject;
import global.ConsoleColor;
import http.Http;
import java.io.IOException;
import mysql.Mysql;

/**
 * Maak de marktGevens op de balance op de vragen
 *
 * @author michel
 */
public class Poloniex extends MainMarktGevens {

    Mysql mysql = new Mysql();
    Http http = new Http();

    private final String BASIS_URL = "https://poloniex.com/public?command=returnTicker";
    private int idExchange;

    public Poloniex() {
    
        try {
            this.idExchange = super.getExchangeNummer("poloniex");
            ConsoleColor.out("Poloniex idNummer is: "+idExchange);
        } catch (Exception ex) {
            ConsoleColor.err("Error bij poloniex marktgevens in de constructor. Dit is de error: "+ex+". Het systeem wordt afgesloten");
            System.exit(0);
        }
    }
    
    @Override
    public void getMarktData(boolean saveData) {

        //get String
        String responseString;
        try {
            responseString = http.getHttpBrowser(BASIS_URL);
        } catch (IOException ex) {
            ConsoleColor.err("lol"+ex);
            return;
        }
        
        //maak er een jsonObject van
        JSONObject response = new JSONObject(responseString);
        
        
        //loop door het object heen
        for (int i = 0; i < response.names().length(); i++) {

            //vraag keyNaam op
            String keyNaam = response.names().getString(i);

            //pak het eerste object
            JSONObject object2 = response.getJSONObject(keyNaam);

            String[] parts = keyNaam.split("_");
            String part1 = parts[0];
            
            //vraag de gegevens op
            double high = object2.getDouble("high24hr");
            double last = object2.getDouble("last");
            double bid = object2.getDouble("highestBid");
            double low = object2.getDouble("low24hr");
            double ask = object2.getDouble("lowestAsk");

            //vaak volume en volumeBTC double aan
            double volume;
            double volumeBTC;

            if ("USDT".equals(part1)) {
                //dit moet zo gebeuren omdat we van btc naar dollar om zetten en dat is dan het volume
                volume = object2.getDouble("baseVolume") * bid;
                volumeBTC = object2.getDouble("baseVolume");
            } else {
                volume = object2.getDouble("baseVolume");
                volumeBTC = object2.getDouble("baseVolume") * bid;
            }
            
            //als er een error op treed bij het toevoegen of updaten van de data
            try {
                
                //vraag de marktnaam nummer op
                int idMarktPositie = super.getDBMarktNummer(keyNaam, idExchange);
                
                
                //stuur het jsonobject naar de webSoacket
                super.setMarktDataUpdate(keyNaam, idMarktPositie);
                
                //roep de methoden op die het opslaan systeem doet
                super.marktDataUpdate(high, low, volume, volumeBTC, bid, ask, last, idExchange,
                        idMarktPositie, saveData);
            } catch (Exception ex) {
                ConsoleColor.err("Error bij poloniex in de package marktGegevens. " + ex);
            }
        }
    }

}
