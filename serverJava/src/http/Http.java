package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 *
 * @author michel
 */
public class Http {

    /**
     * Methoden voor een http get
     * @param uri
     * @return
     * @throws MalformedURLException
     * @throws IOException 
     */
    public String getHTTP(String uri) throws MalformedURLException, IOException, Exception {

        //kijk of er internet verbinding is
        global.InternetConnection.internetConnectionTest(uri);
        
        //maak de url aan
        URL url = new URL(uri);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        String strTemp = "";
        while (null != (strTemp = br.readLine())) {
            return strTemp;
        }
        
        throw new Exception("Er is een probleem om alle opgevraagde data te verwerken van de url: "+ uri);
    }

    /**
     *
     * @param uri url
     * @return http reponse
     * @throws MalformedURLException error exception
     * @throws IOException error exception
     */
    public String getHttpObject(String uri) throws MalformedURLException, IOException {
        String requestURL = uri;
        URL wikiRequest = new URL(requestURL);
        URLConnection connection = wikiRequest.openConnection();
        connection.setDoOutput(true);

        Scanner scanner = new Scanner(wikiRequest.openStream());
        String response = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return response;

    }

    /**
     * Methoden voor een http request als een browser
     * @param url2 url
     * @return return stament zit alle data in
     * @throws MalformedURLException error exceptie
     * @throws IOException io error
     */
    public String getHttpBrowser(String url2) throws MalformedURLException, IOException {

        //maak er een url van
        URL url = new URL(url2);

        HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
        connection.addRequestProperty("User-Agent", "Mozilla/4.0");
        InputStream input;
        if (connection.getResponseCode() == 200) {
            input = connection.getInputStream();
        } else {
            input = connection.getErrorStream();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String msg;
        while ((msg = reader.readLine()) != null) {
            return msg;
        }
        return "false";
    }
}
