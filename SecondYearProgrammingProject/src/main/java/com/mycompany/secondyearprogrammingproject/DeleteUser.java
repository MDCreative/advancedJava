package com.mycompany.secondyearprogrammingproject;

import java.io.IOException;
import java.io.PrintWriter;
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
 * The DeleteUser servlet, which allows for the removal of a user
 * 
 * @author Benjamin Williams <eeu222@bangor.ac.uk>
 * @author Jason Hall <eeu23e@bangor.ac.uk>
 * @author Liam Chapman <eeu239@bangor.ac.uk>
 * @author James Jackson <eeu203@bangor.ac.uk>
*/
public class DeleteUser extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            HttpSession session = request.getSession();
            if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }
            int type = (int) session.getAttribute("type");
            String idHolder = (String)request.getParameter("id");
            String sessIdHolder = (String) session.getAttribute("id");
            if(idHolder.equals(sessIdHolder)){
                out.print("You cannot delete yourself");
                return;
            }
            if(type < 2){
                out.print("You cannot do that");
            }
            Connection conn = null;
            Statement stat = null;
            ResultSet result = null;
            String rows = "";
            try {
                SimpleDataSource.init("/database.properties");
                conn = SimpleDataSource.getConnection();
                stat = conn.createStatement();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM `user` WHERE `user_id` = ?;");
                ps.setInt(1, Integer.parseInt(request.getParameter("id")));
                int affected = ps.executeUpdate();
                if(affected > 0)
                    response.sendRedirect("MembersArea?deleted=1#/users");
                
            } catch (SQLException ex) {
                Logger.getLogger(DeleteWord.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DeleteWord.class.getName()).log(Level.SEVERE, null, ex);
            }
        
                
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
