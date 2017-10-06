/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mysql;

/*
 * Class om alle mysql staments te runnen
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jaros
 */
public class Mysql {

    //variable
    private final String USERNAME = "root";
    private final String PASSWORD = "Pulsar11";
    private final String IPADDRESS = "localhost";
    private final String POORT = "3306";
    private final String DATABASENAAM = "serverproject";
    private final boolean AUTORECONNECT = true;
    private final boolean SSL = false;
    private final String CONN_STRING = "jdbc:mysql://" + IPADDRESS + ":" + POORT + "/" + DATABASENAAM + "?autoReconnect=" + AUTORECONNECT + "&useSSL=" + SSL;
    private Statement stmt;
    
    public Mysql() {
        try {
            Connection conn;
            conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
            this.stmt = (Statement) conn.createStatement();
        } catch (SQLException ex) {
            System.err.println("Er is een error opgetreden met het aanmaken van connenctie in mysql."
                    +" De applicatie wordt veilig afgesloten.");
            System.exit(0);
        }
    }

    
    
    /**
     * Select stament
     *
     * @param sqlString mysql string
     * @return return de velden
     * @throws SQLException als er iets fout gaat
     */
    public ResultSet mysqlSelect(String sqlString) throws SQLException {
        
        //return
        return stmt.executeQuery(sqlString);
    }

    /**
     * Run sql staments zonder return
     *
     * @param sqlString sql stament
     * @throws SQLException als er iets fout gaat
     */
    public void mysqlExecute(String sqlString) throws SQLException {

        
        //run stament
        stmt.execute(sqlString);
        
        return;
    }

    public int mysqlCount(String sqlString) throws SQLException, Exception {

        
        //return
        ResultSet rs = stmt.executeQuery(sqlString);
        while (rs.next()) {
            return rs.getInt("total");
        }

        //als er een error optreed
        throw new Exception("Mysql count error. Mogelijk geen connectie of er is geen geldig colum ingevuld.");
    }

    /**
     * Return nummer wat er is
     *
     * @param sqlString nummer
     * @return return 1 nummer
     * @throws SQLException sql error
     * @throws Exception andere exceptions
     */
    public int mysqlExchangeNummer(String sqlString) throws SQLException, Exception {

      
        //return
        ResultSet rs = stmt.executeQuery(sqlString);
        while (rs.next()) {
            return rs.getInt("nummer");
        }

        //als er een error optreed
        throw new Exception("Mysql kan geen nummer return geven");
    }

    /**
     * Return nummer wat er is
     *
     * @param sqlString nummer
     * @return return 1 nummer
     * @throws SQLException sql error
     * @throws Exception andere exceptions
     */
    public int mysqlNummer(String sqlString) throws SQLException, Exception {

        //return
        ResultSet rs = stmt.executeQuery(sqlString);
        while (rs.next()) {
            return rs.getInt("nummer");
        }

        //als er een error optreed
        throw new Exception("Mysql kan geen nummer return geven");
    }

    /**
     * Return het nummer van de marktID
     *
     * @param sqlString sql string het count object met nummer heten
     * @return het nummer
     * @throws SQLException als de database response leeg is
     */
    public int mysqlIdMarktNaam(String sqlString) throws SQLException {

        //return
        ResultSet rs = stmt.executeQuery(sqlString);
        while (rs.next()) {
            return rs.getInt("nummer");
        }
        throw new SQLDataException("De database reponse is leeg");
    }

}
