package com.servlet;

import com.dao.ItemDAO;
import com.entity.TUser;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/updateItemStatus")
public class AuditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        TUser currentUser = (TUser) session.getAttribute("currentUser");
        
        // 修复权限判断！！！
        if (currentUser == null || currentUser.getRole() != 1) {
            response.getWriter().write("<script>alert('无权限！');window.location.href='home.jsp';</script>");
            return;
        }

        String itemIdStr = request.getParameter("itemId");
        String status = request.getParameter("status");

        if (itemIdStr == null || status == null) {
            response.getWriter().write("<script>alert('参数错误！');window.history.back();</script>");
            return;
        }

        int itemId;
        try {
            itemId = Integer.parseInt(itemIdStr);
        } catch (Exception e) {
            response.getWriter().write("<script>alert('ID错误！');window.history.back();</script>");
            return;
        }

        ItemDAO itemDAO = new ItemDAO();
        boolean success = itemDAO.updateItemStatus(itemId, status);

        if (success) {
            response.sendRedirect(request.getContextPath() + "/information-audit-manage.jsp");
        } else {
            response.getWriter().write("<script>alert('失败！');window.history.back();</script>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}