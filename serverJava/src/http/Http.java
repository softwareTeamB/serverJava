/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author michel
 */
public class Http {

    public String GetHttp(String uri) {
        try {
            URL url = new URL(uri);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String strTemp = "";
            while (null != (strTemp = br.readLine())) {
                System.out.println(strTemp);
                return strTemp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "false";
        }
        return "false";
    }
}