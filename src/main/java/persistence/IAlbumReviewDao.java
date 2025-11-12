package persistence;

import model.AlbumReview;
import model.Review;
import observer.Observer;

import java.util.ArrayList;

public interface IAlbumReviewDao {

    public int addAlbumReview(int reviewId, int albumId);

   // public ArrayList<AlbumReview> getAllAlbumReviewsByUsername(int ratingId);

    public ArrayList<AlbumReview> getAllAlbumReviewsByUsername();

    public boolean checkIfRatingIsLessThan10(double rating);

    public boolean listOfAlbumReviewsBasedOnUsersName(String username, int albumId);

    public ArrayList<AlbumReview> getAllAlbumReviews();

    public ArrayList<AlbumReview> getAllAlbumReviewsFromUser(String username);

    boolean register(Observer o);

    boolean unregister(Observer o);


}
