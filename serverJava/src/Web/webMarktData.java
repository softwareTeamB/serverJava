package Web;

import JSON.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 * webMarktDta rooter
 *
 * @author michel
 */
public class webMarktData implements HttpHandler {

    /**
     * Methoden die de MarktData terug geeft
     * @param he http get
     * @throws IOException  error melding
     */
    @Override
    public void handle(HttpExchange he) throws IOException {
        
        //vraag de markt data op uit het object en maarkt er een string van
        JSONObject object = serverjava.ServerJava.webSocket.marktDataUpdate;
        String response = object.toString();
        
        //return
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
