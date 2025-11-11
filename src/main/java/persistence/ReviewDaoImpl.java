package persistence;

import model.Review;
import model.ReviewDummyClass;

import java.sql.*;
import java.util.ArrayList;

public class ReviewDaoImpl extends MySQLDao implements IReviewDao{


    public ReviewDaoImpl(String databaseName){
        super(databaseName);
    }

    public ReviewDaoImpl(Connection conn){
        super(conn);
    }

    public ReviewDaoImpl(){
        super();
    }

/*
    @Override
    public Review searchForAlbumToBeReviewedById(int albumId){

        Review review = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM review WHERE album_id =  ? ";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setInt(1, albumId);
            rs = ps.executeQuery();

            if(rs.next()){

                review = mapRow(rs);

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
        return review;
    }


 */

    @Override
    public int addReview(int reviewId, String username, double rating, String comment, String reviewType){
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

        try(PreparedStatement ps = conn.prepareStatement("insert into review values(?,?,?,?, " +
                "?)")) {
                // Fill in the blanks, i.e. parameterize the update
                ps.setInt(1, reviewId);
                ps.setString(2, username);
                ps.setDouble(3, rating);
                ps.setString(4, comment);
                ps.setString(5, reviewType);


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


    @Override
    public boolean checkIfReviewAlreadyExist(String username, int album_id){
        // DATABASE CODE
        //
        // Create variable to hold the result of the operation
        // Remember, where you are NOT doing a select, you will only ever get
        // a number indicating how many things were changed/affected
        boolean complete = false;


        Connection conn = super.getConnection();

        // TRY to prepare a statement from the connection
        // When you are parameterizing the update, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM review where username = ? AND album_id = ?")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, username);
            ps.setInt(2, album_id);


            // Execute the update and store how many rows were affected/changed
            // when inserting, this number indicates if the row was
            // added to the database (>0 means it was added)
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    complete = true;
                }
            }
        }// Add an extra exception handling block for where there is already an entry
        // with the primary key specified
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Constraint Exception occurred: " + e.getMessage());
            // Set the rowsAffected to -1, this can be used as a flag for the display section
            complete = false;
        }catch(SQLException e){
            System.out.println("SQL Exception occurred when attempting to prepare/execute SQL");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return complete;
    }


    @Override
    public boolean checkIfRatingIsLessThan10(double rating){

        if (rating <=10){
            return true;
        }
        return false;
    }


    @Override
    public ArrayList<Review> getAllReviews(){

        ArrayList<Review> reviews = new ArrayList<>();

        // Get a connection using the superclass
        Connection conn = super.getConnection();
        // TRY to get a statement from the connection
        // When you are parameterizing the query, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try (PreparedStatement ps = conn.prepareStatement("Select * from review")) {
            // TRY to execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Extract the information from the result set
                // Use extraction method to avoid code repetition!
                while(rs.next()){

                    Review r = mapRow(rs);
                    reviews.add(r);
                }
            } catch (SQLException e) {
                System.out.println("SQL Exception occurred when executing SQL or processing results.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }finally {
            // Close the connection using the superclass method
            super.freeConnection(conn);
        }
        return reviews;
    }




    // get all album reviews by username

/*
    @Override
    public ArrayList<Review> getAllAlbumReviewsByUsername(String username){

        ArrayList<Review> reviews = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM review WHERE username = ? AND review_type = 'albumReview'";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            while(rs.next()){
                Review a = mapRow(rs);
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
    public Review getTheLatestReviewAddedByUser(String username){

        Review reviews = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM review WHERE username = ? ORDER BY review_id DESC LIMIT 1";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();

            if(rs.next()){
                reviews = mapRow(rs);

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


    private Review mapRow(ResultSet rs)throws SQLException {

        Review r = new ReviewDummyClass(

                rs.getInt("review_id"),
                rs.getString("username"),
                rs.getDouble("rating"),
                rs.getString("comment"),
                rs.getString("review_type")
        );
        return r;
    }



}
