package invullenMarktlijst;

import java.sql.SQLException;

/**
 * Bitstamp markt updater
 *
 * @author michel
 */
public class BitstampMarktUpdate extends MainMarktUpdate {

    private final int BISTAMP_NUMMER;
    private final String EXCHANGE_NAAM = "bitstamp";

    //array
    String[] marktLijst = {"btcusd", "xrpusd", "xrpbtc", "ltcusd", "ltcbtc",
        "ethusd", "ethbtc"};
    String[] baseCoinLijst = {"USDT", "USDT", "BTC", "USDT", "BTC", "USDT", "BTC"};
    String[] marktCurrentieLijst = {"BTC", "XRP", "XRP", "LTC", "LTC", "ETH", "ETH"};

    /**
     * Constructor
     *
     * @throws SQLException sql error
     * @throws Exception als er een andere error is
     */
    public BitstampMarktUpdate() throws SQLException, Exception {

        String sqlFunctions = "SELECT getExchangeNummer('" + EXCHANGE_NAAM + "') AS nummer;";

        //vul de bittrex nummer
        this.BISTAMP_NUMMER
                = mysql.mysqlExchangeNummer(sqlFunctions);
    }

    /**
     * Update lijst voor bitstamps
     *
     * @throws Exception Error exceptie
     */
    @Override
    public void marktUpdateLijsten() throws Exception {

        //for loop door de array heen
        for (int i = 0; i < marktLijst.length; i++) {

            //sql Select
            String sqlCount = "SELECT COUNT(*) AS total FROM marktlijstvolv1"
                    + " WHERE handelsplaatsNaam='" + EXCHANGE_NAAM + "' AND naamMarkt='" + marktLijst[i] + "';";
            
            //kijk of die in de lijst staat
            if(mysql.mysqlCount(sqlCount) == 0){
                //declare marktDBNummer
                int marktDBNummer;
            
                //marktNaamDB
                String marktNaamDB = baseCoinLijst[i] + "-" + marktCurrentieLijst[i];

                //String marktLijsten
                String sqlcount1 = "SELECT COUNT(*) AS total FROM marktnaam WHERE marktnaamDb='"+marktNaamDB+"';";
                
                //count nummer
                int count1 = mysql.mysqlCount(sqlcount1);
                if (count1 == 0) {

                    //kijk of de markt in de lijst staat van de DB lijst
                    //en krijg het marktNaamDB nummer op
                    marktDBNummer = super.marktNaam(marktNaamDB, baseCoinLijst[i], marktCurrentieLijst[i]);
                } else {

                    //vraag marktDBNummer
                    String getMarktDBNummer = "SELECT idMarktNaam AS nummer FROM marktnaam "+
                            "WHERE marktnaamDb='"+marktNaamDB+"' ";
                    marktDBNummer = mysql.mysqlExchangeNummer(getMarktDBNummer);
                }
                
                //vraag het exchange nummer op
                String sqlExchange = "select getExchangeNummer('"+EXCHANGE_NAAM+"')AS nummer;";
                System.out.println(sqlExchange);
                int exchangeNummer = mysql.mysqlExchangeNummer(sqlExchange);
                
                //insert in marktlijsten
                super.insertMarktLijsten(exchangeNummer, marktDBNummer, marktLijst[i]);
                
            }
        
            System.out.println("BitstampMarktUpdate is geladen");
        }
    }

}
