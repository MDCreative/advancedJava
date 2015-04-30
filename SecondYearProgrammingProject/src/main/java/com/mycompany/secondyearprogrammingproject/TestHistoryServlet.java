/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 */
public class TestHistoryServlet extends HttpServlet
{
    public TestHistoryServlet()
    {
        if(SimpleDataSource.isInitialized())
            return;
        
        try
        {            
            //Try and initialize the database from the properties file.
            SimpleDataSource.init("/database.properties");
        }
        catch(IOException | ClassNotFoundException e)
        {
            System.out.println("[error] Couldn't initialize the database properties file.");
            System.out.println(e.getMessage());
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession();
        
        int id = 123;
        
        if(session != null)
        {
            String strid = (String)session.getAttribute("id");
            
            if(strid != null)
                id = Integer.parseInt(strid);
        }
                
        response.setContentType("text/html");
        
        DocTemplate template = new DocTemplate(this.getClass().getResource("/testHistory.html"));
        
        String formattedRows = "";
        
        for(String row : getTestHistoryForUser(id))
            formattedRows += row + "\n";
        
        template.replacePlaceholder("tableData", formattedRows);
        out.print(template.getTheDoc());
                
    }
    
    private String[] getTestHistoryForUser(int userid)
    {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        ArrayList<String> testResults = new ArrayList<String>();
                
        try
        {
            connection = SimpleDataSource.getConnection();
            
            statement = connection.prepareStatement("SELECT * FROM `test_history` WHERE `user_id` = ? ORDER BY `date` DESC;");
            
            statement.setInt(1, userid);
            
            rs = statement.executeQuery();
            
            while(rs.next())
            {
                int test_id  = rs.getInt("test_id");
                int score    = rs.getInt("score");
                Timestamp date = rs.getTimestamp("date");
                String data  = rs.getString("data");
                
                testResults.add(
                    "<tr>" +
                        "<td>" + test_id + "</td>" +
                        "<td>" + score   + "/20 (" + Math.round(score / 20.0 * 100.0) + "%) </td>" +
                        "<td>" + date.toLocalDateTime().toLocalDate() + ", " +
                                 date.toLocalDateTime().toLocalTime() + "</td>" +
                    "</tr>"
                );
            }
        }
        catch(SQLException e)
        {
            System.out.println("[error] Failed to insert quiz data into the database: ");
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
                connection.close();
                statement.close();
            }
            catch(SQLException e)
            {
                System.out.println("[error] Failed to close connection or statement objects.");
            }
        }
        
        return testResults.toArray(new String[testResults.size()]);
    }
}
