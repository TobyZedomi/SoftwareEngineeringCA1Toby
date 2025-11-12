package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.AlbumReview;
import network.TCPNetworkLayer;
import observer.Observer;
import observer.ReviewWatcher;
import persistence.AlbumReviewDaoImpl;
import persistence.IAlbumReviewDao;
import service.UserUtilities;

@Slf4j
public class UserObserverCommand implements Command {




    private JsonObject request;
    private JsonObject response;
    private String username;


    private IAlbumReviewDao albumReviewDao;
    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public UserObserverCommand(JsonObject request,
                               JsonObject response, String username, boolean loginStatus, TCPNetworkLayer networkLayer, IAlbumReviewDao albumReviewDao){
        this.request = request;
        this.response = response;
        this.username = username;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;
        this.albumReviewDao = albumReviewDao;
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
        if (!loginStatus) {

            JsonObject payload = (JsonObject) request.get("payload");
            if (payload.size() == 1) {
                String observeAlbumReviews = payload.get("observe").getAsString();


                Observer observer = new ReviewWatcher(username);

                if (!observeAlbumReviews.isEmpty()) {
                    if (observeAlbumReviews != null) {
                        if (!observeAlbumReviews.equalsIgnoreCase("no")) {
                            if (observeAlbumReviews.equalsIgnoreCase("yes")) {
                                jsonResponse = createStatusResponse(UserUtilities.NOTI_SUCCESS, "You have notifications on for album reviews");

                                albumReviewDao.register(observer);
                                log.info("User {} searched for observeAlbumReviews with name {} ", username, observeAlbumReviews);
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.NO_ARTISTS_WITH_THIS_NAME, "No observeAlbumReviews found");
                            log.info("User {} searched for observeAlbumReviews with name {} but there is no observeAlbumReviews ", username, observeAlbumReviews);

                        }
                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.EMPTY_ARTIST_NAME, "Artist name was left empty");
                }

            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);
        }

        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }


}
