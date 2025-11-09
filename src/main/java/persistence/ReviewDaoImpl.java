package persistence;

import model.Album;
import model.Review;
import model.User;

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


    @Override
    public int addReview(Review newReview){
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
        try(PreparedStatement ps = conn.prepareStatement("insert into review values(?,?,?, " +
                "?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, newReview.getUsername());
            ps.setInt(2, newReview.getAlbum_id());
            ps.setDouble(3, newReview.getRating());
            ps.setString(4, newReview.getComment());


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

    private Review mapRow(ResultSet rs)throws SQLException {

        Review r = new Review(

                rs.getString("username"),
                rs.getInt("album_id"),
                rs.getDouble("rating"),
                rs.getString("comment")
        );
        return r;
    }

}
