package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Album;
import model.Artist;
import network.TCPNetworkLayer;
import persistence.IAlbumDao;
import persistence.IArtistDao;
import persistence.IGenreDao;
import service.UserUtilities;

import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class GetAllAlbumsCommand implements Command{

    private JsonObject request;
    private JsonObject response;

    private IArtistDao artistDao;

    private IAlbumDao albumDao;

    private String username;

    private String email;

    private boolean loginStatus;

    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public GetAllAlbumsCommand(JsonObject request,
                               JsonObject response, IArtistDao artistDao, IAlbumDao albumDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.artistDao = artistDao;
        this.username = username;
        this.albumDao = albumDao;
        this.loginStatus = loginStatus;
        this.networkLayer = networkLayer;

    }



    @Override
    public JsonObject execute() {
        JsonObject jsonResponse;
        if (!loginStatus) {
            ArrayList<Album> allAlbum = albumDao.getAllAlbums();

            if (!allAlbum.isEmpty()) {
                if (allAlbum != null) {
                    jsonResponse = serializeAlbum(allAlbum);
                    log.info("All Artist retrieved all there emails ", username);

                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.YOU_HAVE_NO_ALBUMS, "Theres no albums");
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

    public JsonObject serializeAlbum(ArrayList<Album> albums) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.ARTIST_DELIMITER2);

        for (Album a : albums) {
            joiner.add(serializeAlbum(a));
            jsonResponse = createStatusResponse3(UserUtilities.ALBUMS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeAlbum(Album m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Album");
        }
        return "ID: " + m.getAlbum_id() + UserUtilities.ARTIST_DELIMITER + "Name: " + m.getAlbum_name() + UserUtilities.ARTIST_DELIMITER + "Artist: " + artistDao.getArtistNameById(m.getArtist_id()) + UserUtilities.ARTIST_DELIMITER + "Description: " + m.getDescription() + UserUtilities.ARTIST_DELIMITER + "Release Date: " + m.getDate_of_release();
    }

    private JsonObject createStatusResponse3(String status, String albums) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("albums", albums);
        return invalidResponse;
    }

}
