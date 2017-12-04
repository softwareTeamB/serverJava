package invullenMarktlijst;

import global.ConsoleColor;
import http.Http;
import mysql.Mysql;

/**
 * Bitfinex
 * @author michel
 */
public class Bitfinex  extends MainMarktUpdate {
    
    Http http = new Http();
    Mysql mysql = new Mysql();
    
    private final String BITFINEX_URL = "https://api.bitfinex.com/v1/symbols";

    /**
     * 
     * @throws Exception 
     */
    @Override
    public void marktUpdateLijsten() throws Exception {
    
        String httpResponse = "https://api.bitfinex.com/v1";
        ConsoleColor.warn("");
    
    
    }
}
