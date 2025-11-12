package observer;

import model.Album;
import model.AlbumReview;
import persistence.AlbumReviewDaoImpl;

import javax.swing.*;

public class ReviewWatcher implements Observer {

    private String name;

    public ReviewWatcher(String name)
    {

        this.name = name;
    }
    // The method used by the Observable entity to notify an observer that there
    // has been a change in state
    // Provide a reference back to the Observable entity so that this object can access
    // relevent information
    public void update(AlbumReviewDaoImpl a)
    {
        JOptionPane.showMessageDialog(null,"User" + name+ " made a new review for the album "  + " and its the review detail are:  " + a.getAllAlbumReviews());
        System.out.println("Alert displayed to user.");

    }
}
