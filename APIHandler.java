import java.io.IOException;
import java.net.ProtocolException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

public class APIHandler {
    public static JSONObject handle(HttpExchange httpExchange, DBManager manager, LoginVerifier verifier) {
        Map<String, String> parameterMap = new HashMap<String,String>();
        try {
            parameterMap = (new Parameters(httpExchange.getRequestURI().getRawQuery())).getMap();
        } catch (Exception e) {}
        String remoteAddress = httpExchange.getRemoteAddress().toString() + "/";
        // concatenate an extra slash so we can check the base path more easily
        String path = httpExchange.getRequestURI().getRawPath();
        String fragment = httpExchange.getRequestURI().getRawFragment();

        JSONObject response = new JSONObject();
        JSONObject unauthorizedResponse = new JSONObject();
        JSONObject badRequestResponse = new JSONObject();

        unauthorizedResponse.put("responseCode", 403);
        badRequestResponse.put("responseCode", 400);

        System.out.printf("%s : %s\n", remoteAddress, path);

        if (path.startsWith("/api/")) {
            response.put("responseCode", 200);
        } else {
            response.put("responseCode", 501);
        }

        String action = path.substring(1).split("/")[1];

        if (action.equals("signup")) {
            String email = parameterMap.get("email");
            String password = parameterMap.get("password");
            String firstName = parameterMap.get("firstName");
            String lastName = parameterMap.get("lastName");
            if (email == null || password == null || firstName == null || lastName == null) {
                badRequestResponse.put("error", "empty field");
                return badRequestResponse;
            }
            try {
                if (verifier.verify(email, password)) {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    manager.addUser(newUser);
                    response.put("responseCode", 200);
                    response.put("email", email);
                    response.put("fullName", firstName + " " + lastName);
                }
            } catch (Exception e) {
                return badRequestResponse;
            }
        }

        if (action.equals("users")) {
            List<User> users = manager.queryUsers(new Filter());
            for (User user : users) {
                response.put(user.getId().toString(), user.getEmail());
            }

            if(users.size() == 0) {
                response.put("error", "no users");
            }
        }

        return response;
    }
}
