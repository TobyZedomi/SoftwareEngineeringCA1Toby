package persistence;


import model.ArtistReview;

import java.sql.*;
import java.util.ArrayList;

public class ArtistReviewDaoImpl extends MySQLDao implements IArtistReviewDao {

    public ArtistReviewDaoImpl(String databaseName){
        super(databaseName);
    }

    public ArtistReviewDaoImpl(Connection conn){
        super(conn);
    }

    public ArtistReviewDaoImpl(){
        super();
    }




    @Override
    public int addArtistReview(int reviewId, int albumId, int numberOfConcerts){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected
        int rowsAffected = 0;

        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)

        try(PreparedStatement ps = conn.prepareStatement("insert into artistReview values(?, ?, " +
                "?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setInt(1, reviewId);
            ps.setInt(2, albumId);
            ps.setInt(3, numberOfConcerts);



            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)
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

        return rowsAffected;
    }

    public boolean checkIfRatingIsLessThan10(double rating){

        if (rating <=10){
            return true;
        }
        return false;
    }


    @Override
    public boolean checkArtistReviewsBasedOnUsersNameAndArtistId(String username, int artistId){
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
        try(PreparedStatement ps = conn.prepareStatement("SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, artistreview.artist_id, artistreview.numberOfConcerts" +
                " FROM review INNER JOIN artistreview ON review.review_id = artistreview.review_id WHERE review.review_type='artistReview' AND review.username = ? AND artistreview.artist_id = ?")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, username);
            ps.setInt(2, artistId);



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





    @Override
    public ArrayList<ArtistReview> listArtistReviewsBasedOnUsersName(String username){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected

        ArrayList<ArtistReview> reviews = new ArrayList<>();

        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try(PreparedStatement ps = conn.prepareStatement("SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, artistreview.artist_id, artistreview.numberOfConcerts" +
                " FROM review INNER JOIN artistreview ON review.review_id = artistreview.review_id WHERE review.review_type='artistReview' AND review.username = ? ")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, username);



            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    ArtistReview a = mapRow(rs);
                    reviews.add(a);
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

        return reviews;
    }


    @Override
    public ArrayList<ArtistReview> getAllArtistReviews(){

        ArrayList<ArtistReview> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT review.review_id, review.username, review.rating, review.comment, review.review_type, artistreview.artist_id, artistreview.numberOfConcerts " +
                    "FROM review INNER JOIN artistreview " +
                    "ON review.review_id = artistreview.review_id WHERE review.review_type = 'artistReview'";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            rs = ps.executeQuery();

            while(rs.next()){
                ArtistReview a = mapRow(rs);
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


    private ArtistReview mapRow(ResultSet rs)throws SQLException {

        ArtistReview r = new ArtistReview(

                rs.getInt("review_id"),
                rs.getString("username"),
                rs.getDouble("rating"),
                rs.getString("comment"),
                rs.getString("review_type"),
                rs.getInt("artist_id"),
                rs.getInt("numberOfConcerts")
        );
        return r;
    }

}
