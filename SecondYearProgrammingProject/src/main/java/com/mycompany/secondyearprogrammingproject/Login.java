package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The Login servlet,which builds the login page
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class Login extends HttpServlet {

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Boolean nameNotSupplied = "".equals(username);
        Boolean passNotSupplied = "".equals(password);
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login</title>");
        out.println("</head>");
        out.println("<body>");
        if (nameNotSupplied || passNotSupplied) {
            out.println("<p>Please supply a username and password</p>");
        } else {
            //out.println("<p>Hello "+request.getParameter("username")+". Here are the facilities you can use:</p>");
            Connection conn = null;
            try {
                // DB check stuff
                SimpleDataSource.init("/database.properties");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                conn = SimpleDataSource.getConnection();
                Statement stat = conn.createStatement();
                ResultSet result = stat.executeQuery("SELECT * FROM `user` "
                        + "WHERE `username` = \"" + username + "\""
                        + "AND `password` =\""+ Security.getHash(password) +"\";");
                if (result.next()) { // if already a login
                    int userType = result.getInt("type");
                    String id = result.getString("user_id");
                    HttpSession session = request.getSession(); // creates a session.
                    session.setAttribute("type", userType); 
                    session.setAttribute("username", username); 
                    session.setAttribute("id", id); 
                    session.setAttribute("email", result.getString("email"));
                    response.sendRedirect("MembersArea"); // goes to members area to check type.
                } else { // if it is not already a login
                    response.sendRedirect("index.html#failed");
                }
                conn.close();
                stat.close();
                result.close();
            } catch (SQLException e) {

            }
        }
        out.println("</body>");
        out.println("</html>");
    }
    
    
    
    
    
    
 }
