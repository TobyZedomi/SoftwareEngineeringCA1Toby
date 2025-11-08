package service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.User;
import network.TCPNetworkLayer;
import persistence.IUserDao;
import persistence.UserDaoImpl;

import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
public class TCPServer implements Runnable {

    private Socket clientDataSocket;
    private TCPNetworkLayer networkLayer;
    private UserDaoImpl userDao;


    private static String username;

    private final Gson gson = new Gson();


    public TCPServer(Socket clientDataSocket, UserDaoImpl userDao, String username) throws IOException {
        this.clientDataSocket = clientDataSocket;
        this.networkLayer = new TCPNetworkLayer(clientDataSocket);

        this.userDao = userDao;
        this.username = username;
    }

    public void run() {

        try {
            boolean validClientSession = true;
            boolean loginStatus = false;

            System.out.println("hello");

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

                            /*
                            jsonResponse = loginUser(jsonRequest, userDao, username);
                            if (jsonResponse == createStatusResponse(UserUtilities.LOGIN_SUCCESSFUL, "Login Successful")) {
                                loginStatus = true;
                            }


                             */

                            break;
                        case UserUtilities.LOGOUT:
                            loginStatus = false;
                            break;
                        case UserUtilities.EXIT:
                            /*
                            jsonResponse = createStatusResponse(UserUtilities.GOODBYE, "Goodbye");
                            validClientSession = false;

                             */
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


}
