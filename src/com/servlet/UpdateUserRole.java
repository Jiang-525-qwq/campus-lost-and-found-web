package com.servlet;

import com.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/updateUserRole")
public class UpdateUserRole extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        int role = Integer.parseInt(request.getParameter("role"));

        UserDAO userDAO = new UserDAO();
        userDAO.updateUserRole(studentId, role);

        response.sendRedirect("admin");
    }
}