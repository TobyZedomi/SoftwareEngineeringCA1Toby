package commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import model.User;
import network.TCPNetworkLayer;
import persistence.IUserDao;
import service.UserUtilities;

@Slf4j
public class RegisterCommand implements Command{

    private JsonObject request;
    private JsonObject response;

    private IUserDao userDao;

    private String username;

    private boolean loginStatus;


    private TCPNetworkLayer networkLayer;
    private final Gson gson = new Gson();
    public RegisterCommand(JsonObject request,
                                 JsonObject response, IUserDao userDao, String username, boolean loginStatus, TCPNetworkLayer networkLayer){
        this.request = request;
        this.response = response;
        this.userDao = userDao;
        this.username = username;
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
        JsonObject jsonResponse = null;
        JsonObject payload = (JsonObject) request.get("payload");
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
                            if (!confirmPassword.isEmpty()) {
                                if (confirmPassword != null) {
                                    if (!checkIfUserExist == true) {
                                        if (checkPasswordsMatch == true) {
                                            if (checkPasswordFormat == true) {

                                                userDao.registerUser(new User(usernameReg, password));
                                                username = usernameReg;
                                                jsonResponse = createStatusResponse(UserUtilities.REGISTER_SUCCESSFUL, "Registration Successful");
                                                loginStatus = true;
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
                                } else {
                                    jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                                }
                            } else {
                                jsonResponse = createStatusResponse(UserUtilities.INVALID, "Cant leave confirm password empty");
                            }
                        } else {
                            jsonResponse = createStatusResponse(UserUtilities.INVALID, "Invalid");
                        }
                    } else {
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

        String response = gson.toJson(jsonResponse);
        // Send response
        networkLayer.send(response);
        return jsonResponse;
    }



}
