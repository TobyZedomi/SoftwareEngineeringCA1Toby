package service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.Artist;
import model.User;
import network.TCPNetworkLayer;
import persistence.ArtistDaoImpl;
import persistence.IArtistDao;
import persistence.IUserDao;
import persistence.UserDaoImpl;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.StringJoiner;

@Slf4j
public class TCPServer implements Runnable {

    private Socket clientDataSocket;
    private TCPNetworkLayer networkLayer;
    private UserDaoImpl userDao;

    private ArtistDaoImpl artistDao;

    private static String username;

    private final Gson gson = new Gson();


    public TCPServer(Socket clientDataSocket, UserDaoImpl userDao, ArtistDaoImpl artistDao,  String username) throws IOException {
        this.clientDataSocket = clientDataSocket;
        this.networkLayer = new TCPNetworkLayer(clientDataSocket);

        this.userDao = userDao;
        this.artistDao = artistDao;
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
        return "ID: " + m.getArtist_id() + UserUtilities.ARTIST_DELIMITER + "Name: " + m.getArtist_name() + UserUtilities.ARTIST_DELIMITER + "Genre: " + m.getGenre() + UserUtilities.ARTIST_DELIMITER +  "Date: " + m.getDate_of_birth();
    }

    private JsonObject createStatusResponse2(String status, String artists) {
        JsonObject invalidResponse = new JsonObject();
        invalidResponse.addProperty("status", status);
        invalidResponse.addProperty("artists", artists);
        return invalidResponse;
    }
}
