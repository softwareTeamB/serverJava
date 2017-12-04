package Web;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 *
 * @author michel
 */
public class Index {

    /**
     * get Routers
     * @throws IOException IO excepties
     */
    public void index() throws IOException {
        int port = 9091;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/marktData", new webMarktData());
        server.setExecutor(null);
        server.start();
    }
}
