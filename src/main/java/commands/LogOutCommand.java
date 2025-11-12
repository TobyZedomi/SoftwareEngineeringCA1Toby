package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.User;
import network.TCPNetworkLayer;
import persistence.IUserDao;
import service.UserUtilities;

public class LogOutCommand implements Command{


    private JsonObject request;
    private JsonObject response;

    private String username;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public LogOutCommand(JsonObject request,
                           JsonObject response, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.username = username;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;
    }

    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }

    @Override
    public JsonObject execute() {
        JsonObject jsonResponse;
        jsonResponse = createStatusResponse(UserUtilities.GOODBYE, username + " logged out of the system");

        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }
}
