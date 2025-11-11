package persistence;

import model.Review;

import java.util.ArrayList;

public interface IReviewDao {


  //  public Review searchForAlbumToBeReviewedById(int albumId);


    public int addReview(int reviewId, String username, double rating, String comment, String reviewType);
  public boolean checkIfRatingIsLessThan10(double rating);

    public boolean checkIfReviewAlreadyExist(String username, int album_id);

    public ArrayList<Review> getAllReviews();

    public Review getTheLatestReviewAddedByUser(String username);

    //public ArrayList<Review> getAllAlbumReviewsByUsername(String username);
}
