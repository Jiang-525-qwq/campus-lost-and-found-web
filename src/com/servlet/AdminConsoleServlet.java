package com.servlet;

import com.entity.TUser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/adminConsole")
public class AdminConsoleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 权限校验
        TUser user = (TUser) request.getSession().getAttribute("currentUser");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("logIn.html");
            return;
        }
        // 进入管理员控制台
        request.getRequestDispatcher("/adminConsole.jsp").forward(request, response);
    }
}