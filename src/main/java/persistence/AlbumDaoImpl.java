package persistence;

import model.Album;
import model.Artist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlbumDaoImpl extends MySQLDao implements IAlbumDao {


    public AlbumDaoImpl(String databaseName){
        super(databaseName);
    }

    public AlbumDaoImpl(Connection conn){
        super(conn);
    }

    public AlbumDaoImpl(){
        super();
    }




    /**
     * Get all album information
     * @return all albums
     */
    @Override
    public ArrayList<Album> getAllAlbums(){

        ArrayList<Album> albums = new ArrayList<>();

        // Get a connection using the superclass
        Connection conn = super.getConnection();
        // TRY to get a statement from the connection
        // When you are parameterizing the query, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try (PreparedStatement ps = conn.prepareStatement("Select * from album")) {
            // TRY to execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Extract the information from the result set
                // Use extraction method to avoid code repetition!
                while(rs.next()){

                    Album a = mapRow(rs);
                    albums.add(a);
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
        return albums;
    }


    @Override
    public ArrayList<Album> searchForAlbumByAlbumName(String albumName){

        ArrayList<Album> album = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try{

            con = getConnection();

            String query = "SELECT * FROM album WHERE album_name LIKE '%' ? '%'";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, albumName);
            rs = ps.executeQuery();

            while(rs.next()){
                Album a = mapRow(rs);
                album.add(a);

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
        return album;
    }





    private Album mapRow(ResultSet rs)throws SQLException {

        Album a = new Album(

                rs.getInt("album_id"),
                rs.getString("album_name"),
                rs.getInt("artist_id"),
                rs.getString("description"),
                rs.getDate("date_of_release").toLocalDate()
        );
        return a;
    }

}
