package com.servlet;

import com.dao.LostDao;
import com.entity.Lost;
import com.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebServlet("/addLost")
public class AddLostServlet extends HttpServlet {

    private LostDao lostDao = new LostDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.getWriter().write("请先登录！");
            return;
        }

        // 接收前端提交的数据
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String place = request.getParameter("place");
        String lostTimeStr = request.getParameter("lostTime");

        // 封装 Lost 对象
        Lost lost = new Lost();
        // ===== 只删除这一行报错的：lost.setUserId(user.getId()); =====
        // 其他全部保留！
        lost.setTitle(title);
        lost.setContent(content);
        lost.setPlace(place);
        lost.setLostTime(new Date());
        lost.setStatus(0);

        boolean success = lostDao.addLost(lost);

        if (success) {
            response.sendRedirect("home.jsp");
        } else {
            response.getWriter().write("发布失败");
        }
    }
}