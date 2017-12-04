package invullenMarktlijst;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
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
                
                //marktnaam check
                String countSql = "SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb='"+marktNaamDB+"'";
                int count2 = mysql.mysqlCount(countSql);
                
                int marktDBNummer;
                
                if(count2 == 0){
                    
                    //voeg marktDBNummer toe en geef het nummer terug
                    marktDBNummer = super.marktNaam(marktNaamDB, objectCount.getString("BaseCurrency"), objectCount.getString("MarketCurrency"));
                } else {
                    
                    //vraag het nummer op
                    String getIdMarktNaamDB = "SELECT idMarktNaam as nummer FROM serverproject.marktnaam WHERE marktnaamDb='"+marktNaamDB+"'";
                    
                    marktDBNummer = mysql.mysqlNummer(getIdMarktNaamDB);
                }

                //voeg de markt toe
                super.insertMarktLijsten(BITTREX_NUMMER, marktDBNummer, marktNaamDB);
                
            } else {
                ConsoleColor.out("MarktNaamDB: " + marktNaamDB + "staat al in de marktLijsten database.");
            }
        }
    }
}
