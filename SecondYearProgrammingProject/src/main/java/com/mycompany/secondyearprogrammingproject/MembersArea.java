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

    public MembersArea() {
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
        String deleted = request.getParameter("deleted");
        String div = "";

        try (PrintWriter out = response.getWriter()) {
            session = request.getSession(); // get a session.
            if (session.getAttribute("type") == null) {
                response.sendRedirect("index.html#failed");
                return;
            }

            dt.prepareNewDoc();
            int type = (int) session.getAttribute("type");
            if (type == 0) { // Student
                dt.replacePlaceholder("TITLE", "Student Lobby");
                dt.replacePlaceholder("CONTENT", buildStudentDocument());
            } else if (type == 2 || type == 1) { // administrator
                if (deleted != null) {
                    div = "<div class =\"ui positive message fadeOut\">"
                            + "     <p>"
                            + "Deletion successful"
                            + "</p>"
                            + "</div>";
                }
                if (success) {
                    div = "<div class =\"ui positive message fadeOut\">"
                            + "     <p>User added succesfully</p>"
                            + "</div>";
                }
                dt.replacePlaceholder("CONTENT", div + buildAdminDocument(type));
            }
            out.print(dt.getTheDoc());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String buildStudentDocument() {
        dt.replacePlaceholder("TITLE", "Student Lobby");
        String sLobby = ""
                + "<div class=\"ui tabular menu\">\n"
                + "   <div class=\"item\" data-tab=\"profile\">Profile</div>\n"
                + "   <div class=\"item\" data-tab=\"words\">Words</div>\n"
                + "</div>"
                + buildProfile()
                + "<div class=\"ui tab\" data-tab=\"words\">"
                + getWords(false)
                + "</div>"
                + "<script type=\"text/javascript\">$('.tabular.menu .item').tab();</script>";
        return sLobby;
    }

    private String buildProfile() {
        try {
            URL profileEdit = this.getClass().getResource("/editProfile.html");
            String profileEditStr = DocTemplate.getHTMLString(profileEdit);
            profileEditStr = DocTemplate.replacePlaceholder("USERNAME",
                    (String) session.getAttribute("username"), profileEditStr);
            profileEditStr = DocTemplate.replacePlaceholder("ID",
                    (String) session.getAttribute("id"), profileEditStr);
            profileEditStr = DocTemplate.replacePlaceholder("EMAIL",
                    (String) session.getAttribute("email"), profileEditStr);
            return "<div class=\"ui tab\" data-tab=\"profile\">"
                    + profileEditStr
                    + "</div>";
        } catch (URISyntaxException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String buildAdminDocument(int type) {
        if (type == 2) {
            dt.replacePlaceholder("TITLE", "Administrator Lobby");
        } else if (type == 1) {
            dt.replacePlaceholder("TITLE", "Instructor Lobby");
        }
        String adminMenu = "";
        String admin = "";
        String registrations = "";
        if (type == 2) {
            URL regStr = this.getClass().getResource("/register.html");
            adminMenu += "<div class=\"item\" data-tab=\"register\">Register Users</div>\n";

            try {
                registrations += "<div class=\"ui tab\" data-tab=\"register\">";
                registrations += DocTemplate.getHTMLString(regStr);
                registrations += "</div>";
            } catch (URISyntaxException ex) {
                Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        admin += "<div class=\"ui tabular menu\">\n"
                + adminMenu
                + "   <div class=\"item\" data-tab=\"profile\">Profile</div>\n"
                + "   <div class=\"item\" data-tab=\"users\">Users</div>\n"
                + "   <div class=\"item\" data-tab=\"words\">Words</div>\n"
                + "</div>"
                + registrations
                + buildProfile()
                + "<div class=\"ui tab\" data-tab=\"users\">"
                + "<table class='ui table segment'>"
                + "     <thead><th></th><th>Username</th><th>Type</th>"
                + "         <th>Email</th></thead>"
                + getMembers((String) session.getAttribute("id"), type) + "</table>"
                + "</div>";

        admin += "<div class=\"ui tab\" data-tab=\"words\">";
        admin += getWords(true);
        admin += "</div>";
        admin += "<script type=\"text/javascript\">$('.tabular.menu .item').tab();</script>";
        return admin;
    }

    private String getMembers(String id, int type) {
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
            try {
                conn = SimpleDataSource.getConnection();
                stat = conn.createStatement();
                result = stat.executeQuery("SELECT SQL_NO_CACHE * FROM `user`;");
                while (result.next()) { // if not already a login
                    String name = result.getString("username");
                    String userType = result.getString("type");
                    String email = result.getString("email");
                    String user_id = result.getString("user_id");
                    String classer = (user_id.equals(id)) ? " class=\"active\"" : "";
                    if (!(type == 1 && "2".equals(userType))) {
                        String row = "<tr" + classer + "><td>";
                        if (type == 2) {
                            row += "<a href=\"EditUser?id=" + user_id + "\"><i class=\"write icon\"></i></a>"
                                    + "<a href=\"DeleteUser?id=" + user_id + "\" class=\"removeButton\">\n\t\t"
                                    + "     <i class=\"ui icon remove red\"></i>\n\t"
                                    + "</a>\n";
                        }
                        row += "<a href=\"TestHistoryServlet?id=" + user_id + "\"><i class=\"student icon\"></i></a></td>"
                                + "<td>" + name + "</td>";
                        String typeStr = "";
                        switch (Integer.parseInt(userType)) {
                            case 2:
                                typeStr = "Administrator";
                                break;
                            case 1:
                                typeStr = "Instructor";
                                break;
                            default:
                                typeStr = "User";
                        }
                        row += "<td>" + typeStr + "</td>";
                        row += "<td>" + email + "</td></tr>";
                        rows += row;
                    }
                }
            } finally {
                if (result != null) {
                    result.close();
                }
                if (stat != null) {
                    stat.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            return rows;
        } catch (SQLException ex) {
            Logger.getLogger(MembersArea.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
        return null;
    }

    private String getWords(boolean canEdit) {
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
            while (result.next()) {
                catNames[i] = result.getString("name");
                i++;
            }
            result = stat.executeQuery(query);

            while (result.next()) {
                String row = "";
                row += "<tr>\n"
                        + "<td>\n\t";
                if (canEdit) {
                    row += "       <a href=\"EditWord?id=" + result.getInt("id") + "\">\n\t\t"
                            + "         <i class=\"ui icon write\"></i>\n\t"
                            + "     </a>\n"
                            + "     <a href=\"DeleteWord?id=" + result.getInt("id") + "\" class=\"removeButton\">\n\t\t"
                            + "         <i class=\"ui icon remove red\"></i>\n\t"
                            + "     </a>\n";
                }
                row += "  </td>\n"
                        + "<td>" + result.getString("word") + "</td>"
                        + "<td>" + result.getString("translation") + "</td>"
                        + "<td>" + catNames[result.getInt("category") - 1] + "</td>"
                        + "<td>" + genders[result.getInt("gender")] + "</td>"
                        + "</tr>";
                rows += row;
            }
            String wordModal = ""
                    + "<div class=\"ui modal\">\n"
                    + "  <i class=\"close icon\"></i>\n"
                    + "  <div class=\"header\">\n"
                    + "    Are you sure?\n"
                    + "  </div>\n"
                    + "  <div class=\"content\">\n"
                    + "    <div class=\"description\">\n"
                    + "      Are you sure you want to delete this?\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "  <div class=\"actions\">\n"
                    + "    <div class=\"ui button actions positive\">Yes</div>\n"
                    + "    <div class=\"ui button actions negative\">No</div>\n"
                    + "  </div>\n"
                    + "</div>";
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
            return tableHead + words + tableFoot + wordModal;
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
