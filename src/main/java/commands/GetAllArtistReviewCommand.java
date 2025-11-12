package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.ArtistReview;
import model.Review;
import network.TCPNetworkLayer;
import persistence.IArtistDao;
import persistence.IArtistReviewDao;
import persistence.IReviewDao;
import service.UserUtilities;

import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class GetAllArtistReviewCommand implements Command {


    private JsonObject request;
    private JsonObject response;

    private IArtistReviewDao artistReviewDao;

    private IReviewDao reviewDao;

    private IArtistDao artistDao;
    private String username;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public GetAllArtistReviewCommand(JsonObject request,
                                  JsonObject response, IArtistReviewDao artistReviewDao, IReviewDao reviewDao, IArtistDao artistDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.artistReviewDao = artistReviewDao;
        this.reviewDao = reviewDao;
        this.artistDao = artistDao;
        this.username = username;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;

    }


    @Override
    public JsonObject execute() {
        JsonObject jsonResponse;
        if (!loginStatus) {
            ArrayList<ArtistReview> allArtistReviews = artistReviewDao.getAllArtistReviews();

            if (!allArtistReviews.isEmpty()) {
                if (allArtistReviews != null) {
                    jsonResponse = serializeArtistReview(allArtistReviews);
                    log.info("All Artist retrieved all there emails ", username);

                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.NO_ARTIST_REVIEWS, "There is no artist reviews");
                log.info("Theres no artist to retrieve ", username);
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




    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }

    public JsonObject serializeArtistReview(ArrayList<ArtistReview> reviews) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.ARTIST_DELIMITER2);

        for (ArtistReview r : reviews) {
            joiner.add(serializeArtistReview(r));
            jsonResponse = createStatusResponse5(UserUtilities.REVIEWS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeArtistReview(ArtistReview m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Album");
        }
        return "Username: " + m.getUsername() + UserUtilities.ARTIST_DELIMITER + "Artist: " + artistDao.getArtistNameById(m.getArtist_id()) + UserUtilities.ARTIST_DELIMITER + "Rating: " + m.getRating() + UserUtilities.ARTIST_DELIMITER + "Comment: " + m.getComment() +  "Number Of Concerts: " + m.getNumberOfConcerts()  + UserUtilities.ARTIST_DELIMITER + "Review Type: " + m.getReview_type();
    }

    private JsonObject createStatusResponse5(String status, String reviews) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("reviews", reviews);
        return invalidResponse;
    }
}
