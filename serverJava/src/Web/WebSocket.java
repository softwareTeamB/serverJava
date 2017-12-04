package Web;

import JSON.JSONObject;
import global.ConsoleColor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import mysql.Mysql;

/**
 * Websocket voor de markt gegevens
 *
 * @author michel
 */
public class WebSocket {

    //private variable die via de ServerJava geupdate moet worden
    public JSONObject marktDataUpdate = new JSONObject();

    private ServerSocket serverSocket;

    //maak het mysql object aan
    Mysql mysql = new Mysql();
    
    /**
     * Constructor
     *
     * @throws java.io.IOException IO error
     * @throws java.sql.SQLException
     */
    public WebSocket() throws IOException, SQLException {

        //serverSocket poort
        serverSocket = new ServerSocket(9099);

        //begin dat de poort open is
        ConsoleColor.out("Waiting for client on port "
                + serverSocket.getLocalPort() + "...");
            
        //maak een object aan
        JSONObject object = new JSONObject();
        
        //maak alle exchange objecten aan. Gebruik de exchangeID
        String handelsplaatsSql = "SELECT idHandelsplaats FROM handelsplaats;";
        ResultSet rs = mysql.mysqlSelect(handelsplaatsSql);
        while (rs.next()) {
            
            //maak het 2de object aan
            JSONObject object2 = new JSONObject();
            
            //vraag de idHandelsplaats en zet het om in een String
            String idHandelsplaats = ""+rs.getInt("idHandelsplaats");
            
            
            //voeg het nieuwe object toe in het object
            object.put(idHandelsplaats, object2);
        }
        
        //update websocket object
        marktDataUpdate = object;
    }

    /**
     * Start main methoden voor de websocket
     *
     * @throws IOException IO error
     */
    public void main() throws IOException {

        //deze while loop moet bestaan zodat de zodat altijd open is
        while (true) {
            try {
                Socket server = serverSocket.accept();

                System.out.println("Just connected to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());

                //lees de data van de dataInputStream
                //String data = in.readUTF();

                //krijg het remoteAddress
                //String remoteAddress = "" + server.getRemoteSocketAddress();

                //stuur alles door naar de methoden
                //String dataReturn = switchMethoden(data, remoteAddress);
                //return
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                
                //JSONObject naar een string om zetten
                String marktUpdateString = marktDataUpdate.toString();
                
                //maak er een string vans
                out.writeUTF(marktUpdateString);
                
            } catch (SocketTimeoutException s) {
                ConsoleColor.err("Socket timed out! Dit is de error: " + s);
                break;
            } catch (IOException ex) {
                ConsoleColor.err(ex);
                break;
            }
        }
    }
}
