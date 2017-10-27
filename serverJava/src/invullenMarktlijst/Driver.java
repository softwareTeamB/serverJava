package invullenMarktlijst;

import global.ConsoleColor;

/**
 * Methoden die alle methoden in invullenMarktlijst kan laten
 * @author michel
 */
public class Driver {
    
    CexIoMarktUpdate cexMarktUpdate;
    GDAXMarktUpdate gdaxMarktUpdate;
    BittrexMarktUpdate bittrexMarktUpdate;

    /**
     * Constructor
     */
    public Driver() {
        try {
            
            //object
            bittrexMarktUpdate = new BittrexMarktUpdate("bittrex");
            gdaxMarktUpdate = new GDAXMarktUpdate("GDAX");
            cexMarktUpdate = new CexIoMarktUpdate();
            
            
        } catch (Exception ex) {
            ConsoleColor.err(ex);
            System.exit(0);
        }
    }
    
    
    
    public void driver(){
        
        //roep de marktUpdateLijsten aan
        try {
            
            bittrexMarktUpdate.marktUpdateLijsten();
            
            //voor cexIo
            cexMarktUpdate.marktUpdateLijsten();
            
            //gdax
            gdaxMarktUpdate.marktUpdateLijsten();
        } catch (Exception ex) {
            
            //error bericht
            ConsoleColor.err(ex);
        }
    }
    
}
