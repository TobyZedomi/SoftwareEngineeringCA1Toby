package service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Album;
import model.Artist;
import model.Review;
import model.User;
import network.TCPNetworkLayer;
import persistence.*;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class TCPServer implements Runnable {

    private Socket clientDataSocket;
    private TCPNetworkLayer networkLayer;
    private UserDaoImpl userDao;

    private ArtistDaoImpl artistDao;

    private GenreDaoImpl genreDao;

    private AlbumDaoImpl albumDao;

    private ReviewDaoImpl reviewDao;

    private static String username;

    private final Gson gson = new Gson();


    public TCPServer(Socket clientDataSocket, UserDaoImpl userDao, ArtistDaoImpl artistDao, GenreDaoImpl genreDao, AlbumDaoImpl albumDao, ReviewDaoImpl reviewDao,  String username) throws IOException {
        this.clientDataSocket = clientDataSocket;
        this.networkLayer = new TCPNetworkLayer(clientDataSocket);

        this.userDao = userDao;
        this.artistDao = artistDao;
        this.genreDao = genreDao;
        this.albumDao = albumDao;
        this.reviewDao = reviewDao;
        this.username = username;
    }

    public void run() {

        try {
            boolean validClientSession = true;
            boolean loginStatus = false;


            while (validClientSession) {

                String request = networkLayer.receive();
                System.out.println("Request: " + request);

                JsonObject jsonResponse = null;

                JsonObject jsonRequest = gson.fromJson(request, JsonObject.class);


                if (jsonRequest.has("action")) {
                    String action = jsonRequest.get("action").getAsString();
                    switch (action) {
                        case UserUtilities.REGISTER:
                            jsonResponse = registerUser(jsonRequest, userDao);
                            if (jsonResponse == createStatusResponse(UserUtilities.REGISTER_SUCCESSFUL, "Register Successful")) {
                                loginStatus = true;
                            }
                            break;
                        case UserUtilities.LOGIN:
                            jsonResponse = loginUser(jsonRequest, userDao, username);
                            if (jsonResponse == createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful")) {
                                loginStatus = true;
                            }
                            break;
                        case UserUtilities.GET_ALL_ARTIST:
                            jsonResponse = getAllArtist(loginStatus, artistDao);
                            break;
                        case UserUtilities.SEARCH_FOR_ARTIST:
                            jsonResponse = searchForArtist(loginStatus, jsonRequest, artistDao);
                            break;
                        case UserUtilities.GET_ALL_ALBUM:
                            jsonResponse = getAllAlbum(loginStatus, albumDao);
                            break;
                        case UserUtilities.SEARCH_FOR_ALBUM:
                            jsonResponse = searchForAlbum(loginStatus, jsonRequest, albumDao);
                            break;
                        case UserUtilities.SEARCH_FOR_ALBUM_FOR_USER_REVIEW:
                            jsonResponse = searchForAlbumForUserReview(loginStatus, jsonRequest, albumDao);
                            break;
                        case UserUtilities.ADD_REVIEW:
                            jsonResponse = addReview(loginStatus, jsonRequest, reviewDao, albumDao);
                            break;
                        case UserUtilities.LOGOUT:
                            jsonResponse = createStatusResponse(UserUtilities.GOODBYE, username+ " logged out of the system");
                            loginStatus = false;
                            break;
                        case UserUtilities.EXIT:

                            jsonResponse = createStatusResponse(UserUtilities.GOODBYE, "Goodbye");
                            validClientSession = false;
                            break;
                    }

                }

                if (jsonResponse == null) {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }

                String response = gson.toJson(jsonResponse);
                // Send response
                networkLayer.send(response);

            }

            networkLayer.disconnect();
        } catch (IOException e) {
            System.out.println("ERROR");
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject createStatusResponse(String status, String message) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("message", message);
        return invalidResponse;
    }
    private static JsonObject registerUser(JsonObject jsonRequest, IUserDao userDao) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //String jsonResponse;
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) jsonRequest.get("payload");
        if (payload.size() == 3) {

            String usernameReg = payload.get("username").getAsString();
            String password = payload.get("password").getAsString();
            String confirmPassword = payload.get("confirmPassword").getAsString();


            boolean checkIfUserExist = userDao.checkIfUserExist(usernameReg);

            boolean checkPasswordsMatch = userDao.checkIfPasswordsAreTheSame(password, confirmPassword);

            boolean checkPasswordFormat = userDao.checkIfPasswordsMatchRegex(password, confirmPassword);


            if (usernameReg != null) {
                if (!usernameReg.isEmpty()) {
                    if (!password.isEmpty()) {
                        if (password != null) {
                            if(!confirmPassword.isEmpty()) {
                                if (confirmPassword != null) {
                                    if (!checkIfUserExist == true) {
                                        if (checkPasswordsMatch == true) {
                                            if (checkPasswordFormat == true) {

                                                userDao.registerUser(new User(usernameReg, password));
                                                username = usernameReg;
                                                jsonResponse = createStatusResponse(UserUtilities.REGISTER_SUCCESSFUL, "Registration Successful");
                                                log.info("User {} successfully registered with us ", usernameReg);

                                            } else {
                                                jsonResponse = createStatusResponse(UserUtilities.INVALID_PASSWORD_FORMAT, "Password format must be 8 or more characters long, have at least 1 capital letter, 1 upper case and 1 special character");
                                                log.info("User {} failed registration", usernameReg);
                                            }

                                        } else {
                                            jsonResponse = createStatusResponse(UserUtilities.PASSWORDS_DONT_MATCH, "Passwords dont match");
                                            log.info("User {} failed registration", usernameReg);
                                        }
                                    } else {
                                        jsonResponse = createStatusResponse(UserUtilities.USER_ALREADY_EXIST, "User already exist");
                                        log.info("User {} failed registration", usernameReg);
                                    }
                                }else {
                                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                                }
                            }else{
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave confirm password empty");
                            }
                        }else{
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    }else{
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave password empty");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave username empty");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            log.info("User {} failed registration", username);
        }
        return jsonResponse;
    }




    private static JsonObject loginUser(JsonObject jsonRequest, IUserDao userDao, String email) {
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) jsonRequest.get("payload");
        if (payload.size() == 2) {

            String usernameLoggedIn = payload.get("username").getAsString();
            email = usernameLoggedIn;
            username = usernameLoggedIn;
            String password = payload.get("password").getAsString();

            boolean loginUser = userDao.loginUser(usernameLoggedIn, password);

            if (!usernameLoggedIn.isEmpty()) {
                if (usernameLoggedIn != null) {
                    if (!password.isEmpty()) {
                        if (password != null) {
                            if (loginUser == true) {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful");
                                log.info("User {} successfully logged in ", usernameLoggedIn);
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.LOGIN_FAILED, "Login Failed");
                                log.info("User {} failed logged in", username);

                            }
                        }else{
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    }else {
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave password empty");
                    }
                } else {
                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                }
            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave username empty");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            log.info("User {} failed logged in", username);

        }
        return jsonResponse;
    }


    private JsonObject getAllArtist(boolean loginStatus, IArtistDao artistDao) {
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
        return jsonResponse;
    }




    private JsonObject searchForArtist(boolean loginStatus, JsonObject jsonRequest, IArtistDao artistDao) {
        JsonObject jsonResponse;
        if (!loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
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
                    }else{
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                    }
                }else{
                    jsonResponse = createStatusResponse(UserUtilities.EMPTY_ARTIST_NAME, "Artist name was left empty");
                }

            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);
        }
        return jsonResponse;
    }



    private JsonObject getAllAlbum(boolean loginStatus, IAlbumDao albumDao) {
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
        return jsonResponse;
    }




    private JsonObject searchForAlbum(boolean loginStatus, JsonObject jsonRequest, IAlbumDao albumDao) {
        JsonObject jsonResponse;
        if (!loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
            if (payload.size() == 1) {
                String album = payload.get("album").getAsString();

                ArrayList<Album> albumForSearch = albumDao.searchForAlbumByAlbumName(album);

                if (!album.isEmpty()) {
                    if (album != null) {
                        if (!albumForSearch.isEmpty()) {
                            if (albumForSearch != null) {
                                jsonResponse = serializeAlbum(albumForSearch);
                                log.info("User {} searched for album with name {} ", username, album);
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.NO_ALBUMS_WITH_THIS_NAME, "No album found");
                            log.info("User {} searched for album with name {} but there is no album ", username, album);

                        }
                    }else{
                        jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                    }
                }else{
                    jsonResponse = createStatusResponse(UserUtilities.EMPTY_ALBUM_NAME, "Album name was left empty");
                }

            } else {
                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
            }
        } else {
            jsonResponse = createStatusResponse(UserUtilities.NOT_LOGGED_IN, "Not logged in");
            log.info("{} is not logged in", username);
        }
        return jsonResponse;
    }



    // search for album for user to review

    private JsonObject searchForAlbumForUserReview(boolean loginStatus, JsonObject jsonRequest, IAlbumDao albumDao) {
        JsonObject jsonResponse;
        if (!loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
            if (payload.size() == 1) {

                try {

                    int albumID = Integer.parseInt(payload.get("album").getAsString());

                    Album albumForReviewSearch = albumDao.searchForAlbumWithAlbumId(albumID);

                        if (albumForReviewSearch != null) {
                            jsonResponse = createStatusResponse(UserUtilities.ALBUM_FOUND, "Album found and going to review");
                            log.info("User {} searched for album with name {} ", username, albumForReviewSearch.getAlbum_name());
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.NO_ALBUMS_WITH_THIS_NAME, "No album found");
                        }


                }catch (NumberFormatException ex) {
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
        return jsonResponse;
    }



    // Add review

    private JsonObject addReview(boolean loginStatus, JsonObject jsonRequest, IReviewDao reviewDao, IAlbumDao albumDao) {

        JsonObject jsonResponse = null;

        if (!loginStatus) {

            JsonObject payload = (JsonObject) jsonRequest.get("payload");
            if (payload.size() == 3) {

                try {

                    String username1 = username;
                    int albumID2 = Integer.parseInt(payload.get("album").getAsString());
                    double ratingUserGave = Double.parseDouble(payload.get("rating").getAsString());
                    String comment = payload.get("comment").getAsString();

                    System.out.println(albumID2);

                    boolean checkIfRatingLessThan10 = reviewDao.checkIfRatingIsLessThan10(ratingUserGave);
                    //boolean checkIfAlbumExist = albumDao.checkIfAlbumExistWithId(albumID2);
                    boolean checkIfReviewExist = reviewDao.checkIfReviewAlreadyExist(username1, albumID2);


                            if (checkIfRatingLessThan10 == true) {
                                if (!checkIfReviewExist == true) {
                                    reviewDao.addReview(new Review(username1, albumID2, ratingUserGave, comment));

                                    jsonResponse = createStatusResponse(UserUtilities.REVIEW_OF_ALBUM_SUCCESSFULLY_SENT, "Album review Successfully sent");
                                    log.info("User {} tried to review album {} ", username, albumDao.searchForAlbumWithAlbumId(albumID2).getAlbum_name());
                                } else {
                                    jsonResponse = createStatusResponse(UserUtilities.REVIEW_ALREADY_EXIST, "Review already exist");
                                    log.info("User {} tried to review an album but it doesnt exist ", username);
                                }
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.RATING_OVER, "Rating must be 10 or lower");
                            }

                }catch (NumberFormatException ex) {
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
        return "Name: " + m.getArtist_name() + UserUtilities.ARTIST_DELIMITER + "Genre: " + genreDao.getGenreNameById(m.getGenre_id()) + UserUtilities.ARTIST_DELIMITER +  "Date: " + m.getDate_of_birth();
    }

    private JsonObject createStatusResponse2(String status, String artists) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("artists", artists);
        return invalidResponse;
    }


    // album serilization


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
        return "ID: " +m.getAlbum_id() + UserUtilities.ARTIST_DELIMITER + "Name: " + m.getAlbum_name() + UserUtilities.ARTIST_DELIMITER + "Artist: " + artistDao.getArtistNameById(m.getArtist_id()) + UserUtilities.ARTIST_DELIMITER + "Description: " + m.getDescription() + UserUtilities.ARTIST_DELIMITER +  "Release Date: " + m.getDate_of_release();
    }

    private JsonObject createStatusResponse3(String status, String albums) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("albums", albums);
        return invalidResponse;
    }






    // review serilization


    public JsonObject serializeReview(ArrayList<Review> reviews) {
        JsonObject jsonResponse = null;

        StringJoiner joiner = new StringJoiner(UserUtilities.ARTIST_DELIMITER2);

        for (Review r : reviews) {
            joiner.add(serializeReview(r));
            jsonResponse = createStatusResponse4(UserUtilities.REVIEWS_RETRIEVED_SUCCESSFULLY, joiner.toString());
        }
        return jsonResponse;
    }

    public String serializeReview(Review m) {
        if (m == null) {
            throw new IllegalArgumentException("Cannot serialise null Album");
        }
        return "Name: " + m.getUsername() + UserUtilities.ARTIST_DELIMITER + "Album: " + artistDao.getArtistNameById(m.getAlbum_id()) + UserUtilities.ARTIST_DELIMITER + "Rating: " + m.getRating() + UserUtilities.ARTIST_DELIMITER +  "Comment: " + m.getComment();
    }

    private JsonObject createStatusResponse4(String status, String reviews) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("reviews", reviews);
        return invalidResponse;
    }
}
