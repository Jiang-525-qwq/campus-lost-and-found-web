package com.servlet;
//内容管理
import com.dao.ItemDAO;
import com.entity.Item;
import com.entity.TUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/adminAllItem")
public class AdminAllItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 登录校验（管理员权限）
        TUser user = (TUser) request.getSession().getAttribute("currentUser");
        // ====================== 只改了这一行 ======================
        if (user == null || user.getRole() != 1) {
            response.sendRedirect("logIn.html");
            return;
        }

        ItemDAO dao = new ItemDAO();
        List<Item> allItems = dao.getAllItems();
        request.setAttribute("allItems", allItems);

        // 转发到内容管理页
        request.getRequestDispatcher("/content-manage.jsp").forward(request, response);
    }
}