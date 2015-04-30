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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author jason_000
 */
public class MembersArea extends HttpServlet {
    private DocTemplate dt;
    private HttpSession session;
    public MembersArea(){
        super();
        URL file = this.getClass().getResource("/header.html");
        dt = new DocTemplate(file);
        
    }

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
        boolean success = request.getParameter("success") != null;
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            
            session = request.getSession(); // get a session.
            dt.prepareNewDoc();
            int type = (int) session.getAttribute("type");
            if(type == 0){ // Student
                dt.replacePlaceholder("TITLE", "Student Lobby");
                dt.replacePlaceholder("CONTENT", "Content Here");
               
            }else if(type == 1){ // instructor
                dt.replacePlaceholder("TITLE", "Instructor Lobby");
                dt.replacePlaceholder("CONTENT", "Content Here");
            }else if(type == 2 || type == 3){ // administrator
                String div = "";
                if(success)
                    div = "<div class =\"ui positive message fadeOut\">"
                        + "     <p>User added succesfully</p>"
                        + "</div>";
                dt.replacePlaceholder("CONTENT", div + buildAdminDocument());
            }
            out.print(dt.getTheDoc());  
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String buildAdminDocument(){
        dt.replacePlaceholder("TITLE", "Administrator Lobby");
        URL regStr = this.getClass().getResource("/register.html");
        
        String admin = ""
                + "<div class=\"ui tabular menu\">\n"
                + "     <div class=\"item\" data-tab=\"register\">Register Users</div>\n"
                + "     <div class=\"item\" data-tab=\"users\">Users</div>\n"
                + "     <div class=\"item\" data-tab=\"words\">Words</div>\n"
                + "</div>";
        admin += "<div class=\"ui tab\" data-tab=\"register\">";        
        try {
            admin += DocTemplate.getHTMLString(regStr);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        admin += "</div>";
        admin += "<div class=\"ui tab\" data-tab=\"users\">";
        admin += "<table class='ui table segment'>";
        admin += "<thead><th></th><th>Username</th><th>Type</th>"+
                "<th>Email</th></thead>";
        admin += getMembers((String)session.getAttribute("id"))+"</table>";
        admin += "</div>";
        admin += "<div class=\"ui tab\" data-tab=\"words\">";
        admin += getWords();
        admin += "</div>";
        admin += "<script type=\"text/javascript\">$('.tabular.menu .item').tab();</script>";
        return admin;
    }
    
    private String getMembers(String id){
        System.out.println("********** HERE **********");
        try {
            SimpleDataSource.init("/database.properties");
        } catch (IOException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        Connection conn = null;
        Statement stat = null;
        ResultSet result = null;
        String rows = "";
        try {
            try{
            conn = SimpleDataSource.getConnection();
            stat = conn.createStatement();
            result = stat.executeQuery("SELECT SQL_NO_CACHE * FROM `user`;");
            while(result.next()){ // if not already a login
                String name = result.getString("username");
                String type = result.getString("type");
                String email = result.getString("email");
                String user_id = result.getString("user_id");
                String classer = (user_id.equals(id))? " class=\"active\"" : "";
                String row = "<tr"+classer+"><td>"
                        + "<a href=\"EditUser?id="+user_id+"\"><i class=\"write icon\"></i></a>"
                        + "<a href=\"TestHistoryServlet?id="+user_id+"\"><i class=\"student icon\"></i></a></td>"
                        + "<td>" + name +"</td>";
                String typeStr = "";
                switch(Integer.parseInt(type)){
                    case 2:
                        typeStr = "Administrator";
                        break;
                    case 1:
                        typeStr = "Instructor";
                        break;
                    default:
                        typeStr = "User";
                }
                row += "<td>"+typeStr+"</td>";
                row += "<td>"+email+"</td></tr>";
                rows += row;
            }
            System.out.println("********** HERE2 **********");
            } finally {
                if(result != null)result.close();
                if(stat != null)stat.close();
                if(conn != null)conn.close();
            }
            return rows;
        } catch (SQLException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            
            
        }
        return null;
    }
    
    private String getWords(){
        String words = null;
        Connection conn = null;
        Statement stat = null;
        ResultSet result = null;
        String rows = "";
        try {
            
            SimpleDataSource.init("/database.properties");
            
            conn = SimpleDataSource.getConnection();
            String[] genders = GenderIdentifier.getGenders(conn);
            String query = "SELECT * FROM `word`";
            String catQuery = "SELECT `name` FROM `category`";
            stat = conn.createStatement();
            result = stat.executeQuery(catQuery);
            int size = 0;
            int i = 0;
            result.last();
            size = result.getRow();
            result.beforeFirst();
            String[] catNames = new String[size];
            while(result.next()){
                catNames[i] = result.getString("name");
                i++;
            }
            result = stat.executeQuery(query);
            
            while(result.next()){
                String row = "";
                row += "<tr>\n"
                        + "<td>\n\t"
                        + "     <a href=\"EditWord?id=" + result.getInt("id") + "\">\n\t\t"
                        + "         <i class=\"ui icon write\"></i>\n\t"
                        + "     </a>\n"
                        + "</td>\n"
                        + "<td>" + result.getString("word") + "</td>"
                        + "<td>" + result.getString("translation") + "</td>"
                        + "<td>" + catNames[result.getInt("category") - 1] + "</td>"
                        + "<td>" + genders[result.getInt("gender")] + "</td>"
                    +  "</tr>";
                rows += row;
            }
            words = rows;
            conn.close();
            result.close();
            String tableHead = ""
                    + "<table class=\"ui table segment display\" id=\"wordsTable\">"
                    + "     <thead>"
                    + "         <tr>"
                    + "             <th></th>"
                    + "             <th class=\"searchMe\">German Word</th>"
                    + "             <th class=\"searchMe\">English Word</th>"
                    + "             <th class=\"searchMe\">Category</th>"
                    + "             <th>Gender</th>"
                    + "         </tr>"
                    + "     </thead>"
                    + "     <tfoot>"
                    + "         <tr>"
                    + "             <th></th>"
                    + "             <th>German Word</th>"
                    + "             <th>English Word</th>"
                    + "             <th>Category</th>"
                    + "             <th>Gender</th>"
                    + "         </tr>"
                    + "     </tfoot>"
                    + "     <tbody>";
            
            String tableFoot = ""
                    + "     </tbody>"
                    + "</table>";
            return tableHead + words + tableFoot;
        } catch (IOException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
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
