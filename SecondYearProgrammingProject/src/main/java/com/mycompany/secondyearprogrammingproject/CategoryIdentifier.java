
package com.mycompany.secondyearprogrammingproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The CategoryIdentifier servlet, which returns a representation of a category
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class CategoryIdentifier {
    /**
     * Static method to return the category string of a given id 
     * @param category the id of the category
     * @param conn connection to database
     * @return the string representation of the category
     */
    public static String getCategory(int category, Connection conn){
        try {
            Statement stat = conn.createStatement();
            PreparedStatement categoryStmnt = conn.prepareStatement("SELECT `name` FROM `category`"
                    + " WHERE id = ?;");
            categoryStmnt.setInt(1, category);
            ResultSet categoryRes = categoryStmnt.executeQuery();
            if(!categoryRes.next())
                throw new IllegalArgumentException();
            
            return categoryRes.getString("name");
        } catch (SQLException ex) {
            Logger.getLogger(CategoryIdentifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
   /**
     * Static method to return the category array of a given id 
     * @param category the id of the category
     * @param conn connection to database
     * @return the array representation of the category
     */
    public static String[] getCategories(Connection conn){
        try {
            Statement stat = conn.createStatement();
            ResultSet categoryRes = stat.executeQuery("SELECT `name` FROM `category`");
            if(!categoryRes.next())
                throw new IllegalArgumentException();
            int size = 0;
            int i = 0;
            categoryRes.last();
            size = categoryRes.getRow();
            categoryRes.beforeFirst();
            String[] categoryNames = new String[size];
            while(categoryRes.next()){
                categoryNames[i] = categoryRes.getString("name");
                i++;
            }
            
            return categoryNames;
        } catch (SQLException ex) {
            Logger.getLogger(CategoryIdentifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
