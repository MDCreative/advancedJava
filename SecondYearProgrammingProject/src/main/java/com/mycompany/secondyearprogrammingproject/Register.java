package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
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
 * The Register servlet, which allows for the addition of a user
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class Register extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
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
        HttpSession session = request.getSession();
        if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }
        response.setContentType("text/html");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passConf = request.getParameter("passwordconf");
        String email = request.getParameter("email");
        String usertype = request.getParameter("type");
        
        Boolean nameNotSupplied = "".equals(username);
        Boolean passNotSupplied = "".equals(password);
        Boolean passConfNotSupplied = "".equals(passConf);
        Boolean emailNotSupplied = "".equals(email);
        
        PrintWriter out = response.getWriter();
        
        int type = (int) session.getAttribute("type");
        if(type != 2){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("You cannot do that!!!!");
        } else {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Register</title>");
        out.println("</head>");
        out.println("<body>");
        if (nameNotSupplied || passNotSupplied || emailNotSupplied || passConfNotSupplied) {
            out.println("<p>Please supply all credentials</p>");
        } else {
            try {
                // DB check stuff
                SimpleDataSource.init("/database.properties");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Connection conn = null;
            try{
                
                conn = SimpleDataSource.getConnection();
                Statement stat = conn.createStatement();
                
                // look up user
                ResultSet resultUsername = stat.executeQuery("SELECT * FROM `user` WHERE "
                        + "`username` = \"" + username + "\";");
                Boolean un = resultUsername.next();
                ResultSet resultEmail = stat.executeQuery("SELECT * FROM `user` WHERE "
                        + "`email` = \"" + email + "\";");
                Boolean em = resultEmail.next();
                Boolean both = un && em;
                if(both)
                    out.println("Sorry this username and email is taken please go back and re-enter a different username.");
                else if(un)
                    out.println("Sorry this username is taken please go back and re-enter a different username.");
                else if(em)
                    out.println("Sorry this email is taken please go back and re-enter a different username.");
                else{
                    stat.executeUpdate("INSERT INTO `user` (username, email, password, type) "
                            + "VALUES (\""+ username + "\",\""+ email + "\",\"" + Security.getHash(password) 
                            + "\"," + usertype + ");");
                    response.sendRedirect("MembersArea#/users?success=1");
                }
                
                
            } catch (SQLException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            out.println(
                    "</body>");
            out.println(
                    "</html>");
        }
        }
    }
}
