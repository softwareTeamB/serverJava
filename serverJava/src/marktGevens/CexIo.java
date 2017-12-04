package marktGevens;

import JSON.JSONArray;
import JSON.JSONObject;
import global.ConsoleColor;
import global.FileSystem;
import http.Http;
import invullenMarktlijst.CexIoMarktUpdate;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Methoden om data op te slaan van cexIo
 *
 * @author michel
 */
public class CexIo extends MainMarktGevens {

    FileSystem fileSystem = new FileSystem();
    Http http = new Http();

    private String cexIoParameters;
    private String CEX_IO = "https://cex.io/api";
    private int exchangeNummer;

    /**
     * Constructor
     *
     * @throws IOException als het fileSysteem fallt
     * @throws Exception exeptie
     */
    public CexIo() throws IOException, Exception {

        //als het bestand bestaat
        boolean fileExits = fileSystem.fileExits("./temp/cexParameters.txt");

        //als het bestand niet bestaat
        if (!fileExits) {

            //roep de methoden op die de cexParameters maakt
            CexIoMarktUpdate cIMU = new CexIoMarktUpdate();
            cIMU.marktUpdateLijsten();
        }

        //vul de sstring met de cexIo parameters
        this.cexIoParameters = fileSystem.readFile("./temp/cexParameters.txt");

        //vraag het exchange nummer op
        this.exchangeNummer = mysql.mysqlNummer("select getExchangeNummer('cexIo') AS nummer");
    }

    @Override
    public void getMarktData(boolean saveData) {

        //zet de reponse in een string
        String httpReponse;
        try {
            httpReponse = http.getHttpBrowser("https://cex.io/api/tickers" + cexIoParameters);
        } catch (IOException ex) {
            ConsoleColor.err(ex);
            return;
        }

        //zet de reponse in een JSONObject
        JSONObject object = new JSONObject(httpReponse);

        //haal de JSONArray uit het object
        JSONArray array = object.getJSONArray("data");

        //for loop
        for (int i = 0; i < array.length(); i++) {

            //pak het eerst volgende object
            JSONObject countObject = array.getJSONObject(i);

            String marktNaam = countObject.getString("pair");
            double bid = countObject.getDouble("bid");
            double ask = countObject.getDouble("ask");
            double last = countObject.getDouble("last");
            double low = countObject.getDouble("low");
            double high = countObject.getDouble("high");
            double volume = countObject.getDouble("volume");
            double volumeBTC = volume * last;

            //vraag het nummer op uit idMarktnaam
            int idMarktNaam;
            try {
                idMarktNaam = mysql.mysqlNummer("SELECT idMarktNaam AS nummer FROM marktlijsten "
                        + "WHERE naamMarkt='" + marktNaam + "' AND idHandelsplaats='" + exchangeNummer + "'");

                //roep de saveController op
                super.marktDataUpdate(high, low, volume, volumeBTC, bid, ask, last, exchangeNummer, idMarktNaam, saveData);
            } catch (Exception ex) {
                Logger.getLogger(CexIo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
