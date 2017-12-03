package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import mysql.Mysql;

/**
 * Methoden om de markt data op te slaan
 *
 * @author michel
 */
public abstract class MainMarktGevens {

    Mysql mysql = new Mysql();

    //coinLijstObject
    private JSONObject coinLijstObject = new JSONObject();

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
            ConsoleColor.out(idHandelsplaats + "_" + idMarktnaam);
            //voeg toe in mysql
            mysql.mysqlExecute(sqlString);

            ConsoleColor.out("Een markt is toegevoegd in marktupdate");
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
        
        //kijk of de histry of geslagen moet worden
        if (history) {
            String sqlString = "INSERT INTO marktupdatehistory(high, low, volume, volumeBTC, bid, ask, last, "
                    + "idMarktNaam, idHandelsplaats, idtimestamp) values ('" + high + "', '" + low + "', '" + volume + 
                    "', '" + volumeBTC + "', '" + bid + "', '" + ask + "', '" + last + "', '" + idMarktnaam + "', '" 
                    +idHandelsplaats + "', '"
                    + timestampString() + "')";

            mysql.mysqlExecute(sqlString);

        }
    }

    private String timestampString() {

        //get timetampx
        Date date = new Date();
        long time = date.getTime();
        String timeStamp = String.valueOf(time / 1000);

        return timeStamp;
    }

    private int timeStamp() throws Exception {

        int timeId = 0;
        Date date = new Date();
        long time = date.getTime();
        String timeStamp = String.valueOf(time / 1000);

        //ophalen datum vandaag
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //datum in losse delen zetten
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //lose delen bewerken
        String dayS = dmToString(day);
        String monthS = dmToString(month);

        //maken datum string
        String dateString = year + "/" + monthS + "/" + dayS;

        int count = mysql.mysqlCount("SELECT COUNT(*) AS total FROM timestamp WHERE time = '" + timeStamp + "'");

        if (count < 1) {
            mysql.mysqlExecute("INSERT INTO timestamp (time, date) values ('" + timeStamp + "', '" + dateString + "')");
            ResultSet rs = mysql.mysqlSelect("SELECT idtimestamp from timestamp where time = '" + timeStamp + "'");
            if (rs.next()) {
                timeId = rs.getInt("idtimestamp");
            }
        }
        return timeId;
    }

    /**
     * aanpassen formaat zodat de datum in de database kan
     *
     * @param item maand of dag die bewerkt moet worden
     * @return string die in de database past
     */
    private String dmToString(int item) {
        String bewerkt = "";
        if (item < 10) {
            bewerkt = "0" + item;
        } else {
            bewerkt = "" + item;
        }

        return bewerkt;
    }

    /**
     * Om trickers op te slaan
     *
     * @param ask
     * @param bid
     * @param volume
     */
    private void saveTrickers(double ask, double bid, double volume) {

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
     * krijg het idMarktPositie
     * @param marktNaam marktnaam van de markt naam op de exchange
     * @param idExchange het exchange nummer
     * @return idMarktPositie
     * @throws SQLException sqlException error
     * @throws Exception algemene error
     */
    public int getDBMarktNummer(String marktNaam, int idExchange) throws SQLException, Exception {

        //kijk of de cointag al in de memoryLijst staat
        if (coinLijstObject.has(marktNaam)) {

            //vraag het nummer op
            int idMarktPositie = coinLijstObject.getInt(marktNaam);

            //return idMarktNaam
            return idMarktPositie;

        } else {
            
            //kijk of de markt in de marktlijsten staat
            String countSql = "SELECT COUNT(*) AS total FROM marktlijsten "
                    + "WHERE naamMarkt='" + marktNaam + "' AND idHandelsplaats='" + idExchange + "'";

            //kijk of het nummer er in staat
            int count = mysql.mysqlCount(countSql);

            //als het nummer er niet in staat wordt het if stament geladen
            if (count == 0) {

                //krijg het verbindings teken
                String verbindingsTekenSql = "SELECT verbindingsTeken FROM handelsplaats "
                        + "WHERE idHandelsplaats='" + idExchange + "'";

                ResultSet rs = mysql.mysqlSelect(verbindingsTekenSql);
                String verbindingsTeken = null;
                while (rs.next()) {

                    verbindingsTeken = rs.getString("verbindingsTeken");
                }

                //split de marktNaamDB. Bij split is wordt het verbindings teken gebruikt
                String[] parts = marktNaam.split(verbindingsTeken);
                String baseCoin = parts[0];
                String marktCoin = parts[1];

                //maakt de marktNaamDB
                String marktNaamDB = baseCoin + "-" + marktCoin;

                //kijk of het countsql2 stament basecoin en marktCurrency al in de marktnaam staat
                String countSql2 = "SELECT COUNT(*) AS total FROM marktnaam "
                        + "WHERE marktnaamDb='"+marktNaamDB+"' AND basecoin='" + baseCoin + "' "
                        + "AND marktCurrency='" + marktCoin + "'";
                int count2 = mysql.mysqlCount(countSql2);

                //stament bij 0 voeg de baseCoin, marktCoin en marktnaamDB toe
                if (count2 == 0) {

                    //insert stament
                    String insertStament = "INSERT INTO marktnaam (marktnaamDb, basecoin, marktCurrency) "
                            + "VALUES ('" + marktNaamDB + "', '" + baseCoin + "', '" + marktCoin + "')";
                    //run stament
                    mysql.mysqlExecute(insertStament);
                }

                //vraag idMarktNaam markt op
                String idMarktNaamSql = "SELECT idMarktNaam AS nummer FROM marktnaam WHERE marktnaamDb='"+marktNaamDB+"'";
                int idMarktNaam = mysql.mysqlNummer(idMarktNaamSql);
                
                //voeg het toe in marktLijsten toe
                String inserInto2 = "INSERT INTO marktlijsten (naamMarkt, idMarktNaam, idHandelsplaats) "
                        + "VALUES ('"+marktNaam+"', '"+idMarktNaam+"', '"+idExchange+"')";
                mysql.mysqlExecute(inserInto2);   
            }
            
            //krijg de idMarktPosistie
            String idMarktPositieSql = "SELECT idMarktPositie AS nummer FROM marktlijsten "
                    + "WHERE naamMarkt='"+marktNaam+"' AND idHandelsplaats='" + idExchange + "' ";
            int idMarktPositie = mysql.mysqlNummer(idMarktPositieSql);
            
            //JSONObject
            coinLijstObject.put(marktNaam, idMarktPositie);
            
            //return idMarktNaam
            return idMarktPositie;
        }
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
    
        /**
     * Update marktData via de setMethoden
     *
     * @param marktDataUpdate string die de marktData aan moet leveren
     * @param exchangeID exchangeID geld als key
     */
    public void setMarktDataUpdate(String marktDataUpdate, int exchangeID) {
        
        //maak van een int een string
        String exchangeIDString = exchangeID+"";
       
        //jsonobject remove key
        serverjava.ServerJava.webSocket.marktDataUpdate.remove(exchangeIDString);
        
        
        //voeg het nieuwe object toe
        serverjava.ServerJava.webSocket.marktDataUpdate.put(exchangeIDString, marktDataUpdate);
    }
}
