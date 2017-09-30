package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
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
    
    public GDAX(boolean saveData) throws Exception {
        //vul de final met de exchange id nummer
        this.ID_EXCHANGE = super.getExchangeNummer(EXCHANGE_NAAM);

        //vraag alle markt namen op met het exchange nummer
    }

    /**
     *Methoden om alle data te krijgen
     * @param saveData sla de data op
     */
    @Override
    public void getMarktData(boolean saveData) {
        
    }

    //wacht 1 seconde
    //       TimeUnit.SECONDS.sleep(1);   
    
    
}
