package persistence;

import model.Genre;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreDaoImpl extends MySQLDao implements IGenreDao{


    public GenreDaoImpl(String databaseName){
        super(databaseName);
    }

    public GenreDaoImpl(Connection conn){
        super(conn);
    }

    public GenreDaoImpl(){
        super();
    }



    @Override
    public String getGenreNameById(int id) {

        Genre genre = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        try {

            con = getConnection();

            String query = "SELECT * FROM genre where genre_id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();


            if (rs.next()) {

                genre = mapRow(rs);
            }

        } catch (SQLException e) {
            System.out.println("Exception occured in the getMovieById() method: " + e.getMessage());
        } finally {
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
        return genre.getGenre_name();
    }



    private Genre mapRow(ResultSet rs)throws SQLException {

        Genre u = new Genre(

                rs.getInt("genre_id"),
                rs.getString("genre_name")
        );
        return u;
    }

}
