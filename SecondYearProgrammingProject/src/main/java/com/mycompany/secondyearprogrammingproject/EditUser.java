package com.mycompany.secondyearprogrammingproject;


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
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * @author eeu222 - Ben Williams
 * @author eeu239 - Liam Chapman
 * @author eeu23e - Jason Hall
 * @author eeu203 - James Jackson
 */
public class EditUser extends HttpServlet {

    private DocTemplate dt;
    private HttpSession session;

    public EditUser() {
        super();
        URL file = this.getClass().getResource("/header.html");
        dt = new DocTemplate(file);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> methods.
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
            if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }

            dt.prepareNewDoc();
            int type = (int) session.getAttribute("type");

            if (id == null) {
                out.println("You must supply an id");
                return;
            }
            if (type != 2) {
                out.println("You do not have sufficient privilges to do this");
            } else {
                Connection conn = null;
                conn = SimpleDataSource.getConnection();
                Statement stat = conn.createStatement();
                String query = "SELECT `username`, `email`, `type` FROM `user`"
                        + "WHERE `user_id` = " + id + ";";

                ResultSet user = stat.executeQuery(query);
                if (!user.next()) {
                    out.println("User not found");
                } else {
                    dt.replacePlaceholder("TITLE", "Edit User");
                    URL edit = this.getClass().getResource("/edit.html");
                    try {
                        String div = "";
                        if (success) {
                            div = "<div class =\"ui positive message fadeOut\">"
                                    + "     <p>User edited succesfully</p>"
                                    + "</div>";
                        }
                        dt.replacePlaceholder("CONTENT", div + DocTemplate.getHTMLString(edit));
                        dt.replacePlaceholder("USERNAME", user.getString("username"));
                        dt.replacePlaceholder("EMAIL", user.getString("email"));
                        dt.replacePlaceholder("ID", id);
                        switch (user.getInt("type")) {
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
     * Processes requests for both HTTP <code>POST</code> methods.
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
        if(session.getAttribute("type") == null){
                response.sendRedirect("index.html#failed");
                return;
            }
        int type = (int) session.getAttribute("type");
        String idHolder = (String)request.getParameter("user_id");
        String sessIdHolder = (String) session.getAttribute("id");
        if (!idHolder.equals(sessIdHolder) && type < 2) {
            out.print("You do not have permission to do that");
        }
        String oldPass = (String) request.getParameter("oldPass");
        String newPass = (String) request.getParameter("newPass");
        String query = "";
        Connection conn = null;
        conn = SimpleDataSource.getConnection();
        boolean updatingPass = false;
        if (oldPass != null) {
            PreparedStatement stat = conn.prepareStatement("SELECT `password` FROM `user` WHERE `user_id` = ?;");
            stat.setInt(1, Integer.parseInt(sessIdHolder));
            ResultSet rs = stat.executeQuery();
            if (updatingPass = rs.next()) {
                if (rs.getString("password").equals(Security.getHash(oldPass)) && !"".equals(newPass)) {
                    query = "UPDATE `user` SET"
                            + "`email` = ?, "
                            + "`username` = ?,"
                            + "`password` = ?"
                            + "WHERE user_id = ?;";
                } else if("".equals(newPass)){
                    out.print("You must supply a new password");
                    return;
                } else {
                    out.print("Your old password was not recognized");
                    return;
                }
            }

        } else if (type < 2) {
            query = "UPDATE `user` SET"
                    + "`email` = ?, "
                    + "`username` = ?"
                    + "WHERE user_id = ?;";
        } else {
            query = "UPDATE `user` SET"
                    + "`email` = ?, "
                    + "`username` = ?,"
                    + "`type` = ?"
                    + "WHERE user_id = ?;";
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String userType = request.getParameter("type");
        String id = request.getParameter("user_id");

        PreparedStatement stUname = conn.prepareStatement("SELECT * FROM `user` "
                + "WHERE `username` = ? AND `user_id` <> ?;");
        PreparedStatement stEmail = conn.prepareStatement("SELECT * FROM `user` "
                + "WHERE `email` = ? AND `user_id` <> ?;");
        stUname.setString(1, username);
        stUname.setInt(2, Integer.parseInt(idHolder));
        stEmail.setString(1, email);
        stEmail.setInt(2, Integer.parseInt(idHolder));
        ResultSet bu = stUname.executeQuery();
        ResultSet be = stEmail.executeQuery();
        boolean[] check = {false, false};
        if (bu.next()) {
            check[0] = true;
            out.println("Sorry that username is already taken");
        }
        if (be.next()) {
            check[1] = true;
            out.println("Sorry that email is already registered");
        }
        if (check[0] || check[1]) {
            return;
        }

        PreparedStatement stat = conn.prepareStatement(query);
        stat.setString(1, email);
        stat.setString(2, username);
        if(type == 2){
            stat.setInt(3, Integer.parseInt(userType));
            stat.setInt(4, Integer.parseInt(id));
        } else if(updatingPass){
            stat.setString(3, Security.getHash(newPass));
            stat.setInt(4, Integer.parseInt(id));
        } else
            stat.setInt(3, Integer.parseInt(id));
        int changed = stat.executeUpdate();
        if (changed != 0) {
            if(type < 2){
                session.setAttribute("email", email);
                session.setAttribute("username", username);
                response.sendRedirect("MembersArea");
            
            }else
                response.sendRedirect("EditUser?id=" + id + "&success=1");
        } else {
            out.println("Oops, something went wrong please try again");
        }
        conn.close();

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
