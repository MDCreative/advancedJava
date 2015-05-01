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
public class AddWord extends HttpServlet {
    private DocTemplate dt;
    private HttpSession session;
    public AddWord(){
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
            
            
            session = request.getSession(); // get a session.
            if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }
            
            dt.prepareNewDoc();
            int type = (int) session.getAttribute("type");
            
            
            
            if(type == 1 || type == 2){
                Connection conn = null;
                try {
                    SimpleDataSource.init("/database.properties");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AddWord.class.getName()).log(Level.SEVERE, null, ex);
                }
                conn = SimpleDataSource.getConnection();
                
                    dt.replacePlaceholder("TITLE", "Add Word");
                    
                    URL edit = this.getClass().getResource("/editWord.html");
                    try {
                        String div = "";
                        if(success)
                            div = "<div class =\"ui positive message fadeOut\">"
                                + "     <p>Word edited succesfully</p>"
                                + "</div>";
                        
                        dt.replacePlaceholder("CONTENT", div + DocTemplate.getHTMLString(edit));
                        dt.replacePlaceholder("ACTION", "AddWord");
                        dt.replacePlaceholder("ENGLISH", "");
                        dt.replacePlaceholder("GERMAN", "");
                        dt.replacePlaceholder("ID", "");
                        
                        
                        String[] genders = GenderIdentifier.getGenders(conn);
                        String[] categories = CategoryIdentifier.getCategories(conn);
                        int i = 0;
                        String genderOptions = "";
                        for(String genderStr: genders){
                            genderOptions += "<option value=\""+ i++ +"\" "
                                    + ">"+genderStr+"</option>";
                        }
                        dt.replacePlaceholder("GENDERS", genderOptions);
                        
                        i = 0;
                        String categoryOptions = "";
                        for(String catStr: categories){
                            
                            categoryOptions += "<option value=\""+ i++ +"\" "
                                    + ">"+catStr+"</option>";
                        }
                        dt.replacePlaceholder("CATEGORIES", categoryOptions);
                        
                        out.print(dt.getTheDoc());
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(EditWord.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                conn.close();
                
                     
            }else
                out.println("You do not have sufficient privilges to do this");
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EditWord.class.getName()).log(Level.SEVERE, null, ex);
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
        
        if(type == 2 || type == 1){
            String english = request.getParameter("english");
            String german = request.getParameter("german");
            String category = request.getParameter("category");
            String gender = request.getParameter("gender");
            String query = ""
                    + "INSERT INTO `word` (`translation`,`word`,`category`,"
                    + "`gender`) " 
                    + "VALUES (?, ?, ?, ?)";
            System.out.println(query);
            Connection conn = null;
            conn = SimpleDataSource.getConnection();
            PreparedStatement stat = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, english);
            stat.setString(2, german);
            stat.setInt(3, Integer.parseInt(category) + 1);
            stat.setInt(4, Integer.parseInt(gender));
            System.out.println("***********" + stat);
            int changed = stat.executeUpdate();
            ResultSet rs = stat.getGeneratedKeys();
            int last_inserted_id = 0;
            if(rs.next())
                last_inserted_id = rs.getInt(1);
            if(changed != 0)
                response.sendRedirect("EditWord?id="+last_inserted_id+"&success=2");
            else
                out.println("Oops, something went wrong please try again");
        } else
            out.print("Sorry you do not have permission to do that");
       
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
