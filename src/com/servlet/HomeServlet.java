package com.servlet;

import com.dao.LostDao;
import com.dao.FoundDao;
import com.entity.Lost;
import com.entity.Found;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. 查询数据库
        LostDao lostDao = new LostDao();
        FoundDao foundDao = new FoundDao();

        List<Lost> lostList = lostDao.findAllLost();
        List<Found> foundList = foundDao.findAllFound();

        // 2. 存入数据
        request.setAttribute("lostList", lostList);
        request.setAttribute("foundList", foundList);

        // 3. 直接转发到你现有的 home.html !!!
        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}