package persistence;

import model.Review;

public interface IReviewDao {


    public Review searchForAlbumToBeReviewedById(int albumId);


    public int addReview(Review newReview);

    public boolean checkIfRatingIsLessThan10(double rating);

    public boolean checkIfReviewAlreadyExist(String username, int album_id);
}
