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

@WebServlet("/adminAuditList")
public class AdminAuditListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 管理员权限校验
        HttpSession session = request.getSession();
        TUser currentUser = (TUser) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != 1) {
            response.sendRedirect("home.jsp");
            return;
        }

        // 2. 查询所有物品数据
        ItemDAO dao = new ItemDAO();
        List<Item> list = dao.findAllItems();

        // 3. 统计各状态数量
        int pending = 0, approved = 0, rejected = 0;
        for (Item item : list) {
            String status = item.getStatus();
            if ("pending".equals(status)) {
                pending++;
            } else if ("approved".equals(status)) {
                approved++;
            } else {
                rejected++;
            }
        }

        // 4. 把数据存入request，转发到JSP页面
        request.setAttribute("itemList", list);
        request.setAttribute("pendingCount", pending);
        request.setAttribute("approvedCount", approved);
        request.setAttribute("rejectedCount", rejected);
        request.setAttribute("totalCount", list.size());

        request.getRequestDispatcher("/information-audit-manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}