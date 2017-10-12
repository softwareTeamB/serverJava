/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemiddelde;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import mysql.Mysql;

/**
 *
 * @author Jaros
 */
public class gemiddeldeMarktupdatehistory {

    Mysql mysql = new Mysql();

    public int control(int idMarkt, int idHandel, String colum, String begin, String eind) {
        int avg = 0;
       
        String ophalen = "Select avg(" + colum + ") from Marktupdatehistory where idMarktNaam= '" + idMarkt + "' and idHandelsplaats = '" + idHandel + "' and (idtimestamp between "
                + "(Select idtimestamp from timestamp where date = '" + begin + "') and (Select idtimestamp from timestamp where date = '" + eind + "'))";
        System.out.println(ophalen);
        
        
        //idMarktNaam
        return avg;
    }

}
