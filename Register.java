package com.mycompany.secondyearprogrammingproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author jason_000
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
        response.setContentType("text/html");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passConf = request.getParameter("passwordconf");
        String email = request.getParameter("email");
        
        Boolean nameNotSupplied = "".equals(username);
        Boolean passNotSupplied = "".equals(password);
        Boolean passConfNotSupplied = "".equals(passConf);
        Boolean emailNotSupplied = "".equals(email);
        
        PrintWriter out = response.getWriter();
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
                ResultSet result = stat.executeQuery("SELECT * FROM `user` WHERE `username` = \"" + username + "\";");
                if(!result.next()){ // if not already a login
                     stat.executeUpdate("INSERT INTO `user` (username, password, type) VALUES (\""+ username + "\",\"" + password + "\"," + "0" + ");");
                }else{ // if it is already a login
                    out.println("IMPOSSIBRU!");
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
