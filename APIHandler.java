import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

public class APIHandler {

    private static JSONObject buildResponse(int code, JSONObject data, String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("responseCode", code);
        response.put("success", code >= 200 && code < 300);
        
        if (data != null) {
            response.put("data", data);
        }
        
        if (errorMessage != null) {
            JSONObject errorObj = new JSONObject();
            errorObj.put("message", errorMessage);
            response.put("error", errorObj);
        }
        
        return response;
    }
    
    public static JSONObject handle(HttpExchange httpExchange, DBManager manager) {
        String remoteAddress = httpExchange.getRemoteAddress().toString() + "/";
        String path = httpExchange.getRequestURI().getRawPath();

        if (!httpExchange.getRequestMethod().equals("POST")) {
            return buildResponse(400, null, "Method not allowed. Only POST is supported.");
        }

        JSONObject requestBody;
        try {
            requestBody = new JSONObject(StreamReader.readStream(httpExchange.getRequestBody()));
        } catch (Exception e) {
            return buildResponse(400, null, "Malformed JSON request body.");
        }

        if (ServerConfig.PRINT_DEBUG) System.out.printf("%s : %s\n", remoteAddress, path);

        if (!path.startsWith("/api/")) {
            return buildResponse(501, null, "Not implemented.");
        }

        String[] pathParts = path.substring(1).split("/");
        if (pathParts.length < 2) {
            return buildResponse(400, null, "Invalid API endpoint");
        }

        String action = pathParts[1];

        try {
            if (action.equals("signup")) {
                String email = requestBody.optString("email", null);
                String password = requestBody.optString("password", null);
                String firstName = requestBody.optString("firstName", null);
                String lastName = requestBody.optString("lastName", null);
                
                if (email == null || password == null || firstName == null || lastName == null) {
                    return buildResponse(400, null, "Missing required fields: email, password, firstName, or lastName");
                }
                
                Filter emailFilter = new Filter();
                emailFilter.addFilter("email", email);
                
                if (manager.queryUser(emailFilter) != null) {
                    return buildResponse(400, null, "Account already exists with the same email.");
                }
                
                if (LoginVerifier.verify(email, password)) {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    
                    if (manager.addUser(newUser)) {
                        JSONObject data = new JSONObject();
                        data.put("email", email);
                        data.put("fullName", firstName + " " + lastName);
                        return buildResponse(200, data, null);
                    } else {
                        return buildResponse(500, null, "Unknown database error.");
                    }
                } else {
                    return buildResponse(400, null, "Incorrect email or password. Verification failed.");
                }
            }

            if (action.equals("login")) {
                String email = requestBody.optString("email", null);
                String password = requestBody.optString("password", null);
                
                if (email == null || password == null) {
                    return buildResponse(400, null, "Missing required fields: email or password");
                }
                
                Filter emailFilter = new Filter();
                emailFilter.addFilter("email", email);
                User userByEmail = manager.queryUser(emailFilter);
                
                if (userByEmail == null) {
                    return buildResponse(404, null, "Account does not exist.");
                }
                
                if (LoginVerifier.verify(email, password)) {
                    userByEmail.generateToken();
                    manager.updateUser(userByEmail);
                    
                    JSONObject data = new JSONObject();
                    data.put("sessionToken", userByEmail.getToken());
                    data.put("userId", userByEmail.getId());
                    return buildResponse(200, data, null);
                } else {
                    return buildResponse(401, null, "Incorrect email or password. Verification failed.");
                }
            }

            if (action.equals("users")) {
                if (!requestBody.has("userId") || !requestBody.has("sessionToken")) {
                    return buildResponse(401, null, "Missing credentials: userId or sessionToken not provided.");
                }
                
                Integer userId = requestBody.optInt("userId", 0);
                String sessionToken = requestBody.optString("sessionToken", null);
                
                Filter areCredentialsValid = new Filter();
                areCredentialsValid.addFilter("id", userId);
                areCredentialsValid.addFilter("token", sessionToken);

                if (!manager.doesUniqueUserExist(areCredentialsValid)) {
                    return buildResponse(403, null, "Invalid credentials.");
                }

                List<User> users = manager.queryUsers(new Filter());
                JSONArray usersArray = new JSONArray();
                
                for (User user : users) {
                    JSONObject userObj = new JSONObject();
                    userObj.put("id", user.getId());
                    userObj.put("email", user.getEmail());
                    userObj.put("name", user.getFullName());
                    usersArray.put(userObj);
                }

                JSONObject data = new JSONObject();
                data.put("users", usersArray);
                
                return buildResponse(200, data, null); 
            }

            return buildResponse(404, null, "Endpoint action not found");

        } catch (Exception e) {
            if (ServerConfig.PRINT_STACK_TRACES) e.printStackTrace();
            return buildResponse(500, null, "Internal server error: " + e.getMessage());
        }
    }
}