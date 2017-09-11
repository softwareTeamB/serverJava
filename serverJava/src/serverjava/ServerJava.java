/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverjava;

import invullenMarktlijst.insertFuncties;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jaros
 */
public class ServerJava {
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        insertFuncties InstersFuncies = new insertFuncties();
        
        
        try {
            InstersFuncies.invullenCoinsBittrex();
            // TODO code application logic here
        } catch (SQLException ex) {
            Logger.getLogger(ServerJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
