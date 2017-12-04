package invullenMarktlijst;

import global.ConsoleColor;
import java.sql.SQLException;
import mysql.Mysql;

/**
 * Main update methoden om de markt de update met abstracte methoden
 *
 * @author michel
 */
public abstract class MainMarktUpdate {

    //objecten maken
    Mysql mysql = new Mysql();

    //urls
    private final String URL_BITTREX = "https://bittrex.com/api/v1.1";
    private final String URL_BITSTAMP = "https://www.bitstamp.net/api/v2/";
    private final String URL_GDAX = "https://api-public.sandbox.gdax.com";
    private final String URL_CEX_IO = "https://cex.io/api";

    /**
     * Voeg marktnaam toe
     *
     * @param marktNaamDB db markt naam
     * @param baseCoin base coin
     * @param marktCoin markt coin
     * @throws Exception error exception
     */
    public int marktNaam(String marktNaamDB, String baseCoin, String marktCoin) throws Exception {

        //kijk nog een keer of de marktNaamDB echt niet in het database staat
        String countSql = "SELECT COUNT(*) AS total FROM marktnaam"
                + " WHERE marktnaamDb='" + marktNaamDB + "';";
        int count = mysql.mysqlCount(countSql);

        if (count == 0) {
            ConsoleColor.out("marktNaamDB staat niet in de database.");

            //voeg de data toe
            String insertSql = "INSERT INTO marktnaam(MarktnaamDb, baseCoin, MarktCurrency)"
                    + " values ('" + marktNaamDB + "', '" + baseCoin + "' ,'" + marktCoin + "')";
            mysql.mysqlExecute(insertSql);

            ConsoleColor.out("Marktnaam is in de database toegevoegd.");

        } else {
            ConsoleColor.out("marktNaamDB staat in de database.");
        }
        //vraag het nummer op
        String sqlSelect = "SELECT idMarktNaam AS nummer FROM marktnaam"
                + " WHERE marktnaamDb='" + marktNaamDB + "';";

        return mysql.mysqlExchangeNummer(sqlSelect);
    }

    /**
     * Add markt in de database bij dat bij houd
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     * @throws java.sql.SQLException sqlExceptie
     */
    public void insertMarktLijsten(int exchangeNummer, int marktNaamDB, String marktNaamExchange) throws SQLException {

        //insert in marktlijsten
        String insertInto = "INSERT INTO marktLijsten(idHandelsplaats, idMarktNaam, naamMarkt) "
                + "VALUES (" + exchangeNummer + ", " + marktNaamDB + ", '" + marktNaamExchange + "')";
        mysql.mysqlExecute(insertInto);
    }

    /**
     * Add markt in de database bij dat bij houdt
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     * @param tradeMinSize
     * @throws java.sql.SQLException om sql error op te vangen
     */
    public void marktLijsten(int exchangeNummer, String marktNaamDB, String marktNaamExchange, int tradeMinSize) throws SQLException {

        //insert in marktlijsten
        String insertInto = "INSERT INTO marktLijsten(idHandelsplaats, idMarktNaam, naamMarkt) "
                + "VALUES (" + exchangeNummer + ", " + marktNaamDB + ", '" + marktNaamExchange + "')";
        mysql.mysqlExecute(insertInto);
    }

    /**
     * Methoden die kijkt of de marktDBNaam in de marktnaamDb staat
     *
     * @param marktNaamDB marktNaamDB
     * @return true als de coin er in staat en anders false
     * @throws java.lang.Exception exceptie
     */
    public boolean marktDbNaamBoolean(String marktNaamDB) throws Exception {

        //de sql count string
        String sqlCountStament = "SELECT count(*) AS total FROM marktnaam WHERE marktnaamDb='" + marktNaamDB + "'";

        //krijg het nummer terug van de count
        int count = mysql.mysqlCount(sqlCountStament);

        //als count == 1 dan staat de marktNaamDb er in en moet het dus true zijn
        return count == 1;
    }

    /**
     * Methoden die kijkt of de marketNaam in de marktLijsten staat
     *
     * @param marktNaam marktNaamDB
     * @param exchangeNummer exchange nummer
     * @return true als de coin er in staat en anders false
     * @throws java.lang.Exception exceptie error
     */
    public boolean marktLijstenmBoolean(String marktNaam, int exchangeNummer) throws Exception {

        //de sql count string
        String sqlCountStament = "SELECT COUNT(*) AS total FROM marktlijsten WHERE naamMarkt='" + marktNaam + "' "
                + "AND idHandelsplaats=" + exchangeNummer + ";";

        ConsoleColor.out(sqlCountStament);

        //krijg het nummer terug van de count
        int count = mysql.mysqlCount(sqlCountStament);

        //als count == 1 dan staat de marktNaamDb er in en moet het dus true zijn
        return count == 1;
    }

    //abstracten methoden
    public abstract void marktUpdateLijsten() throws Exception;

    /**
     * Getter voor bittrex url
     *
     * @return bittrex url
     */
    public String getURL_BITTREX() {
        return URL_BITTREX;
    }

    /**
     * Getter voor bitstamp url
     *
     * @return bitstamp basis url
     */
    public String getURL_BITSTAMP() {
        return URL_BITSTAMP;
    }

    /**
     * Getter voor GDAX url;
     *
     * @return GDAX url
     */
    public String getUrl_GDAX() {
        return URL_GDAX;
    }

    /**
     * CexIo getter
     *
     * @return cexIo url
     */
    public String getURL_CEX_IO() {
        return URL_CEX_IO;
    }
}
