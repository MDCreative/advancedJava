package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A servlet to view the results of a specific quiz.
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 */
public class ViewQuiz extends HttpServlet 
{
    private final int SPECIAL_USER_INVALID = -1;
    
    /**
     * Constructs a new servlet to view the results of a specified quiz.
     */
    public ViewQuiz()
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
        //Get writer for outputting html
        PrintWriter out = response.getWriter();
        
        //Attempt session id
        int sessId = SPECIAL_USER_INVALID;
        
        //Get this user's session
        HttpSession session = request.getSession();
        
        if(session != null)
        {
            //Grab the user id associated with this session.
            String strId = (String)session.getAttribute("id");
            
            //If it's not null, try and parse it
            if(strId != null)
                sessId = Integer.parseInt(strId);
        }
       
        //Prepare the template and output variable
        DocTemplate template = new DocTemplate(this.getClass().getResource("/results.html"));
        template.prepareNewDoc();
        String formattedRows = "";
        
        if(sessId == SPECIAL_USER_INVALID)
        {
            //Nobody is logged in, so error
            template.replacePlaceholder("resultsData", "You are not logged in!");
            out.println(template.getTheDoc());
            return;
        }
        
        //Get the requested test id
        String strTestId = request.getParameter("id");
        
        if(strTestId == null)
        {
            //If it's not specified, error:
            template.replacePlaceholder("resultsData", "No question is specified.");
            out.println(template.getTheDoc());
            return;
        }
        
        //Try and grab the user's type, and try and parse the test id.
        int type = Integer.parseInt(session.getAttribute("type").toString());
        int testId = Integer.parseInt(strTestId);
        
        //Get the test data JSON
        String data = getTestData(sessId, testId, type);
        
        if(data == null)
        {
            //If it returned null, then they weren't authorized:
            template.replacePlaceholder("resultsData", "You cannot view these results!");
            out.println(template.getTheDoc());
            return;
        }
        
        //Replace JSON and score div
        template.replacePlaceholder("questionData", data.replace("'", "\\'"));
        template.replacePlaceholder("resultsData", "<p>You scored <span class=\"result-text\"></span>. Your results are as follows:</p>");
        
        //And output the page
        out.println(template.getTheDoc());
    }
    
    /**
     * Gets the test data JSON for a specific test id.
     * 
     * @param sessId The user's id from the session.
     * @param testId The test id that is requested.
     * @param type The type of user that requested.
     * @return JSON data to be put into the page and parsed
     */
    private String getTestData(int sessId, int testId, int type)
    {
        //Set up database vars
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        try
        {
            //Grab a connection, set up a statement 
            connection = SimpleDataSource.getConnection();
            statement = connection.prepareStatement("SELECT `user_id`, `data` FROM `test_history` WHERE `test_id` = ?;");
            
            //Bind params to prepared statement.
            statement.setInt(1, testId);
            
            //Execute query and grab singular (unique) row from result set
            rs = statement.executeQuery();
            rs.next();
            
            //Get the user id this question is associated with, to test
            int user_id = rs.getInt("user_id");
            
            //If the user's id is not the one in the session, and they're not a member of staff, then
            //they are not authorized:
            if(user_id != sessId && type == 0)
                return null;
            
            //Get the JSON data and return it.
            String data = rs.getString("data");
            return data;
        }
        catch(SQLException e)
        {
            System.out.println("[error] Couldn't query the database:");
            System.out.println(e.getMessage());
            return null;
        }
        finally
        {
            try
            {
                connection.close();
                statement.close();
                rs.close();
            }
            catch(SQLException e)
            {
                System.out.println("[error] Failed to close the connection, statement or result set objects.");
                return null;
            }
        }
    }

}
