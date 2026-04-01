package com.servlet;

import com.dao.FoundDao;
import com.dao.LostDao;
import com.dao.UserDAO;
import com.entity.Found;
import com.entity.Lost;
import com.entity.TUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        TUser currentUser = (TUser) session.getAttribute("currentUser");

        // 权限判断
        if (currentUser == null) {
            response.getWriter().write("<script>alert('请先登录！');window.location.href='logIn.html';</script>");
            return;
        }

        // 你已经改好的正确判断
        if (currentUser.getRole() != 1) {
            response.getWriter().write("<script>alert('无管理员权限！');window.location.href='home.jsp';</script>");
            return;
        }

        // 你原来的失物+招领查询（完全保留）
        LostDao lostDao = new LostDao();
        FoundDao foundDao = new FoundDao();

        List<Lost> allLost = lostDao.findAllLost();
        List<Found> allFound = foundDao.findAllFound();

        request.setAttribute("lostList", allLost);
        request.setAttribute("foundList", allFound);

        // ====================== 我只在这里新增用户查询 ======================
        UserDAO userDAO = new UserDAO();
        List<TUser> userList = userDAO.findAllUsers();
        request.setAttribute("userList", userList);
        request.setAttribute("currentUser", currentUser);

        // 跳转到用户管理页（保留你原来的）
        request.getRequestDispatcher("user-manage.jsp").forward(request, response);
    }
}