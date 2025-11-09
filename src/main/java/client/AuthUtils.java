package client;

public class AuthUtils {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 12000;


    // REQUEST
    public static final String LOGIN = "LOGIN";

    public static final String REGISTER = "REGISTER";

    public static final String SEND_EMAIL = "SEND_EMAIL";

    public static final String GET_ALL_ARTIST = "GET_ALL_ARTIST";

    public static final String SEARCH_FOR_ARTIST = "SEARCH_FOR_ARTIST";

    public static final String GET_ALL_ALBUM = "GET_ALL_ALBUM";

    public static final String SEARCH_FOR_ALBUM = "SEARCH_FOR_ALBUM";

    public static final String SEARCH_FOR_ALBUM_FOR_USER_REVIEW = "SEARCH_FOR_ALBUM_FOR_USER_REVIEW";

    public static final String ADD_REVIEW = "ADD_REVIEW";

    public static final String GET_CONTENT_RECEIVED_EMAILS = "GET_CONTENT_RECEIVED_EMAILS";
    public static final String GET_CONTENT_SENT_EMAIL = "GET_CONTENT_SENT_EMAIL";

    public static final String GET_RECEIVED_EMAIL_BY_ID = "GET_RECEIVED_EMAIL_BY_ID";

    public static final String GET_SENT_EMAIL_BY_ID = "GET_SENT_EMAIL_BY_ID";

    public static final String LOGOUT = "LOGOUT";

    public static final String EXIT = "EXIT";


    // RESPONSES

    public static final String REVIEW_OF_ALBUM_SUCCESSFULLY_SENT = "{\"status\":\"REVIEW_OF_ALBUM_SUCCESSFULLY_SENT\",\"message\":\"Album review Successfully sent\"}";
    public static final String GOODBYE = "{\"status\":\"GOODBYE\",\"message\":\"Goodbye\"}";
    public static final String LOGIN_SUCCESSFUL = "{\"status\":\"LOGIN_SUCCESSFUL\",\"message\":\"Login Successful\"}";
    public static final String NO_ARTISTS_WITH_THIS_NAME = "{\"status\":\"NO_ARTISTS_WITH_THIS_NAME\",\"message\":\"No artist found\"}";
    public static final String REGISTER_SUCCESSFUL =   "{\"status\":\"REGISTER_SUCCESSFUL\",\"message\":\"Registration Successful\"}";
    public static final String NON_NUMERIC_ID = "{\"status\":\"NON_NUMERIC_ID\",\"message\":\"Id must be a number\"}";
    public static final String EMAIL_ID_DOESNT_EXIST =   "{\"status\":\"EMAIL_ID_DOESNT_EXIST\",\"message\":\"Email with this id doesnt exist\"}";
    public static final String INVALID =   "{\"status\":\"INVALID\",\"message\":\"Invalid\"}";
    public static final String YOU_HAVE_NO_ARTISTS =   "{\"status\":\"YOU_HAVE_NO_ARTISTS\",\"message\":\"You have no artists\"}";
    public static final String NOT_LOGGED_IN =   "{\"status\":\"NOT_LOGGED_IN\",\"message\":\"Not logged in\"}";
    public static final String EMPTY_ARTIST_NAME =   "{\"status\":\"EMPTY_ARTIST_NAME\",\"message\":\"Artist name was left empty\"}";
    public static final String EMAIL_ID_LESS_THAN_1 =   "{\"status\":\"EMAIL_ID_LESS_THAN_1\",\"message\":\"Email id cant be less than 1\"}";

    public static final String YOU_HAVE_NO_ALBUMS =   "{\"status\":\"YOU_HAVE_NO_ALBUMS\",\"message\":\"Theres no albums\"}";

    public static final String NO_ALBUMS_WITH_THIS_NAME = "{\"status\":\"NO_ALBUMS_WITH_THIS_NAME\",\"message\":\"No album found\"}";


    public static final String EMPTY_ALBUM_NAME =   "{\"status\":\"EMPTY_ALBUM_NAME\",\"message\":\"Album name was left empty\"}";


    public static final String ALBUM_FOUND =   "{\"status\":\"ALBUM_FOUND\",\"message\":\"Album found and going to review\"}";


    public static final String REVIEW_ALREADY_EXIST =   "{\"status\":\"REVIEW_ALREADY_EXIST\",\"message\":\"Review already exist\"}";


    public static final String RATING_OVER =   "{\"status\":\"RATING_OVER\",\"message\":\"Rating must be 10 or lower\"}";




    /// Delimiter
    public static final String DELIMITER = "%%";
}
