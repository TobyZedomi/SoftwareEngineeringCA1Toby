package commands;

import com.google.gson.JsonObject;
import model.AlbumReview;
import network.TCPNetworkLayer;
import persistence.*;
import service.UserUtilities;

public class CommandFactory {

    public Command createCommand(TCPNetworkLayer networkLayer, JsonObject request,
                                 JsonObject response, IUserDao userDao, String username, boolean loginStatus, String email, IArtistDao artistDao, IAlbumDao albumDao, IAlbumReviewDao albumReviewDao, IArtistReviewDao artistReviewDao, IReviewDao reviewDao, IGenreDao genreDao, boolean validClientSession) {
        Command c;
        if (request.has("action")) {
            String action = request.get("action").getAsString();
            switch (action) {
                case UserUtilities.REGISTER:
                    c = new RegisterCommand(request, response,userDao,username, loginStatus, networkLayer);
                    break;
                case UserUtilities.LOGIN:
                    c = new LoginCommand(networkLayer, request, response,userDao,username,email, loginStatus);
                    break;
                case UserUtilities.GET_ALL_ARTIST:
                    c = new GetAllArtistCommand(request, response, artistDao, genreDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.SEARCH_FOR_ARTIST:
                    c = new SearchForArtistCommand(request, response, artistDao, genreDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.ORDER_ARTIST_BY_NAME:
                    c = new OrderArtistByNameCommand(request, response, artistDao, genreDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.GET_ALL_ALBUM:
                    c = new GetAllAlbumsCommand(request, response, artistDao, albumDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.SEARCH_FOR_ALBUM:
                    c = new SearchForAlbumCommand(request, response, artistDao, albumDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.SEARCH_FOR_ALBUM_FOR_USER_REVIEW:
                    c = new SearchForAlbumForUserReviewCommand(request, response, artistDao, albumDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.ADD_REVIEW:
                    c = new AddReviewCommand(request, response, albumReviewDao, reviewDao, albumDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.GET_ALL_ALBUM_REVIEWS:
                    c = new GetAllAlbumReviewsCommand(request, response,albumReviewDao, reviewDao, albumDao, username, loginStatus,networkLayer);
                    break;
                case UserUtilities.GET_ALL_ALBUM_REVIEWS_FROM_USER:
                    c = new GetAllAlbumReviewsFromUser(request, response,albumReviewDao, reviewDao, albumDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.ADD_ARTIST_REVIEW:
                    c = new AddArtistReviewCommand(request, response,artistReviewDao, reviewDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.GET_ALL_ARTIST_REVIEWS:
                    c = new GetAllArtistReviewCommand(request, response,artistReviewDao, reviewDao, artistDao, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.GET_ALL_ARTIST_REVIEWS_FROM_USER:
                    c = new GetAllArtistReviewsFromUser(request, response,artistReviewDao, reviewDao, artistDao, username, loginStatus,networkLayer);
                    break;
                case UserUtilities.OBSERVER:
                    c = new UserObserverCommand(request, response, username, loginStatus, networkLayer, albumReviewDao);
                    break;
                case UserUtilities.LOGOUT:
                    c = new LogOutCommand(request, response, username, loginStatus, networkLayer);
                    break;
                case UserUtilities.EXIT:
                    c = new ExitCommand(request, response, validClientSession, loginStatus, networkLayer);
                    break;
                default:
                    c = new ErrorCommand(request, response, networkLayer);
            }
        } else {
            c = new ErrorCommand(request, response, networkLayer);
        }
        return c;
    }
}
