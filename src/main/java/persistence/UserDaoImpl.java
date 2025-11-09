package persistence;

import model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;

public class UserDaoImpl extends MySQLDao implements IUserDao{

    public UserDaoImpl(String databaseName){
        super(databaseName);
    }

    public UserDaoImpl(Connection conn){
        super(conn);
    }

    public UserDaoImpl(){
        super();
    }


    @Override
    public ArrayList<User> getAllUsers(){

        ArrayList<User> users = new ArrayList<>();

        // Get a connection using the superclass
        Connection conn = super.getConnection();
        // TRY to get a statement from the connection
        // When you are parameterizing the query, remember that you need
        // to use the ? notation (so you can fill in the blanks later)
        try (PreparedStatement ps = conn.prepareStatement("Select * from users")) {
            // TRY to execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Extract the information from the result set
                // Use extraction method to avoid code repetition!
                while(rs.next()){

                    User u = mapRow(rs);
                    users.add(u);
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
        return users;
    }


    @Override
    public int registerUser(User newUser){
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
        try(PreparedStatement ps = conn.prepareStatement("insert into users values(?, " +
                "?)")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, newUser.getUsername());
            ps.setString(2, hashPassword(newUser.getPassword()));

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
    public boolean checkIfUserExist(String username){
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
        try(PreparedStatement ps = conn.prepareStatement("SELECT * FROM users where username = ?")) {
            // Fill in the blanks, i.e. parameterize the update
            ps.setString(1, username);

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
    public User findUserByUsername(String username){

        User user = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{


            con = getConnection();

            String query = "SELECT * FROM users where username = ?";
            ps = con.prepareStatement(query);
            // Fill in the blanks, i.e. parameterize the query
            ps.setString(1, username);
            rs = ps.executeQuery();



            if(rs.next()){

                user = mapRow(rs);
            }


        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to prepare SQL for execution");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
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
                System.out.println("Exception occurred in the finally section of the method: " + e.getMessage());
            }
        }
        return user;
    }


    /**
     * Login user based on if username and password match whats in the hashmap
     * @param username is the username being searched
     * @param password is the password being searched
     * @return true if there is a match and false if no match
     */
    public boolean loginUser(String username, String password){

        boolean match = false;

        User u = findUserByUsername(username);


        if (u == null){

            match = false;
        }

        if (u != null){
            if (checkPassword(password, u.getPassword())){

                match = true;
            }
        }
        return match;
    }


    /**
     * Check if passwords are the same
     * @param password is the password being searched
     * @param confirmPassword is the password being searched
     * @return true if they match and false if they dont match
     */
    public boolean checkIfPasswordsAreTheSame(String password, String confirmPassword){

        boolean match = false;

        if (password.equals(confirmPassword)){

            match = true;
        }

        return match;
    }


    // check if password match regex

    /**
     * Check if password matches regex format
     * @param password is the password being searched
     * @param confirmPassword is the confirm password being searched
     * @return true if match and false if no match
     */
    public boolean checkIfPasswordsMatchRegex(String password, String confirmPassword){

        boolean match = false;

        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

        if (password.matches(pattern) && confirmPassword.matches(pattern)){

            match = true;
        }


        return match;
    }




    private static int workload = 12;

    /**
     * Hash the password based
     * @param password_plaintext is the password being hashed
     * @return hashed password
     */

    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password_plaintext, salt);


        return(hashed_password);
    }

    /**
     * Check password matches hash password
     * @param password_plaintext is the password being searched
     * @param stored_hash is the hashed password being searched
     * @return true if tehy match and false if they don't match
     */

    private static boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }



    /**
     * Search through each row in the user
     * @param rs is the query for user to be searched
     * @return the user information
     * @throws SQLException is username and email isn't unique
     */
    private User mapRow(ResultSet rs)throws SQLException {

        User u = new User(

                rs.getString("username"),
                rs.getString("password")
        );
        return u;
    }

}
