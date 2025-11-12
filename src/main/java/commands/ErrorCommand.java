package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import network.TCPNetworkLayer;
import service.UserUtilities;

public class ErrorCommand implements Command {


    private JsonObject request;
    private JsonObject response;


    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();


    private boolean loginStatus;
    public ErrorCommand(JsonObject request,
                       JsonObject response, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
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
        JsonObject jsonResponse = null;
        if (jsonResponse == null) {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
        }

        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }

}
