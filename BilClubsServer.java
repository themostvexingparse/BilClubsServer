import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
 
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.*;
 
public class BilClubsServer {

    static DBManager manager = new DBManager();

    private final static HttpHandler api = new HttpHandler() {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            JSONObject response = APIHandler.handle(httpExchange, manager);

            final byte[] out = (response.toString()).getBytes("UTF-8");

            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");

            int responseCode = response.optInt("responseCode", 400);

            httpExchange.sendResponseHeaders(responseCode, out.length);

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

        TimeUnit.SECONDS.sleep(2);

        // Sample code

        /*

        JSONObject signupRequest = new JSONObject();

        signupRequest.put("email", "...@ug.bilkent.edu.tr");
        signupRequest.put("password", "...");
        signupRequest.put("firstName", "...");
        signupRequest.put("lastName", "...");

        JSONObject response = RequestManager.sendPostRequest("api/signup", signupRequest);
        System.out.printf("Response: %s\n",  response);

        JSONObject loginRequest = new JSONObject();

        loginRequest.put("email", "...");
        loginRequest.put("password", "...");

        JSONObject response2 = RequestManager.sendPostRequest("api/login", loginRequest);
        System.out.printf("Response: %s\n",  response2);

        String token = response2.getJSONObject("data").getString("sessionToken");
        Integer id = response2.getJSONObject("data").getInt("userId");

        JSONObject accessRequest = new JSONObject();

        accessRequest.put("sessionToken", token);
        accessRequest.put("userId", id);

        JSONObject response3 = RequestManager.sendPostRequest("api/users", accessRequest);
        System.out.printf("Response: %s\n",  response3);

        */
    }
}
