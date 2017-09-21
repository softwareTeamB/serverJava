package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import http.Http;
import java.sql.SQLException;

/**
 *
 * @author michel
 */
public class BittrexMarktUpdate extends MainMarktUpdate {

    Http http = new Http();

    //bittrex nummer
    private final int BITTREX_NUMMER;

    /**
     * Constructor
     *
     * @param exchangeNaam naam van de exchange
     * @throws SQLException sql error
     * @throws Exception andere mogelijke problemen
     */
    public BittrexMarktUpdate(String exchangeNaam) throws SQLException, Exception {

        String sqlFunctions = "SELECT getExchangeNummer('" + exchangeNaam + "') AS nummer;";

        //vul de bittrex nummer
        this.BITTREX_NUMMER
                = mysql.mysqlExchangeNummer(sqlFunctions);
    }

    /**
     * Update de lijsten voor bittrex
     *
     * @throws Exception Als er ergens in het proces een bug optreed
     */
    @Override
    public void marktUpdateLijsten() throws Exception {

        //bittrex url
        String urlString = super.getURL_BITTREX() + "/public/getmarkets";

        //http get
        String httpReponse = http.getHTTP(urlString);

        //maak er een jsonobject van
        JSONObject object = new JSONObject(httpReponse);

        //stament om te kijken of er een error is
        if (!object.getBoolean("success")) {
            
            //als er een error is dan word de void methoden gestopt
            throw new Exception("Bij bittrex geeft geen goede return. Dit is de error " + httpReponse);
        }

        //result
        JSONArray array = object.getJSONArray("result");

        for (int i = 0; i < array.length(); i++) {

            //vul er een object
            JSONObject objectCount = array.getJSONObject(i);

            String marktNaamDB = objectCount.getString("MarketName");

            //count string
            String countSqlString = "SELECT COUNT(*) AS total FROM marktlijstvolv1"
                    + " WHERE naamMarkt='" + marktNaamDB + "'"
                    + " AND handelsplaatsNaam='bittrex'";
            int count = mysql.mysqlCount(countSqlString);

            //count of de markt bestaat
            if (count == 0) {

                //kijk om de markt in marktNaam staat zoniet word het toegevoegd
                super.marktNaam(marktNaamDB, objectCount.getString("BaseCurrency"), objectCount.getString("MarketCurrency"));

                //voeg de markt toe in de marktLijsten
                //code moet verbeterd worden
                //super.marktLijsten(BITTREX_NUMMER, marktNaamDB, marktNaamDB, objectCount.getInt("MinTradeSize"));

            } else {
                System.out.println("MarktNaamDB: " + marktNaamDB + "staat al in de marktLijsten database.");

                //voeg de markt toe in de marktLijsten
                //code moet verbeterd worden
                //super.marktLijsten(BITTREX_NUMMER, marktNaamDB, marktNaamDB, objectCount.getInt("MinTradeSize"));
            }
        }
    }
}
