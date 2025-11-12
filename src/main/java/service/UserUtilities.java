package service;

public class UserUtilities {

    public static final String HOSTNAME = "localhost";
    public static final int PORT = 12000;


    // REQUESTS
    public static final String LOGIN = "LOGIN";

    public static final String REGISTER = "REGISTER";

    public static final String SEARCH_FOR_ARTIST = "SEARCH_FOR_ARTIST";

    public static final String GET_ALL_ARTIST = "GET_ALL_ARTIST";

    public static final String SEARCH_FOR_ALBUM = "SEARCH_FOR_ALBUM";

    public static final String GET_ALL_ALBUM = "GET_ALL_ALBUM";
    public static final String SEARCH_FOR_ALBUM_FOR_USER_REVIEW = "SEARCH_FOR_ALBUM_FOR_USER_REVIEW";


    public static final String ADD_REVIEW = "ADD_REVIEW";

    public static final String ORDER_ARTIST_BY_NAME = "ORDER_ARTIST_BY_NAME";

    public static final String GET_ALL_ALBUM_REVIEWS = "GET_ALL_ALBUM_REVIEWS";

    public static final String GET_ALL_ALBUM_REVIEWS_FROM_USER = "GET_ALL_ALBUM_REVIEWS_FROM_USER";


    public static final String NO_ARTIST_REVIEWS = "NO_ARTIST_REVIEWS";

    public static final String ADD_ARTIST_REVIEW = "ADD_ARTIST_REVIEW";

    public static final String REVIEW_OF_ARTIST_SUCCESSFULLY_SENT = "REVIEW_OF_ARTIST_SUCCESSFULLY_SENT";

    public static final String REVIEW_ARTIST_ALREADY_EXIST = "REVIEW_ARTIST_ALREADY_EXIST";

    public static final String GET_ALL_ARTIST_REVIEWS_FROM_USER = "GET_ALL_ARTIST_REVIEWS_FROM_USER";


    public static final String GET_ALL_ARTIST_REVIEWS = "GET_ALL_ARTIST_REVIEWS";


    public static final String EXIT = "EXIT";
    public static final String LOGOUT = "LOGOUT";

    public static final String OBSERVER = "OBSERVER";



    //RESPONSES

    public static final String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";


    public static final String PASSWORDS_DONT_MATCH = "PASSWORDS_DONT_MATCH";

    public static final String INVALID_PASSWORD_FORMAT = "INVALID_PASSWORD_FORMAT";

    public static final String INVALID_EMAIL_FORMAT = "INVALID_EMAIL_FORMAT";


    public static final String REGISTER_SUCCESSFUL = "REGISTER_SUCCESSFUL";

    public static final String USER_ALREADY_EXIST = "USER_ALREADY_EXIST";

    public static final String NOT_LOGGED_IN = "NOT_LOGGED_IN";

    public static final String ARTISTS_RETRIEVED_SUCCESSFULLY = "ARTISTS_RETRIEVED_SUCCESSFULLY";

    public static final String ALBUMS_RETRIEVED_SUCCESSFULLY = "ALBUMS_RETRIEVED_SUCCESSFULLY";


    public static final String REVIEWS_RETRIEVED_SUCCESSFULLY = "REVIEWS_RETRIEVED_SUCCESSFULLY";


    public static final String YOU_HAVE_NO_ARTISTS = "YOU_HAVE_NO_ARTISTS";


    public static final String NO_ARTISTS_WITH_THIS_NAME = "NO_ARTISTS_WITH_THIS_NAME";

    public static final String EMPTY_ARTIST_NAME = "EMPTY_ARTIST_NAME";

    public static final String YOU_HAVE_NO_ALBUMS= "YOU_HAVE_NO_ALBUMS";

    public static final String NO_ALBUMS_WITH_THIS_NAME = "NO_ALBUMS_WITH_THIS_NAME";

    public static final String EMPTY_ALBUM_NAME = "EMPTY_ALBUM_NAME";


    public static final String ALBUM_FOUND = "ALBUM_FOUND";

    public static final String REVIEW_ALREADY_EXIST = "REVIEW_ALREADY_EXIST";

    public static final String RATING_OVER = "RATING_OVER";

    public static final String REVIEW_OF_ALBUM_SUCCESSFULLY_SENT = "REVIEW_OF_ALBUM_SUCCESSFULLY_SENT";


    public static final String ALBUM_DONT_EXIST = "ALBUM_DONT_EXIST";

    public static final String NON_NUMERIC_ID= "NON_NUMERIC_ID";

    public static final String ALBUM_DOESNT_EXIST= "ALBUM_DOESNT_EXIST";

    public static final String NOTI_SUCCESS= "NOTI_SUCCESS";



    // DELIMITERS
    public static final String DELIMITER = "%%";
    public static final String ARTIST_DELIMITER = ", ";

    public static final String ARTIST_DELIMITER2 = "##";


    // GENERAL MALFORMED RESPONSE:
    public static final String INVALID = "INVALID";

    public static final String GOODBYE = "GOODBYE";


}
