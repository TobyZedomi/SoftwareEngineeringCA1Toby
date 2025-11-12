package persistence;

import model.AlbumReview;
import model.Review;
import observer.Observer;

import java.sql.*;
import java.util.ArrayList;

public class AlbumReviewDaoImpl extends MySQLDao implements IAlbumReviewDao {

    public AlbumReviewDaoImpl(String databaseName){
        super(databaseName);
    }

    public AlbumReviewDaoImpl(Connection conn){
        super(conn);
    }

    public AlbumReviewDaoImpl(){
        super();
    }


    private final ArrayList<Observer> observers = new ArrayList();



    @Override
    public int addAlbumReview(int reviewId, int albumId){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected
        int rowsAffected = 0;

        boolean notify = false;


        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)

        try(PreparedStatement ps = conn.prepareStatement("insert into albumReview values(?, " +
                "?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setInt(1, reviewId);
            ps.setInt(2, albumId);


            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)

            notify = true;
            rowsAffected = ps.executeUpdate();

        }// Add an extra exception handling block for where there is already an entry
        // with the primary key specified
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            // Set the rowsAffected to -1, this can be used as a flag for the display section
            rowsAffected = -1;
        }catch(SQLException e){
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        if(notify){
            notifyObservers();
        }

        return rowsAffected;
    }

    public boolean register(Observer o)
    {
        synchronized(observers){
            // If the observer to be added to the list exists
            // and isn't already present in the list
            if(o != null && !observers.contains(o)){
                // Add the new observer
                observers.add(o);
               // System.out.println("Adding observer " + o.toString() + " to list of observers for " + album_id + ".");
                return true;
            }
            return false;
        }
    }

    public synchronized boolean unregister(Observer o)
    {
        synchronized(observers){
            // If the observer being removed from the list exists
            // and could be successfully removed
            if(o!= null && observers.remove(o))
            {
               // System.out.println("Removed observer " + o.toString() + " from list of observers for " + album_id + ".");
                return true;
            }
            return false;
        }
    }

    private void notifyObservers()
    {
        synchronized(observers){
            // For each observer in the list, call their update method to notify them
            // that something has changed
            observers.stream().forEach((o) ->
            {
                o.update(this);
            });
        }
    }


    @Override
    public boolean listOfAlbumReviewsBasedOnUsersName(String username, int albumId){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected

       // AlbumReview reviews = null;

        boolean complete = false;


        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try(PreparedStatement ps = conn.prepareStatement("SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, albumreview.album_id" +
                " FROM review INNER JOIN albumreview ON review.review_id = albumreview.review_id WHERE review.review_type='albumReview' AND review.username = ? AND albumreview.album_id = ?")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, username);
            ps.setInt(2, albumId);



            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                   complete = true;
                }
            }
        }// Add an extra exception handling block for where there is already an entry
        // with the primary key specified
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            // Set the rowsAffected to -1, this can be used as a flag for the display section
        }catch(SQLException e){
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return complete;
    }




    public boolean checkIfRatingIsLessThan10(double rating){

        if (rating <=10){
            return true;
        }
        return false;
    }

    // get album id by rating id

    /*
    @Override
    public ArrayList<AlbumReview> getAllAlbumReviewsByUsername(int ratingId){

        ArrayList<AlbumReview> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM albumReview WHERE username = ? AND review_type = 'albumReview'";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setInt(1, ratingId);
            rs = ps.executeQuery();

            while(rs.next()){
                AlbumReview a = mapRow(rs);
                reviews.add(a);

            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return reviews;
    }

     */


    @Override
    public ArrayList<AlbumReview> getAllAlbumReviewsByUsername(){

        ArrayList<AlbumReview> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, albumreview.review_id, albumreview.album_id " +
                    "FROM review INNER JOIN albumreview " +
                    "ON review.review_id = albumreview.review_id WHERE review.review_type = 'albumReview';";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            rs = ps.executeQuery();

            while(rs.next()){
                AlbumReview a = mapRow(rs);
                reviews.add(a);

            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return reviews;
    }



    // GET ALL Album reviews


    @Override
    public ArrayList<AlbumReview> getAllAlbumReviews(){

        ArrayList<AlbumReview> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, albumreview.album_id " +
                    "FROM review INNER JOIN albumreview " +
                    "ON review.review_id = albumreview.review_id WHERE review.review_type = 'albumReview';";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            rs = ps.executeQuery();

            while(rs.next()){
                AlbumReview a = mapRow(rs);
                reviews.add(a);
            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return reviews;
    }


    @Override
    public ArrayList<AlbumReview> getAllAlbumReviewsFromUser(String username){

        ArrayList<AlbumReview> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, albumreview.album_id " +
                    "FROM review INNER JOIN albumreview " +
                    "ON review.review_id = albumreview.review_id WHERE review.review_type = 'albumReview' AND username = ?;";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);

            rs = ps.executeQuery();

            while(rs.next()){
                AlbumReview a = mapRow(rs);
                reviews.add(a);
            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution" + e.getMessage());
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    freeConnection(con);
                }
            } catch (SQLException e) {
                System.out.println("Exception occured in the finally section of the getProductByCode() method: " + e.getMessage());
            }
        }
        return reviews;
    }





    private AlbumReview mapRow(ResultSet rs)throws SQLException {

        AlbumReview r = new AlbumReview(

                rs.getInt("review_id"),
                rs.getString("username"),
                rs.getDouble("rating"),
                rs.getString("comment"),
                rs.getString("review_type"),
                rs.getInt("album_id")
        );
        return r;
    }

}
