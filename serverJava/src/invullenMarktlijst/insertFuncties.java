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

    public void invullenCoinsBittrex() throws SQLException, Exception {

        int bittrex = 1;//standaard code bittrex in db

        //ophalen van informatie bittrex api
        JSONObject objectBittrex = new JSONObject(http.GetHttp("https://bittrex.com/api/v1.1/public/getmarkets"));
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

            String sql = "INSERT INTO marktlijsten (naamMarkt, idMarktnaam, idHandelsplaats) values ('" + marktnaam + "', '" + idMarkt + "', 1)";
            mysql.mysqlExecute(sql);
        }

    }
}
