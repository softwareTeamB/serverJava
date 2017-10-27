package mysql;

import global.ConsoleColor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * KLasse die na kijkt of mysql connenctie opgezet kan worden
 *
 * @author root
 */
public class MysqlConnectionTest {

    /**
     * Methoden die kijkt of er een mysql connenctie is
     *
     * @param usernaam gebruikers naam
     * @param wachtwoord wachtwoord
     * @param ipAddress ip address
     * @param poort poort nummer
     * @param dbNaam database naam
     * @return
     */
    public static boolean mysqlConnecntieTest(
            String usernaam,
            String wachtwoord,
            String ipAddress,
            String poort,
            String dbNaam
    ) {
        //connection op null zetten
        Connection connection = null;

        //kijken of je de connecntie kan op zetten
        try {

            ConsoleColor.out("-------- MySQL JDBC Connection Testing ------------");
            ConsoleColor.out("jdbc:mysql://" + ipAddress + ":" + poort + "/" + dbNaam +"/"+ usernaam +"/"+ wachtwoord);

            //connecntie op zetten
            connection = DriverManager
                    .getConnection("jdbc:mysql://" + ipAddress + ":" + poort + "/" + dbNaam, usernaam, wachtwoord);
        } catch (SQLException e) {
            ConsoleColor.err("Er is een probleem met de connectie op met mysql op te zetten. Error is: " + e);
            return false;
        }

        if (connection != null) {
            ConsoleColor.out("Er is een database connecntie!");
            return true;
        } else {
            ConsoleColor.err("Er is geen database connenctie. Los het probleem op!");
            return false;
        }

    }
}
