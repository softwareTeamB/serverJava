/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.Mysql;

/**
 *
 * @author Jaros
 */
public class insertFuncties {

    Mysql mysql = new Mysql();
    Http http = new Http();

    public void invullenCoinsBittrex() throws SQLException {

        int bittrex = 0;

        JSONObject objectBittrex = new JSONObject(http.GetHttp("https://bittrex.com/api/v1.1/public/getmarkets"));
        JSONArray coinLijsten = objectBittrex.getJSONArray("result");
        String HandelsplaatsNaam = "bittrex";
        String sqlEen = ("Select idHandelsplaats from handelsplaats where handelsplaatsNaam = 'bittrex' ");
        ResultSet rs = mysql.mysqlSelect(sqlEen);
        bittrex = rs.getInt("idHandelsplaats");

        //get info
        for (int i = 0; i < coinLijsten.length(); i++) {

            JSONObject autoCountObject = coinLijsten.getJSONObject(i);

            if (bittrex != 0) {

                String dbNaam = autoCountObject.getString("MarketName");
                String marktnaamDb = autoCountObject.getString("MarketName");

                boolean bestaatAlM = ingevuld(marktnaamDb); // controle of mart al bestaat
                if (bestaatAlM != true) {
                    String baseCoin = autoCountObject.getString("BaseCurrency");
                    String marktCurrency = autoCountObject.getString("MarketCurrency");
                    String sqlTwee = ("INSTERT INTO marktnaam(MarktnaamDb, baseCoin, MarktCurrency) values ('" + marktnaamDb + "', " + baseCoin + "'," + marktCurrency + "')");
                    try {
                        mysql.mysqlExecute(sqlTwee);
                    } catch (SQLException ex) {
                        Logger.getLogger(insertFuncties.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        }
    }

    private boolean ingevuld(String dbNaam) {
        boolean bestaatAl = true;

        try {
            String sqlString = ("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = " + '"' + dbNaam + '"');
            int total = mysql.mysqlCount(sqlString);
            if (total == 1) {
                bestaatAl = false;
            }
        } catch (Exception ex) {
            Logger.getLogger(insertFuncties.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bestaatAl;
    }
    
    public void invullenMarttLijsten(){
        
        
        
        
        
        
        
        
        
    }

}
