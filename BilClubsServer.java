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

    private final static HttpHandler api = new HttpHandler() {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            JSONObject response = APIHandler.handle(httpExchange);

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

        APIHandler.initializeDB();

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(5000), 0);
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.createContext("/", api);
        httpServer.start();
        System.out.println("Server Started");

        // Sample code


        
        // TimeUnit.SECONDS.sleep(2);

        // JSONObject signupRequest = new JSONObject();

        // signupRequest.put("email", "...");
        // signupRequest.put("password", "...");
        // signupRequest.put("firstName", "...");
        // signupRequest.put("lastName", "...");

        // Response response = RequestManager.sendPostRequest("api/signup", signupRequest);
        // System.out.printf("Response: %s\n",  response);


        // JSONObject loginRequest = new JSONObject();

        // loginRequest.put("email", "...");
        // loginRequest.put("password", "...");

        // Response response2 = RequestManager.sendPostRequest("api/login", loginRequest);
        // System.out.printf("Response: %s\n",  response2);

        // String token = response2.getPayload().getString("sessionToken");
        // Integer id = response2.getPayload().getInt("userId");

        // JSONObject accessRequest = new JSONObject();

        // accessRequest.put("sessionToken", token);
        // accessRequest.put("userId", id);

        // Response response3 = RequestManager.sendPostRequest("api/users", accessRequest);
        // System.out.printf("Response: %s\n",  response3);

    }
}
