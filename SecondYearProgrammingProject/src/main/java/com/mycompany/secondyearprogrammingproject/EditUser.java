package com.mycompany.secondyearprogrammingproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 *
 * @author jason_000
 */
public class EditUser extends HttpServlet {
    private DocTemplate dt;
    private HttpSession session;
    public EditUser(){
        super();
        URL file = this.getClass().getResource("/header.html");
        dt = new DocTemplate(file);
    }

    /**
     * Processes requests for both HTTP <code>GET</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        boolean success = request.getParameter("success") != null;
        try (PrintWriter out = response.getWriter()) {
            
            String id = request.getParameter("id");
            
            session = request.getSession(); // get a session.
            
            dt.prepareNewDoc();
            int type = (int) session.getAttribute("type");
            
            if(id == null){
                out.println("You must supply an id");
                return;
            }
            if(type != 2)
                out.println("You do not have sufficient privilges to do this");
            else{
                Connection conn = null;
                conn = SimpleDataSource.getConnection();
                Statement stat = conn.createStatement();
                String query = "SELECT `username`, `email`, `type` FROM `user`"
                        + "WHERE `user_id` = " + id + ";";
                        
                ResultSet user = stat.executeQuery(query);
                if(!user.next())
                    out.println("User not found");
                else{
                    dt.replacePlaceholder("TITLE", "Edit User");
                    URL edit = this.getClass().getResource("/edit.html");
                    try {
                        String div = "";
                        if(success)
                            div = "<div class =\"ui positive message fadeOut\">"
                                + "     <p>User edited succesfully</p>"
                                + "</div>";
                        dt.replacePlaceholder("CONTENT", div + DocTemplate.getHTMLString(edit));
                        dt.replacePlaceholder("USERNAME", user.getString("username"));
                        dt.replacePlaceholder("EMAIL", user.getString("email"));
                        dt.replacePlaceholder("ID", id);
                        switch(user.getInt("type")){
                            case 2:
                                dt.replacePlaceholder("CHECKED2", " selected");
                                dt.replacePlaceholder("CHECKED1", "");
                                dt.replacePlaceholder("CHECKED0", "");
                                break;
                            case 1:
                                dt.replacePlaceholder("CHECKED2", "");
                                dt.replacePlaceholder("CHECKED1", " selected");
                                dt.replacePlaceholder("CHECKED0", "");
                                break;
                            default:
                                dt.replacePlaceholder("CHECKED2", "");
                                dt.replacePlaceholder("CHECKED1", "");
                                dt.replacePlaceholder("CHECKED0", " selected");
                                break;
                        }
                        out.print(dt.getTheDoc());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                conn.close();
                stat.close();
                user.close();
                     
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Processes requests for both HTTP <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processPostRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        PrintWriter out = response.getWriter();
        session = request.getSession();
        int type = (int)session.getAttribute("type");
        if(type != 2)
            out.print("Sorry you do not have permission to do that");
        else{
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String userType = request.getParameter("type");
            String id = request.getParameter("user_id");
            String query = "UPDATE `user` SET "
                    + "`email` = ?, "
                    + "`type` = ?, "
                    + "`username` = ?"
                    + "WHERE user_id = ?;";
            System.out.println(query);
            Connection conn = null;
            conn = SimpleDataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(query);
            stat.setString(1, email);
            stat.setInt(2, Integer.parseInt(userType));
            stat.setString(3, username);
            stat.setInt(4, Integer.parseInt(id));
            int changed = stat.executeUpdate();
            if(changed != 0)
                response.sendRedirect("EditUser?id="+id+"&success=1");
            else
                out.println("Oops, something went wrong please try again");
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
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
        try {
            processPostRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(EditUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
