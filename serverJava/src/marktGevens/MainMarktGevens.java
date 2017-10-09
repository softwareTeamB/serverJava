package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql.Mysql;

/**
 * Methoden om de markt data op te slaan
 *
 * @author michel
 */
public abstract class MainMarktGevens {

    Mysql mysql = new Mysql();

    //abstracten methodens
    public abstract void getMarktData(boolean saveData);
   
    /**
     * Markt saver
     *
     * @param high hoogte prijs 24 uur
     * @param low laagste prijs 24 uur
     * @param volume volume in de coin naam
     * @param volumeBTC volume in btc
     * @param bid de hoogste bid prijs
     * @param ask hoogste vraag prijs
     * @param last laatst bepaalde prijss
     * @param idHandelsplaats id van de exchange
     * @param idMarktnaam id van de marktnaamDB
     * @param history of de data opgeslagen moet worden
     * @throws java.lang.Exception als er een error optreed
     *
     */
    public void marktDataUpdate(
            double high,
            double low,
            double volume,
            double volumeBTC,
            double bid,
            double ask,
            double last,
            int idHandelsplaats,
            int idMarktnaam,
            boolean history
    ) throws Exception {

        //kijk of de codes al in het database staat
        String countSql = "SELECT COUNT(*) AS total FROM marktupdate"
                + " WHERE idMarktNaam='" + idMarktnaam + "'"
                + " AND idHandelsplaats='" + idHandelsplaats + "'";

        int count = mysql.mysqlCount(countSql);
        
        //als de markt niet bekend is word het toegevoegd
        //of anders worde de markt update
        if (count == 0) {

            //sql insert stament
            String sqlString = "INSERT INTO marktupdate(high, low, volume, volumeBTC, bid, ask, last, idMarktNaam, idHandelsplaats) values "
                    + "('" + high + "', '" + low + "', '" + volume + "', '" + volumeBTC + "', '" + bid + "', '" + ask + "', '" + last + "', '" + idMarktnaam + "', '" + +idHandelsplaats + "')";
            System.out.println(idHandelsplaats + "_" + idMarktnaam);
            //voeg toe in mysql
            mysql.mysqlExecute(sqlString);

            System.out.println("Een markt is toegevoegd in marktupdate");
        } else {

            //update stament
            String sqlUpdate = "UPDATE marktupdate"
                    + " SET high= " + high + ","
                    + " low = " + low + ","
                    + " volume = " + volume + ","
                    + " volumeBTC= " + volumeBTC + ","
                    + " bid=" + bid + ","
                    + " ask=" + ask + ","
                    + " last=" + last
                    + " WHERE idMarktNaam='" + idMarktnaam + "'"
                    + " AND idHandelsplaats='" + idHandelsplaats + "'";

            //updater
            mysql.mysqlExecute(sqlUpdate);
        }

        if (history) {
            System.err.println("Bouw de code in het opslaat in de markt history");
        }
    }
    
    /**
     * Om trickers op te slaan
     * @param ask
     * @param bid
     * @param volume 
     */
    private void saveTrickers(double ask, double bid, double volume){
    
    
    }

    /**
     * Methoden om de exchange nummer te krijgen
     *
     * @param exchangeNaam exchange naam
     * @return het exchange nummer in de database
     * @throws Exception
     */
    public int getExchangeNummer(String exchangeNaam) throws Exception {

        String getNummer = "SELECT getExchangeNummer('" + exchangeNaam + "') AS nummer;";
        return mysql.mysqlNummer(getNummer);
    }

    /**
     * Methoden om de memory te vullen met een jsonobject db
     *
     * @param exchangeNaam naam van de handelsplaats
     * @return return een object waar een array in zit en een JSONObject
     * @throws SQLException als er een error is
     */
    public JSONObject fixKeysMarktLijst(String exchangeNaam) throws SQLException {

        JSONArray arrayMarkt = new JSONArray();
        JSONObject marktKey = new JSONObject();

        //count is voor later belangrijk
        int count = 0;

        //vraag alle marken op uit de exchange
        String sqSelectl = "SELECT * FROM marktlijstvolv1 WHERE handelsplaatsNaam='" + exchangeNaam + "'";
        ResultSet rs = mysql.mysqlSelect(sqSelectl);

        //loop door de resultset heen
        while (rs.next()) {

            //update count
            count = 1;

            //marktnaamExchange
            String marktNaam = rs.getString("naamMarkt");
            int idMarktNaam = rs.getInt("idMarktNaam");

            //array
            arrayMarkt.put(marktNaam);

            //object
            marktKey.put(marktNaam, idMarktNaam);
        }

        if (count == 0) {
            throw new SQLException("Er is geen lege reponse.");
        }

        //maak er een object van
        JSONObject responseObject = new JSONObject();
        responseObject.put("object", marktKey);
        responseObject.put("array", arrayMarkt);

        //reponse object
        return responseObject;
    }

}
