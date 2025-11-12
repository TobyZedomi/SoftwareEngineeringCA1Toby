package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.AlbumReview;
import model.Review;
import network.TCPNetworkLayer;
import persistence.IAlbumDao;
import persistence.IAlbumReviewDao;
import persistence.IReviewDao;
import service.UserUtilities;

import java.util.ArrayList;
import java.util.StringJoiner;


@Slf4j
public class GetAllAlbumReviewsCommand implements Command{


    private JsonObject request;
    private JsonObject response;

    private IAlbumReviewDao albumReviewDao;

    private IReviewDao reviewDao;

    private IAlbumDao albumDao;

    private String username;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public GetAllAlbumReviewsCommand(JsonObject request,
                            JsonObject response, IAlbumReviewDao albumReviewDao, IReviewDao reviewDao, IAlbumDao albumDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.albumReviewDao = albumReviewDao;
        this.reviewDao = reviewDao;
        this.albumDao = albumDao;
        this.username = username;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;
    }


    @Override
    public JsonObject execute() {
        JsonObject jsonResponse;
        if (!loginStatus) {
            ArrayList<AlbumReview> allAlbumReviews = albumReviewDao.getAllAlbumReviews();

            for (int i = 0; i < allAlbumReviews.size();i++){
                System.out.println(allAlbumReviews.get(i));
            }

            System.out.println("hello");

            if (!allAlbumReviews.isEmpty()) {
                if (allAlbumReviews != null) {
                    jsonResponse = serializeReview(allAlbumReviews);

                    System.out.println("hello you here");

                    log.info("{} retrieved all there albums reviewed", username);

                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.YOU_HAVE_NO_ALBUMS, "Theres no albums");
                log.info("{} tried to retrieve all album reviews but there are no albums ", username);
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

    public JsonObject serializeReview(ArrayList<AlbumReview> reviews) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.ARTIST_DELIMITER2);

        for (AlbumReview r : reviews) {
            joiner.add(serializeReview(r));
            jsonResponse = createStatusResponse4(UserUtilities.REVIEWS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeReview(AlbumReview m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Album");
        }
        return "Username: " + m.getUsername() + UserUtilities.ARTIST_DELIMITER + "Album: " + albumDao.getAlbumNameById(m.getAlbum_id()) + UserUtilities.ARTIST_DELIMITER + "Rating: " + m.getRating() + UserUtilities.ARTIST_DELIMITER + "Comment: " + m.getComment() + m.getRating() + UserUtilities.ARTIST_DELIMITER + "Review Type: " + m.getReview_type();
    }

    private JsonObject createStatusResponse4(String status, String reviews) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("reviews", reviews);
        return invalidResponse;
    }

    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }
}
