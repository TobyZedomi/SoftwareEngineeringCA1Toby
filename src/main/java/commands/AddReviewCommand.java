package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Review;
import model.User;
import network.TCPNetworkLayer;
import persistence.IAlbumDao;
import persistence.IAlbumReviewDao;
import persistence.IReviewDao;
import persistence.IUserDao;
import service.UserUtilities;

@Slf4j
public class AddReviewCommand implements Command {

    private JsonObject request;
    private JsonObject response;

    private IAlbumReviewDao albumReviewDao;

    private IReviewDao reviewDao;

    private IAlbumDao albumDao;

    private String username;

    private boolean loginStatus;


    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public AddReviewCommand(JsonObject request,
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
        JsonObject jsonResponse = null;

        if (!loginStatus) {

            JsonObject payload = (JsonObject) request.get("payload");
            if (payload.size() == 3) {

                try {

                    String username1 = "Toby";
                    int albumID2 = Integer.parseInt(payload.get("album").getAsString());
                    double ratingUserGave = Double.parseDouble(payload.get("rating").getAsString());
                    String comment = payload.get("comment").getAsString();

                    System.out.println(albumID2);

                    boolean checkIfRatingLessThan10 = albumReviewDao.checkIfRatingIsLessThan10(ratingUserGave);
                    // boolean checkIfAlbumExist = albumDao.checkIfAlbumExistWithId(albumID2);
                    boolean checkIfReviewExist = albumReviewDao.listOfAlbumReviewsBasedOnUsersName(username1, albumID2);


                    if (checkIfRatingLessThan10 == true) {
                        if (!checkIfReviewExist == true) {

                            reviewDao.addReview(0, username1, ratingUserGave, comment, "albumReview");

                            Review r  = reviewDao.getTheLatestReviewAddedByUser(username1);
                            albumReviewDao.addAlbumReview(r.getReview_id(), albumID2);

                            System.out.println("Review successful" + albumID2 + ratingUserGave);

                            jsonResponse = createStatusResponse(UserUtilities.REVIEW_OF_ALBUM_SUCCESSFULLY_SENT, "Album review Successfully sent");
                            log.info("User {} tried to review album {} ", username, albumDao.searchForAlbumWithAlbumId(albumID2).getAlbum_name());
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.REVIEW_ALREADY_EXIST, "Review already exist");
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
