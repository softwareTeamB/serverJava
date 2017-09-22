/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
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

    public void invullenCoinsBittrex() throws SQLException, Exception {

        int bittrex = 2;//standaard code bittrex in db

        //ophalen van informatie bittrex api
        JSONObject objectBittrex = new JSONObject(http.getHTTP("https://bittrex.com/api/v1.1/public/getmarkets"));
        JSONArray coinLijsten = objectBittrex.getJSONArray("result");

        for (int i = 0; i < coinLijsten.length(); i++) { //loopen door aantal gevonden markten
            //System.out.println("ronde " + i);

            JSONObject autoCountObject = coinLijsten.getJSONObject(i);//loopen door data

            String marktnaamDb = autoCountObject.getString("MarketName");

            boolean bestaatAlM = ingevuld(marktnaamDb); // controle of markt al bestaat

            if (bestaatAlM == false) { //als markt niet bestaat vul deze in
                //maken sql string
                String baseCoin = autoCountObject.getString("BaseCurrency");
                String marktCurrency = autoCountObject.getString("MarketCurrency");
                String sqlTwee = ("INSERT INTO marktnaam(MarktnaamDb, baseCoin, MarktCurrency) values ('" + marktnaamDb + "', '" + baseCoin + "' ,'" + marktCurrency + "')");

                //uitvoeren mysqlstring
                try {
                    mysql.mysqlExecute(sqlTwee);
                    invullenMarttLijstenBittrex(marktnaamDb);
                } catch (SQLException ex) {
                    Logger.getLogger(insertFuncties.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                int count = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktlijstvolv1 WHERE handelsplaatsNaam = 'bittrex' and marktnaamDb = " + '"' + marktnaamDb + '"');
                if (count == 0) {
                    invullenMarttLijstenBittrex(marktnaamDb);
                }

            }

        }
    }

    /**
     *
     * @param dbNaam naam die gecontroleerd worden
     * @return bestaat waarde al
     */
    private boolean ingevuld(String dbNaam) {//controle of markt al bestaat
        boolean bestaatAl = true;

        try {
            String sqlString = ("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = " + '"' + dbNaam + '"');//opstellen sql string

            int total = mysql.mysqlCount(sqlString);

            if (total < 1) {//als naam er niet voorkomt zorg ervoor 
                bestaatAl = false;
            }
        } catch (Exception ex) {
            Logger.getLogger(insertFuncties.class.getName()).log(Level.SEVERE, null, ex);
        }

        return bestaatAl;
    }

    public void invullenMarttLijstenBittrex(String marktnaam) throws SQLException {

        ResultSet rs = mysql.mysqlSelect("SELECT idMarktNaam from marktNaam where marktnaamDb = '" + marktnaam + "'");
        while (rs.next()) {
            int idMarkt = rs.getInt("idMarktNaam");

            String sql = "INSERT INTO marktlijsten (naamMarkt, idMarktnaam, idHandelsplaats) values ('" + marktnaam + "', " + idMarkt + ", 1)";
            mysql.mysqlExecute(sql);
        }

    }

    public void invullenCoinsPolo(String markt, String url, String symbool) throws Exception {//autamatiseren van verschillende markten door input is mogenlijk

        int idMarktNaam = 2;
        int IdHndelsplaats = 0;

        ResultSet rs3 = mysql.mysqlSelect("select idHandelsplaats from handelsplaats where handelsplaatsNaam = '" + markt + "'");
        if (rs3.next()) {
            IdHndelsplaats = rs3.getInt("idHandelsplaats");
            System.out.println("hij denkt " + IdHndelsplaats);
        }

        
        //System.out.println(http.getHttpObject("https://api.coinmarketcap.com/v1/ticker/"));
        JSONArray coinLijsten = new JSONArray(http.getHttpObject("https://api.coinmarketcap.com/v1/ticker/"));
        JSONObject marktdata = new JSONObject(http.getHTTP(url));
        //System.out.println(coinLijsten.length());

        for (int i = 0; i < 10; i++) {//aantal hoofd coins
            JSONObject autoCountObject = coinLijsten.getJSONObject(i);
            String begin = autoCountObject.getString("symbol");
            for (int j = 0; j < coinLijsten.length(); j++) {//aantal mogelijke markten
                System.out.println("keer " + i + "....ronde " + j);
                if (i != j) {

                    JSONObject dataStroom = coinLijsten.getJSONObject(j);

                    String dbNaam = begin + "-" + dataStroom.getString("symbol");//maken naam voor in de database
                    String naamMarkt = begin + symbool + dataStroom.getString("symbol");//maken naam voor de markt

                    if (marktdata.has(naamMarkt)) {//aantal bestaande markten
                        int bestaat = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb = " + '"' + dbNaam + '"');//kijken of makrt al bestaat in marktnaam

                        if (bestaat < 1) {//als nog niet bestaat maak dan aan

                            String marktCurrency = dataStroom.getString("symbol");

                            String sqlTwee = ("INSERT INTO marktnaam(MarktnaamDb, baseCoin, MarktCurrency) values ('" + dbNaam + "', '" + begin + "' ,'" + marktCurrency + "')");
                            mysql.mysqlExecute(sqlTwee);
                        }

                        ResultSet rs = mysql.mysqlSelect("SELECT idMarktNaam from marktNaam where marktnaamDb = '" + dbNaam + "'");//ophalen id in marktnaam 
                        if (rs.next()) {
                            idMarktNaam = rs.getInt("idMarktNaam");
                        }

                        int bestaatLijsten = mysql.mysqlCount("SELECT COUNT(*) AS total FROM marktlijsten WHERE idMarktnaam = " + idMarktNaam
                                + " and  idHandelsplaats = " + IdHndelsplaats + "");//controle of markt al bekend is bij marktlijsten

                        if (bestaatLijsten < 1) {//invullen van martktlijsten
                            System.out.println("inserted");

                            String sql = "INSERT INTO marktlijsten (naamMarkt, idMarktnaam, idHandelsplaats) values ('" + naamMarkt + "', '" + idMarktNaam + "', '" + IdHndelsplaats + "')";
                            //System.out.println(sql);
                            mysql.mysqlExecute(sql);
                        }
                    }

                }

            }

        }

    }
}
