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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import static javax.servlet.SessionTrackingMode.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author jason_000
 */
public class MembersArea extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            URL file = this.getClass().getResource("/header.html");
            DocTemplate dt = new DocTemplate(file);
            
            HttpSession session = request.getSession(); // get a session.
            
            int type = (int) session.getAttribute("type");
            if(type == 0){ // Student
                dt.replacePlaceholder("TITLE", "Student Lobby");
                dt.replacePlaceholder("CONTENT", "Content Here");
               
            }else if(type == 1){ // instructor
                dt.replacePlaceholder("TITLE", "Instructor Lobby");
                dt.replacePlaceholder("CONTENT", "Content Here");
            }else if(type == 2 || type == 3){ // administrator
                dt.replacePlaceholder("TITLE", "Administrator Lobby");
                URL regStr = this.getClass().getResource("/register.html");
                String admin = DocTemplate.getHTMLString(regStr);
                admin += "<table>"+getMembers()+"</table>";
                dt.replacePlaceholder("CONTENT", admin);
            }
            System.out.println(dt.getTheDoc());
            out.print(dt.getTheDoc());  
            
        } catch (URISyntaxException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getMembers(){
        try {
            SimpleDataSource.init("/database.properties");
        } catch (IOException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn;
        try {
            conn = SimpleDataSource.getConnection();
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("SELECT * FROM `user`;");
            String rows = "";
            while(result.next()){ // if not already a login
                String name = result.getString("username");
                String type = result.getString("type");
                String email = result.getString("email");
                String row = "<tr><td>"+name+"</td><td>"+type+"</td><td>"+email+"</td></tr>";
                rows += row;
            }
            conn.close();
            return rows;
        } catch (SQLException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
        processRequest(request, response);
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
        processRequest(request, response);
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
