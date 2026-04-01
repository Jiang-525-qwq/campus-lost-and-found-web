package com.servlet;

import com.dao.ItemDAO;
import com.entity.Item;
import com.entity.TUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/myItems")
public class MyItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 校验登录
        HttpSession session = request.getSession();
        TUser user = (TUser) session.getAttribute("currentUser");
        if (user == null) {
            response.sendRedirect("logIn.html");
            return;
        }

        // 2. 查询我的发布
        ItemDAO itemDAO = new ItemDAO();
        List<Item> myItems = itemDAO.getItemsByUserId(user.getStudentId());

        // 3. 存入Request，转发到个人主页
        request.setAttribute("items", myItems);
        request.getRequestDispatcher("/myself.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}