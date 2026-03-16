import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StreamReader {
    public static String readStream(InputStream stream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
            return builder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }    
}
