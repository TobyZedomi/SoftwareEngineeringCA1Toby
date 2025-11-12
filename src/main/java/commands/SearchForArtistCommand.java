package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Artist;
import model.User;
import network.TCPNetworkLayer;
import persistence.IArtistDao;
import persistence.IGenreDao;
import persistence.IUserDao;
import service.UserUtilities;

import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class SearchForArtistCommand implements Command{




    private JsonObject request;
    private JsonObject response;

    private IArtistDao artistDao;

    private String username;

    private IGenreDao genreDao;


    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public SearchForArtistCommand(JsonObject request,
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

            JsonObject payload = (JsonObject) request.get("payload");
            if (payload.size() == 1) {
                String artist = payload.get("artist").getAsString();

                ArrayList<Artist> artistFromSearch = artistDao.searchForArtistByArtistName(artist);

                if (!artist.isEmpty()) {
                    if (artist != null) {
                        if (!artistFromSearch.isEmpty()) {
                            if (artistFromSearch != null) {
                                jsonResponse = serializeArtist(artistFromSearch);
                                log.info("User {} searched for artist with name {} ", username, artist);
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.NO_ARTISTS_WITH_THIS_NAME, "No artist found");
                            log.info("User {} searched for artist with name {} but there is no artist ", username, artist);

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
