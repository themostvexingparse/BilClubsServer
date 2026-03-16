import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class RequestManager {
    private static String defaultAddress = "http://127.0.0.1:5000/";

    public static void setDefaultAddress(String address) {
        address = address.replaceAll("/+$", "");
        defaultAddress = address + "/";
    }

    public static Response sendPostRequest(String ENDPOINT, JSONObject json) {
        try {
            URL url = new URL(defaultAddress + ENDPOINT);

            HttpURLConnection conn = (HttpURLConnection)(url.openConnection());
            
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();

            InputStream stream = responseCode < 400 ? conn.getInputStream() : conn.getErrorStream();
            String responseString = StreamReader.readStream(stream);

            conn.disconnect();
            return new Response(new JSONObject(responseString.trim()));    
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Response();
        }
    }
}
