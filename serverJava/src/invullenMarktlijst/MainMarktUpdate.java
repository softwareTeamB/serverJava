package invullenMarktlijst;

import java.util.logging.Level;
import java.util.logging.Logger;
import mysql.Mysql;

/**
 * Main update methoden om de markt de update met abstracte methoden
 *
 * @author michel
 */
public abstract class MainMarktUpdate {

    //objecten maken
    Mysql mysql = new Mysql();

    //bittrex url
    private final String URL_BITTREX = "https://bittrex.com/api/v1.1";

    /**
     * Voeg marktnaam toe
     *
     * @param marktNaamDB db markt naam
     * @param baseCoin base coin
     * @param marktCoin markt coin
     * @throws Exception error exception
     */
    public void marktNaam(String marktNaamDB, String baseCoin, String marktCoin) throws Exception {

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
        } else {
            System.out.println("marktNaamDB staat in de database");
        }
    }

    /**
     * Add markt in de database bij dat bij houd
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     */
    public void marktLijsten(int exchangeNummer, String marktNaamDB, String marktNaamExchange) {

    }

    /**
     * Add markt in de database bij dat bij houd
     *
     * @param exchangeNummer
     * @param marktNaamDB
     * @param marktNaamExchange
     * @param tradeMinSize
     */
    public void marktLijsten(int exchangeNummer, String marktNaamDB, String marktNaamExchange, int tradeMinSize) {

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

}
