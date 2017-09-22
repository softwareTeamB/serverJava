package invullenMarktlijst;

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

    /**
     * Voeg marktnaam toe
     *
     * @param marktNaamDB db markt naam
     * @param baseCoin base coin
     * @param marktCoin markt coin
     * @return return het nummer dat de marktNaamDB heeft
     * @throws Exception error exception
     */
    public int marktNaam(String marktNaamDB, String baseCoin, String marktCoin) throws Exception {

        //kijk nog een keer of de marktNaamDB echt niet in het database staat
        String countSql = "SELECT COUNT(*) AS total FROM marktnaam"
                + " WHERE marktnaamDb='" + marktNaamDB + "';";
        int count = mysql.mysqlCount(countSql);

        if (count == 0) {
            System.out.println("marktNaamDB staat niet in de database");

            //voeg de data toe
            String insertSql = "INSERT INTO marktnaam(MarktnaamDb, baseCoin, MarktCurrency)"
                    + " values ('" + marktNaamDB + "', '" + baseCoin + "' ,'" + marktCoin + "')";
            mysql.mysqlExecute(insertSql);

            System.out.println("Marktnaam is in de database toegevoegd");

            //vraag het nummer op
            String sqlSelect = "SELECT idMarktNaam AS nummer FROM marktnaam"
                    + " WHERE marktnaamDb='" + marktNaamDB + "';";

            return mysql.mysqlExchangeNummer(sqlSelect);
        } else {
            System.out.println("marktNaamDB staat in de database");

            //vraag het nummer op
            String sqlSelect = "SELECT idMarktNaam AS nummer FROM marktnaam"
                    + " WHERE marktnaamDb='" + marktNaamDB + "';";

            return mysql.mysqlExchangeNummer(sqlSelect);
        }
    }

    /**
     * Add markt in de database bij dat bij houd
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     */
    public void insertMarktLijsten(int exchangeNummer, int marktNaamDB, String marktNaamExchange) throws SQLException {

        //insert in marktlijsten
        String insertInto = "INSERT INTO marktLijsten(idHandelsplaats, idMarktNaam, naamMarkt) "
                + "VALUES (" + exchangeNummer + ", " + marktNaamDB + ", '" + marktNaamExchange + "')";
        mysql.mysqlExecute(insertInto);
    }

    /**
     * Add markt in de database bij dat bij houd
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     * @param tradeMinSize
     */
    public void insertMarktLijsten(int exchangeNummer, int marktNaamDB, String marktNaamExchange, int tradeMinSize) {

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

}
