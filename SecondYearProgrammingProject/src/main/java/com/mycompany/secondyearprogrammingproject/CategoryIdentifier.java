/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.secondyearprogrammingproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacko
 */
public class CategoryIdentifier {
    /**
     * Static method to return the gender string of a given id between 0 and 5 inclusive
     * @param gender the id of the gender (0 - 5)
     * @return the string representation of the gender
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
