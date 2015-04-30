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
public class GenderIdentifier {
    /**
     * Static method to return the gender string of a given id between 0 and 5 inclusive
     * @param gender the id of the gender (0 - 5)
     * @return the string representation of the gender
     */
    public static String getGender(int gender, Connection conn){
        try {
            Statement stat = conn.createStatement();
            PreparedStatement genderStmnt = conn.prepareStatement("SELECT `name` FROM `gender`"
                    + " WHERE id = ?;");
            genderStmnt.setInt(1, gender);
            ResultSet genderRes = genderStmnt.executeQuery();
            if(!genderRes.next())
                throw new IllegalArgumentException();
            
            return genderRes.getString("name");
        } catch (SQLException ex) {
            Logger.getLogger(GenderIdentifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String[] getGenders(Connection conn){
        try {
            Statement stat = conn.createStatement();
            ResultSet genderRes = stat.executeQuery("SELECT `name` FROM `gender`");
            if(!genderRes.next())
                throw new IllegalArgumentException();
            int size = 0;
            int i = 0;
            genderRes.last();
            size = genderRes.getRow();
            genderRes.beforeFirst();
            String[] genNames = new String[size];
            while(genderRes.next()){
                genNames[i] = genderRes.getString("name");
                i++;
            }
            
            return genNames;
        } catch (SQLException ex) {
            Logger.getLogger(GenderIdentifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
