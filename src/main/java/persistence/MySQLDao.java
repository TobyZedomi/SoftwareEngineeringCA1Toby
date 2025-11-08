package persistence;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Properties;


public class MySQLDao {
    private Properties properties;
    private Connection conn;
    private String databaseName = "software_engineering";

    public MySQLDao(){
    }

    public MySQLDao(Connection conn){
        this.conn = conn;
    }
    public MySQLDao(String propertiesFilename){
        properties = new Properties();
        try {
            // Get the path to the specified properties file
            String rootPath = Thread.currentThread().getContextClassLoader().getResource(propertiesFilename).getPath();
            // Load in all key-value pairs from properties file
            properties.load(new FileInputStream(rootPath));
        }catch(IOException e){
            System.out.println("An exception occurred when attempting to load properties from \"" + propertiesFilename + "\": " + e.getMessage());
            e.printStackTrace();
        }

    }



    public Connection getConnection(){

        if(conn != null){
            return conn;
        }

        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password", "");
        try{
            Class.forName(driver);

            try{
                Connection conn = DriverManager.getConnection(url+database, username, password);
                return conn;
            }catch(SQLException e){
                System.out.println(LocalDateTime.now() + ": An SQLException  occurred while trying to connect to the " + url +
                        "database.");
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }catch(ClassNotFoundException e){
            System.out.println(LocalDateTime.now() + ": A ClassNotFoundException occurred while trying to load the MySQL driver.");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public void freeConnection(Connection con){
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred when attempting to free connection to database.");
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}