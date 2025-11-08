package persistence;

import model.Artist;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArtistDaoImpl extends MySQLDao implements IArtistDao{

    public ArtistDaoImpl(String databaseName){
        super(databaseName);
    }

    public ArtistDaoImpl(Connection conn){
        super(conn);
    }

    public ArtistDaoImpl(){
        super();
    }

    /**
     * Get all artist information
     * @return all artist
     */
    @Override
    public ArrayList<Artist> getAllArtist(){

        ArrayList<Artist> artist = new ArrayList<>();

        // Get a connection using the superclass
        Connection conn = super.getConnection();
        // TRY to get a statement from the connection
        // When you are parameterizing the query, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try (PreparedStatement ps = conn.prepareStatement("Select * from artist")) {
            // TRY to execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Extract the information from the result set
                // Use extraction method to avoid code repetition!
                while(rs.next()){

                    Artist a = mapRow(rs);
                    artist.add(a);
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
        return artist;
    }


    @Override
    public ArrayList<Artist> searchForArtistByArtistName(String artistName){

        ArrayList<Artist> movieProducts = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM artist WHERE artist_name LIKE '%' ? '%'";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, artistName);
            rs = ps.executeQuery();

            while(rs.next()){
                Artist a = mapRow(rs);
                movieProducts.add(a);

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
        return movieProducts;
    }



    private Artist mapRow(ResultSet rs)throws SQLException {

        Artist a = new Artist(

                rs.getInt("artist_id"),
                rs.getString("artist_name"),
                rs.getInt("genre_id"),
                rs.getString("overview"),
                rs.getDate("date_of_birth").toLocalDate()
        );
        return a;
    }



}
