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

    /**
     * Main methoden die reload moet worden
     *
     * @throws IOException als er een file error is
     * @throws Exception als er andere exceptie plaats vind
     */
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
                    if (rs.next()) {
                        idMarktNaam = rs.getInt("idMarktNaam");
                    }
                    updateMarkt(autoCountObject, idMarktNaam);
                    String sqlString = sqlString(autoCountObject, timeStamp, idMarktNaam);
                    mysql.mysqlExecute(sqlString);
                }

            }

        }

    }

    /**
     * Methoden die een sql stament maakt die uitgevoerd moet worden
     *
     * @param autocoutObject het object
     * @param idtimestamp id nummer van de timestamp
     * @param idMarktNaam id marktnaam
     * @return sql Insert string
     */
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

    /**
     * Methoden die het id nummer van de timestamp return
     *
     * @return idTimestamp
     * @throws Exception als er een error is mysql
     */
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
    
    /**
     * Markt updater
     * @param autocoutObject de exchange object
     * @param idMarktNaam idmarktnaam
     * @throws Exception als er een mysql error is
     */
    private void updateMarkt(JSONObject autocoutObject, int idMarktNaam) throws Exception {
        String sqlString = " ";
        double high = autocoutObject.getDouble("High");
        double low = autocoutObject.getDouble("Low");
        double volume = autocoutObject.getDouble("Volume");
        double bid = autocoutObject.getDouble("Bid");
        double ask = autocoutObject.getDouble("Ask");
        double last = autocoutObject.getDouble("Last");
        double volumeBTC = volume * last;
        int count = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktupdate WHERE idMarktNaam = '" + idMarktNaam + "' and idHandelsplaats = 1");

        if (count < 1) {
            sqlString = "INSERT INTO marktupdate(high, low, volume, volumeBTC, bid, ask, last, idMarktNaam, idHandelsplaats) values "
                    + "('" + high + "', '" + low + "', '" + volume + "', '" + volumeBTC + "', '" + bid + "', '" + ask + "', '" + last + "', '" + idMarktNaam + "', '" + BITTREX_NUMMER + "')";
        } else if (count == 1) {
            sqlString = "UPDATE marktupdate SET high = '" + high + "', low = '" + low + "', volume = '" + volume + "', volumeBTC = '" + volumeBTC
                    + "', bid =" + bid + ", ask = '" + ask + "', last = '" + last + "' where idMarktNaam = '" + idMarktNaam + "' and idhandelsplaats = 1";

        }
        mysql.mysqlExecute(sqlString);

    }

}
