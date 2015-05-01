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
 * Servlet to generate test history for a specific user.
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class TestHistoryServlet extends HttpServlet
{
    private final int SPECIAL_USER_LIST_ALL = -3;
    private final int SPECIAL_USER_INVALID  = -1;
            
    /**
     * Constructs an instance of the test history servlet.
     */
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
        //Get the writer and session
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }
        
        //Set up session id attempt string and numeric id
        String sessionID = "";
        int sessID = SPECIAL_USER_INVALID;
        
        if(session != null)
        {
            //There is a session, try and get their user id:
            sessionID = (String)session.getAttribute("id");
            
            //If it's not null, try parse it
            if(sessionID != null)
                sessID = Integer.parseInt(sessionID);
        }

        //Set up get id attempt string and numeric id
        String strGetID = request.getParameter("id");
        int getID = SPECIAL_USER_INVALID;
        
        //If the parameter is specified, try and parse it into a number
        if(strGetID != null)
            getID = Integer.parseInt(strGetID);
        
        //Set the content type to html
        response.setContentType("text/html");
        
        //Set up the template to work with and the output
        DocTemplate template = new DocTemplate(this.getClass().getResource("/testHistory.html"));
        template.prepareNewDoc();
        String formattedRows = "";
        
        //Try and get their user type
        int type = Integer.parseInt(session.getAttribute("type").toString());
        
        //If no get param is specified, but their session is, default to session id
        if(getID == SPECIAL_USER_INVALID && sessID != SPECIAL_USER_INVALID)
        {
            //Set to session id if they're a normal user, otherwise use special value
            if(type == 0)
                getID = sessID;
            else
                getID = SPECIAL_USER_LIST_ALL;
        }
        
        //If get id or session id is still not specified, output that there was issues.
        if(getID == SPECIAL_USER_INVALID || sessID == SPECIAL_USER_INVALID)
        {
            template.replacePlaceholder("tableHeader", "<table>");
            template.replacePlaceholder("tableData", "Nobody is logged in!");
            out.println(template.getTheDoc());
            return;
        }
        
        if(getID != sessID)
        {
            if(type == 0)
            {
                //If they're trying to access someone's history, but they're not staff, show error
                template.replacePlaceholder("tableHeader", "<table>");
                template.replacePlaceholder("tableData", "You are not authorized to view this member's history.");
                out.println(template.getTheDoc());
                return;
            }
        }
        
        //Run through each generated row and append to the builder string.
        for(String row : getTestHistoryForUser(getID, type))
            formattedRows += row + "\n";
        
        //And finally generate the table and print it
        template.replacePlaceholder("tableHeader", makeTableHeader(type));
        template.replacePlaceholder("tableData", formattedRows);
        out.print(template.getTheDoc());
                
    }
    
    /**
     * Constructs the header content for tables.
     * 
     * @param type The integer which represents whether the user is a student, instructor or an admin.
     * @return The table header.
     */
    private String makeTableHeader(int type)
    {        
        String columnSize = "three";
        
        if(type > 0)
            columnSize = "five";
            
        String builtString =
                
        "<table class=\"ui " + columnSize + " column celled striped table\">" +
        "<thead>" +
            "<tr>" +
                ((type > 0) ? (
                    "<th>User ID <i class=\"right floated user icon\"></i></th>" +
                    "<th>Quiz ID <i class=\"right floated code icon\"></i></th>"
                ) : ("")) + 
                "<th>Score <i class=\"right floated trophy icon\"></i></th>" +
                "<th>Date <i class=\"right floated calendar icon\"></i></th>" +
                "<th>History <i class=\"right floated book icon\"></i></th>" +
            "</tr>" +
        "</thead>";
        
        return builtString;       
    }
    
    /**
     * Obtains a specific users test history if access rights allows it.
     * @param userid the identifier of the specific user.
     * @param type The integer which represents whether the user is a student, instructor or an admin.
     * @return The test history for a specific user.
     */
    private String[] getTestHistoryForUser(int userid, int type)
    {
        //Set up connection, statement and result set vars
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        //We don't know how many results are coming back, so use a list
        ArrayList<String> testResults = new ArrayList<String>();
                
        try
        {
            //Connect and prepare statement for execution - bind params
            connection = SimpleDataSource.getConnection();
            
            if(userid == SPECIAL_USER_LIST_ALL)
            {
                //If they want to list everything, and they're a user, then skip
                if(type == 0)
                    return null;
                
                //Otherwise show everything
                statement = connection.prepareStatement("SELECT * FROM `test_history` ORDER BY `date` DESC;");
            }
            else
            {
                //Otherwise, just show results for this user
                statement = connection.prepareStatement("SELECT * FROM `test_history` WHERE `user_id` = ? ORDER BY `date` DESC;");
                statement.setInt(1, userid);
            }
            
            //Execute the query
            rs = statement.executeQuery();
            
            while(rs.next())
            {
                //Loop through each row, grab each part of it:
                int user_id  = rs.getInt("user_id");
                int test_id  = rs.getInt("test_id");
                int score    = rs.getInt("score");
                Timestamp date = rs.getTimestamp("date");

                //Add the row into the list 
                testResults.add(
                    "<tr>" +
                            
                        //If the user is staff:
                        ((type > 0) ? (
                        "<td>" + user_id + "</td>" +
                        "<td>" + test_id + "</td>"
                        ) : ("")) + 
                        
                        //Anyone:
                        "<td>" + score   + "/20 (" + Math.round(score / 20.0 * 100.0) + "%) </td>" +
                        "<td>" + date.toString() + "</td>" +
                        "<td><a href=\"ViewQuiz?id=" + test_id + "\">" +
                            "<div class=\"ui right labeled icon blue small button\">"
                                + "<i class=\"ui angle right icon\"></i>" 
                                + "View Summary" + 
                            "</div>" +
                            "</a></td>" +
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
        
        //Return a string array the size of the list
        return testResults.toArray(new String[testResults.size()]);
    }
}
