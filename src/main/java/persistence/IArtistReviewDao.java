package persistence;

import model.ArtistReview;

import java.util.ArrayList;

public interface IArtistReviewDao {


    public boolean checkIfRatingIsLessThan10(double rating);

    public int addArtistReview(int reviewId, int albumId, int numberOfConcerts);

    public ArrayList<ArtistReview> listArtistReviewsBasedOnUsersName(String username);
    public ArrayList<ArtistReview> getAllArtistReviews();

    public boolean checkArtistReviewsBasedOnUsersNameAndArtistId(String username, int artistId);

}
