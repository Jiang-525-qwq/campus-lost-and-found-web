package com.servlet;

import com.dao.UserDAO;
import com.entity.TUser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            request.setCharacterEncoding("UTF-8");
            String studentId = request.getParameter("studentId");
            String password = request.getParameter("password");

            UserDAO userDAO = new UserDAO();
            TUser user = userDAO.login(studentId, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);

                if ("admin".equals(user.getRole())) {
                    response.sendRedirect("adminConsole.jsp");
                } else {
                    response.sendRedirect("home.jsp");
                }

            } else {
                // 修复这里！！！
                response.sendRedirect("logIn.html?error=1");
            }
    }
}