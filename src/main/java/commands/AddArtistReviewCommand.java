package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Review;
import network.TCPNetworkLayer;
import persistence.IAlbumDao;
import persistence.IAlbumReviewDao;
import persistence.IArtistReviewDao;
import persistence.IReviewDao;
import service.UserUtilities;

@Slf4j
public class AddArtistReviewCommand implements Command{


    private JsonObject request;
    private JsonObject response;

    private IArtistReviewDao artistReviewDao;

    private IReviewDao reviewDao;


    private String username;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public AddArtistReviewCommand(JsonObject request,
                            JsonObject response, IArtistReviewDao artistReviewDao, IReviewDao reviewDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.artistReviewDao = artistReviewDao;
        this.reviewDao = reviewDao;
        this.username = username;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;

    }


    @Override
    public JsonObject execute() {
        JsonObject jsonResponse = null;

        if (!loginStatus) {

            JsonObject payload = (JsonObject) request.get("payload");
            if (payload.size() == 4) {

                try {

                    String username1 = username;
                    int artistID2 = Integer.parseInt(payload.get("artist").getAsString());
                    double ratingUserGave = Double.parseDouble(payload.get("rating").getAsString());
                    String comment = payload.get("comment").getAsString();
                    int numOfConcerts = Integer.parseInt(payload.get("numOfConcerts").getAsString());

                    System.out.println(artistID2);

                    boolean checkIfRatingLessThan10 = artistReviewDao.checkIfRatingIsLessThan10(ratingUserGave);
                    // boolean checkIfAlbumExist = albumDao.checkIfAlbumExistWithId(artistID2);
                    boolean checkIfReviewExist = artistReviewDao.checkArtistReviewsBasedOnUsersNameAndArtistId(username1, artistID2);


                    if (checkIfRatingLessThan10 == true) {
                        if (!checkIfReviewExist == true) {

                            reviewDao.addReview(0, username1, ratingUserGave, comment, "artistReview");

                            Review r  = reviewDao.getTheLatestReviewAddedByUser(username1);
                            artistReviewDao.addArtistReview(r.getReview_id(), artistID2, numOfConcerts);

                            System.out.println("Review successful" + artistID2 + ratingUserGave);

                            jsonResponse = createStatusResponse(UserUtilities.REVIEW_OF_ARTIST_SUCCESSFULLY_SENT, "Artist review Successfully sent");
                            log.info("User {} tried to review album {} ", username);
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.REVIEW_ARTIST_ALREADY_EXIST, "Review artist already exist");
                            log.info("User {} tried to review an album but it doesnt exist ", username);
                        }

                    } else {
                        jsonResponse = createStatusResponse(UserUtilities.RATING_OVER, "Rating must be between 0 - 10");
                    }

                } catch (NumberFormatException ex) {
                    jsonResponse = createStatusResponse(UserUtilities.NON_NUMERIC_ID, "Id must be a number");
                    log.info("User {} entered a non numeric id", username);
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




    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }
}
