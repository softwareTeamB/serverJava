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
    private boolean saveData;

    public GDAX(boolean saveData) throws Exception {
        
        //geef aan of alle markt data opgeslagen moet worden
        this.saveData = saveData;

        //vul de final met de exchange id nummer
        this.ID_EXCHANGE = super.getExchangeNummer(EXCHANGE_NAAM);

        //vraag alle markt namen op met het exchange nummer
    }

    /**
     *
     */
    @Override
    public void getMarktData() {
        
    }

    //wacht 1 seconde
    //       TimeUnit.SECONDS.sleep(1);   
    
    
    /**
     * Een methoden om in de klasse een update door te geven of er data wel of niet opgeslagen moet worden
     *
     * @param saveData een boolean of alle data in het database opgeslagen moet worden
     */
    @Override
    public void setterSaveData(boolean saveData) {

        //update private methoden van de save boolean
        this.saveData = saveData;
    }

}
