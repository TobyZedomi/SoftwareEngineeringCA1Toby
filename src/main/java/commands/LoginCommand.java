package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.User;
import network.TCPNetworkLayer;
import persistence.IUserDao;
import service.UserUtilities;

import java.net.Socket;


@Slf4j
public class LoginCommand implements Command{

    private JsonObject request;
    private JsonObject response;

    private IUserDao userDao;

    private String username;

    private String email;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();



    private boolean loginStatus;
    public LoginCommand(TCPNetworkLayer networkLayer, JsonObject request,
                           JsonObject response, IUserDao userDao, String username, String email, boolean loginStatus){


        this.networkLayer = networkLayer;
        this.request = request;
        this.response = response;
        this.userDao = userDao;
        this.username = username;
        this.email = email;
        this.loginStatus = loginStatus;

    }



    @Override
    public JsonObject execute() {
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) request.get("payload");
        if (payload.size() == 2) {

            String usernameLoggedIn = payload.get("username").getAsString();
            email = usernameLoggedIn;
            username = usernameLoggedIn;
            String password = payload.get("password").getAsString();

            boolean loginUser = userDao.loginUser(usernameLoggedIn, password);

            if (!usernameLoggedIn.isEmpty()) {
                if (usernameLoggedIn != null) {
                    if (!password.isEmpty()) {
                        if (password != null) {
                            if (loginUser == true) {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful");
                                log.info("User {} successfully logged in ", usernameLoggedIn);

                                loginStatus = true;
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_FAILED, "Login Failed");
                                log.info("User {} failed logged in", username);

                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave password empty");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave username empty");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            log.info("User {} failed logged in", username);

        }

        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }

    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }


}
