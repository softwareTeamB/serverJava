/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql.Mysql;

/**
 *
 * @author Jaros
 */
public class CEXioMarktUpdate {

    Mysql mysql = new Mysql();
    Http http = new Http();

    private String marktnaamDb;

    public void invullenCoinsCEXio() throws Exception {
       
        JSONArray coinLijsten = new JSONArray(http.getHttpObject("https://www.cex.io/api/currency_limits"));

        for (int i = 0; i < coinLijsten.length(); i++) {

            JSONObject autoCountObject = coinLijsten.getJSONObject(i);
            Boolean bestaatMarkt = marktBestaat(autoCountObject);

            if (!bestaatMarkt) {
                maakMarkt(autoCountObject);
                System.out.println("toegevoegt");

            }

        }
        
       

    }

    /**
     * maak de url lijst compleet
     *
     * @return de coins die opgehaald moeten worden
     * @throws IOException
     */
    private String urlMaker() throws IOException {
        String url = "";
        //ophalen coins van coinlijsten
        JSONArray coinLijsten = new JSONArray(http.getHttpObject("https://cex.io/api/currency_limits"));

        for (int i = 0; i < coinLijsten.length(); i++) {

            //controle of waarde USD is
            JSONObject autoCountObject = coinLijsten.getJSONObject(i);
            String check = autoCountObject.getString("symbol");
            if (check.equals("USD")) {
                check = "USDT";
            }

            //maken url
            url = url + "/" + check;

        }
        return url;
    }

    /**
     *
     * @param autoCountObject object met marktnaamen
     * @return bestaat de markt al in de database
     * @throws Exception
     */
    private boolean marktBestaat(JSONObject autoCountObject) throws Exception {
        //aanmaaken controle informatie
        String USD = "USD";
        String begin = autoCountObject.getString("symbol1");
        String eind = autoCountObject.getString("symbol2");

        //aanpassen USD naar USDT
        if (begin.equals(USD)) {
            begin = "USDT";
        }
        if (eind.equals(USD)) {
            eind = "USDT";
        }

        //controle mogelijkheid een
        String marktnaamDb = begin + "-" + eind;

        int aantal = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = " + marktnaamDb + "'");
        if (aantal == 1) {
            this.marktnaamDb = marktnaamDb;
            return true;

            //controle mogelijkheid twee
        } else {
            marktnaamDb = eind + "-" + begin;
            aantal = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = " + marktnaamDb + "'");
            if (aantal == 1) {
                this.marktnaamDb = marktnaamDb;
                return true;
            } else {
                return false;
            }

        }

    }

    private void maakMarkt(JSONObject autoCountObject) throws SQLException {
        String baseCoin = autoCountObject.getString("symbol1");
        String marktCurrency = autoCountObject.getString("symbol2");
        mysql.mysqlExecute("INSERT INTO marktnaam (marktnaamDb, baseCoin, marktCurrency) values ('" + this.marktnaamDb + "', '" + baseCoin + "', '" + marktCurrency + "')");
    }

}
