package observer;

import model.AlbumReview;
import persistence.AlbumReviewDaoImpl;

public interface Observer {


    void update (AlbumReviewDaoImpl a);
}
