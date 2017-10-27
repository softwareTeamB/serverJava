/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public String getHTTP(String uri) {
        try {
            URL url = new URL(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                return strTemp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "false";
        }
        return "false";
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

    public String getHttpBrowser(String url2) throws MalformedURLException, IOException {

        //maak er een url van
        URL url = new URL(url2);

        HttpURLConnection connection = ((HttpURLConnection) url.openConnection());
        connection.addRequestProperty("User-Agent", "Mozilla/4.0");
        InputStream input;
        if (connection.getResponseCode() == 200)  {
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
