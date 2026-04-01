package com.servlet;

import com.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/updateUserStatus")
public class UpdateUserStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId = request.getParameter("studentId");
        int status = Integer.parseInt(request.getParameter("status"));

        UserDAO userDAO = new UserDAO();
        userDAO.updateUserStatus(studentId, status);

        response.sendRedirect("admin");
    }
}