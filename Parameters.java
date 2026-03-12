import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private Map<String, String> map = new HashMap<String, String>();

    public Parameters(String data) {
        String[] parameters = data.split("&");
        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair.length != 2) {
                System.out.println("Error processing param: " + parameter);
                continue;
            }
            try { keyValuePair[0] = URLDecoder.decode(keyValuePair[0], "UTF-8" ); }
            catch (Exception e) {}
            try {  keyValuePair[1] = URLDecoder.decode(keyValuePair[1], "UTF-8" ); }
            catch (Exception e) {}
            map.put(keyValuePair[0], keyValuePair[1]);
        }
    }

    public String get(String key) {
        return map.get(key);
    }

    public Map<String, String> getMap() {
        return map;
    }
}
