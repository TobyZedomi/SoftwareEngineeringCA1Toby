package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import network.TCPNetworkLayer;
import service.UserUtilities;

@Slf4j
public class ExitCommand implements Command{



    private JsonObject request;
    private JsonObject response;

    private String username;

   private boolean validClientSession;


    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();

    private boolean loginStatus;
    public ExitCommand(JsonObject request,
                         JsonObject response, boolean validClientSession,  boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.validClientSession = validClientSession;
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
        jsonResponse = createStatusResponse(UserUtilities.GOODBYE, "Goodbye");
        validClientSession = false;
        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }
}
