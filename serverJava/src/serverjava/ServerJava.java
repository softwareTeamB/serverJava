/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverjava;

import invullenMarktlijst.BittrexMarktUpdate;
import invullenMarktlijst.bittrexMarktdata;

import invullenMarktlijst.insertFuncties;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import invullenMarktlijst.BitstampMarktUpdate;

/**
 *
 * @author Jaros
 */
public class ServerJava {
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        insertFuncties InstersFuncies = new insertFuncties();
        
        String markt = "poloniex";//naam van de makrt in de database
        String url = "https://poloniex.com/public?command=returnTicker"; //website voor controle informatie
        String symbool = "_"; //symbool voor de naam van de markt
        /*
        try {
            bittrexMarktdata bit = new bittrexMarktdata();
            bit.bittrexMarktdataControler();
            
            //InstersFuncies.invullenCoinsBittrex();
            //InstersFuncies.invullenCoinsPolo(markt, url, symbool);
            // TODO code application logic here
        } catch (SQLException ex) {
            Logger.getLogger(ServerJava.class.getName()).log(Level.SEVERE, null, ex);
        }
        //BittrexMarktUpdate bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
        //bittrexMarktUpdate.marktUpdateLijsten();
        }*/
        //BittrexMarktUpdate bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
        //bittrexMarktUpdate.marktUpdateLijsten();
        BitstampMarktUpdate bMU = new BitstampMarktUpdate();
        bMU.marktUpdateLijsten();
        
        
    }
}
