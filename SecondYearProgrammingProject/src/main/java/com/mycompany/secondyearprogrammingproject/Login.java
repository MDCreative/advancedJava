package com.mycompany.secondyearprogrammingproject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Uses POST to get a username and password from a user and displays the username
 * along with a greeting
 * @author Jason Hall, James Jackson
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
        Boolean nameNotSupplied = "".equals(request.getParameter("username"));
        Boolean passNotSupplied = "".equals(request.getParameter("password"));
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login</title>");
        out.println("</head>");
        out.println("<body>");
        if(nameNotSupplied || passNotSupplied){
            out.println("<p>Please supply a username and password</p>");
        } else {
            out.println("<p>Hello "+request.getParameter("username")+". Here are the facilities you can use:</p>");
            /**
             * <ul>
             *      <li> LINK <\li>
             *          ...
             * and so on...
             */
        }
        out.println("</body>");
        out.println("</html>");
    }
}