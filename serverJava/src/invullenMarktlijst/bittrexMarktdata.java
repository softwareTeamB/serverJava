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
import java.util.Date;
import mysql.Mysql;

/**
 *
 * @author Jaros
 */
public class bittrexMarktdata {

    Mysql mysql = new Mysql();
    Http http = new Http();
    private boolean eerstekeer = true;
    private final int BITTREX_NUMMER = 1;
    private final String url = "https://bittrex.com/api/v1.1/public/getmarketsummaries";

    public void bittrexMarktdataControler() throws IOException, Exception {
        boolean bekend = false;
        int idMarktNaam = 0;
        JSONObject marktDataR = new JSONObject(http.getHttpObject(url));
        JSONArray marktData = marktDataR.getJSONArray("result");
        int timeStamp = timeStamp();

        if (timeStamp != 0) {
            for (int i = 0; i < marktData.length(); i++) {
                JSONObject autoCountObject = marktData.getJSONObject(i);
                String dbNaam = autoCountObject.getString("MarketName");
                bekend = marktCheck(dbNaam);
                if (bekend) {
                    ResultSet rs = mysql.mysqlSelect("SELECT idMarktNaam FROM marktnaam where marktnaamDb = '" + dbNaam + "'");
                    if (rs.next()) 
                        idMarktNaam  = rs.getInt("idMarktNaam");

                    }
                    String sqlString = sqlString(autoCountObject, timeStamp, idMarktNaam);
                    mysql.mysqlExecute(sqlString);

                }

            }

        }

    

    private String sqlString(JSONObject autocoutObject, int idtimestamp, int idMarktNaam) {
        double high = autocoutObject.getDouble("High");
        double low = autocoutObject.getDouble("Low");
        double volume = autocoutObject.getDouble("Volume");
        double bid = autocoutObject.getDouble("Bid");
        double ask = autocoutObject.getDouble("Ask");
        double last = autocoutObject.getDouble("Last");
        double volumeBTC = volume * last;

        String sqlString = "INSERT INTO marktupdatehistory(high, low, volume, volumeBTC, bid, ask, last, idMarktNaam, idHandelsplaats, idtimestamp) values "
                + "('" + high + "', '" + low + "', '" + volume + "', '" + volumeBTC + "', '" + bid + "', '" + ask + "', '" + last + "', '" + idMarktNaam + "', '" + +BITTREX_NUMMER + "', '" + idtimestamp + "')";

        return sqlString;
    }

    private boolean marktCheck(String dbNaam) throws Exception {
        boolean gelukt = false;
        boolean ronde2 = false;

        int kloptHet = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = '" + dbNaam + "'");
        if (kloptHet == 1) {
            gelukt = true;
        } else if (kloptHet > 1) {
            if (eerstekeer) {
                eerstekeer = false;
                BittrexMarktUpdate bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
                bittrexMarktUpdate.marktUpdateLijsten();
                ronde2 = marktCheck(dbNaam);
                if (ronde2) {
                    eerstekeer = true;
                    return ronde2;
                }
            }
        }

        eerstekeer = true;
        return gelukt;
    }

    private int timeStamp() throws Exception {
        int timeId = 0;
        Date date = new Date();
        long time = date.getTime();
        String timeStamp = String.valueOf(time / 1000);
        int count = mysql.mysqlCount("SELECT COUNT(*) AS total FROM timestamp WHERE time = '" + timeStamp + "'");

        if (count < 1) {
            mysql.mysqlExecute("INSERT INTO timestamp (time) values ('" + timeStamp + "')");
            ResultSet rs = mysql.mysqlSelect("SELECT idtimestamp from timestamp where time = '" + timeStamp + "'");
            if (rs.next()) {
                timeId = rs.getInt("idtimestamp");
            }
        }
        return timeId;
    }

}
