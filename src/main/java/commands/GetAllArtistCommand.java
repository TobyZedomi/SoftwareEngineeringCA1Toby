package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Artist;
import network.TCPNetworkLayer;
import persistence.IArtistDao;
import persistence.IGenreDao;
import persistence.IUserDao;
import service.UserUtilities;

import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class GetAllArtistCommand implements Command {

    private JsonObject request;
    private JsonObject response;

    private IArtistDao artistDao;

    private IGenreDao genreDao;

    private String username;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public GetAllArtistCommand(JsonObject request,
                        JsonObject response, IArtistDao artistDao, IGenreDao genreDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.artistDao = artistDao;
        this.username = username;
        this.genreDao = genreDao;
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
        if (!loginStatus) {
            ArrayList<Artist> allArtist = artistDao.getAllArtist();

            if (!allArtist.isEmpty()) {
                if (allArtist != null) {
                    jsonResponse = serializeArtist(allArtist);
                    log.info("All Artist retrieved all there emails ", username);

                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.YOU_HAVE_NO_ARTISTS, "You have no artists");
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


    public JsonObject serializeArtist(ArrayList<Artist> artists) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.ARTIST_DELIMITER2);

        for (Artist a : artists) {
            joiner.add(serializeArtist(a));
            jsonResponse = createStatusResponse2(UserUtilities.ARTISTS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeArtist(Artist m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Movie");
        }
        return "ArtistId: " + m.getArtist_id()+ UserUtilities.ARTIST_DELIMITER + "Name: " + m.getArtist_name() + UserUtilities.ARTIST_DELIMITER + "Genre: " + genreDao.getGenreNameById(m.getGenre_id()) + UserUtilities.ARTIST_DELIMITER + "Date: " + m.getDate_of_birth();
    }

    private JsonObject createStatusResponse2(String status, String artists) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("artists", artists);
        return invalidResponse;
    }

}
