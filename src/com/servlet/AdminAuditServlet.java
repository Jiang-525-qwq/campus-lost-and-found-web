package com.servlet;
//审核通过/驳回
import com.dao.ItemDAO;
import com.entity.TUser;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/audit")
public class AdminAuditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 权限判断
        TUser user = (TUser) request.getSession().getAttribute("currentUser");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("home.jsp");
            return;
        }

        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            String status = request.getParameter("status");

            ItemDAO dao = new ItemDAO();
            dao.updateItemStatus(itemId, status);

            // 审核完成 → 跳回审核列表
            response.sendRedirect("adminAuditList");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("adminAuditList?error=1");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}