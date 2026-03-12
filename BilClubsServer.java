import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
 
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.json.*;
 
public class BilClubsServer {

    static DBManager manager = new DBManager();
    static LoginVerifier verifier = new LoginVerifier();

    private final static HttpHandler api = new HttpHandler() {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            JSONObject response = APIHandler.handle(httpExchange, manager, verifier);

            System.out.println(response);

            final byte[] out = (response.toString()).getBytes("UTF-8");

            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            httpExchange.sendResponseHeaders((int) response.get("responseCode"), out.length);

            OutputStream os = httpExchange.getResponseBody();
            os.write(out);
            os.close();

        }
    };
    public static void main(String[] args) throws Exception {
        manager.initialize("db");

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(5000), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.createContext("/", api);
        httpServer.start();
        System.out.println("Server Started");
    }
}
