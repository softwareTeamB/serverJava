package marktGevens;

import mysql.Mysql;

/**
 * Methoden om de markt data op te slaan
 *
 * @author michel
 */
public abstract class MainMarktGevens {

    Mysql mysql = new Mysql();

    public abstract void getMarktData();

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
        System.out.println(count);

        //als de markt niet bekend is word het toegevoegd
        //of anders worde de markt update
        if (count == 0) {

            //sql insert stament
            String sqlString = "INSERT INTO marktupdate(high, low, volume, volumeBTC, bid, ask, last, idMarktNaam, idHandelsplaats) values "
                    + "('" + high + "', '" + low + "', '" + volume + "', '" + volumeBTC + "', '" + bid + "', '" + ask + "', '" + last + "', '" + idMarktnaam + "', '" + +idHandelsplaats + "')";

            //voeg toe in mysql
            mysql.mysqlExecute(sqlString);

            System.out.println("Een markt is toegevoegd in marktupdate");
        } else {

            //update stament
            //String updateSql = ;
            System.err.println("De markt data moet geupdate worden maar die code is nog niet gebouwd");
        }

    }
}
