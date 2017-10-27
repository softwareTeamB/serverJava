package serverjava;

import global.ConsoleColor;
import global.LoadPropFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Scanner;
import mysql.MysqlConnectionTest;

/**
 * Main installer
 *
 * @author michel
 */
public class InstallerV2 {

    //folder naam locatie
    private final String[] FOLDER_NAME = {"config", "temp"};
    private final String[] BESTAND_CHECK = {"config", "mysql"};
    private final Scanner SC;

    /**
     * Constructor
     */
    public InstallerV2() {

        //maak de scanner aan
        SC = new Scanner(System.in);
    }

    public void main() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            ConsoleColor.out("MySQL JDBC Driver Beschikbaar!");
        } catch (ClassNotFoundException e) {

            //Melden van de error
            ConsoleColor.err("Error bij InstallV2. JDBC drivers neit beschikbaar. Dit is de error \n" + e);

            //sluit het systeem af
            ConsoleColor.err("JDBC drivers niet gevonden. De applicatie wordt nu afgesloten");
            System.exit(0);
        }

        //roep de FOLDER_NAME check
        makeFolder();
        bestandMake();
        

        try {

            //config
            configSetUp();
            
            //mysql set up
            mysqlSetUp();
        } catch (IOException ex) {
            ConsoleColor.err(ex);
            System.exit(0);
        }
    }

    private void configSetUp() throws IOException {

        //change
        boolean change = false;
        
        //keys array
        String[] keys = {"reloadMarktDataSave", "checkMarktLijst", "reloadTijd"};
        
        //file naam
        String fileNaam = "config";
        
        //laat de prop file
        Properties file = LoadPropFile.loadPropFile(fileNaam);

        //loop door alle keys heen die in de key array staan
        for (String keyNaam : keys) {
            //vraag de naam van de key op
            //null check
            if (file.getProperty(keyNaam) == null) {

                change = true;

                //geef in de terminal aan de de key leeg is
                ConsoleColor.out("Er bestaat een key niet in de properties file die wel zou moet bestaand.");

                //objcet scanner
                Object scannerInput;

                //switch
                switch (keyNaam) {

                    //google mail address
                    case "reloadMarktDataSave":

                        ConsoleColor.out("Sla alle markt data op true of false: ");

                        //input
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        file.setProperty(keyNaam, scannerInput.toString());
                        break;

                    //google wachtwoord
                    case "checkMarktLijst":

                        ConsoleColor.out("Kijk of de markt check gedaan moet worden: ");

                        //input
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        file.setProperty(keyNaam, scannerInput.toString());
                        break;

                    case "reloadTijd":
                        ConsoleColor.out("De reload tijd: ");

                        //input
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        file.setProperty(keyNaam, scannerInput.toString());
                        break;
                        
                    //deault
                    default:
                        ConsoleColor.err("Er is geen optie bekend bij mailServer voor het invullen van de onberekende key."
                                + " De key is: " + keyNaam);
                        break;
                }
            }
        }

        //kijk of het bestand verandert is
        if (change) {
            FileOutputStream output = new FileOutputStream("./config/" + fileNaam + ".properties");

            //sla het nieuwe bestand op
            file.store(output, null);
        }

    }

    /**
     * Mysql set up
     */
    private void mysqlSetUp() throws IOException {

        //boolean change
        boolean change = false;

        //file naam
        String fileNaam = "mysql";

        //laat de prop file
        Properties file = LoadPropFile.loadPropFile(fileNaam);
        ConsoleColor.out(file);

        //connectie variable
        String username = file.getProperty("username");
        String password = file.getProperty("password");
        String ipAddress = file.getProperty("ipAddress");
        String poort = file.getProperty("poort");
        String dbNaam = file.getProperty("DBnaam");
        String autoReconnect = file.getProperty("autoReconnect");
        String ssl = file.getProperty("ssl");

        //mysql array
        String[] mysqlArray = {
            "username", "password", "DBnaam"
        };

        //if stament
        if (ipAddress == null || poort == null
                || autoReconnect == null || ssl == null) {

            ConsoleColor.out("Het ip address, poort nummer, auto reconnect, ssl is mogelijk niet ingevuld."
                    + " Wilt u de standaart zetting gebruiker? Ja of Nee");

            //input of de gebruiker de standaart setting wilt gebruiker
            Object inputDefault = SC.next();
            if ("ja".equals(inputDefault.toString())) {

                //default settings
                ipAddress = "localhost";
                poort = "3306";
                autoReconnect = "true";
                ssl = "false";
            } else {
                ConsoleColor.err("Het systeem kan nu nog niet ipaddress pport ssl auto reconnect aanpassen."
                        + " Standaart setting wordt gebruikt.");

                //default settings
                ipAddress = "localhost";
                poort = "3306";
                autoReconnect = "true";
                ssl = "false";
            }
        }

        //loop door alle keys heen die in de key array staan
        for (int i = 0; i < mysqlArray.length; i++) {

            //vraag de naam van de key op
            String keyNaam = mysqlArray[i];

            //null check
            if (file.getProperty(keyNaam) == null) {

                //zet deze op true zodat straks het nieuwe properties bestand wordt opgeslagen
                change = true;

                //input object van de scanner
                Object scannerInput = null;

                //geef in de terminal aan de de key leeg is
                ConsoleColor.out("Er bestaat een key niet in de properties file die wel zou moet bestaand.");

                //switch
                switch (keyNaam) {

                    case "username":

                        ConsoleColor.out("Vul de usernaam van mysql in: ");
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        username = scannerInput.toString();
                        break;

                    case "password":
                        ConsoleColor.out("Vul het wachtwoord in van mysql naam in: ");
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        password = scannerInput.toString();
                        break;

                    case "DBnaam":

                        ConsoleColor.out("Vul de database naam in: ");
                        scannerInput = SC.next();

                        //voeg de input toe aan het bestand
                        dbNaam = scannerInput.toString();

                        break;

                    //deault
                    default:
                        ConsoleColor.err("Er is geen optie bekend bij mailServer voor het invullen van de onberekende key."
                                + " De key is: " + keyNaam);
                        break;
                }
            }
        }

        //kijk of de connenctie met mysql opgezet kan worden
        boolean connectionDB = MysqlConnectionTest.mysqlConnecntieTest(username, password, ipAddress, poort, dbNaam);
        if (!connectionDB) {
            ConsoleColor.err("Er kan geen connectie opgezet worden met mysql. Is mysql running?"
                    + " U wordt verzocht om alles opnieuw in te vullen.");

            //roep mysql opnieuw op
            mysqlSetUp();
        }

        //kijk of het bestand verandert is
        if (change) {

            file.setProperty("username", username);
            file.setProperty("password", password);
            file.setProperty("ipAddress", ipAddress);
            file.setProperty("poort", poort);
            file.setProperty("DBnaam", dbNaam);
            file.setProperty("autoReconnect", autoReconnect);
            file.setProperty("ssl", ssl);

            //fileOUtputStream
            FileOutputStream output = new FileOutputStream("./config/" + fileNaam + ".properties");

            //sla het nieuwe bestand op
            file.store(output, null);
        }

    }

    /**
     * methoden om een folfder aan te maken
     */
    private void makeFolder() {

        //loop door de array heen
        for (int i = 0; i < FOLDER_NAME.length; i++) {

            //maak het object folder aan
            File folder = new File(FOLDER_NAME[i]);

            //kijk of de folder bestaat
            if (!folder.exists()) {

                try {
                    //maak de folder aan
                    folder.mkdirs();

                    //geef aan de de folder is aangemaakt
                    ConsoleColor.out("Folder aangemaakt: " + folder.getName());

                } catch (SecurityException se) {
                    ConsoleColor.err("Er is een error bij de installer om folder aan te maken. \n Error is " + se);
                }
            } else {
                ConsoleColor.out("Folder " + FOLDER_NAME[i] + " bestaat al.");
            }
        }
    }

    /**
     * Maak de bestanden aan
     */
    private void bestandMake() {

        //Properties bestand
        Properties prop = new Properties();
        OutputStream output = null;
        for (int i = 0; i < BESTAND_CHECK.length; i++) {

            //bestnad locatie
            File f = new File("./config/" + BESTAND_CHECK[i] + ".properties");
            if (!f.exists()) {

                try {
                    //maak een fileOutPutStream
                    output = new FileOutputStream("./config/" + BESTAND_CHECK[i] + ".properties");
                    prop.store(output, null);
                } catch (FileNotFoundException ex) {
                    ConsoleColor.err(ex);
                    System.exit(i);
                } catch (IOException ex) {
                    ConsoleColor.err(ex);
                    System.exit(i);
                }
            }
        }
    }

}
