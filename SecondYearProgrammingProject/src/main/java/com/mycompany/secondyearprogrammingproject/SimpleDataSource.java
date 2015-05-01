package com.mycompany.secondyearprogrammingproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The SimpleDataSource servlet,which makes the connection to the DB
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class SimpleDataSource {

    private static String url;
    private static String usr;
    private static String pwd;
    private static boolean initialized;
    
    /**
     * Returns true when it is initialized
     * 
     * @return initialized boolean 
     */
    public static boolean isInitialized()
    {
        return initialized;
    }
    
  public static void init(String fileName) throws IOException, ClassNotFoundException 
    {
        Properties props = new Properties();
        InputStream in = SimpleDataSource.class.getResourceAsStream(fileName);
        props.load(in);
        String driver = props.getProperty("jdbc.driver");
        url = props.getProperty("jdbc.url");
        usr = props.getProperty("jdbc.username");
        pwd = props.getProperty("jdbc.password");
        
        if (driver != null) {
            Class.forName(driver);
        }
        
        initialized = true;
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usr, pwd);
    }
}
